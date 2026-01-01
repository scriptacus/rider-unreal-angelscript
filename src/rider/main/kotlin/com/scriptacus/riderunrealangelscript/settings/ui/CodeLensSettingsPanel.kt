package com.scriptacus.riderunrealangelscript.settings.ui

import com.intellij.ui.dsl.builder.panel
import com.scriptacus.riderunrealangelscript.settings.AngelScriptLspSettings
import javax.swing.JPanel

class CodeLensSettingsPanel {
    private val blueprintClasses = mutableListOf<String>()

    fun createPanel(): JPanel = panel {
        row {
            label("Show 'Create Blueprint' code lens for classes inheriting from:")
                .comment("Classes that inherit from these will show a code lens to create a Blueprint asset")
        }
        // TODO: Add list editor for blueprint classes
        // Default: AActor, UUserWidget
        // Use ToolbarDecorator with JBList to allow adding/removing class names
    }

    fun reset(state: AngelScriptLspSettings.State) {
        blueprintClasses.clear()
        blueprintClasses.addAll(state.showCreateBlueprintClasses)
    }

    fun apply(state: AngelScriptLspSettings.State) {
        state.showCreateBlueprintClasses.clear()
        state.showCreateBlueprintClasses.addAll(blueprintClasses)
    }

    fun isModified(state: AngelScriptLspSettings.State): Boolean {
        return state.showCreateBlueprintClasses != blueprintClasses
    }
}
