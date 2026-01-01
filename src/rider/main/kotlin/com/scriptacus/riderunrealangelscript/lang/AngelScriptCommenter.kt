package com.scriptacus.riderunrealangelscript.lang

import com.intellij.lang.Commenter

/**
 * Provides comment support for AngelScript language.
 * Enables Ctrl+/ for line comments and Ctrl+Shift+/ for block comments.
 */
class AngelScriptCommenter : Commenter {
    override fun getLineCommentPrefix(): String = "//"

    override fun getBlockCommentPrefix(): String = "/*"

    override fun getBlockCommentSuffix(): String = "*/"

    override fun getCommentedBlockCommentPrefix(): String? = null

    override fun getCommentedBlockCommentSuffix(): String? = null
}