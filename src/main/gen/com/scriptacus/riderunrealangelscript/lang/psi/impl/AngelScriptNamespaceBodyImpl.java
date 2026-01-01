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

public class AngelScriptNamespaceBodyImpl extends ASTWrapperPsiElement implements AngelScriptNamespaceBody {

  public AngelScriptNamespaceBodyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull AngelScriptVisitor visitor) {
    visitor.visitNamespaceBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AngelScriptVisitor) accept((AngelScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<AngelScriptAccessDecl> getAccessDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AngelScriptAccessDecl.class);
  }

  @Override
  @NotNull
  public List<AngelScriptAssetDecl> getAssetDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AngelScriptAssetDecl.class);
  }

  @Override
  @NotNull
  public List<AngelScriptClassDecl> getClassDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AngelScriptClassDecl.class);
  }

  @Override
  @NotNull
  public List<AngelScriptDelegateDecl> getDelegateDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AngelScriptDelegateDecl.class);
  }

  @Override
  @NotNull
  public List<AngelScriptEnumDecl> getEnumDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AngelScriptEnumDecl.class);
  }

  @Override
  @NotNull
  public List<AngelScriptEventDecl> getEventDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AngelScriptEventDecl.class);
  }

  @Override
  @NotNull
  public List<AngelScriptGlobalFunctionDecl> getGlobalFunctionDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AngelScriptGlobalFunctionDecl.class);
  }

  @Override
  @NotNull
  public List<AngelScriptNamespaceDecl> getNamespaceDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AngelScriptNamespaceDecl.class);
  }

  @Override
  @NotNull
  public List<AngelScriptStructDecl> getStructDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AngelScriptStructDecl.class);
  }

  @Override
  @NotNull
  public List<AngelScriptVariableDecl> getVariableDeclList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, AngelScriptVariableDecl.class);
  }

}
