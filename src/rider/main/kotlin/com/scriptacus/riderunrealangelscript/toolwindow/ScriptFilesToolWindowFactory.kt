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
        val titleActionGroup = DefaultActionGroup()

        // Select Opened File action (title bar button)
        titleActionGroup.add(object : AnAction(
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
        toolWindow.setTitleActions(listOf(titleActionGroup))

        // Add gear menu actions (appears under 3 dots)
        toolWindow.setAdditionalGearActions(DefaultActionGroup().apply {
            // Behavior submenu
            add(DefaultActionGroup("Behavior", true).apply {
                // Always Select Opened File toggle action
                add(object : com.intellij.openapi.actionSystem.ToggleAction(
                    "Always Select Opened File",
                    "Automatically scroll to the file when you open it in the editor",
                    null
                ) {
                    override fun isSelected(e: AnActionEvent): Boolean {
                        return panel.isAutoscrollFromSource()
                    }

                    override fun setSelected(e: AnActionEvent, state: Boolean) {
                        panel.setAutoscrollFromSource(state)
                        if (state) {
                            // Immediately select the currently opened file when enabled
                            panel.selectOpenedFile()
                        }
                    }

                    override fun getActionUpdateThread(): ActionUpdateThread {
                        return ActionUpdateThread.BGT
                    }
                })

                // Auto-Switch Tool Windows toggle action
                add(object : com.intellij.openapi.actionSystem.ToggleAction(
                    "Auto-Switch Tool Windows",
                    "Automatically switch between AngelScript Files and Solution Explorer based on file type",
                    null
                ) {
                    override fun isSelected(e: AnActionEvent): Boolean {
                        return ToolWindowSwitcher.isAutoSwitchEnabled(project)
                    }

                    override fun setSelected(e: AnActionEvent, state: Boolean) {
                        ToolWindowSwitcher.setAutoSwitchEnabled(project, state)
                    }

                    override fun getActionUpdateThread(): ActionUpdateThread {
                        return ActionUpdateThread.BGT
                    }
                })
            })
        })
    }
}
