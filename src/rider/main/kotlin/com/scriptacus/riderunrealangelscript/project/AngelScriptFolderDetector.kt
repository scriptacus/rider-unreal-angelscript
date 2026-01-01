package com.scriptacus.riderunrealangelscript.project

import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.util.indexing.IndexableSetContributor
import com.intellij.openapi.vfs.*
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.messages.MessageBusConnection
import java.util.concurrent.ConcurrentHashMap

/**
 * Automatically detects and provides AngelScript Script folders for indexing.
 *
 * This ensures that Script folders (which follow Unreal Engine conventions) are indexed
 * and available to LSP4IJ even when they're not explicitly part of the Rider project structure.
 *
 * Unlike AdditionalLibraryRootsProvider (which treats folders as read-only library code),
 * IndexableSetContributor adds folders for indexing while preserving full highlighting
 * and external annotator support.
 *
 * Detection strategy:
 * 1. Find all .uproject files → check for <parent>/Script/
 * 2. Find all .uplugin files → check for <parent>/Script/
 *
 * Cache invalidation:
 * - Invalidates when .uproject/.uplugin files are added/deleted
 * - Invalidates when Script folders are created/deleted
 * - Invalidates when exiting dumb mode (to retry failed scans)
 *
 * This catches:
 * - Project AngelScript code
 * - Project plugin AngelScript code
 * - Engine-level plugin AngelScript code
 */
class AngelScriptFolderDetector : IndexableSetContributor() {
    private val LOG = Logger.getInstance(AngelScriptFolderDetector::class.java)

    // Thread-safe cache with project-specific listeners
    private val cache = ConcurrentHashMap<Project, Set<VirtualFile>>()
    private val listeners = ConcurrentHashMap<Project, MessageBusConnection>()
    private val computationInProgress = ConcurrentHashMap<Project, Boolean>()

    override fun getAdditionalRootsToIndex(): Set<VirtualFile> {
        // Not used - we provide project-specific roots instead
        return emptySet()
    }

    override fun getAdditionalProjectRootsToIndex(project: Project): Set<VirtualFile> {
        // Check cache first
        cache[project]?.let { cached ->
            return cached
        }

        // Set up listeners for this project if not already done
        setupListeners(project)

        // Don't block EDT with file system traversal - schedule async computation and return empty set for now
        // The cache will be populated asynchronously and the platform will pick it up on next indexing cycle
        scheduleBackgroundRecomputation(project)

        return emptySet()
    }

    private fun setupListeners(project: Project) {
        if (listeners.containsKey(project)) {
            return // Already set up
        }

        val connection = project.messageBus.connect()
        listeners[project] = connection

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
        // Remove cached data
        cache.remove(project)

        // Schedule background recomputation (don't block EDT!)
        scheduleBackgroundRecomputation(project)
    }

    private fun scheduleBackgroundRecomputation(project: Project) {
        // Prevent multiple concurrent computations for same project
        if (computationInProgress.putIfAbsent(project, true) != null) {
            LOG.debug("Recomputation already in progress for project: ${project.name}")
            return // Already computing
        }

        ReadAction.nonBlocking<Set<VirtualFile>> {
            computeScriptFolders(project)
        }
        .expireWith(project)
        .finishOnUiThread(ModalityState.defaultModalityState()) { folders ->
            cache[project] = folders
            computationInProgress.remove(project)

            // Note: The platform will automatically detect the changed indexable roots
            // when getAdditionalProjectRootsToIndex() is called next time
        }
        .submit(AppExecutorUtil.getAppExecutorService())
    }

    private fun computeScriptFolders(project: Project): Set<VirtualFile> {
        // Avoid circular dependency during indexing
        if (DumbService.isDumb(project)) {
            return emptySet()
        }

        val basePath = project.basePath
        if (basePath == null) {
            LOG.warn("Project has no base path, cannot detect Script folders")
            return emptySet()
        }

        // Use VirtualFileManager directly to avoid circular dependency with projectScope()
        val baseDir = VirtualFileManager.getInstance().findFileByUrl("file://$basePath")
        if (baseDir == null) {
            LOG.warn("Cannot find base directory at: $basePath")
            return emptySet()
        }

        val scriptFolders = mutableSetOf<VirtualFile>()

        // Visit all files recursively using VFS API (safe - no circular dependency)
        VfsUtil.visitChildrenRecursively(baseDir, object : VirtualFileVisitor<Unit>() {
            override fun visitFile(file: VirtualFile): Boolean {
                // Support cancellation of long-running operations
                ProgressManager.checkCanceled()

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
        } else {
            scriptFolders.forEach { LOG.info("  - ${it.path}") }
        }

        return scriptFolders
    }
}
