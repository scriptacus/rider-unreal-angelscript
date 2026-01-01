package com.scriptacus.riderunrealangelscript.debug

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.Disposer
import com.intellij.util.io.BaseOutputReader
import com.redhat.devtools.lsp4ij.dap.definitions.DebugAdapterServerDefinition
import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptor
import com.scriptacus.riderunrealangelscript.lang.AngelScriptFileType
import com.scriptacus.riderunrealangelscript.settings.AngelScriptLspSettings
import com.scriptacus.riderunrealangelscript.util.NodeJsExecutableFinder
import com.scriptacus.riderunrealangelscript.util.TempFileManager
import java.io.File

/**
 * Descriptor for the AngelScript Debug Adapter.
 * Configures and launches the bundled Node.js-based debug adapter.
 */
class AngelScriptDebugAdapterDescriptor(
    options: RunConfigurationOptions,
    environment: ExecutionEnvironment,
    definition: DebugAdapterServerDefinition
) : DebugAdapterDescriptor(options, environment, definition) {

    private val LOG = Logger.getInstance(AngelScriptDebugAdapterDescriptor::class.java)
    private val tempFileManager = TempFileManager().also {
        Disposer.register(environment.project, it)
    }

    @Throws(ExecutionException::class)
    override fun startServer(): ProcessHandler {
        // Find Node.js executable
        val nodeExecutable = NodeJsExecutableFinder.find(environment.project)
            ?: throw ExecutionException("Node.js executable not found. Please install Node.js.")

        LOG.info("Starting AngelScript Debug Adapter with Node.js: $nodeExecutable")

        // Get the bundled debug adapter script
        val debugAdapterScript = getDebugAdapterScript()

        // Create command line
        val commandLine = GeneralCommandLine(nodeExecutable, debugAdapterScript.absolutePath)
        environment.project.basePath?.let { commandLine.withWorkDirectory(it) }

        // Add stdio mode flag
        commandLine.addParameter("--stdio")

        LOG.info("Debug adapter command: ${commandLine.commandLineString}")

        // Start the process with custom reader options for mostly idle daemon processes
        val processHandler = object : OSProcessHandler(commandLine) {
            override fun readerOptions(): BaseOutputReader.Options {
                // Debug adapter is a mostly idle daemon process - reduce CPU usage
                return BaseOutputReader.Options.forMostlySilentProcess()
            }

            // NOTE: DAP JSON protocol messages may appear in the console during debugging.
            // This is a cosmetic issue caused by LSP4IJ's DAPConsoleView filtering only applying
            // to NORMAL_OUTPUT, not SYSTEM_OUTPUT. The protocol messages don't affect functionality.
            // Cannot override notifyTextAvailable() to filter them as it would break DAP communication
            // (DAPProcessListener needs the stdout notifications to receive protocol messages).
        }

        // IMPORTANT: Must call startNotify() to initialize the process handler
        // and begin monitoring stdin/stdout. Without this, the streams won't
        // be properly connected and will cause errors in the debug adapter.
        // See: lsp4ij's OSProcessStreamConnectionProvider.java:128 for reference
        processHandler.startNotify()

        LOG.info("Debug adapter process started successfully")

        return processHandler
    }

    override fun getDapParameters(): Map<String, Any> {
        val params = mutableMapOf<String, Any>()

        // Get port from settings
        val settings = AngelScriptLspSettings.getInstance()
        val port = settings.state.unrealConnectionPort
        params["port"] = port

        // Set hostname (default 127.0.0.1)
        val hostname = "127.0.0.1"
        params["hostname"] = hostname

        LOG.info("DAP parameters: port=$port, hostname=$hostname")

        return params
    }

    override fun getFileType(): FileType? {
        // Return AngelScript file type to enable breakpoints in .as files
        return AngelScriptFileType();
    }

    private fun getDebugAdapterScript(): File {
        // Extract the bundled debug adapter from plugin resources to a temporary file
        val resourcePath = "/js/angelscript-debug-adapter.js"
        val inputStream = javaClass.getResourceAsStream(resourcePath)
            ?: throw IllegalStateException("Debug adapter script not found in plugin resources: $resourcePath")

        try {
            // Create a temporary file managed by tempFileManager
            val tempFile = tempFileManager.createTempFile("angelscript-debug-adapter-", ".js")

            // Copy the resource to the temp file
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            return tempFile
        } catch (e: Exception) {
            throw IllegalStateException("Failed to extract debug adapter script: ${e.message}", e)
        }
    }
}
