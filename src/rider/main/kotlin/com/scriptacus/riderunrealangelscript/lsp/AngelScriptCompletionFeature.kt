package com.scriptacus.riderunrealangelscript.lsp

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.icons.AllIcons
import com.intellij.openapi.diagnostic.Logger
import com.redhat.devtools.lsp4ij.client.features.LSPCompletionFeature
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionItemKind
import javax.swing.Icon

/**
 * Custom completion feature that uses AngelScriptCompletionProposal to fix
 * lsp4ij's additionalTextEdits ordering bug.
 *
 * This feature overrides the default LSPCompletionProposal creation to use our
 * fixed version that applies text edits with a stable sort order.
 *
 * Also customizes icon mapping for AngelScript's scripting language semantics:
 * - Method -> Function icon (AngelScript uses "function" terminology)
 * - Field -> Property icon (AngelScript uses "property" terminology)
 */
class AngelScriptCompletionFeature : LSPCompletionFeature() {

    private val LOG = Logger.getInstance(AngelScriptCompletionFeature::class.java)

    override fun createLookupElement(
        item: CompletionItem,
        context: LSPCompletionContext
    ): LookupElement? {
        if (item.label.isNullOrBlank()) {
            LOG.warn("Invalid completion item with null/blank label - ignoring")
            return null
        }

        // Use our fixed completion proposal instead of the default
        return AngelScriptCompletionProposal(item, context, this)
    }

    /**
     * Map LSP completion item kinds to appropriate icons for AngelScript.
     *
     * AngelScript is a scripting language, so we use function/property terminology
     * instead of method/field terminology typical of OOP languages.
     */
    override fun getIcon(item: CompletionItem): Icon? {
        return when (item.kind) {
            // Map Method to Function icon (scripting language semantics)
            CompletionItemKind.Method -> AllIcons.Nodes.Function

            // Map Field to Property icon (scripting language semantics)
            CompletionItemKind.Field -> AllIcons.Nodes.Property

            // Use default mapping for everything else
            else -> super.getIcon(item)
        }
    }
}