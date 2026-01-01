package com.scriptacus.riderunrealangelscript.lang

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptFile

/**
 * Handles custom typing behavior for AngelScript files.
 *
 * Note: Brace matching ({}, [], ()) is now handled by AngelScriptBraceMatcher.
 * This class is kept for potential future typing customizations.
 */
class AngelScriptTypedHandler : TypedHandlerDelegate() {

    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (file !is AngelScriptFile) {
            return Result.CONTINUE
        }

        // Future custom typing behavior can be added here

        return Result.CONTINUE
    }
}
