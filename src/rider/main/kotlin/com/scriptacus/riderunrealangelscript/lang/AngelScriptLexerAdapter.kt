package com.scriptacus.riderunrealangelscript.lang

import com.intellij.lexer.FlexAdapter
import com.scriptacus.riderunrealangelscript.lang.lexer.AngelScriptLexer

class AngelScriptLexerAdapter : FlexAdapter(AngelScriptLexer(null))
