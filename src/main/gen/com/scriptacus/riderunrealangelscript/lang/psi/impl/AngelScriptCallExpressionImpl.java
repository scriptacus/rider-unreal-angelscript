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

public class AngelScriptCallExpressionImpl extends ASTWrapperPsiElement implements AngelScriptCallExpression {

  public AngelScriptCallExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull AngelScriptVisitor visitor) {
    visitor.visitCallExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AngelScriptVisitor) accept((AngelScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AngelScriptArgumentList getArgumentList() {
    return findChildByClass(AngelScriptArgumentList.class);
  }

  @Override
  @Nullable
  public AngelScriptScopedIdentifier getScopedIdentifier() {
    return findChildByClass(AngelScriptScopedIdentifier.class);
  }

  @Override
  @Nullable
  public AngelScriptTypename getTypename() {
    return findChildByClass(AngelScriptTypename.class);
  }

}
