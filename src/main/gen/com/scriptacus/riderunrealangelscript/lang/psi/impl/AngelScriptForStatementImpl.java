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

public class AngelScriptForStatementImpl extends ASTWrapperPsiElement implements AngelScriptForStatement {

  public AngelScriptForStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull AngelScriptVisitor visitor) {
    visitor.visitForStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AngelScriptVisitor) accept((AngelScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AngelScriptExpr getExpr() {
    return findChildByClass(AngelScriptExpr.class);
  }

  @Override
  @Nullable
  public AngelScriptForInit getForInit() {
    return findChildByClass(AngelScriptForInit.class);
  }

  @Override
  @Nullable
  public AngelScriptForUpdate getForUpdate() {
    return findChildByClass(AngelScriptForUpdate.class);
  }

  @Override
  @NotNull
  public AngelScriptStatement getStatement() {
    return findNotNullChildByClass(AngelScriptStatement.class);
  }

}
