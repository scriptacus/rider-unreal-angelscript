package com.scriptacus.riderunrealangelscript.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.scriptacus.riderunrealangelscript.AngelScriptIcons
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

/**
 * Custom cell renderer for the Script files tree.
 * Shows appropriate icons for directories and .as files with VCS status coloring.
 */
class ScriptFileTreeCellRenderer(private val project: Project) : ColoredTreeCellRenderer() {
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

                // Get VCS status and apply color
                val fileStatus = FileStatusManager.getInstance(project).getStatus(file)
                val statusColor = fileStatus.color

                val attributes = SimpleTextAttributes(
                    SimpleTextAttributes.STYLE_PLAIN,
                    statusColor
                )

                // Set text with VCS color
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
}
