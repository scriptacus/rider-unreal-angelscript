package com.scriptacus.riderunrealangelscript.toolwindow.actions

import com.intellij.icons.AllIcons
import com.intellij.ide.actions.RevealFileAction
import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.projectView.impl.nodes.ProjectViewProjectNode
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import java.io.IOException

/**
 * Action to create a new AngelScript file in a folder.
 */
class NewFileAction(private val parentFolder: VirtualFile) : AnAction("New File...") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val fileName = Messages.showInputDialog(
            project,
            "Enter file name:",
            "New AngelScript File",
            AllIcons.FileTypes.Text,
            "NewFile.as",
            object : InputValidator {
                override fun checkInput(inputString: String): Boolean {
                    return inputString.isNotBlank() &&
                           (inputString.endsWith(".as") || inputString.endsWith(".ash"))
                }

                override fun canClose(inputString: String): Boolean {
                    return checkInput(inputString)
                }
            }
        ) ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            try {
                val newFile = parentFolder.createChildData(this, fileName)
                FileEditorManager.getInstance(project).openFile(newFile, true)
            } catch (ex: IOException) {
                Messages.showErrorDialog(project, "Failed to create file: ${ex.message}", "Error")
            }
        }
    }
}

/**
 * Action to create a new folder.
 */
class NewFolderAction(private val parentFolder: VirtualFile) : AnAction("New Folder...") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val folderName = Messages.showInputDialog(
            project,
            "Enter folder name:",
            "New Folder",
            AllIcons.Nodes.Folder
        ) ?: return

        if (folderName.isBlank()) {
            Messages.showErrorDialog(project, "Folder name cannot be empty", "Error")
            return
        }

        WriteCommandAction.runWriteCommandAction(project) {
            try {
                parentFolder.createChildDirectory(this, folderName)
            } catch (ex: IOException) {
                Messages.showErrorDialog(project, "Failed to create folder: ${ex.message}", "Error")
            }
        }
    }
}

/**
 * Action to rename a file or folder.
 */
class RenameFileAction(private val file: VirtualFile) : AnAction("Rename...") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val newName = Messages.showInputDialog(
            project,
            "Enter new name:",
            "Rename",
            null,
            file.name,
            null
        ) ?: return

        if (newName.isBlank()) {
            Messages.showErrorDialog(project, "Name cannot be empty", "Error")
            return
        }

        WriteCommandAction.runWriteCommandAction(project) {
            try {
                file.rename(this, newName)
            } catch (ex: IOException) {
                Messages.showErrorDialog(project, "Failed to rename: ${ex.message}", "Error")
            }
        }
    }
}

/**
 * Action to delete a file or folder.
 */
class DeleteFileAction(private val file: VirtualFile) : AnAction("Delete") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val result = Messages.showYesNoDialog(
            project,
            "Delete ${file.name}?",
            "Confirm Delete",
            Messages.getQuestionIcon()
        )

        if (result == Messages.YES) {
            WriteCommandAction.runWriteCommandAction(project) {
                try {
                    file.delete(this)
                } catch (ex: IOException) {
                    Messages.showErrorDialog(project, "Failed to delete: ${ex.message}", "Error")
                }
            }
        }
    }
}

/**
 * Action to show file in OS Explorer/Finder.
 */
class ShowInExplorerAction(private val file: VirtualFile) : AnAction("Explorer") {
    override fun actionPerformed(e: AnActionEvent) {
        RevealFileAction.openFile(file.toNioPath())
    }
}

/**
 * Action to show file in Rider's File System view.
 */
class ShowInFileSystemAction(private val file: VirtualFile) : AnAction("File System") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // Activate Project View and select the file
        val projectView = ProjectView.getInstance(project)
        projectView.select(null, file, false)
    }
}
