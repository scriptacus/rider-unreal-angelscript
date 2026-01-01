package com.scriptacus.riderunrealangelscript.debug

import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.diagnostic.Logger
import com.redhat.devtools.lsp4ij.dap.configurations.DAPRunConfigurationOptions
import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptor
import com.redhat.devtools.lsp4ij.dap.descriptors.DebugAdapterDescriptorFactory
import com.intellij.execution.configurations.RunConfigurationOptions

/**
 * Factory for creating AngelScript Debug Adapter descriptors.
 * Registered in plugin.xml as a debugAdapterServer extension.
 */
class AngelScriptDebugAdapterDescriptorFactory : DebugAdapterDescriptorFactory() {
    override fun createDebugAdapterDescriptor(
        options: RunConfigurationOptions,
        environment: ExecutionEnvironment
    ): DebugAdapterDescriptor {
        return AngelScriptDebugAdapterDescriptor(options, environment, serverDefinition)
    }
}
