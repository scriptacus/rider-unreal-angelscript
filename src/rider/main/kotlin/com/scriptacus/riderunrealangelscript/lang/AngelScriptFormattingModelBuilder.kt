package com.scriptacus.riderunrealangelscript.lang

import com.intellij.formatting.*
import com.intellij.psi.codeStyle.CodeStyleSettings

/**
 * Formatting model builder for AngelScript language.
 *
 * Provides code formatting support including:
 * - Indentation for class bodies, function bodies, and statement blocks
 * - Spacing rules for operators, keywords, and delimiters
 * - Smart Enter handling for auto-indentation
 *
 * Phase 1: Basic indentation support
 */
class AngelScriptFormattingModelBuilder : FormattingModelBuilder {

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val codeStyleSettings = formattingContext.codeStyleSettings
        val rootBlock = AngelScriptBlock(
            node = formattingContext.node,
            wrap = Wrap.createWrap(WrapType.NONE, false),
            alignment = Alignment.createAlignment(),
            spacingBuilder = null // Phase 2 will add spacing rules
        )

        return FormattingModelProvider.createFormattingModelForPsiFile(
            formattingContext.containingFile,
            rootBlock,
            codeStyleSettings
        )
    }
}
