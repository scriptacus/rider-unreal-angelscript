package com.scriptacus.riderunrealangelscript.lang.psi

import com.scriptacus.riderunrealangelscript.lang.AngelScriptFileType
import com.scriptacus.riderunrealangelscript.lang.AngelScriptLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class AngelScriptFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, AngelScriptLanguage.INSTANCE) {
    override fun getFileType(): FileType = AngelScriptFileType.INSTANCE
}
