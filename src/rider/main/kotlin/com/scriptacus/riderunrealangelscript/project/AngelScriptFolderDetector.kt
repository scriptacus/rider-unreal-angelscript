package com.scriptacus.riderunrealangelscript.project

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider
import com.intellij.openapi.roots.ModuleRootEvent
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.SyntheticLibrary
import com.intellij.openapi.vfs.*
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.messages.MessageBusConnection
import com.intellij.openapi.application.runReadAction
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import javax.swing.Icon

/**
 * DEPRECATED: This class is no longer used. AngelScript Script folders are now registered
 * as module content roots via AngelScriptContentRootProvider for better search prioritization.
 *
 * Historical note: This used AdditionalLibraryRootsProvider to add Script folders as library roots,
 * but this caused them to be deprioritized in "Navigate to File" searches. The new approach treats
 * them as first-class project source files.
 *
 * See: AngelScriptContentRootProvider for the current implementation
 *
 * @deprecated Use AngelScriptContentRootProvider instead
 */
@Deprecated("Use AngelScriptContentRootProvider instead")
class AngelScriptFolderDetector : AdditionalLibraryRootsProvider(), Disposable {
    private val LOG = Logger.getInstance(AngelScriptFolderDetector::class.java)

    // Thread-safe cache with project-specific listeners
    private val cache = ConcurrentHashMap<Project, Collection<SyntheticLibrary>>()
    private val listeners = ConcurrentHashMap<Project, MessageBusConnection>()
    private val cs = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val computationInProgress = ConcurrentHashMap<Project, Job>()

    override fun getAdditionalProjectLibraries(project: Project): Collection<SyntheticLibrary> {
        // Check if project is already disposed
        if (project.isDisposed) {
            return emptyList()
        }

        // Check cache first
        val cached = cache[project]
        if (cached != null) {
            LOG.debug("Cache hit for project: ${project.name}, ${cached.size} libraries")
            return cached
        }

        LOG.debug("Cache miss for project: ${project.name}, scheduling background computation")

        // Set up listeners for this project if not already done
        setupListeners(project)

        // Don't block EDT with file system traversal - schedule async computation and return empty list for now
        // The cache will be populated asynchronously and the platform will pick it up on next indexing cycle
        if (!computationInProgress.containsKey(project)) {
            scheduleBackgroundRecomputation(project)
        }

        return emptyList()
    }

