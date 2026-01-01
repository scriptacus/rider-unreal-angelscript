package com.scriptacus.riderunrealangelscript.lsp

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.util.concurrency.AppExecutorUtil
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.scriptacus.riderunrealangelscript.lang.AngelScriptLanguage
import com.scriptacus.riderunrealangelscript.settings.AngelScriptLspSettings
import com.scriptacus.riderunrealangelscript.settings.AngelScriptLspSettingsSync
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Sends initial configuration to the LSP server when a project opens.
 * Implements retry logic with exponential backoff to handle slow LSP startup.
 */
class AngelScriptLspInitializer : ProjectActivity {
    private val LOG = Logger.getInstance(AngelScriptLspInitializer::class.java)

    override suspend fun execute(project: Project) {
        val settings = AngelScriptLspSettings.getInstance().state
        val initialDelay = settings.lspInitialDelayMs.toLong()
        val maxRetries = settings.lspMaxRetries
        val backoffBase = settings.lspRetryBackoffMs.toLong()

        LOG.info("Scheduling LSP initialization for project: ${project.name} (initial delay: ${initialDelay}ms, max retries: $maxRetries)")

        // Schedule the first attempt
        scheduleSettingsSync(project, initialDelay, maxRetries, backoffBase, AtomicInteger(0))
    }

    private fun scheduleSettingsSync(
        project: Project,
        delay: Long,
        maxRetries: Int,
        backoffBase: Long,
        attemptCounter: AtomicInteger
    ) {
        AppExecutorUtil.getAppScheduledExecutorService().schedule({
            val attempt = attemptCounter.incrementAndGet()
            LOG.info("Attempting to send configuration to AngelScript LSP server (attempt $attempt/${maxRetries + 1})")

            // Check if LSP server is ready
            val serverStatus = LanguageServerManager.getInstance(project)
                .getServerStatus("angelscript")

            val isLspReady = serverStatus == com.redhat.devtools.lsp4ij.ServerStatus.started

            if (isLspReady || attempt > maxRetries) {
                if (isLspReady) {
                    LOG.info("LSP server is ready (status: $serverStatus), sending configuration")
                    AngelScriptLspSettingsSync(project).syncSettingsToLsp()
                } else {
                    LOG.warn("LSP server not ready after $attempt attempts (status: $serverStatus), sending configuration anyway")
                    AngelScriptLspSettingsSync(project).syncSettingsToLsp()
                }
            } else {
                // Calculate exponential backoff: backoffBase * (2^(attempt-1))
                val nextDelay = backoffBase * (1 shl (attempt - 1))
                LOG.info("LSP server not ready yet (status: $serverStatus), retrying in ${nextDelay}ms (attempt ${attempt + 1}/${maxRetries + 1})")
                scheduleSettingsSync(project, nextDelay, maxRetries, backoffBase, attemptCounter)
            }
        }, delay, TimeUnit.MILLISECONDS)
    }
}