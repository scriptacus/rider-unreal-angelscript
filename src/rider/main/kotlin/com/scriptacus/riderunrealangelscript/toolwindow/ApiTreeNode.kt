package com.scriptacus.riderunrealangelscript.toolwindow

import javax.swing.tree.DefaultMutableTreeNode

/**
 * Tree node representing an AngelScript API item (namespace, class, function, etc.)
 *
 * @property id Unique identifier for this API item (used for LSP requests)
 * @property displayName Display name shown in the tree
 * @property type Type of API item (namespace, class, function, variable, etc.)
 * @property hasChildren Whether this node has children that can be expanded
 * @property data Full data array from the LSP server (used for getAPIDetails)
 * @property childrenLoaded Whether children have been loaded from the LSP server
 */
data class ApiTreeNode(
    val id: String,
    val displayName: String,
    val type: String,
    val hasChildren: Boolean,
    val data: Any? = null,
    var childrenLoaded: Boolean = false
) : DefaultMutableTreeNode(displayName) {
    override fun toString(): String = displayName
}