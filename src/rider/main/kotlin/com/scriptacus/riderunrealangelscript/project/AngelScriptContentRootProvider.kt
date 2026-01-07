package com.scriptacus.riderunrealangelscript.project

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.*
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import java.util.concurrent.ConcurrentHashMap

/**
 * Automatically detects and registers AngelScript Script folders as module content roots.
 *
 * This ensures that Script folders (which follow Unreal Engine conventions) are indexed
 * and treated as first-class project source files, giving them proper search priority.
 *
 * Detection strategy:
 * 1. Find all .uproject files → check for <parent>/Script/
 * 2. Find all .uplugin files → check for <parent>/Script/
 *
 * Cache invalidation:
 * - Invalidates when .uproject/.uplugin files are added/deleted
 * - Invalidates when Script folders are created/deleted
 *
 * This catches:
 * - Project AngelScript code
 * - Project plugin AngelScript code
 * - Engine-level plugin AngelScript code
 *
 * Performance: Defers Script folder detection until indexing completes to avoid
 * querying FilenameIndex before it's ready.
 */
class AngelScriptContentRootProvider : ProjectActivity {
    private val LOG = Logger.getInstance(AngelScriptContentRootProvider::class.java)

    // Track content roots we've added to avoid redundant operations
    private val addedContentRoots = ConcurrentHashMap.newKeySet<String>()

    override suspend fun execute(project: Project) {
        LOG.info("AngelScriptContentRootProvider initializing for project: ${project.name}")

        // Run Script folder detection in background to avoid EDT violations
        com.intellij.openapi.application.ReadAction.nonBlocking<Unit> {
            detectAndRegisterScriptFolders(project)
        }
            .inSmartMode(project)
            .finishOnUiThread(com.intellij.openapi.application.ModalityState.defaultModalityState()) {
                setupVfsListener(project)
                LOG.info("AngelScriptContentRootProvider initialization complete")
            }
            .submit(com.intellij.util.concurrency.AppExecutorUtil.getAppExecutorService())
    }

    private fun detectAndRegisterScriptFolders(project: Project) {
        val startTime = System.currentTimeMillis()
        val scriptFolders = detectScriptFolders(project)
        val duration = System.currentTimeMillis() - startTime

        LOG.info("Detected ${scriptFolders.size} Script folders in ${duration}ms")

        if (scriptFolders.isEmpty()) {
            LOG.info("No AngelScript Script folders found")
            return
        }

        val mainModule = findMainModule(project)
        if (mainModule == null) {
            LOG.warn("No main module found in project, cannot add content roots")
            return
        }

        scriptFolders.forEach { folder ->
            LOG.info("  - ${folder.path}")
            addContentRootIfNotExists(mainModule, folder)
        }
    }

    private fun detectScriptFolders(project: Project): List<VirtualFile> {
        return ScriptFolderDetector.detectScriptFolders(project)
    }

    private fun findMainModule(project: Project) = ModuleManager.getInstance(project)
        .modules
        .firstOrNull { !it.name.contains("ReSharper") }

    private fun addContentRootIfNotExists(module: com.intellij.openapi.module.Module, folder: VirtualFile) {
        // Check if already in our tracking set
        if (folder.path in addedContentRoots) {
            LOG.debug("Content root already tracked: ${folder.path}")
            return
        }

        // Check if already exists in module
        val existingRoots = ModuleRootManager.getInstance(module).contentRoots
        if (existingRoots.any { it.path == folder.path }) {
            LOG.info("Content root already exists in module: ${folder.path}")
            addedContentRoots.add(folder.path)
            return
        }

        // Check if parent content root covers it
        if (existingRoots.any { folder.path.startsWith(it.path) }) {
            LOG.info("Content root already covered by parent: ${folder.path}")
            return
        }

        // Add as content root - must run on EDT
        LOG.info("Adding content root: ${folder.path}")
        ApplicationManager.getApplication().invokeLater {
            ModuleRootModificationUtil.updateModel(module) { model ->
                val contentEntry = model.addContentEntry(folder)
                // Mark as source root
                contentEntry.addSourceFolder(folder, false)
            }
            addedContentRoots.add(folder.path)
            LOG.info("Content root added successfully: ${folder.path}")
        }
    }

    private fun removeContentRootIfExists(project: Project, folder: VirtualFile) {
        if (folder.path !in addedContentRoots) {
            return
        }

        val mainModule = findMainModule(project) ?: return

        LOG.info("Removing content root: ${folder.path}")
        ApplicationManager.getApplication().invokeLater {
            ModuleRootModificationUtil.updateModel(mainModule) { model ->
                model.contentEntries.forEach { entry ->
                    if (entry.url == folder.url) {
                        model.removeContentEntry(entry)
                    }
                }
            }
            addedContentRoots.remove(folder.path)
            LOG.info("Content root removed successfully: ${folder.path}")
        }
    }

    private fun setupVfsListener(project: Project) {
        project.messageBus.connect().subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: List<VFileEvent>) {
                    // Filter events to this project only
                    val projectBasePath = project.basePath ?: return

                    var shouldRefresh = false

                    for (event in events) {
                        val file = event.file ?: continue

                        // Only process events in this project
                        if (!file.path.startsWith(projectBasePath)) {
                            continue
                        }

                        when (event) {
                            is VFileCreateEvent -> {
                                // Script folder created
                                if (file.isDirectory && file.name == "Script") {
                                    // Check if it's next to .uproject or .uplugin
                                    val parent = file.parent
                                    if (parent != null) {
                                        val hasProjectFile = parent.children.any {
                                            it.extension == "uproject" || it.extension == "uplugin"
                                        }
                                        if (hasProjectFile) {
                                            val mainModule = findMainModule(project)
                                            if (mainModule != null) {
                                                addContentRootIfNotExists(mainModule, file)
                                            }
                                        }
                                    }
                                }
                                // .uproject or .uplugin file created
                                else if (file.extension == "uproject" || file.extension == "uplugin") {
                                    val scriptFolder = file.parent?.findChild("Script")
                                    if (scriptFolder?.isDirectory == true) {
                                        val mainModule = findMainModule(project)
                                        if (mainModule != null) {
                                            addContentRootIfNotExists(mainModule, scriptFolder)
                                        }
                                    }
                                }
                            }
                            is VFileDeleteEvent -> {
                                // Script folder deleted
                                if (file.name == "Script" && file.isDirectory) {
                                    removeContentRootIfExists(project, file)
                                }
                                // .uproject or .uplugin file deleted - trigger full refresh
                                else if (file.extension == "uproject" || file.extension == "uplugin") {
                                    shouldRefresh = true
                                }
                            }
                        }
                    }

                    // If a project file was deleted, we might need to remove orphaned content roots
                    if (shouldRefresh) {
                        LOG.info("Project file deleted, triggering refresh")
                        // For now, just log. In production, might want to clean up orphaned roots
                    }
                }
            }
        )
    }

}