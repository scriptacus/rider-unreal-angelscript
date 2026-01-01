package com.scriptacus.riderunrealangelscript.services

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerManager
import java.util.concurrent.CompletableFuture

/**
 * Project-level service for communicating with the AngelScript LSP server's custom API requests.
 *
 * Provides methods for:
 * - Getting API tree data (angelscript/getAPI)
 * - Getting API details/documentation (angelscript/getAPIDetails)
 * - Searching the API (angelscript/getAPISearch)
 */
@Service(Service.Level.PROJECT)
class AngelScriptApiService(private val project: Project) {

    companion object {
        private val LOG = Logger.getInstance(AngelScriptApiService::class.java)
    }

    private val gson = Gson()
    /**
     * Get API tree data from the language server.
     *
     * @param root Root identifier for the API tree ("" for root, or a namespace/class ID)
     * @param callback Callback invoked with the list of API items
     */
    fun getApi(root: String, callback: (List<Map<String, Any>>) -> Unit) {
        sendRequest("angelscript/getAPI", root) { result ->
            @Suppress("UNCHECKED_CAST")
            val apiItems = when (result) {
                is List<*> -> {
                    // Convert Gson's JsonObject instances to proper Maps
                    result.mapNotNull { item ->
                        convertToMap(item)
                    }
                }
                null -> {
                    LOG.warn("angelscript/getAPI returned null - LSP server may not be connected")
                    emptyList()
                }
                else -> {
                    LOG.warn("Unexpected response type: ${result.javaClass}")
                    emptyList()
                }
            }
            callback(apiItems)
        }
    }

    /**
     * Convert Gson's internal types (JsonObject, JsonArray, etc.) to standard Java types.
     */
    private fun convertToMap(obj: Any?): Map<String, Any>? {
        if (obj == null) return null

        return try {
            // Convert via JSON to handle Gson's internal types
            val json = gson.toJson(obj)
            gson.fromJson(json, object : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}.type)
        } catch (e: Exception) {
            LOG.warn("Failed to convert object to Map: ${obj.javaClass}", e)
            null
        }
    }

    /**
     * Get detailed documentation for an API item.
     *
     * @param id Identifier for the API item
     * @param callback Callback invoked with the markdown documentation
     */
    fun getApiDetails(id: Any, callback: (String) -> Unit) {
        sendRequest("angelscript/getAPIDetails", id) { result ->
            val details = when (result) {
                is String -> result
                null -> {
                    LOG.warn("getAPIDetails returned null")
                    "No details available"
                }
                else -> {
                    LOG.warn("getAPIDetails returned unexpected type: ${result.javaClass}")
                    result.toString()
                }
            }
            callback(details)
        }
    }

    /**
     * Search the API for items matching a filter string.
     *
     * @param filter Search filter string
     * @param callback Callback invoked with the list of matching API items
     */
    fun getApiSearch(filter: String, callback: (List<Map<String, Any>>) -> Unit) {
        sendRequest("angelscript/getAPISearch", filter) { result ->
            @Suppress("UNCHECKED_CAST")
            val apiItems = when (result) {
                is List<*> -> {
                    // Convert Gson's JsonObject instances to proper Maps
                    result.mapNotNull { item ->
                        convertToMap(item)
                    }
                }
                null -> {
                    LOG.warn("angelscript/getAPISearch returned null")
                    emptyList()
                }
                else -> {
                    LOG.warn("Unexpected search response type: ${result.javaClass}")
                    emptyList()
                }
            }
            callback(apiItems)
        }
    }

    /**
     * Send a custom LSP request to the AngelScript language server using type-safe methods.
     *
     * @param method LSP method name (e.g., "angelscript/getAPI")
     * @param params Request parameters
     * @param callback Callback invoked with the result (or null on error)
     */
    private fun sendRequest(method: String, params: Any, callback: (Any?) -> Unit) {
        try {
            // Get the language server manager
            val manager = LanguageServerManager.getInstance(project)

            // Get the AngelScript language server
            val serverItemFuture = manager.getLanguageServer("angelscript-lsp")

            serverItemFuture.thenAccept { serverItem ->
                if (serverItem == null) {
                    LOG.warn("[$method] AngelScript LSP server not available")
                    callback(null)
                    return@thenAccept
                }

                try {
                    val server = serverItem.server

                    // Cast to our custom AngelScriptLanguageServer interface for type-safe requests
                    if (server is com.scriptacus.riderunrealangelscript.lsp.AngelScriptLanguageServer) {
                        // Call the appropriate type-safe method based on the request type
                        val requestFuture: CompletableFuture<*> = when (method) {
                            "angelscript/getAPI" -> server.getAPI(params as String)
                            "angelscript/getAPIDetails" -> server.getAPIDetails(params)
                            "angelscript/getAPISearch" -> server.getAPISearch(params as String)
                            else -> {
                                LOG.error("Unknown custom request method: $method")
                                callback(null)
                                return@thenAccept
                            }
                        }

                        requestFuture
                            .thenAccept { result ->
                                callback(result)
                            }
                            .exceptionally { throwable ->
                                LOG.error("LSP request failed: $method - ${throwable.message}", throwable)
                                callback(null)
                                null
                            }
                    } else {
                        LOG.error("Server does not implement AngelScriptLanguageServer interface: ${server.javaClass.name}")
                        callback(null)
                    }
                } catch (e: Exception) {
                    LOG.error("Failed to send LSP request: $method", e)
                    callback(null)
                }
            }.exceptionally { throwable ->
                LOG.error("Failed to get language server for: $method", throwable)
                callback(null)
                null
            }
        } catch (e: Exception) {
            LOG.error("Failed to access language server manager: $method", e)
            callback(null)
        }
    }
}
