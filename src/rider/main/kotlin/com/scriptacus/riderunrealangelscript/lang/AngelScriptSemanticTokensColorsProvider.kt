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
            "as_namespace" -> AngelScriptSyntaxHighlighter.SEMANTIC_NAMESPACE

            // Type names with semantic context
            "as_typename" -> AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME
            "as_typename_actor" -> AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_ACTOR
            "as_typename_component" -> AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_COMPONENT
            "as_typename_struct" -> AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_STRUCT
            "as_typename_event" -> AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_EVENT
            "as_typename_delegate" -> AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_DELEGATE
            "as_typename_primitive" -> AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_PRIMITIVE

            // Template types
            "as_template_base_type" -> AngelScriptSyntaxHighlighter.SEMANTIC_TEMPLATE_BASE_TYPE

            // Functions
            "as_member_function" -> AngelScriptSyntaxHighlighter.SEMANTIC_MEMBER_FUNCTION
            "as_global_function" -> AngelScriptSyntaxHighlighter.SEMANTIC_GLOBAL_FUNCTION

            // Variables
            "as_parameter" -> AngelScriptSyntaxHighlighter.SEMANTIC_PARAMETER
            "as_local_variable" -> AngelScriptSyntaxHighlighter.SEMANTIC_LOCAL_VARIABLE
            "as_member_variable" -> AngelScriptSyntaxHighlighter.SEMANTIC_MEMBER_VARIABLE
            "as_global_variable" -> AngelScriptSyntaxHighlighter.SEMANTIC_GLOBAL_VARIABLE

            // Accessors (properties)
            "as_member_accessor" -> AngelScriptSyntaxHighlighter.SEMANTIC_MEMBER_ACCESSOR
            "as_global_accessor" -> AngelScriptSyntaxHighlighter.SEMANTIC_GLOBAL_ACCESSOR

            // Access specifiers (public, private, protected, etc.)
            "as_access_specifier" -> AngelScriptSyntaxHighlighter.SEMANTIC_ACCESS_SPECIFIER

            // Error and warning states
            "as_unimported_symbol" -> AngelScriptSyntaxHighlighter.SEMANTIC_UNIMPORTED_SYMBOL
            "as_unknown_error" -> AngelScriptSyntaxHighlighter.SEMANTIC_UNKNOWN_ERROR

            // Fallback to default behavior for unknown token types
            else -> super.getTextAttributesKey(tokenType, tokenModifiers, file)
        }
    }
}
