package com.scriptacus.riderunrealangelscript.lsp

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.AbstractDocumentMatcher

/**
 * Custom document matcher for AngelScript files.
 *
 * This matcher ensures that all `.as` and `.ash` files are considered valid
 * for LSP features, regardless of their location relative to workspace roots.
 *
 * This is necessary because projects may have complex directory structures where
 * AngelScript files are in subdirectories of the project root.
 */
class AngelScriptDocumentMatcher : AbstractDocumentMatcher() {

    override fun match(file: VirtualFile, project: Project): Boolean {
        val extension = file.extension
        val matches = extension == "as" || extension == "ash"
        return matches
    }
}