    private fun setupListeners(project: Project) {
        if (listeners.containsKey(project)) {
            return // Already set up
        }

        val connection = project.messageBus.connect()
        listeners[project] = connection

        // Listen for project close - clean up resources
        connection.subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
            override fun projectClosing(closingProject: Project) {
                if (closingProject == project) {
                    cleanupProject(project)
                }
            }
        })

        // Listen for dumb mode changes - invalidate cache when smart mode starts
        connection.subscribe(DumbService.DUMB_MODE, object : DumbService.DumbModeListener {
            override fun exitDumbMode() {
                invalidateCache(project)
            }
        })

        // Listen for VFS changes - invalidate when relevant files change
        connection.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                var shouldInvalidate = false

                for (event in events) {
                    val file = event.file ?: continue

                    when (event) {
                        is VFileCreateEvent, is VFileDeleteEvent -> {
                            // Invalidate if .uproject or .uplugin files are added/removed
                            if (file.extension == "uproject" || file.extension == "uplugin") {
                                shouldInvalidate = true
                                break
                            }

                            // Invalidate if Script folder is created/deleted
                            if (file.isDirectory && file.name == "Script") {
                                shouldInvalidate = true
                                break
                            }
                        }
                    }
                }

                if (shouldInvalidate) {
                    invalidateCache(project)
                }
            }
        })

        LOG.debug("Set up listeners for project: ${project.name}")
    }

    private fun invalidateCache(project: Project) {
        // Keep old cache until new one is ready (avoid temporary empty results)
        // Don't remove: cache.remove(project)

        // Schedule background recomputation (don't block EDT!)
        scheduleBackgroundRecomputation(project)
    }

    private fun scheduleBackgroundRecomputation(project: Project) {
        // Don't schedule if project is already disposed
        if (project.isDisposed) {
            return
        }

        // Cancel previous computation if still running
        computationInProgress[project]?.cancel()

        LOG.debug("Scheduling background recomputation for project: ${project.name}")

        val job = cs.launch {
            val startTime = System.currentTimeMillis()
            val libraries = runReadAction {
                computeScriptLibraries(project)
            }
            val duration = System.currentTimeMillis() - startTime

            LOG.info("Recomputed Script libraries for ${project.name} in ${duration}ms: ${libraries.size} libraries found")

            // Check disposal before EDT dispatch to prevent deadlock during shutdown
            if (!project.isDisposed) {
                try {
                    withContext(Dispatchers.Main) {
                        cache[project] = libraries
                        computationInProgress.remove(project)

                        // Notify platform that library roots have changed so it can re-index
                        LOG.debug("Notifying platform of library changes for project: ${project.name}")
                        ProjectRootManager.getInstance(project).incModificationCount()
                    }
                } catch (e: Exception) {
                    // Gracefully handle EDT dispatch failures (e.g., during shutdown)
                    LOG.warn("Failed to update cache for project ${project.name}: ${e.message}")
                    computationInProgress.remove(project)
                }
            } else {
                // Project was disposed during computation - clean up
                LOG.debug("Project ${project.name} disposed during computation, skipping cache update")
                computationInProgress.remove(project)
            }
        }

        computationInProgress[project] = job
    }

    private fun computeScriptLibraries(project: Project): Collection<SyntheticLibrary> {
        // Avoid circular dependency during indexing
        if (DumbService.isDumb(project)) {
            return emptyList()
        }

        val basePath = project.basePath
        if (basePath == null) {
            LOG.warn("Project has no base path, cannot detect Script folders")
            return emptyList()
        }

        // Use VirtualFileManager directly to avoid circular dependency with projectScope()
        val baseDir = VirtualFileManager.getInstance().findFileByUrl("file://$basePath")
        if (baseDir == null) {
            LOG.warn("Cannot find base directory at: $basePath")
            return emptyList()
        }

        val scriptFolders = mutableSetOf<VirtualFile>()

        // Visit all files recursively using VFS API (safe - no circular dependency)
        VfsUtil.visitChildrenRecursively(baseDir, object : VirtualFileVisitor<Unit>() {
            override fun visitFile(file: VirtualFile): Boolean {
                // Support cancellation of long-running operations
                ProgressManager.checkCanceled()

                // Skip common non-source directories for performance
                if (file.isDirectory && file.name in SKIP_DIRS) {
                    return false
                }

                // Check for .uproject or .uplugin files
                if (!file.isDirectory && (file.extension == "uproject" || file.extension == "uplugin")) {
                    val parentDir = file.parent
                    if (parentDir != null) {
                        val scriptFolder = parentDir.findChild("Script")
                        if (scriptFolder != null && scriptFolder.isDirectory) {
                            LOG.info("Found Script folder for ${file.extension}: ${scriptFolder.path}")
                            scriptFolders.add(scriptFolder)
                        }
                    }
                }
                return true // Continue visiting
            }
        })

        if (scriptFolders.isEmpty()) {
            LOG.info("No AngelScript Script folders found")
            return emptyList()
        } else {
            scriptFolders.forEach { LOG.info("  - ${it.path}") }
        }

        // Convert VirtualFile folders to SyntheticLibrary instances
        return scriptFolders.map { folder ->
            AngelScriptSyntheticLibrary(folder)
        }
    }

    /**
     * Represents an AngelScript Script folder as a synthetic library.
     * This makes the files visible in Navigate to File and available for indexing.
     */
    private class AngelScriptSyntheticLibrary(
        private val sourceRoot: VirtualFile
    ) : SyntheticLibrary(), ItemPresentation {

        override fun getSourceRoots(): Collection<VirtualFile> = listOf(sourceRoot)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is AngelScriptSyntheticLibrary) return false
            return sourceRoot == other.sourceRoot
        }

        override fun hashCode(): Int = sourceRoot.hashCode()

        // ItemPresentation for Project View display
        override fun getPresentableText(): String = "AngelScript: ${sourceRoot.name}"

        override fun getLocationString(): String = sourceRoot.path

        override fun getIcon(unused: Boolean): Icon? = null
    }

    private fun cleanupProject(project: Project) {
        LOG.debug("Cleaning up resources for project: ${project.name}")
        computationInProgress.remove(project)?.cancel()
        listeners.remove(project)?.disconnect()
        cache.remove(project)
        LOG.debug("Cleaned up resources for project: ${project.name}")
    }

    override fun dispose() {
        LOG.info("Disposing AngelScriptFolderDetector")
        computationInProgress.values.forEach { it.cancel() }
        computationInProgress.clear()
        listeners.values.forEach { it.disconnect() }
        listeners.clear()
        cache.clear()
        cs.cancel()
        LOG.info("AngelScriptFolderDetector disposed successfully")
    }

    companion object {
        // Directories to skip during VFS traversal for performance
        private val SKIP_DIRS = setOf(
            ".git",
            "node_modules",
            "Binaries",
            "Intermediate",
            "Saved",
            ".idea",
            "DerivedDataCache",
            ".vs",
            ".vscode"
        )
    }
}
