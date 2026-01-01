package com.scriptacus.riderunrealangelscript.lsp

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.diagnostic.Logger
import com.redhat.devtools.lsp4ij.client.features.LSPCompletionFeature
import org.eclipse.lsp4j.CompletionItem

/**
 * Custom completion feature that uses AngelScriptCompletionProposal to fix
 * lsp4ij's additionalTextEdits ordering bug.
 *
 * This feature overrides the default LSPCompletionProposal creation to use our
 * fixed version that applies text edits with a stable sort order.
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
}