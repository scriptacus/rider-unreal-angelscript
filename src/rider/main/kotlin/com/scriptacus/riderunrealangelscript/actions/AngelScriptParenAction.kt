package com.scriptacus.riderunrealangelscript.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger

/**
 * No-op action to handle the 'angelscript.paren' command from the LSP server.
 *
 * The VSCode extension uses this command to insert parentheses after function completion
 * and trigger signature help. In Rider, we rely on the IDE's built-in completion behavior
 * which already handles this well by default.
 *
 * This action exists solely to prevent the "Missing 'angelscript.paren' command" error
 * when the LSP server sends completion items with this command.
 */
class AngelScriptParenAction : AnAction() {

    companion object {
        private val LOG = Logger.getInstance(AngelScriptParenAction::class.java)
    }

    override fun actionPerformed(e: AnActionEvent) {
        // No-op: Rider's default completion behavior already handles parenthesis insertion
        // and signature help triggering. If we need custom behavior in the future, it will
        // be implemented here.
        LOG.debug("angelscript.paren command invoked (no-op)")
    }

    override fun update(e: AnActionEvent) {
        // Always enabled
        e.presentation.isEnabledAndVisible = true
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
