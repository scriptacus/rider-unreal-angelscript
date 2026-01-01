// This is a generated file. Not intended for manual editing.
package com.scriptacus.riderunrealangelscript.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.scriptacus.riderunrealangelscript.lang.psi.*;

public class AngelScriptStatementImpl extends ASTWrapperPsiElement implements AngelScriptStatement {

  public AngelScriptStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull AngelScriptVisitor visitor) {
    visitor.visitStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AngelScriptVisitor) accept((AngelScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AngelScriptBreakStatement getBreakStatement() {
    return findChildByClass(AngelScriptBreakStatement.class);
  }

  @Override
  @Nullable
  public AngelScriptContinueStatement getContinueStatement() {
    return findChildByClass(AngelScriptContinueStatement.class);
  }

  @Override
  @Nullable
  public AngelScriptExpressionStatement getExpressionStatement() {
    return findChildByClass(AngelScriptExpressionStatement.class);
  }

  @Override
  @Nullable
  public AngelScriptFallthroughStatement getFallthroughStatement() {
    return findChildByClass(AngelScriptFallthroughStatement.class);
  }

  @Override
  @Nullable
  public AngelScriptForStatement getForStatement() {
    return findChildByClass(AngelScriptForStatement.class);
  }

  @Override
  @Nullable
  public AngelScriptForeachStatement getForeachStatement() {
    return findChildByClass(AngelScriptForeachStatement.class);
  }

  @Override
  @Nullable
  public AngelScriptIfStatement getIfStatement() {
    return findChildByClass(AngelScriptIfStatement.class);
  }

  @Override
  @Nullable
  public AngelScriptReturnStatement getReturnStatement() {
    return findChildByClass(AngelScriptReturnStatement.class);
  }

  @Override
  @Nullable
  public AngelScriptStatementBlock getStatementBlock() {
    return findChildByClass(AngelScriptStatementBlock.class);
  }

  @Override
  @Nullable
  public AngelScriptSwitchStatement getSwitchStatement() {
    return findChildByClass(AngelScriptSwitchStatement.class);
  }

  @Override
  @Nullable
  public AngelScriptVariableDecl getVariableDecl() {
    return findChildByClass(AngelScriptVariableDecl.class);
  }

  @Override
  @Nullable
  public AngelScriptWhileStatement getWhileStatement() {
    return findChildByClass(AngelScriptWhileStatement.class);
  }

}
