package com.scriptacus.riderunrealangelscript.lang

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.redhat.devtools.lsp4ij.features.semanticTokens.DefaultSemanticTokensColorsProvider

/**
 * Provides color mappings for LSP semantic tokens from the AngelScript language server.
 *
 * The VS Code language server provides semantic token types prefixed with "as_" that represent
 * different semantic elements in AngelScript code (functions, variables, types, etc.).
 * This class maps those tokens to IntelliJ's standard color scheme.
 */
class AngelScriptSemanticTokensColorsProvider : DefaultSemanticTokensColorsProvider() {

    override fun getTextAttributesKey(tokenType: String, tokenModifiers: List<String>, file: com.intellij.psi.PsiFile): TextAttributesKey? {
        return when (tokenType) {
            // Namespaces and modules
            "as_namespace" -> DefaultLanguageHighlighterColors.CLASS_NAME

            // Type names with semantic context
            "as_typename" -> DefaultLanguageHighlighterColors.CLASS_NAME
            "as_typename_actor" -> DefaultLanguageHighlighterColors.CLASS_NAME
            "as_typename_component" -> DefaultLanguageHighlighterColors.CLASS_NAME
            "as_typename_struct" -> DefaultLanguageHighlighterColors.CLASS_NAME
            "as_typename_event" -> DefaultLanguageHighlighterColors.INTERFACE_NAME
            "as_typename_delegate" -> DefaultLanguageHighlighterColors.INTERFACE_NAME
            "as_typename_primitive" -> DefaultLanguageHighlighterColors.KEYWORD

            // Template types
            "as_template_base_type" -> DefaultLanguageHighlighterColors.CLASS_NAME

            // Functions
            "as_member_function" -> DefaultLanguageHighlighterColors.INSTANCE_METHOD
            "as_global_function" -> DefaultLanguageHighlighterColors.STATIC_METHOD

            // Variables
            "as_parameter" -> DefaultLanguageHighlighterColors.PARAMETER
            "as_local_variable" -> DefaultLanguageHighlighterColors.LOCAL_VARIABLE
            "as_member_variable" -> DefaultLanguageHighlighterColors.INSTANCE_FIELD
            "as_global_variable" -> DefaultLanguageHighlighterColors.STATIC_FIELD

            // Accessors (properties)
            "as_member_accessor" -> DefaultLanguageHighlighterColors.INSTANCE_FIELD
            "as_global_accessor" -> DefaultLanguageHighlighterColors.STATIC_FIELD

            // Access specifiers (public, private, protected, etc.)
            "as_access_specifier" -> DefaultLanguageHighlighterColors.KEYWORD

            // Error and warning states
            "as_unimported_symbol" -> DefaultLanguageHighlighterColors.IDENTIFIER
            "as_unknown_error" -> DefaultLanguageHighlighterColors.IDENTIFIER

            // Fallback to default behavior for unknown token types
            else -> super.getTextAttributesKey(tokenType, tokenModifiers, file)
        }
    }
}
