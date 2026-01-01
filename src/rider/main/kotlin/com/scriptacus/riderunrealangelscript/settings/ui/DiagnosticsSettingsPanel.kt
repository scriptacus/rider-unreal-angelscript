package com.scriptacus.riderunrealangelscript.settings.ui

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.panel
import com.scriptacus.riderunrealangelscript.settings.AngelScriptLspSettings
import javax.swing.JPanel

class DiagnosticsSettingsPanel {
    private val unrealNamingCheckbox = JBCheckBox("Report Unreal naming convention violations")
    private val correctFloatsCheckbox = JBCheckBox("Correct float literals when expecting double precision")

    fun createPanel(): JPanel = panel {
        row {
            cell(unrealNamingCheckbox)
                .comment("Show diagnostics for classes/functions that don't follow Unreal naming conventions (e.g., AMyActor, UMyObject)")
        }
        row {
            cell(correctFloatsCheckbox)
                .comment("Suggest adding 'f' suffix to float literals or removing it for doubles")
        }
    }

    fun reset(state: AngelScriptLspSettings.State) {
        unrealNamingCheckbox.isSelected = state.diagnosticsForUnrealNamingConvention
        correctFloatsCheckbox.isSelected = state.correctFloatLiteralsWhenExpectingDoublePrecision
    }

    fun apply(state: AngelScriptLspSettings.State) {
        state.diagnosticsForUnrealNamingConvention = unrealNamingCheckbox.isSelected
        state.correctFloatLiteralsWhenExpectingDoublePrecision = correctFloatsCheckbox.isSelected
    }

    fun isModified(state: AngelScriptLspSettings.State): Boolean {
        return state.diagnosticsForUnrealNamingConvention != unrealNamingCheckbox.isSelected ||
                state.correctFloatLiteralsWhenExpectingDoublePrecision != correctFloatsCheckbox.isSelected
    }
}
