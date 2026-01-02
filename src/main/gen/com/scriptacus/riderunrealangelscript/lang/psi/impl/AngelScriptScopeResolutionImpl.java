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

public class AngelScriptScopeResolutionImpl extends ASTWrapperPsiElement implements AngelScriptScopeResolution {

  public AngelScriptScopeResolutionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull AngelScriptVisitor visitor) {
    visitor.visitScopeResolution(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AngelScriptVisitor) accept((AngelScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public AngelScriptIdentifierReference getIdentifierReference() {
    return findNotNullChildByClass(AngelScriptIdentifierReference.class);
  }

  @Override
  @NotNull
  public AngelScriptScopedIdentifier getScopedIdentifier() {
    return findNotNullChildByClass(AngelScriptScopedIdentifier.class);
  }

  @Override
  @NotNull
  public AngelScriptScopedIdentifier getQualifier() {
    return getScopedIdentifier();
  }

  @Override
  @NotNull
  public AngelScriptIdentifierReference getMemberName() {
    return getIdentifierReference();
  }

}
