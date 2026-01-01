package com.scriptacus.riderunrealangelscript.toolwindow

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.ui.JBSplitter
import com.intellij.ui.content.Content
import com.intellij.ui.TreeSpeedSearch
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBTextField
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.Presentation
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JPanel
import javax.swing.BoxLayout
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.scriptacus.riderunrealangelscript.services.AngelScriptApiService
import javax.swing.JEditorPane
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import javax.swing.event.TreeExpansionListener
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.DocumentListener
import javax.swing.event.DocumentEvent

/**
 * Main panel for the AngelScript API Browser tool window.
 *
 * Displays a search box at the top, tree view in the middle, and detailed documentation at the bottom.
 */
class AngelScriptApiPanel(private val project: Project) : JPanel(BorderLayout()) {

    companion object {
        private val LOG = Logger.getInstance(AngelScriptApiPanel::class.java)
    }
    private val tree: Tree
    private val detailsPane: JEditorPane
    private val searchField: SearchTextField
    private val rootNode = DefaultMutableTreeNode("AngelScript API")
    private val treeModel = DefaultTreeModel(rootNode)
    private val apiService = project.getService(AngelScriptApiService::class.java)

    init {
        // Create search field
        searchField = SearchTextField(true)
        searchField.textEditor.emptyText.text = "Search API..."

        // Add search listener
        searchField.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = filterTree()
            override fun removeUpdate(e: DocumentEvent?) = filterTree()
            override fun changedUpdate(e: DocumentEvent?) = filterTree()
        })

        // Create refresh button
        val refreshAction = object : AnAction("Refresh", "Refresh the AngelScript API tree", AllIcons.Actions.Refresh) {
            override fun actionPerformed(e: AnActionEvent) {
                refresh()
            }
        }
        val refreshButton = ActionButton(
            refreshAction,
            Presentation().apply {
                icon = AllIcons.Actions.Refresh
                text = "Refresh"
            },
            "AngelScriptApiToolbar",
            com.intellij.openapi.actionSystem.ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE
        )

        // Create toolbar panel with search and refresh button
        val toolbarPanel = JPanel(BorderLayout())
        toolbarPanel.add(searchField, BorderLayout.CENTER)
        toolbarPanel.add(refreshButton, BorderLayout.EAST)

        // Create tree
        tree = Tree(treeModel)
        tree.cellRenderer = ApiTreeCellRenderer()
        tree.isRootVisible = false
        tree.showsRootHandles = true

        // Enable keyboard-based search (still useful for quick navigation)
        TreeSpeedSearch.installOn(tree, true) { path ->
            (path.lastPathComponent as? ApiTreeNode)?.displayName ?: ""
        }

        // Create details pane with HTML rendering
        detailsPane = JEditorPane("text/html", "<html><body>Select an API item to view details...</body></html>")
        detailsPane.isEditable = false
        detailsPane.contentType = "text/html"

        // Make background match IDE theme
        detailsPane.isOpaque = false
        detailsPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)

        // Create vertical splitter: tree on top, details on bottom
        val splitter = JBSplitter(true, 0.6f)
        splitter.firstComponent = JBScrollPane(tree)
        splitter.secondComponent = JBScrollPane(detailsPane)

        // Layout: toolbar at top, splitter in center
        add(toolbarPanel, BorderLayout.NORTH)
        add(splitter, BorderLayout.CENTER)

        // Setup selection listener
        tree.addTreeSelectionListener { e ->
            LOG.debug("[API Browser] Tree selection changed. Path: ${e.path}, isAddedPath: ${e.isAddedPath}")
            val node = e.path?.lastPathComponent as? ApiTreeNode
            if (node != null) {
                LOG.debug("[API Browser] Selected node: ${node.displayName} (type: ${node.type})")
                loadDetails(node)
            } else {
                LOG.debug("[API Browser] Selected node is not ApiTreeNode: ${e.path?.lastPathComponent?.javaClass?.name}")
            }
        }

        // Setup expansion listener for lazy loading
        tree.addTreeExpansionListener(object : TreeExpansionListener {
            override fun treeExpanded(event: TreeExpansionEvent) {
                val node = event.path.lastPathComponent as? ApiTreeNode
                node?.let { loadChildren(it) }
            }

            override fun treeCollapsed(event: TreeExpansionEvent) {
                // No action needed on collapse
            }
        })

        // Load on first visibility (lazy loading like VSCode)
        var hasLoaded = false
        addHierarchyListener { e ->
            if (isShowing && !hasLoaded) {
                hasLoaded = true
                loadRootApi()
            }
        }
    }

    /**
     * Refresh the API tree (called by refresh action).
     */
    fun refresh() {
        loadRootApi()
    }

    /**
     * Load the root level of the API tree.
     */
    private fun loadRootApi() {
        rootNode.removeAllChildren()
        rootNode.add(DefaultMutableTreeNode("Loading..."))
        treeModel.reload()

        apiService.getApi("") { apiItems ->
            ApplicationManager.getApplication().invokeLater {
                rootNode.removeAllChildren()
                populateTree(rootNode, apiItems)
                treeModel.nodeStructureChanged(rootNode)
                treeModel.reload(rootNode)
                tree.updateUI()
            }
        }
    }

    /**
     * Load children for a specific tree node (lazy loading).
     */
    private fun loadChildren(node: ApiTreeNode) {
        if (node.childrenLoaded) {
            return
        }

        apiService.getApi(node.id) { apiItems ->
            ApplicationManager.getApplication().invokeLater {
                // Remove all children (including "Loading..." placeholder)
                node.removeAllChildren()

                // Populate with actual items
                populateTree(node, apiItems)
                node.childrenLoaded = true

                // Notify model and force UI update
                treeModel.nodeStructureChanged(node)
                treeModel.reload(node)
                tree.updateUI()
            }
        }
    }

    /**
     * Populate a tree node with API items from the LSP server.
     */
    private fun populateTree(parent: DefaultMutableTreeNode, apiItems: List<Map<String, Any>>) {
        parent.removeAllChildren()

        if (apiItems.isEmpty()) {
            if (parent == rootNode) {
                parent.add(DefaultMutableTreeNode("No API data available. Connect to Unreal Engine."))
            }
            return
        }

        for (item in apiItems) {
            val label = item["label"] as? String ?: continue
            val type = item["type"] as? String ?: "unknown"
            val id = item["id"] as? String ?: label
            val data = item["data"] // Keep the full data array for details lookup

            // Infer hasChildren from type - namespaces and classes have children
            val hasChildren = type in listOf("namespace", "class", "struct", "enum")

            val node = ApiTreeNode(
                id = id,
                displayName = label,
                type = type,
                hasChildren = hasChildren,
                data = data // Store data for use in getAPIDetails
            )

            parent.add(node)

            // Add placeholder child for expandable nodes
            if (hasChildren) {
                node.add(DefaultMutableTreeNode("Loading..."))
            }
        }
    }

    /**
     * Load and display details for a selected API tree node.
     */
    private fun loadDetails(node: ApiTreeNode) {
        LOG.debug("[API Browser] loadDetails called for node: ${node.displayName} (id: ${node.id}, type: ${node.type})")
        detailsPane.text = wrapHtmlWithStyles("Loading details...")
        detailsPane.repaint()

        // Use the data array if available, fallback to id
        val detailsParam = node.data ?: node.id
        LOG.debug("[API Browser] Calling getApiDetails with param: $detailsParam")

        apiService.getApiDetails(detailsParam) { markdown ->
            LOG.debug("[API Browser] getApiDetails callback received, markdown length: ${markdown.length}")
            ApplicationManager.getApplication().invokeLater {
                if (markdown.isBlank()) {
                    // No documentation available
                    detailsPane.text = wrapHtmlWithStyles(
                        "Select API to see details..."
                    )
                } else {
                    // Convert markdown to HTML
                    val html = convertMarkdownToHtml(markdown)
                    detailsPane.text = wrapHtmlWithStyles(html)
                }
                detailsPane.caretPosition = 0 // Scroll to top
                detailsPane.revalidate()
                detailsPane.repaint()
            }
        }
    }

    /**
     * Simple markdown to HTML conversion.
     * TODO: Use proper markdown parser (commonmark-java) for better rendering.
     */
    private fun convertMarkdownToHtml(markdown: String): String {
        var html = markdown

        // Convert code blocks (also strip "angelscript_snippet" language tag)
        html = html.replace(Regex("```(?:angelscript_snippet)?([\\s\\S]*?)```")) { matchResult ->
            val code = matchResult.groupValues[1].trim()
            "<pre><code>$code</code></pre>"
        }

        // Convert inline code
        html = html.replace(Regex("`([^`]+)`")) { matchResult ->
            "<code>${matchResult.groupValues[1]}</code>"
        }

        // Convert headers
        html = html.replace(Regex("^### (.+)$", RegexOption.MULTILINE)) { "<h3>${it.groupValues[1]}</h3>" }
        html = html.replace(Regex("^## (.+)$", RegexOption.MULTILINE)) { "<h2>${it.groupValues[1]}</h2>" }
        html = html.replace(Regex("^# (.+)$", RegexOption.MULTILINE)) { "<h1>${it.groupValues[1]}</h1>" }

        // Convert bold (must be before other asterisk handling)
        html = html.replace(Regex("\\*\\*([^*]+)\\*\\*")) { "<strong>${it.groupValues[1]}</strong>" }

        // Convert unordered lists (lines starting with "* ") - use simple bullet character
        html = html.replace(Regex("^\\* ", RegexOption.MULTILINE)) { "• " }

        // Strip emphasis markers (paired single asterisks) - just remove them, don't convert to italic
        // This won't match ** (bold) because we already converted those above
        html = html.replace(Regex("(?<!\\*)\\*([^*\n]+?)\\*(?!\\*)")) { it.groupValues[1] }

        // Clean up any remaining single asterisks (unpaired emphasis markers)
        html = html.replace(Regex("(?<!\\*)\\*(?!\\*)"), "")

        // Convert paragraphs (double newlines)
        html = html.replace("\n\n", "</p><p>")

        // Convert line breaks
        html = html.replace("\n", "<br>")

        return html
    }

    /**
     * Wrap HTML content with styling for consistent appearance.
     * Uses IntelliJ theme colors for proper light/dark theme support.
     */
    private fun wrapHtmlWithStyles(html: String): String {
        // TEMPORARY: No CSS to diagnose parsing issue
        return """
            <html>
            <body>
                $html
            </body>
            </html>
        """.trimIndent()
    }

    /**
     * Convert a Color to hex string for CSS.
     */
    private fun java.awt.Color.toHex(): String {
        return String.format("#%02x%02x%02x", red, green, blue)
    }

    /**
     * Filter the tree based on the search text using LSP server search.
     */
    private fun filterTree() {
        val searchText = searchField.text.trim()

        if (searchText.isEmpty()) {
            // Restore normal root view
            loadRootApi()
            return
        }

        if (searchText.length < 2) {
            // Don't search for single characters
            return
        }

        // Use LSP server's search functionality
        apiService.getApiSearch(searchText) { apiItems ->
            ApplicationManager.getApplication().invokeLater {
                rootNode.removeAllChildren()
                populateTree(rootNode, apiItems)
                treeModel.nodeStructureChanged(rootNode)
                treeModel.reload(rootNode)
                tree.updateUI()

                // Expand all results for easy viewing
                for (i in 0 until rootNode.childCount) {
                    tree.expandRow(i)
                }
            }
        }
    }
}
