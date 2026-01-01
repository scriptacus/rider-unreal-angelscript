package com.scriptacus.riderunrealangelscript.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Factory for creating the AngelScript API Browser tool window.
 */
class ApiToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val apiPanel = AngelScriptApiPanel(project)

        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(apiPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
