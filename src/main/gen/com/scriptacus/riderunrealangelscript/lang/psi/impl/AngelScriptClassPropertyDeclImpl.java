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

public class AngelScriptClassPropertyDeclImpl extends ASTWrapperPsiElement implements AngelScriptClassPropertyDecl {

  public AngelScriptClassPropertyDeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull AngelScriptVisitor visitor) {
    visitor.visitClassPropertyDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AngelScriptVisitor) accept((AngelScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<AngelScriptAccessSpecifier> getAccessSpecifierList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AngelScriptAccessSpecifier.class);
  }

  @Override
  @Nullable
  public AngelScriptUpropertyMacro getUpropertyMacro() {
    return findChildByClass(AngelScriptUpropertyMacro.class);
  }

  @Override
  @NotNull
  public AngelScriptVariableDecl getVariableDecl() {
    return findNotNullChildByClass(AngelScriptVariableDecl.class);
  }

}
