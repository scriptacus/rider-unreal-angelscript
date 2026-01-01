package com.scriptacus.riderunrealangelscript.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.ui.ColoredTreeCellRenderer
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

/**
 * Custom tree cell renderer for API tree nodes.
 * Displays appropriate icons based on the node type.
 */
class ApiTreeCellRenderer : ColoredTreeCellRenderer() {
    override fun customizeCellRenderer(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ) {
        if (value !is ApiTreeNode) {
            // Handle non-ApiTreeNode (e.g., "Loading..." placeholder)
            if (value is DefaultMutableTreeNode) {
                append(value.toString())
            }
            return
        }

        // Set icon based on API item type
        icon = when (value.type) {
            "namespace" -> AllIcons.Nodes.Package
            "class", "typename", "typename_actor", "typename_component", "typename_struct"
                -> AllIcons.Nodes.Class
            "function", "member_function", "global_function"
                -> AllIcons.Nodes.Function
            "variable", "member_variable", "global_variable"
                -> AllIcons.Nodes.Variable
            "property", "member_accessor", "global_accessor"
                -> AllIcons.Nodes.Property
            "typename_event", "typename_delegate"
                -> AllIcons.Nodes.Interface
            else -> AllIcons.FileTypes.Unknown
        }

        // Append display name
        append(value.displayName)
    }
}
