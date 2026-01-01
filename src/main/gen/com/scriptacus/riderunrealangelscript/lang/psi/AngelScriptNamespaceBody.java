// This is a generated file. Not intended for manual editing.
package com.scriptacus.riderunrealangelscript.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AngelScriptNamespaceBody extends PsiElement {

  @NotNull
  List<AngelScriptAccessDecl> getAccessDeclList();

  @NotNull
  List<AngelScriptAssetDecl> getAssetDeclList();

  @NotNull
  List<AngelScriptClassDecl> getClassDeclList();

  @NotNull
  List<AngelScriptDelegateDecl> getDelegateDeclList();

  @NotNull
  List<AngelScriptEnumDecl> getEnumDeclList();

  @NotNull
  List<AngelScriptEventDecl> getEventDeclList();

  @NotNull
  List<AngelScriptGlobalFunctionDecl> getGlobalFunctionDeclList();

  @NotNull
  List<AngelScriptNamespaceDecl> getNamespaceDeclList();

  @NotNull
  List<AngelScriptStructDecl> getStructDeclList();

  @NotNull
  List<AngelScriptVariableDecl> getVariableDeclList();

}
