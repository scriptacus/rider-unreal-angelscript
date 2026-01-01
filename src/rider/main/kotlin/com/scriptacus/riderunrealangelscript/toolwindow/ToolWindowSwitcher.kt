package com.scriptacus.riderunrealangelscript.toolwindow

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Automatically switches between AngelScript Files and Solution Explorer
 * based on the currently opened file type.
 */
class ToolWindowSwitcher : ProjectActivity {
    companion object {
        private const val AUTO_SWITCH_KEY = "angelscript.toolwindow.autoswitch"
        private const val ANGELSCRIPT_FILES_WINDOW_ID = "AngelScript Files"
        private const val PROJECT_WINDOW_ID = "Project"

        fun isAutoSwitchEnabled(project: Project): Boolean {
            return PropertiesComponent.getInstance(project).getBoolean(AUTO_SWITCH_KEY, false)
        }

        fun setAutoSwitchEnabled(project: Project, enabled: Boolean) {
            PropertiesComponent.getInstance(project).setValue(AUTO_SWITCH_KEY, enabled)
        }
    }

    override suspend fun execute(project: Project) {
        val connection = project.messageBus.connect()

        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
                if (!isAutoSwitchEnabled(project)) {
                    return
                }

                val file = event.newFile ?: return
                val isAngelScriptFile = file.extension == "as" || file.extension == "ash"

                ApplicationManager.getApplication().invokeLater {
                    if (project.isDisposed) return@invokeLater

                    val toolWindowManager = ToolWindowManager.getInstance(project)
                    val angelScriptWindow = toolWindowManager.getToolWindow(ANGELSCRIPT_FILES_WINDOW_ID)
                    val projectWindow = toolWindowManager.getToolWindow(PROJECT_WINDOW_ID)

                    if (isAngelScriptFile) {
                        // Switch to AngelScript Files window
                        angelScriptWindow?.let {
                            if (!it.isVisible) {
                                it.show(null)
                            }
                            // Always activate to bring it to front, even if already visible
                            it.activate(null, true)
                        }
                    } else {
                        // Switch to Solution Explorer window
                        projectWindow?.let {
                            if (!it.isVisible) {
                                it.show(null)
                            }
                            // Always activate to bring it to front, even if already visible
                            it.activate(null, true)
                        }
                    }
                }
            }
        })
    }
}