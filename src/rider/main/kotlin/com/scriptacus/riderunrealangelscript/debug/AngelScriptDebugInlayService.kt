package com.scriptacus.riderunrealangelscript.debug

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebuggerManagerListener
import com.redhat.devtools.lsp4ij.dap.DAPDebugProcess
import com.redhat.devtools.lsp4ij.dap.client.DAPStackFrame
import org.eclipse.lsp4j.debug.ScopesArguments
import org.eclipse.lsp4j.debug.VariablesArguments
import java.util.concurrent.ConcurrentHashMap

/**
 * Service that manages inline debug value hints for AngelScript files.
 *
 * Listens to debug session events and caches variable values from the current stack frame.
 * When the debugger pauses, it extracts variables and their positions, then triggers
 * inlay hints refresh to display values inline in the editor.
 */
@Service(Service.Level.PROJECT)
class AngelScriptDebugInlayService(private val project: Project) : Disposable {

    companion object {
        private val LOG = Logger.getInstance(AngelScriptDebugInlayService::class.java)

        fun getInstance(project: Project): AngelScriptDebugInlayService {
            return project.getService(AngelScriptDebugInlayService::class.java)
        }
    }

    /**
     * Cache of variable values per file.
     * Map<VirtualFile, Map<LineNumber, List<VariableInfo>>>
     */
    private val variableCache = ConcurrentHashMap<VirtualFile, MutableMap<Int, MutableList<VariableInfo>>>()

    /**
     * Holds information about a variable to display inline
     */
    data class VariableInfo(
        val name: String,
        val value: String,
        val offset: Int  // Character offset in document
    )

    init {
        // Subscribe to debugger manager events
        val connection = project.messageBus.connect(this)
        connection.subscribe(XDebuggerManager.TOPIC, object : XDebuggerManagerListener {
            override fun processStarted(debugProcess: XDebugProcess) {
                if (debugProcess is DAPDebugProcess) {
                    LOG.info("AngelScript debug process started, attaching session listener")
                    debugProcess.session.addSessionListener(DebugSessionListener(debugProcess), this@AngelScriptDebugInlayService)
                }
            }
        })

        // Listen to editor lifecycle to refresh hints when editor opens
        EditorFactory.getInstance().addEditorFactoryListener(object : EditorFactoryListener {
            override fun editorCreated(event: EditorFactoryEvent) {
                // When an editor is created, if we have cached variables for that file, refresh hints
                val file = event.editor.virtualFile
                if (file != null && variableCache.containsKey(file)) {
                    forceInlayHintsRefresh(file)
                }
            }
        }, this)
    }

    /**
     * Listener for debug session events (pause, resume, stop)
     */
    private inner class DebugSessionListener(private val debugProcess: DAPDebugProcess) : com.intellij.xdebugger.XDebugSessionListener {

        override fun sessionPaused() {
            LOG.info("Debug session paused, collecting variables")

            ApplicationManager.getApplication().runReadAction {
                try {
                    val stackFrame = debugProcess.session.currentStackFrame as? DAPStackFrame
                    if (stackFrame != null) {
                        updateVariableCache(debugProcess, stackFrame)
                    } else {
                        LOG.debug("Current stack frame is not a DAPStackFrame or is null")
                    }
                } catch (e: Exception) {
                    LOG.error("Error collecting variables on session pause", e)
                }
            }
        }

        override fun sessionResumed() {
            LOG.debug("Debug session resumed, clearing variable cache")
            clearVariableCache()
            forceInlayHintsRefresh()
        }

        override fun sessionStopped() {
            LOG.info("Debug session stopped, clearing variable cache")
            clearVariableCache()
            forceInlayHintsRefresh()
        }
    }

