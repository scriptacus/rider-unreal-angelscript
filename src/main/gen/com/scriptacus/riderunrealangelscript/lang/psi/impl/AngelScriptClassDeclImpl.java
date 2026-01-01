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

public class AngelScriptClassDeclImpl extends ASTWrapperPsiElement implements AngelScriptClassDecl {

  public AngelScriptClassDeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull AngelScriptVisitor visitor) {
    visitor.visitClassDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AngelScriptVisitor) accept((AngelScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AngelScriptClassBody getClassBody() {
    return findChildByClass(AngelScriptClassBody.class);
  }

  @Override
  @Nullable
  public AngelScriptClassInheritance getClassInheritance() {
    return findChildByClass(AngelScriptClassInheritance.class);
  }

  @Override
  @Nullable
  public AngelScriptUclassMacro getUclassMacro() {
    return findChildByClass(AngelScriptUclassMacro.class);
  }

}
