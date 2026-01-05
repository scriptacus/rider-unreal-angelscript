package com.scriptacus.riderunrealangelscript.lsp

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.AbstractDocumentMatcher
import com.scriptacus.riderunrealangelscript.settings.AngelScriptLspSettings
import com.scriptacus.riderunrealangelscript.util.GlobMatcher

/**
 * Custom document matcher for AngelScript files.
 *
 * This matcher filters files sent to the LSP server based on:
 * 1. File extension (.as and .ash)
 * 2. Path exclusions via scriptIgnorePatterns
 *
 * Files matching ignore patterns are not sent to the LSP server, preventing:
 * - Build artifacts in Saved/ from being indexed
 * - Version control metadata in .plastic/ from being processed
 * - Duplicate modules in the LSP server's internal database
 */
class AngelScriptDocumentMatcher : AbstractDocumentMatcher() {

    private val LOG = Logger.getInstance(AngelScriptDocumentMatcher::class.java)

    override fun match(file: VirtualFile, project: Project): Boolean {
        // First, check file extension
        val extension = file.extension
        if (extension != "as" && extension != "ash") {
            return false
        }

        // Then, check if file path matches any ignore patterns
        val ignorePatterns = AngelScriptLspSettings.getInstance().state.scriptIgnorePatterns
        if (ignorePatterns.isNotEmpty()) {
            val filePath = file.path
            if (GlobMatcher.matchesAny(filePath, ignorePatterns)) {
                // File matches an ignore pattern - don't send to LSP
                LOG.info("DocumentMatcher: EXCLUDING file from LSP: $filePath (matched ignore pattern)")
                return false
            }
        }

        // File has correct extension and doesn't match ignore patterns
        LOG.debug("DocumentMatcher: ALLOWING file for LSP: ${file.path}")
        return true
    }
}
