package com.scriptacus.riderunrealangelscript.toolwindow

import com.intellij.ide.DataManager
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.*
import com.intellij.ui.TreeUIHelper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.scriptacus.riderunrealangelscript.toolwindow.actions.*
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.AbstractAction
import javax.swing.JPanel
import javax.swing.KeyStroke
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

/**
 * Wrapper for VirtualFile to provide custom display in tree.
 */
data class FileNode(val file: VirtualFile, val displayName: String? = null) {
    override fun toString(): String {
        return displayName ?: file.name
    }
}

/**
 * Represents a logical grouping node (not backed by a file).
 */
data class GroupNode(val name: String) {
    override fun toString(): String = name
}

/**
 * Panel that displays AngelScript Script folders in a tree view.
 * Shows all Script folders found via .uproject and .uplugin files.
 */
class ScriptFilesPanel(private val project: Project) : JPanel(BorderLayout()) {
    private val tree: Tree
    private val rootNode = DefaultMutableTreeNode("Script Folders")
    private val treeModel = DefaultTreeModel(rootNode)
    private val scriptFolders = mutableSetOf<VirtualFile>()
    private val properties = PropertiesComponent.getInstance(project)

    companion object {
        private const val EXPANDED_PATHS_KEY = "angelscript.files.tree.expanded"
    }

