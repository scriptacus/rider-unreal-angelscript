package com.scriptacus.riderunrealangelscript.settings

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.panel
import com.scriptacus.riderunrealangelscript.settings.ui.*
import javax.swing.JComponent

class AngelScriptSettingsConfigurable : SearchableConfigurable {

    private val settings = AngelScriptLspSettings.getInstance()
    private var mainPanel: DialogPanel? = null

    private lateinit var generalPanel: GeneralSettingsPanel
    private lateinit var completionPanel: CompletionSettingsPanel
    private lateinit var diagnosticsPanel: DiagnosticsSettingsPanel
    private lateinit var codeLensPanel: CodeLensSettingsPanel
    private lateinit var navigationPanel: NavigationSettingsPanel

    override fun getId() = "angelscript.settings"

    override fun getDisplayName() = "AngelScript"

    override fun createComponent(): JComponent {
        val state = settings.state
        generalPanel = GeneralSettingsPanel()
        completionPanel = CompletionSettingsPanel()
        diagnosticsPanel = DiagnosticsSettingsPanel()
        codeLensPanel = CodeLensSettingsPanel()
        navigationPanel = NavigationSettingsPanel(state)

        mainPanel = panel {
            group("General") {
                row {
                    cell(generalPanel.createPanel())
                }
            }
            group("Completion") {
                row {
                    cell(completionPanel.createPanel())
                }
            }
            group("Diagnostics") {
                row {
                    cell(diagnosticsPanel.createPanel())
                }
            }
//            group("Code Lenses") {
//                row {
//                    cell(codeLensPanel.createPanel())
//                }
//            }
            group("Navigation") {
                row {
                    cell(navigationPanel.createPanel())
                }
            }
        }

        reset()
        return mainPanel!!
    }

    override fun isModified(): Boolean {
        val state = settings.state
        return generalPanel.isModified(state) ||
                completionPanel.isModified(state) ||
                diagnosticsPanel.isModified(state) ||
                codeLensPanel.isModified(state) ||
                navigationPanel.isModified(state)
    }

    override fun apply() {
        val state = settings.state
        generalPanel.apply(state)
        completionPanel.apply(state)
        diagnosticsPanel.apply(state)
        codeLensPanel.apply(state)
        navigationPanel.apply(state)

        // Notify settings changed
        settings.notifySettingsChanged()

        // Sync settings to all open projects' LSP servers
        ProjectManager.getInstance().openProjects.forEach { project ->
            AngelScriptLspSettingsSync(project).syncSettingsToLsp()
        }
    }

    override fun reset() {
        val state = settings.state
        generalPanel.reset(state)
        completionPanel.reset(state)
        diagnosticsPanel.reset(state)
        codeLensPanel.reset(state)
        navigationPanel.reset(state)
    }

    override fun disposeUIResources() {
        mainPanel = null
    }
}
