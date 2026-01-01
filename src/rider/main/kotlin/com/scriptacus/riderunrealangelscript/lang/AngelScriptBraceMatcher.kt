package com.scriptacus.riderunrealangelscript.lang

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptTypes

/**
 * Provides brace matching for AngelScript code.
 * Matches:
 * - Curly braces: {} (structural)
 * - Parentheses: () (non-structural)
 * - Square brackets: [] (non-structural)
 */
class AngelScriptBraceMatcher : PairedBraceMatcher {
    override fun getPairs(): Array<BracePair> {
        return arrayOf(
            // Curly braces are structural - used for code blocks
            BracePair(AngelScriptTypes.START_STATEMENT_BLOCK, AngelScriptTypes.END_STATEMENT_BLOCK, true),
            // Parentheses are non-structural - used for expressions, function calls
            BracePair(AngelScriptTypes.OPEN_PARENTHESIS, AngelScriptTypes.CLOSE_PARENTHESIS, false),
            // Square brackets are non-structural - used for array access
            BracePair(AngelScriptTypes.OPEN_BRACKET, AngelScriptTypes.CLOSE_BRACKET, false)
        )
    }

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean {
        // Don't auto-insert closing brace in strings or comments
        return contextType != AngelScriptTypes.STRING &&
               contextType != AngelScriptTypes.COMMENT
    }

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int {
        // Return the opening brace position as the start of the code construct
        return openingBraceOffset
    }
}