    /**
     * Extracts variables from the current stack frame and caches them
     */
    private fun updateVariableCache(debugProcess: DAPDebugProcess, stackFrame: DAPStackFrame) {
        // Get the DAP client from the stack frame (not from debug process - parentClient is private!)
        val client = stackFrame.client
        val server = client.debugProtocolServer
        if (server == null) {
            LOG.debug("Debug protocol server is null")
            return
        }

        val sourcePosition = stackFrame.sourcePosition
        if (sourcePosition == null) {
            LOG.debug("Stack frame has no source position")
            return
        }

        val file = sourcePosition.file

        // Get scopes (locals, globals, etc.)
        val scopesArgs = ScopesArguments()
        scopesArgs.frameId = stackFrame.frameId

        server.scopes(scopesArgs).thenAccept { scopesResponse: org.eclipse.lsp4j.debug.ScopesResponse ->
            val scopes = scopesResponse.scopes?.toList() ?: emptyList()

            if (scopes.isEmpty()) {
                LOG.debug("No scopes returned from debug server")
                return@thenAccept
            }

            LOG.debug("Processing ${scopes.size} scopes for file ${file.name}")

            // Get document in read action (required by IntelliJ threading model)
            val document = ApplicationManager.getApplication().runReadAction<com.intellij.openapi.editor.Document?> {
                com.intellij.openapi.fileEditor.FileDocumentManager.getInstance().getDocument(file)
            }

            if (document == null) {
                LOG.warn("No document for file ${file.name}")
                return@thenAccept
            }

            // Request variables directly from DAP protocol for each scope
            val fileVariables = ConcurrentHashMap<Int, MutableList<VariableInfo>>()

            // Collect all variable requests as CompletableFutures
            val variableFutures = scopes.map { scope ->
                val variablesArgs = VariablesArguments()
                variablesArgs.variablesReference = scope.variablesReference

                server.variables(variablesArgs).thenApply { variablesResponse ->
                    val variables = variablesResponse.variables ?: emptyArray()
                    LOG.debug("Scope '${scope.name}' returned ${variables.size} variables")

                    // Get line and offset in read action
                    ApplicationManager.getApplication().runReadAction {
                        val psiFile = com.intellij.psi.PsiDocumentManager.getInstance(project)
                            .getPsiFile(document)

                        for (variable in variables) {
                            val varName = variable.name ?: continue
                            val varValue = variable.value ?: "null"

                            // Find where this variable is declared in the source code
                            val variableLine = findVariableDeclarationLine(psiFile, varName, document)

                            if (variableLine != null) {
                                val offset = document.getLineEndOffset(variableLine)
                                val info = VariableInfo(varName, varValue, offset)
                                fileVariables.computeIfAbsent(variableLine) { mutableListOf() }.add(info)

                                LOG.debug("Cached variable from '${scope.name}': $varName = $varValue at line $variableLine")
                            } else {
                                LOG.debug("Could not find declaration for variable: $varName")
                            }
                        }
                    }
                    variables.size
                }.exceptionally { error ->
                    LOG.error("Error getting variables from scope ${scope.name}", error)
                    0
                }
            }

            // Wait for ALL scopes to complete, then update cache once
            java.util.concurrent.CompletableFuture.allOf(*variableFutures.toTypedArray()).thenRun {
                if (fileVariables.isNotEmpty()) {
                    val totalVars = fileVariables.values.flatten().size
                    variableCache[file] = fileVariables
                    LOG.info("Cached $totalVars variables for ${file.name}")

                    ApplicationManager.getApplication().invokeLater {
                        // Refresh hints for this specific file only (avoid duplicates)
                        forceInlayHintsRefresh(file)
                    }
                } else {
                    LOG.debug("No variables collected from any scope")
                }
            }.exceptionally { error ->
                LOG.error("Error waiting for all scopes to complete", error)
                null
            }
        }.exceptionally { error ->
            LOG.error("Error getting scopes", error)
            null
        }
    }


    /**
     * Clears all cached variables
     */
    private fun clearVariableCache() {
        variableCache.clear()
    }

    /**
     * Forces inlay hints refresh for all open editors with cached variables
     */
    private fun forceInlayHintsRefresh() {
        ApplicationManager.getApplication().invokeLater {
            // Only refresh files that have cached variables
            variableCache.keys.forEach { file ->
                forceInlayHintsRefresh(file)
            }
        }
    }

    /**
     * Forces inlay hints refresh for a specific file
     */
    private fun forceInlayHintsRefresh(file: VirtualFile) {
        ApplicationManager.getApplication().invokeLater {
            try {
                // Skip synthetic files created by debugger (expression evaluator, etc.)
                if (file.name.contains("DAPExpression") || file.name.contains("Fragment")) {
                    return@invokeLater
                }

                val psiFile = com.intellij.psi.PsiManager.getInstance(project).findFile(file)

                if (psiFile != null && file.isValid) {
                    // Trigger a reparse of the file which will cause inlay hints to refresh
                    // This is the proper way to refresh declarative inlay hints
                    com.intellij.util.FileContentUtilCore.reparseFiles(file)

                    // Also restart daemon code analyzer for good measure
                    com.intellij.codeInsight.daemon.DaemonCodeAnalyzer.getInstance(project).restart(psiFile)

                    LOG.debug("Refreshed inlay hints for ${file.name}")
                }
            } catch (e: Exception) {
                LOG.warn("Error refreshing inlay hints for ${file.name}: ${e.message}")
            }
        }
    }

    /**
     * Finds the line where a variable is declared by searching the PSI tree
     * Returns the line number (0-based) or null if not found
     */
    private fun findVariableDeclarationLine(
        psiFile: com.intellij.psi.PsiFile?,
        variableName: String,
        document: com.intellij.openapi.editor.Document
    ): Int? {
        if (psiFile == null) {
            LOG.warn("PSI file is null, cannot find variable $variableName")
            return null
        }

        // Search for any element with matching text
        // We'll search the entire document for the variable name
        val text = document.text
        var searchIndex = 0

        while (true) {
            val index = text.indexOf(variableName, searchIndex)
            if (index == -1) break

            // Check if this is a whole word match (not part of another identifier)
            val isWordStart = index == 0 || !text[index - 1].isJavaIdentifierPart()
            val isWordEnd = index + variableName.length >= text.length ||
                           !text[index + variableName.length].isJavaIdentifierPart()

            if (isWordStart && isWordEnd) {
                // This is a whole word match - likely the first declaration
                val line = document.getLineNumber(index)
                LOG.debug("Found variable '$variableName' at line $line (offset $index)")
                return line
            }

            searchIndex = index + 1
        }

        LOG.info("Could not find variable '$variableName' in source code")
        return null
    }

    /**
     * Gets cached variables for a specific file
     * @return Map of line number to list of variables on that line, or null if no variables cached
     */
    fun getVariablesForFile(file: VirtualFile): Map<Int, List<VariableInfo>>? {
        return variableCache[file]
    }

    override fun dispose() {
        clearVariableCache()
    }
}
