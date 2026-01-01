// This is a generated file. Not intended for manual editing.
package com.scriptacus.riderunrealangelscript.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptTypes.*;
import com.scriptacus.riderunrealangelscript.lang.psi.*;

public class AngelScriptPrimaryExprImpl extends AngelScriptExprImpl implements AngelScriptPrimaryExpr {

  public AngelScriptPrimaryExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull AngelScriptVisitor visitor) {
    visitor.visitPrimaryExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AngelScriptVisitor) accept((AngelScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AngelScriptCastExpression getCastExpression() {
    return findChildByClass(AngelScriptCastExpression.class);
  }

  @Override
  @Nullable
  public AngelScriptConstructorCall getConstructorCall() {
    return findChildByClass(AngelScriptConstructorCall.class);
  }

  @Override
  @Nullable
  public AngelScriptExpr getExpr() {
    return findChildByClass(AngelScriptExpr.class);
  }

  @Override
  @Nullable
  public AngelScriptFstringLiteral getFstringLiteral() {
    return findChildByClass(AngelScriptFstringLiteral.class);
  }

  @Override
  @Nullable
  public AngelScriptNamestringLiteral getNamestringLiteral() {
    return findChildByClass(AngelScriptNamestringLiteral.class);
  }

  @Override
  @Nullable
  public AngelScriptScopedIdentifier getScopedIdentifier() {
    return findChildByClass(AngelScriptScopedIdentifier.class);
  }

  @Override
  @Nullable
  public AngelScriptStringLiteral getStringLiteral() {
    return findChildByClass(AngelScriptStringLiteral.class);
  }

}
