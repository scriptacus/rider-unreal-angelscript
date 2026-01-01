package com.scriptacus.riderunrealangelscript

import com.intellij.openapi.util.IconLoader

/**
 * Icon definitions for the AngelScript plugin.
 */
object AngelScriptIcons {
    /**
     * AngelScript Unreal icons.
     * Same icons as used by the VSCode extension.
     */
    @JvmField
    val ToolWindow = IconLoader.getIcon("/icons/angelscript_unreal.png", AngelScriptIcons::class.java)
    val FileIcon = IconLoader.getIcon("/icons/file-icon-dark-theme.svg", AngelScriptIcons::class.java)
}