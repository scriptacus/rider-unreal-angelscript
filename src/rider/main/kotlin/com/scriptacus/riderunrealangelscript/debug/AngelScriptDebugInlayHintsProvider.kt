package com.scriptacus.riderunrealangelscript.debug

import com.intellij.codeInsight.hints.declarative.HintFormat
import com.intellij.codeInsight.hints.declarative.InlayPayload
import com.intellij.codeInsight.hints.declarative.InlineInlayPosition
import com.intellij.codeInsight.hints.declarative.InlayTreeSink
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.redhat.devtools.lsp4ij.features.AbstractLSPDeclarativeInlayHintsProvider

/**
 * Inlay hints provider that displays debug variable values inline during debugging.
 *
 * Shows variable values at the end of lines where variables are in scope,
 * similar to VSCode's inline values feature.
 *
 * Example:
 * ```
 * FVector Position = FVector(1.0, 2.0, 3.0);  // Position = FVector(1.0, 2.0, 3.0)
 * int Health = 100;                            // Health = 100
 * ```
 */
class AngelScriptDebugInlayHintsProvider : AbstractLSPDeclarativeInlayHintsProvider() {

    companion object {
        private val LOG = Logger.getInstance(AngelScriptDebugInlayHintsProvider::class.java)
        const val PROVIDER_ID = "AngelScriptDebugInlayHints"
    }

    override fun doCollect(
        psiFile: PsiFile,
        editor: Editor,
        inlayHintsSink: InlayTreeSink
    ) {
        val file = psiFile.virtualFile ?: return

        // Get cached variables from the debug service
        val service = AngelScriptDebugInlayService.getInstance(psiFile.project)
        val variables = service.getVariablesForFile(file) ?: return

        if (variables.isEmpty()) {
            return
        }

        LOG.debug("Collecting inline debug hints for ${file.name}: ${variables.size} lines with variables")

        // Group and render variables by line
        variables.forEach { (lineNumber, variableInfos) ->
            if (variableInfos.isEmpty()) return@forEach

            // For each line, create a single hint with all variables
            // Use the offset of the first variable (end of line)
            val offset = variableInfos.first().offset

            val position = InlineInlayPosition(offset, true, 0)

            // Build the hint text
            val hintText = buildHintText(variableInfos)

            inlayHintsSink.addPresentation(
                position = position,
                hintFormat = HintFormat.default
                ) {
                text(" // $hintText");
            }
//            inlayHintsSink.addPresentation(
//                position,
//                null,  // hintFormat (unused)
//                null,  // tooltip
//                true   // hasBackground
//            ) {
//                text(" // $hintText");
//            }

            LOG.debug("Added inline hint at line $lineNumber, offset $offset: $hintText")
        }
    }

    /**
     * Builds the hint text from a list of variables on the same line
     * Example: "Position = FVector(...), Health = 100"
     */
    private fun buildHintText(variables: List<AngelScriptDebugInlayService.VariableInfo>): String {
        return variables.joinToString(", ") { variable ->
            "${variable.name} = ${variable.value}"
        }
    }
}
