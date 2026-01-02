// This is a generated file. Not intended for manual editing.
package com.scriptacus.riderunrealangelscript.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface AngelScriptScopeResolution extends PsiElement {

  @NotNull
  AngelScriptIdentifierReference getIdentifierReference();

  @NotNull
  AngelScriptScopedIdentifier getScopedIdentifier();

  @NotNull
  AngelScriptScopedIdentifier getQualifier();

  @NotNull
  AngelScriptIdentifierReference getMemberName();

}
