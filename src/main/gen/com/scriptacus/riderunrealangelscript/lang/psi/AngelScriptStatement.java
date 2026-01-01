// This is a generated file. Not intended for manual editing.
package com.scriptacus.riderunrealangelscript.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AngelScriptStatement extends PsiElement {

  @Nullable
  AngelScriptBreakStatement getBreakStatement();

  @Nullable
  AngelScriptContinueStatement getContinueStatement();

  @Nullable
  AngelScriptExpressionStatement getExpressionStatement();

  @Nullable
  AngelScriptFallthroughStatement getFallthroughStatement();

  @Nullable
  AngelScriptForStatement getForStatement();

  @Nullable
  AngelScriptForeachStatement getForeachStatement();

  @Nullable
  AngelScriptIfStatement getIfStatement();

  @Nullable
  AngelScriptReturnStatement getReturnStatement();

  @Nullable
  AngelScriptStatementBlock getStatementBlock();

  @Nullable
  AngelScriptSwitchStatement getSwitchStatement();

  @Nullable
  AngelScriptVariableDecl getVariableDecl();

  @Nullable
  AngelScriptWhileStatement getWhileStatement();

}
