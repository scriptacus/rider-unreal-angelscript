package com.scriptacus.riderunrealangelscript.lang.psi

import com.scriptacus.riderunrealangelscript.lang.AngelScriptLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class AngelScriptElementType(@NonNls debugName: String) : IElementType(debugName, AngelScriptLanguage.INSTANCE)
