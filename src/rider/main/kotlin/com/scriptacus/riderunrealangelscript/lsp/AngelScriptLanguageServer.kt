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

    /**
     * Get C++ symbol information for a symbol at a given position.
     * Custom LSP request: angelscript/getCppSymbol
     *
     * Returns the C++ class name and symbol name for client-side navigation in Rider.
     *
     * @param params Object containing uri and position
     * @return CompletableFuture that resolves to a map with "className" and "symbolName", or null if no C++ symbol
     */
    @JsonRequest("angelscript/getCppSymbol")
    fun getCppSymbol(params: Map<String, Any>): CompletableFuture<Map<String, String>?>

    /**
     * Request navigation to C++ source for a symbol at a given position via Unreal Engine.
     * Custom LSP request: angelscript/navigateToCpp
     *
     * This triggers the language server to:
     * 1. Resolve the AngelScript symbol at the position to its C++ equivalent
     * 2. Send a command to Unreal Engine to open the C++ file in the IDE
     *
     * @param params Object containing uri and position
     * @return CompletableFuture that resolves to true if navigation was triggered, false otherwise
     */
    @JsonRequest("angelscript/navigateToCpp")
    fun navigateToCpp(params: Map<String, Any>): CompletableFuture<Boolean>
}
