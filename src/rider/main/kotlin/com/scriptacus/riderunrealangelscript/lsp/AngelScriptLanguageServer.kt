package com.scriptacus.riderunrealangelscript.lsp

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.eclipse.lsp4j.services.LanguageServer
import java.util.concurrent.CompletableFuture

/**
 * Custom language server interface for AngelScript LSP.
 * Extends the standard LSP protocol with custom requests for API browsing and inline debug values.
 */
interface AngelScriptLanguageServer : LanguageServer {

    /**
     * Get API tree data from the language server.
     * Custom LSP request: angelscript/getAPI
     *
     * @param root Root identifier for the API tree ("" for root, or a namespace/class ID)
     * @return CompletableFuture that resolves to a list of API items
     */
    @JsonRequest("angelscript/getAPI")
    fun getAPI(root: String): CompletableFuture<List<*>>

    /**
     * Get detailed documentation for an API item.
     * Custom LSP request: angelscript/getAPIDetails
     *
     * @param id Identifier for the API item
     * @return CompletableFuture that resolves to markdown documentation
     */
    @JsonRequest("angelscript/getAPIDetails")
    fun getAPIDetails(id: Any): CompletableFuture<String>

    /**
     * Search the API for items matching a filter string.
     * Custom LSP request: angelscript/getAPISearch
     *
     * @param filter Search filter string
     * @return CompletableFuture that resolves to a list of matching API items
     */
    @JsonRequest("angelscript/getAPISearch")
    fun getAPISearch(filter: String): CompletableFuture<List<*>>
}
