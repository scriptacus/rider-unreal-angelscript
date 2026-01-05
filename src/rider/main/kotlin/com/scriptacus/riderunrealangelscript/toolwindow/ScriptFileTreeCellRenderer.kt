package com.scriptacus.riderunrealangelscript.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.scriptacus.riderunrealangelscript.AngelScriptIcons
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

/**
 * Custom cell renderer for the Script files tree.
 * Shows appropriate icons for directories and .as files with VCS status coloring.
 * Uses async loading to avoid EDT violations when querying VCS status.
 */
class ScriptFileTreeCellRenderer(
    private val project: Project,
    private val tree: JTree
) : ColoredTreeCellRenderer() {

    // Cache to avoid slow VCS status lookups on EDT
    private val statusCache = ConcurrentHashMap<VirtualFile, FileStatus>()

    fun clearCache() {
        statusCache.clear()
    }

    fun invalidateStatus(file: VirtualFile) {
        statusCache.remove(file)
    }

    override fun customizeCellRenderer(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ) {
        if (value !is DefaultMutableTreeNode) {
            return
        }

        val userObject = value.userObject
        when (userObject) {
            is FileNode -> {
                val file = userObject.file

                // Set icon
                icon = when {
                    file.isDirectory -> if (expanded) AllIcons.Nodes.Folder else AllIcons.Nodes.Folder
                    file.extension == "as" || file.extension == "ash" -> AngelScriptIcons.FileIcon
                    else -> AllIcons.FileTypes.Any_type
                }

                // Get cached status or load asynchronously
                val cachedStatus = statusCache[file]
                if (cachedStatus == null) {
                    // Not cached - load asynchronously in background
                    loadStatusAsync(file)
                }

                // Use cached status color or default
                val statusColor = cachedStatus?.color

                val attributes = SimpleTextAttributes(
                    SimpleTextAttributes.STYLE_PLAIN,
                    statusColor
                )

                append(userObject.toString(), attributes)
            }
            is GroupNode -> {
                // Set icon based on group name
                icon = when (userObject.name) {
                    "Engine" -> AllIcons.Nodes.ModuleGroup
                    "Plugins" -> AllIcons.Nodes.ModuleGroup
                    else -> AllIcons.Nodes.Module  // For project names
                }

                // Use regular text attributes (not grayed)
                append(userObject.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
            }
            else -> {
                // For other nodes (like "No Script folders found")
                append(value.toString(), SimpleTextAttributes.GRAYED_ATTRIBUTES)
            }
        }
    }

    private fun loadStatusAsync(file: VirtualFile) {
        // Load status on background thread
        ReadAction.nonBlocking(Callable {
            FileStatusManager.getInstance(project).getStatus(file)
        }).finishOnUiThread(ModalityState.defaultModalityState()) { status ->
            // Cache result
            statusCache[file] = status
            // Repaint tree to show color
            tree.repaint()
        }.submit(AppExecutorUtil.getAppExecutorService())
    }
}
