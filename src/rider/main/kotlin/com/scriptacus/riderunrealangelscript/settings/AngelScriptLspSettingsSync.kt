package com.scriptacus.riderunrealangelscript.settings

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerManager
import org.eclipse.lsp4j.DidChangeConfigurationParams

class AngelScriptLspSettingsSync(private val project: Project) {

    companion object {
        private val LOG = Logger.getInstance(AngelScriptLspSettingsSync::class.java)
    }

    fun syncSettingsToLsp() {
        val settings = AngelScriptLspSettings.getInstance().state
        val config = buildConfiguration(settings)

        // Send workspace/didChangeConfiguration to AngelScript language server
        val manager = LanguageServerManager.getInstance(project)
        val serverFuture = manager.getLanguageServer("angelscript-lsp")

        serverFuture.thenAccept { serverItem ->
            if (serverItem == null) {
                LOG.warn("AngelScript LSP server not available - cannot sync settings")
                return@thenAccept
            }

            try {
                val params = DidChangeConfigurationParams(config)
                serverItem.server.workspaceService.didChangeConfiguration(params)
            } catch (e: Exception) {
                LOG.error("Failed to sync settings to LSP server", e)
            }
        }
    }

    private fun buildConfiguration(state: AngelScriptLspSettings.State): Map<String, Any> {
        // VSCode extension expects settings under "UnrealAngelscript" key
        // The LSP server reads specific properties, so we must provide ALL of them
        // to avoid "Cannot read properties of undefined" errors
        return mapOf(
            "UnrealAngelscript" to mapOf(
                // General
                "unrealConnectionPort" to state.unrealConnectionPort,
                "scriptIgnorePatterns" to state.scriptIgnorePatterns,

                // Top-level completion settings
                "insertParenthesisOnFunctionCompletion" to false,
                "mathCompletionShortcuts" to state.mathCompletionShortcuts,

                // Nested completion settings
                "completion" to mapOf(
                    "dependencyRestrictions" to emptyList<Any>()
                ),

                // Diagnostics
                "diagnosticsForUnrealNamingConvention" to state.diagnosticsForUnrealNamingConvention,
                "markUnreadVariablesAsUnused" to false,
                "correctFloatLiteralsWhenExpectingDoublePrecision" to state.correctFloatLiteralsWhenExpectingDoublePrecision,

                // Inlay Hints - LSP server requires these even though Rider handles inlay hints natively
                // We provide defaults matching VSCode extension defaults
                "inlayHints" to mapOf(
                    "inlayHintsEnabled" to true,
                    "parameterHintsForConstants" to true,
                    "parameterHintsForComplexExpressions" to true,
                    "parameterReferenceHints" to true,
                    "parameterHintsForSingleParameterFunctions" to false,
                    "typeHintsForAutos" to true,
                    "typeHintsForAutoIgnoredTypes" to emptyList<String>(),
                    "parameterHintsIgnoredParameterNames" to listOf(
                        "Object", "Actor", "FunctionName", "Value", "InValue",
                        "NewValue", "Condition", "Parameters", "Params"
                    ),
                    "parameterHintsIgnoredFunctionNames" to emptyList<String>()
                ),

                // Inline Values - for debug adapter
                "inlineValues" to mapOf(
                    "showInlineValueForFunctionThisObject" to true,
                    "showInlineValueForLocalVariables" to true,
                    "showInlineValueForParameters" to true,
                    "showInlineValueForMemberAssignment" to true
                ),

                // Code Lenses
                "codeLenses" to mapOf(
                    "showCreateBlueprintClasses" to state.showCreateBlueprintClasses
                ),

                // Advanced
                "projectCodeGeneration" to mapOf(
                    "enable" to state.projectCodeGenerationEnable,
                    "generators" to emptyList<Any>()
                )
            )
        )
    }
}
