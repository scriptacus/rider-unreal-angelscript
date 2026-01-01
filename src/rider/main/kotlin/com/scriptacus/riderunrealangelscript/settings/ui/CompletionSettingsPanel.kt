package com.scriptacus.riderunrealangelscript.settings.ui

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.panel
import com.scriptacus.riderunrealangelscript.settings.AngelScriptLspSettings
import javax.swing.JPanel

class CompletionSettingsPanel {
    private val mathShortcutsCheckbox = JBCheckBox("Enable math completion shortcuts")

    fun createPanel(): JPanel = panel {
        row {
            cell(mathShortcutsCheckbox)
                .comment("Enable shortcuts for common math operations")
        }
    }

    fun reset(state: AngelScriptLspSettings.State) {
        mathShortcutsCheckbox.isSelected = state.mathCompletionShortcuts
    }

    fun apply(state: AngelScriptLspSettings.State) {
        state.mathCompletionShortcuts = mathShortcutsCheckbox.isSelected
    }

    fun isModified(state: AngelScriptLspSettings.State): Boolean {
        return state.mathCompletionShortcuts != mathShortcutsCheckbox.isSelected
    }
}
