package com.scriptacus.riderunrealangelscript.lang.psi

import com.scriptacus.riderunrealangelscript.lang.AngelScriptLanguage
import com.intellij.psi.tree.IElementType

class AngelScriptTokenType(debugName: String) : IElementType(debugName, AngelScriptLanguage.INSTANCE) {
    override fun toString(): String = "AngelScriptTokenType.${super.toString()}"
}
