package com.scriptacus.riderunrealangelscript.util

import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterManager
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.EnvironmentUtil
import java.io.File

/**
 * Utility for discovering Node.js executable path.
 *
 * This class provides a unified strategy for finding Node.js across different environments:
 * 1. Rider's configured Node.js interpreter (Settings > Languages & Frameworks > Node.js)
 * 2. PATH environment variable
 * 3. Common installation locations (platform-specific)
 *
 * The finder supports caching to avoid repeated filesystem checks within the same session.
 */
object NodeJsExecutableFinder {
    private val LOG = Logger.getInstance(NodeJsExecutableFinder::class.java)

    @Volatile
    private var cachedNodePath: String? = null

    @Volatile
    private var cacheInitialized = false

    /**
     * Finds the Node.js executable path.
     *
     * @param project The current project, used to check Rider's configured Node.js interpreter
     * @param useCache If true, returns cached result from previous search. Default is true.
     * @return The absolute path to the Node.js executable, or null if not found
     */
    fun find(project: Project?, useCache: Boolean = true): String? {
        // Return cached result if available and caching is enabled
        if (useCache && cacheInitialized) {
            return cachedNodePath
        }

        val foundPath = findNodeExecutableInternal(project)

        // Cache the result
        if (useCache) {
            cachedNodePath = foundPath
            cacheInitialized = true
        }

        return foundPath
    }

    /**
     * Clears the cached Node.js path, forcing the next find() call to perform a fresh search.
     */
    fun clearCache() {
        cachedNodePath = null
        cacheInitialized = false
    }

    private fun findNodeExecutableInternal(project: Project?): String? {
        // Strategy 1: Try to get Node.js from Rider's configured interpreter
        if (project != null) {
            val riderNodePath = findNodeInRiderConfig(project)
            if (riderNodePath != null) {
                LOG.info("Found Node.js in Rider configuration: $riderNodePath")
                return riderNodePath
            }
        }

        // Strategy 2: Try to find Node.js in PATH
        val pathNodeExecutable = findNodeInPath()
        if (pathNodeExecutable != null) {
            LOG.info("Found Node.js in PATH: $pathNodeExecutable")
            return pathNodeExecutable
        }

        // Strategy 3: Try common installation locations as last resort
        val fallbackPath = findNodeInCommonLocations()
        if (fallbackPath != null) {
            LOG.info("Found Node.js in common location: $fallbackPath")
            return fallbackPath
        }

        LOG.warn("Node.js not found in Rider config, PATH, or common locations")
        return null
    }

    private fun findNodeInRiderConfig(project: Project): String? {
        try {
            val interpreter = NodeJsInterpreterManager.getInstance(project).interpreter
            if (interpreter is NodeJsLocalInterpreter) {
                val interpreterPath = interpreter.interpreterSystemDependentPath
                if (File(interpreterPath).exists()) {
                    return interpreterPath
                }
            }
        } catch (e: Exception) {
            LOG.debug("Could not get Node.js interpreter from Rider configuration", e)
        }
        return null
    }

    private fun findNodeInPath(): String? {
        val nodeExecutable = if (SystemInfo.isWindows) "node.exe" else "node"
        val pathDirs = EnvironmentUtil.getValue("PATH")?.split(File.pathSeparator) ?: emptyList()

        for (dir in pathDirs) {
            val nodePath = File(dir, nodeExecutable)
            if (nodePath.exists() && nodePath.canExecute()) {
                return nodePath.absolutePath
            }
        }
        return null
    }

    private fun findNodeInCommonLocations(): String? {
        val commonPaths = when {
            SystemInfo.isWindows -> listOf(
                "C:\\Program Files\\nodejs\\node.exe",
                "C:\\Program Files (x86)\\nodejs\\node.exe",
                "${System.getenv("LOCALAPPDATA")}\\Programs\\nodejs\\node.exe",
                "${System.getenv("ProgramFiles")}\\nodejs\\node.exe",
                "${System.getenv("ProgramFiles(x86)")}\\nodejs\\node.exe"
            )
            SystemInfo.isMac -> listOf(
                "/usr/local/bin/node",
                "/opt/homebrew/bin/node",
                "/usr/bin/node",
                "/opt/local/bin/node"
            )
            else -> listOf(
                "/usr/bin/node",
                "/usr/local/bin/node",
                "/opt/node/bin/node"
            )
        }

        return commonPaths.firstOrNull { File(it).exists() }
    }
}