    init {
        // Create tree
        tree = Tree(treeModel)
        tree.isRootVisible = false
        tree.showsRootHandles = true

        // Use custom cell renderer for file icons with VCS status
        tree.cellRenderer = ScriptFileTreeCellRenderer(project)

        // Register DataProvider for the tree to provide context for VCS actions
        DataManager.registerDataProvider(tree) { dataId ->
            when (dataId) {
                CommonDataKeys.PROJECT.name -> project
                CommonDataKeys.VIRTUAL_FILE.name -> getSelectedFile()
                CommonDataKeys.VIRTUAL_FILE_ARRAY.name -> getSelectedFile()?.let { arrayOf(it) }
                else -> null
            }
        }

        // Enable double-click to open files and right-click for context menu
        TreeUIHelper.getInstance().installTreeSpeedSearch(tree)
        tree.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2 && !e.isPopupTrigger) {
                    val path = tree.getPathForLocation(e.x, e.y) ?: return
                    val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return
                    val file = (node.userObject as? FileNode)?.file ?: return

                    if (!file.isDirectory) {
                        FileEditorManager.getInstance(project).openFile(file, true)
                    }
                }
            }

            override fun mousePressed(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    showContextMenu(e)
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    showContextMenu(e)
                }
            }
        })

        // Track tree expansion state
        tree.addTreeExpansionListener(object : TreeExpansionListener {
            override fun treeExpanded(event: TreeExpansionEvent) {
                saveExpandedState()
            }

            override fun treeCollapsed(event: TreeExpansionEvent) {
                saveExpandedState()
            }
        })

        add(JBScrollPane(tree), BorderLayout.CENTER)

        // Set up VFS listeners for automatic refresh
        setupVfsListeners()

        // Set up keyboard shortcuts
        setupKeyboardShortcuts()

        // Load Script folders on first visibility
        var hasLoaded = false
        addHierarchyListener { _ ->
            if (isShowing && !hasLoaded) {
                hasLoaded = true
                loadScriptFolders()
            }
        }
    }

    private fun setupKeyboardShortcuts() {
        val inputMap = tree.getInputMap(WHEN_FOCUSED)
        val actionMap = tree.actionMap

        // F2 for rename
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "rename")
        actionMap.put("rename", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                performRename()
            }
        })

        // Delete key
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete")
        actionMap.put("delete", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                performDelete()
            }
        })
    }

    private fun performRename() {
        val fileNode = getSelectedFileNode() ?: return
        RenameFileAction(fileNode.file).actionPerformed(
            AnActionEvent.createFromDataContext(
                "Tree",
                null,
                SimpleDataContext.builder()
                    .add(CommonDataKeys.PROJECT, project)
                    .build()
            )
        )
    }

    private fun performDelete() {
        val fileNode = getSelectedFileNode() ?: return
        DeleteFileAction(fileNode.file).actionPerformed(
            AnActionEvent.createFromDataContext(
                "Tree",
                null,
                SimpleDataContext.builder()
                    .add(CommonDataKeys.PROJECT, project)
                    .build()
            )
        )
    }

    private fun getSelectedFileNode(): FileNode? {
        val path = tree.selectionPath ?: return null
        val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return null
        return node.userObject as? FileNode
    }

    private fun getSelectedFile(): VirtualFile? {
        return getSelectedFileNode()?.file
    }

    private fun setupVfsListeners() {
        val connection = project.messageBus.connect()

        connection.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                var shouldRefresh = false

                for (event in events) {
                    val file = event.file ?: continue

                    // .uproject or .uplugin added/removed - may add/remove Script folders
                    if (file.extension == "uproject" || file.extension == "uplugin") {
                        if (event is VFileCreateEvent || event is VFileDeleteEvent) {
                            shouldRefresh = true
                            break
                        }
                    }

                    // Script folder created/deleted
                    if (file.isDirectory && file.name == "Script") {
                        if (event is VFileCreateEvent || event is VFileDeleteEvent) {
                            shouldRefresh = true
                            break
                        }
                    }

                    // Files/folders inside Script folders changed
                    if (event is VFileCreateEvent || event is VFileDeleteEvent ||
                        event is VFileMoveEvent || event is VFilePropertyChangeEvent) {
                        if (isInScriptFolder(file)) {
                            shouldRefresh = true
                            break
                        }
                    }
                }

                if (shouldRefresh) {
                    scheduleRefresh()
                }
            }
        })
    }

    private fun isInScriptFolder(file: VirtualFile): Boolean {
        var current: VirtualFile? = file
        while (current != null) {
            if (scriptFolders.contains(current)) {
                return true
            }
            current = current.parent
        }
        return false
    }

    private fun scheduleRefresh() {
        ApplicationManager.getApplication().invokeLater {
            if (!project.isDisposed) {
                loadScriptFolders()
            }
        }
    }

    /**
     * Reload Script folders from the project.
     */
    fun refresh() {
        loadScriptFolders()
    }

    private fun loadScriptFolders() {
        rootNode.removeAllChildren()
        scriptFolders.clear()

        val basePath = project.basePath
        if (basePath == null) {
            rootNode.add(DefaultMutableTreeNode("No project base path"))
            treeModel.reload()
            return
        }

        val baseDir = VirtualFileManager.getInstance().findFileByUrl("file://$basePath")
        if (baseDir == null) {
            rootNode.add(DefaultMutableTreeNode("Cannot find project directory"))
            treeModel.reload()
            return
        }

        // Categorize Script folders by location
        data class ScriptInfo(
            val scriptFolder: VirtualFile,
            val projectFile: VirtualFile,  // .uproject or .uplugin
            val projectName: String,
            val isEnginePlugin: Boolean,
            val isProjectPlugin: Boolean
        )

        // PASS 1: Find all .uproject and .uplugin files with Script folders
        data class ProjectFileInfo(
            val file: VirtualFile,
            val scriptFolder: VirtualFile
        )

        val allProjectFiles = mutableListOf<ProjectFileInfo>()

        VfsUtil.visitChildrenRecursively(baseDir, object : VirtualFileVisitor<Unit>() {
            override fun visitFile(file: VirtualFile): Boolean {
                if (!file.isDirectory && (file.extension == "uproject" || file.extension == "uplugin")) {
                    val parentDir = file.parent
                    if (parentDir != null) {
                        val scriptFolder = parentDir.findChild("Script")
                        if (scriptFolder != null && scriptFolder.isDirectory && containsAngelScriptFiles(scriptFolder)) {
                            allProjectFiles.add(ProjectFileInfo(file, scriptFolder))
                            scriptFolders.add(scriptFolder)
                        }
                    }
                }
                return true
            }
        })

        // Find the .uproject file(s)
        val uprojectFiles = allProjectFiles.filter { it.file.extension == "uproject" }
        val uprojectFile = uprojectFiles.firstOrNull()?.file

        // PASS 2: Categorize each project file
        val scriptInfos = allProjectFiles.map { info ->
            val file = info.file
            val scriptFolder = info.scriptFolder
            val path = file.path

            val isEnginePlugin = path.contains("/Engine/Plugins/") || path.contains("\\Engine\\Plugins\\")

            // A plugin is a "project plugin" if:
            // 1. It's a .uplugin file
            // 2. It's in a Plugins folder relative to the .uproject
            // 3. It's not an engine plugin
            val isProjectPlugin = if (file.extension == "uplugin" && uprojectFile != null) {
                val pluginPath = file.path
                val projectPath = uprojectFile.parent.path
                pluginPath.startsWith(projectPath) &&
                (pluginPath.contains("/Plugins/") || pluginPath.contains("\\Plugins\\")) &&
                !isEnginePlugin
            } else {
                false
            }

            val projectName = file.parent?.name ?: file.nameWithoutExtension

            ScriptInfo(scriptFolder, file, projectName, isEnginePlugin, isProjectPlugin)
        }

        if (scriptInfos.isEmpty()) {
            rootNode.add(DefaultMutableTreeNode("No Script folders found"))
            treeModel.reload()
            return
        }

        // Group by top-level category
        val enginePlugins = scriptInfos.filter { it.isEnginePlugin }
        val projectPlugins = scriptInfos.filter { it.isProjectPlugin }
        val projectScripts = scriptInfos.filter { !it.isEnginePlugin && !it.isProjectPlugin }

        // Build tree structure
        // Engine node
        if (enginePlugins.isNotEmpty()) {
            val engineNode = DefaultMutableTreeNode(GroupNode("Engine"))
            rootNode.add(engineNode)

            for (info in enginePlugins.sortedBy { it.projectName }) {
                val pluginNode = DefaultMutableTreeNode(FileNode(info.scriptFolder, "${info.projectName} Scripts"))
                engineNode.add(pluginNode)
                addChildren(pluginNode, info.scriptFolder)
            }
        }

        // Group project plugins and scripts by the .uproject file they belong to
        // For project scripts, projectFile IS the .uproject
        // For project plugins, we need to find which .uproject they belong to
        val projectGroups = mutableMapOf<VirtualFile, MutableList<ScriptInfo>>()

        // Add project scripts (their projectFile is the .uproject)
        for (script in projectScripts) {
            projectGroups.getOrPut(script.projectFile) { mutableListOf() }.add(script)
        }

        // Add project plugins under their parent .uproject
        if (uprojectFile != null) {
            for (plugin in projectPlugins) {
                projectGroups.getOrPut(uprojectFile!!) { mutableListOf() }.add(plugin)
            }
        }

        // Build project nodes
        for ((projectFile, infos) in projectGroups.toSortedMap(compareBy { it.name })) {
            val projectNode = DefaultMutableTreeNode(GroupNode(projectFile.nameWithoutExtension))
            rootNode.add(projectNode)

            val projectScript = infos.firstOrNull { !it.isProjectPlugin }
            val plugins = infos.filter { it.isProjectPlugin }

            // Add Script folder if exists
            if (projectScript != null) {
                val scriptNode = DefaultMutableTreeNode(FileNode(projectScript.scriptFolder, "Script"))
                projectNode.add(scriptNode)
                addChildren(scriptNode, projectScript.scriptFolder)
            }

            // Add Plugins group if there are plugins
            if (plugins.isNotEmpty()) {
                val pluginsGroupNode = DefaultMutableTreeNode(GroupNode("Plugins"))
                projectNode.add(pluginsGroupNode)

                for (pluginInfo in plugins.sortedBy { it.projectName }) {
                    val pluginNode = DefaultMutableTreeNode(FileNode(pluginInfo.scriptFolder, "${pluginInfo.projectName} Scripts"))
                    pluginsGroupNode.add(pluginNode)
                    addChildren(pluginNode, pluginInfo.scriptFolder)
                }
            }
        }

        treeModel.reload()

        // Restore expanded state
        restoreExpandedState()
    }

    private fun addChildren(parentNode: DefaultMutableTreeNode, directory: VirtualFile) {
        val children = directory.children ?: return

        // Sort: directories first, then files, both alphabetically
        val sorted = children.sortedWith(compareBy({ !it.isDirectory }, { it.name }))

        for (child in sorted) {
            // Skip non-.as files
            if (!child.isDirectory && !isAngelScriptFile(child)) {
                continue
            }

            // Skip folders that don't contain .as files
            if (child.isDirectory && !containsAngelScriptFiles(child)) {
                continue
            }

            val childNode = DefaultMutableTreeNode(FileNode(child))
            parentNode.add(childNode)

            if (child.isDirectory) {
                addChildren(childNode, child)
            }
        }
    }

    private fun isAngelScriptFile(file: VirtualFile): Boolean {
        return file.extension == "as" || file.extension == "ash"
    }

    private fun containsAngelScriptFiles(directory: VirtualFile): Boolean {
        var hasAngelScript = false

        VfsUtil.visitChildrenRecursively(directory, object : VirtualFileVisitor<Unit>() {
            override fun visitFile(file: VirtualFile): Boolean {
                if (!file.isDirectory && isAngelScriptFile(file)) {
                    hasAngelScript = true
                    return false // Stop searching
                }
                return true // Continue
            }
        })

        return hasAngelScript
    }

    private fun showContextMenu(e: MouseEvent) {
        val path = tree.getPathForLocation(e.x, e.y) ?: return
        tree.selectionPath = path

        val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return

        // Only show context menu for FileNode (not GroupNode)
        val fileNode = node.userObject as? FileNode ?: return
        val file = fileNode.file

        val actionGroup = if (file.isDirectory) {
            createFolderActions(file)
        } else {
            createFileActions(file)
        }

        val popupMenu = ActionManager.getInstance()
            .createActionPopupMenu("AngelScriptFilesTree", actionGroup)

        // Target component is the tree, which provides DataContext via registered DataProvider
        popupMenu.setTargetComponent(tree)

        popupMenu.component.show(e.component, e.x, e.y)
    }

    private fun createFolderActions(folder: VirtualFile): DefaultActionGroup {
        return DefaultActionGroup().apply {
            // Custom actions
            add(NewFileAction(folder))
            add(NewFolderAction(folder))

            addSeparator()

            // Edit submenu
            add(createEditSubmenu(folder))

            addSeparator()

            // Platform actions (VCS, Local History)
            addPlatformActions(this)
        }
    }

    private fun createFileActions(file: VirtualFile): DefaultActionGroup {
        return DefaultActionGroup().apply {
            // Show In submenu
            add(createShowInGroup(file))

            addSeparator()

            // Edit submenu
            add(createEditSubmenu(file))

            addSeparator()

            // Platform actions (VCS, Local History)
            addPlatformActions(this)
        }
    }

    private fun createEditSubmenu(file: VirtualFile): ActionGroup {
        return DefaultActionGroup("Edit", true).apply {
            val actionManager = ActionManager.getInstance()

            // Standard clipboard operations
            actionManager.getAction("\$Cut")?.let { add(it) }
            actionManager.getAction("\$Copy")?.let { add(it) }
            actionManager.getAction("\$Paste")?.let { add(it) }

            addSeparator()

            // File operations
            add(RenameFileAction(file))
            add(DeleteFileAction(file))
        }
    }

    private fun addPlatformActions(group: DefaultActionGroup) {
        val actionManager = ActionManager.getInstance()

        // VCS operations (if file is under version control)
        actionManager.getAction("VcsGroup")?.let {
            if (it is ActionGroup) {
                group.add(it)
                group.addSeparator()
            }
        }

        // Local History
        actionManager.getAction("LocalHistory")?.let {
            if (it is ActionGroup) {
                group.add(it)
            }
        }
    }

    private fun createShowInGroup(file: VirtualFile): DefaultActionGroup {
        return DefaultActionGroup("Show In", true).apply {
            add(ShowInExplorerAction(file))
            add(ShowInFileSystemAction(file))
        }
    }

    fun selectOpenedFile() {
        val selectedFiles = FileEditorManager.getInstance(project).selectedFiles
        if (selectedFiles.isEmpty()) return

        val fileToSelect = selectedFiles[0]

        // Find node for this file
        val nodePath = findNodeForFile(rootNode, fileToSelect) ?: return

        // Expand parents and select
        tree.selectionPath = nodePath
        tree.scrollPathToVisible(nodePath)
    }

    fun canSelectOpenedFile(): Boolean {
        val selectedFiles = FileEditorManager.getInstance(project).selectedFiles
        return selectedFiles.isNotEmpty()
    }

    private fun findNodeForFile(node: DefaultMutableTreeNode, file: VirtualFile): TreePath? {
        val userObject = node.userObject
        if (userObject is FileNode && userObject.file == file) {
            return TreePath(node.path)
        }

        for (i in 0 until node.childCount) {
            val child = node.getChildAt(i) as DefaultMutableTreeNode
            val result = findNodeForFile(child, file)
            if (result != null) return result
        }

        return null
    }

    private fun saveExpandedState() {
        val expandedPaths = mutableListOf<String>()

        for (i in 0 until tree.rowCount) {
            if (tree.isExpanded(i)) {
                val path = tree.getPathForRow(i)
                val node = path?.lastPathComponent as? DefaultMutableTreeNode
                val fileNode = node?.userObject as? FileNode
                if (fileNode != null) {
                    expandedPaths.add(fileNode.file.path)
                }
            }
        }

        // Store as comma-separated paths
        properties.setValue(EXPANDED_PATHS_KEY, expandedPaths.joinToString("|"))
    }

    private fun restoreExpandedState() {
        val expandedPathsStr = properties.getValue(EXPANDED_PATHS_KEY) ?: return
        if (expandedPathsStr.isEmpty()) {
            // No saved state - expand root level by default
            for (i in 0 until rootNode.childCount) {
                tree.expandRow(i)
            }
            return
        }

        val expandedPaths = expandedPathsStr.split("|").toSet()

        // Expand paths that match saved state
        expandedPaths.forEach { filePath ->
            val nodePath = findNodeByFilePath(rootNode, filePath)
            if (nodePath != null) {
                tree.expandPath(nodePath)
            }
        }
    }

    private fun findNodeByFilePath(node: DefaultMutableTreeNode, filePath: String): TreePath? {
        val userObject = node.userObject
        if (userObject is FileNode && userObject.file.path == filePath) {
            return TreePath(node.path)
        }

        for (i in 0 until node.childCount) {
            val child = node.getChildAt(i) as DefaultMutableTreeNode
            val result = findNodeByFilePath(child, filePath)
            if (result != null) return result
        }

        return null
    }
}
