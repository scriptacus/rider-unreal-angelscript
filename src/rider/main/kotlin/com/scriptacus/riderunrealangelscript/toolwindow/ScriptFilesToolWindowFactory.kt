package com.scriptacus.riderunrealangelscript.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Factory for creating the AngelScript Script Files tool window.
 */
class ScriptFilesToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = ScriptFilesPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)

        // Add actions to tool window title bar
        val actionGroup = DefaultActionGroup()

        // Select Opened File action
        actionGroup.add(object : AnAction(
            "Select Opened File",
            "Scroll to currently opened file",
            AllIcons.General.Locate
        ) {
            override fun actionPerformed(e: AnActionEvent) {
                panel.selectOpenedFile()
            }

            override fun update(e: AnActionEvent) {
                e.presentation.isEnabled = panel.canSelectOpenedFile()
            }

            override fun getActionUpdateThread(): ActionUpdateThread {
                return ActionUpdateThread.BGT
            }
        })

        // Set the action group as the tool window's title actions
        toolWindow.setTitleActions(listOf(actionGroup))
    }
}
