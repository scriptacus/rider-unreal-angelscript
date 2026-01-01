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

public class AngelScriptUnaryExprImpl extends AngelScriptExprImpl implements AngelScriptUnaryExpr {

  public AngelScriptUnaryExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull AngelScriptVisitor visitor) {
    visitor.visitUnaryExpr(this);
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

}
