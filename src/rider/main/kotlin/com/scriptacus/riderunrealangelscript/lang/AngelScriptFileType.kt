package com.scriptacus.riderunrealangelscript.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import com.scriptacus.riderunrealangelscript.AngelScriptIcons
import javax.swing.Icon

class AngelScriptFileType : LanguageFileType(AngelScriptLanguage.INSTANCE) {
    override fun getName(): String = "angelscript"
    override fun getDescription(): String = "AngelScript file"
    override fun getDefaultExtension(): String = "as"
    override fun getIcon(): Icon = AngelScriptIcons.FileIcon

    companion object {
        val INSTANCE = AngelScriptFileType()
    }
}
