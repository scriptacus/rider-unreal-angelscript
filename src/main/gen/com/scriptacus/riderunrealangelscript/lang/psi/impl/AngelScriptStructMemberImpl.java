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

public class AngelScriptStructMemberImpl extends ASTWrapperPsiElement implements AngelScriptStructMember {

  public AngelScriptStructMemberImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull AngelScriptVisitor visitor) {
    visitor.visitStructMember(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AngelScriptVisitor) accept((AngelScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AngelScriptClassMethodDecl getClassMethodDecl() {
    return findChildByClass(AngelScriptClassMethodDecl.class);
  }

  @Override
  @Nullable
  public AngelScriptConstructorDecl getConstructorDecl() {
    return findChildByClass(AngelScriptConstructorDecl.class);
  }

  @Override
  @Nullable
  public AngelScriptDestructorDecl getDestructorDecl() {
    return findChildByClass(AngelScriptDestructorDecl.class);
  }

  @Override
  @Nullable
  public AngelScriptStructPropertyDecl getStructPropertyDecl() {
    return findChildByClass(AngelScriptStructPropertyDecl.class);
  }

}
