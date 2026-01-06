package com.scriptacus.riderunrealangelscript.lang

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.impl.LineMarkersPass
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.elementType
import com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptClassMethodDecl
import com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptConstructorDecl
import com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptGlobalFunctionDecl
import com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptTypes

/**
 * Provides method separator line markers for AngelScript functions and methods.
 *
 * This provider adds visual separators between methods/functions in the editor gutter
 * when the "Show method separators" setting is enabled in Settings | Editor | General | Appearance.
 */
class AngelScriptMethodSeparatorProvider : LineMarkerProvider {
    private val daemonSettings = DaemonCodeAnalyzerSettings.getInstance()
    private val colorsManager = EditorColorsManager.getInstance()

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        // Only show separators if the setting is enabled
        if (!daemonSettings.SHOW_METHOD_SEPARATORS) return null

        // Only process leaf elements (performance requirement)
        if (element.firstChild != null) return null

        // Check if we're at the start of a method/constructor/function
        // and get the declaration element (CLASS_MEMBER or GLOBAL_FUNCTION_DECL)
        val declaration = element.getContainingDeclaration() ?: return null

        // Verify this element is at the start of the declaration
        if (!element.isAtStartOfDeclaration(declaration)) return null

        // Find the previous sibling declaration
        val prevDeclaration = declaration.getPrevSiblingDeclaration() ?: return null

        // Only show separator if the previous declaration is multi-line
        if (!prevDeclaration.isMultiLine()) return null

        // Create the separator line marker at this leaf element
        return LineMarkersPass.createMethodSeparatorLineMarker(element, colorsManager)
    }

    /**
     * Finds the declaration container that this element belongs to.
     * Returns CLASS_MEMBER for methods/constructors, or GLOBAL_FUNCTION_DECL for global functions.
     */
    private fun PsiElement.getContainingDeclaration(): PsiElement? {
        var current = this.parent
        while (current != null) {
            when {
                // For class methods/constructors: return the CLASS_MEMBER wrapper
                current is AngelScriptClassMethodDecl || current is AngelScriptConstructorDecl -> {
                    return current.parent // Return CLASS_MEMBER
                }
                // For global functions: return the declaration itself
                current is AngelScriptGlobalFunctionDecl -> {
                    return current
                }
            }
            current = current.parent
        }
        return null
    }

    /**
     * Check if this leaf element is the very first leaf token of its containing declaration.
     */
    private fun PsiElement.isAtStartOfDeclaration(declaration: PsiElement): Boolean {
        // Find the first leaf element in the declaration
        var current = declaration.firstChild
        while (current != null) {
            // Skip whitespace
            if (current is PsiWhiteSpace) {
                current = current.nextSibling
                continue
            }

            // If this element has children, recurse into it to find the first leaf
            if (current.firstChild != null) {
                current = current.firstChild
                continue
            }

            // Found the first leaf - check if it's us
            return current == this
        }
        return false
    }

    /**
     * Checks if a declaration spans multiple lines.
     * We only show separators for multi-line functions to reduce visual clutter.
     */
    private fun PsiElement.isMultiLine(): Boolean {
        return this.text?.contains('\n') == true
    }

    /**
     * Gets the previous sibling declaration (CLASS_MEMBER or GLOBAL_FUNCTION_DECL).
     * Skips whitespace, comments, and semicolons.
     */
    private fun PsiElement.getPrevSiblingDeclaration(): PsiElement? {
        var sibling = this.prevSibling
        while (sibling != null) {
            when {
                sibling is PsiWhiteSpace -> sibling = sibling.prevSibling
                sibling is PsiComment -> sibling = sibling.prevSibling
                sibling.elementType == AngelScriptTypes.END_STATEMENT -> sibling = sibling.prevSibling
                // Found a non-whitespace, non-comment sibling
                else -> return sibling
            }
        }
        return null
    }
}