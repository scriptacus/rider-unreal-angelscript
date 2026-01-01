// This is a generated file. Not intended for manual editing.
package com.scriptacus.riderunrealangelscript.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AngelScriptPrimaryExpr extends AngelScriptExpr {

  @Nullable
  AngelScriptCastExpression getCastExpression();

  @Nullable
  AngelScriptConstructorCall getConstructorCall();

  @Nullable
  AngelScriptExpr getExpr();

  @Nullable
  AngelScriptFstringLiteral getFstringLiteral();

  @Nullable
  AngelScriptNamestringLiteral getNamestringLiteral();

  @Nullable
  AngelScriptScopedIdentifier getScopedIdentifier();

  @Nullable
  AngelScriptStringLiteral getStringLiteral();

}
