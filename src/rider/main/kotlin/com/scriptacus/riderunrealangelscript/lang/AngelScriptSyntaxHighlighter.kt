package com.scriptacus.riderunrealangelscript.lang

import com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.tree.IElementType

class AngelScriptSyntaxHighlighter : SyntaxHighlighter {

    override fun getHighlightingLexer(): Lexer = AngelScriptLexerAdapter()

    override fun getTokenHighlights(type: IElementType): Array<TextAttributesKey> {
        return when {
            // Comments
            type == AngelScriptTypes.COMMENT -> pack(LINE_COMMENT)
            // Numbers
            type == AngelScriptTypes.NUMBER -> pack(NUMBER)
            // Strings
            type == AngelScriptTypes.STRING -> pack(STRING)
            // F-strings
            isFString(type) -> pack(FSTRING)
            // Name strings
            isNameString(type) -> pack(NAMESTRING)
            // Boolean literals
            isBooleanLiteral(type) -> pack(BOOLEAN)
            // Null literal
            type == AngelScriptTypes.NULLPTR -> pack(NULL_KEYWORD)
            // Unreal Engine macros
            isUnrealMacro(type) -> pack(UNREAL_MACRO)
            // Preprocessor directives
            isPreprocessor(type) -> pack(PREPROCESSOR)
            // Access modifiers
            isAccessModifier(type) -> pack(ACCESS_MODIFIER)
            // Primitive type keywords
            isPrimitiveType(type) -> pack(TYPE_KEYWORD)
            // All other keywords
            isKeyword(type) -> pack(KEYWORD)
            // Operators
            isOperator(type) -> pack(OPERATOR)
            // Parentheses
            isParenthesis(type) -> pack(PARENTHESES)
            // Braces
            isBrace(type) -> pack(BRACES)
            // Brackets
            isBracket(type) -> pack(BRACKETS)
            // Dot and scope operators
            isDotOrScope(type) -> pack(DOT)
            // Semicolon
            type == AngelScriptTypes.END_STATEMENT -> pack(SEMICOLON)
            // Comma
            type == AngelScriptTypes.LIST_SEPARATOR -> pack(COMMA)
            // Identifiers
            type == AngelScriptTypes.IDENTIFIER -> pack(DefaultLanguageHighlighterColors.IDENTIFIER)
            // Bad characters
            type == AngelScriptTypes.UNKNOWN -> pack(HighlighterColors.BAD_CHARACTER)
            else -> TextAttributesKey.EMPTY_ARRAY
        }
    }

    companion object {
        // Keywords
        val KEYWORD = createTextAttributesKey("ANGELSCRIPT_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val TYPE_KEYWORD = createTextAttributesKey("ANGELSCRIPT_TYPE_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val ACCESS_MODIFIER = createTextAttributesKey("ANGELSCRIPT_ACCESS_MODIFIER", DefaultLanguageHighlighterColors.KEYWORD)

        // Literals
        val NUMBER = createTextAttributesKey("ANGELSCRIPT_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val STRING = createTextAttributesKey("ANGELSCRIPT_STRING", DefaultLanguageHighlighterColors.STRING)
        val FSTRING = createTextAttributesKey("ANGELSCRIPT_FSTRING", DefaultLanguageHighlighterColors.STRING)
        val NAMESTRING = createTextAttributesKey("ANGELSCRIPT_NAMESTRING", DefaultLanguageHighlighterColors.STRING)
        val BOOLEAN = createTextAttributesKey("ANGELSCRIPT_BOOLEAN", DefaultLanguageHighlighterColors.KEYWORD)
        val NULL_KEYWORD = createTextAttributesKey("ANGELSCRIPT_NULL", DefaultLanguageHighlighterColors.KEYWORD)

        // Comments
        val LINE_COMMENT = createTextAttributesKey("ANGELSCRIPT_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BLOCK_COMMENT = createTextAttributesKey("ANGELSCRIPT_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)

        // Operators and punctuation
        val OPERATOR = createTextAttributesKey("ANGELSCRIPT_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val PARENTHESES = createTextAttributesKey("ANGELSCRIPT_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
        val BRACES = createTextAttributesKey("ANGELSCRIPT_BRACES", DefaultLanguageHighlighterColors.BRACES)
        val BRACKETS = createTextAttributesKey("ANGELSCRIPT_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        val DOT = createTextAttributesKey("ANGELSCRIPT_DOT", DefaultLanguageHighlighterColors.DOT)
        val COMMA = createTextAttributesKey("ANGELSCRIPT_COMMA", DefaultLanguageHighlighterColors.COMMA)
        val SEMICOLON = createTextAttributesKey("ANGELSCRIPT_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)

        // Unreal specific
        val UNREAL_MACRO = createTextAttributesKey("ANGELSCRIPT_UNREAL_MACRO", DefaultLanguageHighlighterColors.METADATA)
        val PREPROCESSOR = createTextAttributesKey("ANGELSCRIPT_PREPROCESSOR", DefaultLanguageHighlighterColors.METADATA)

        private fun isUnrealMacro(type: IElementType): Boolean {
            return type == AngelScriptTypes.UPROPERTY ||
                    type == AngelScriptTypes.UFUNCTION ||
                    type == AngelScriptTypes.UCLASS ||
                    type == AngelScriptTypes.USTRUCT ||
                    type == AngelScriptTypes.UENUM ||
                    type == AngelScriptTypes.UMETA
        }

        private fun isPreprocessor(type: IElementType): Boolean {
            return type == AngelScriptTypes.HASH ||
                    type == AngelScriptTypes.PP_IF ||
                    type == AngelScriptTypes.PP_ELIF ||
                    type == AngelScriptTypes.PP_ELSE ||
                    type == AngelScriptTypes.PP_ENDIF ||
                    type == AngelScriptTypes.PP_DEFINE ||
                    type == AngelScriptTypes.PP_UNDEF ||
                    type == AngelScriptTypes.PP_EDITOR ||
                    type == AngelScriptTypes.PP_TEST
        }

        private fun isBooleanLiteral(type: IElementType): Boolean {
            return type == AngelScriptTypes.TRUE || type == AngelScriptTypes.FALSE
        }

        private fun isAccessModifier(type: IElementType): Boolean {
            return type == AngelScriptTypes.PRIVATE ||
                    type == AngelScriptTypes.PROTECTED ||
                    type == AngelScriptTypes.PUBLIC ||
                    type == AngelScriptTypes.ACCESS
        }

        private fun isKeyword(type: IElementType): Boolean {
            // Control flow keywords
            if (type == AngelScriptTypes.IF ||
                type == AngelScriptTypes.ELSE ||
                type == AngelScriptTypes.FOR ||
                type == AngelScriptTypes.WHILE ||
                type == AngelScriptTypes.SWITCH ||
                type == AngelScriptTypes.CASE ||
                type == AngelScriptTypes.DEFAULT ||
                type == AngelScriptTypes.BREAK ||
                type == AngelScriptTypes.CONTINUE ||
                type == AngelScriptTypes.RETURN ||
                type == AngelScriptTypes.FALLTHROUGH) {
                return true
            }

            // Declaration keywords
            if (type == AngelScriptTypes.CLASS ||
                type == AngelScriptTypes.STRUCT ||
                type == AngelScriptTypes.ENUM ||
                type == AngelScriptTypes.DELEGATE ||
                type == AngelScriptTypes.EVENT ||
                type == AngelScriptTypes.NAMESPACE) {
                return true
            }

            // Modifier keywords
            if (type == AngelScriptTypes.CONST ||
                type == AngelScriptTypes.FINAL ||
                type == AngelScriptTypes.OVERRIDE ||
                type == AngelScriptTypes.PROPERTY ||
                type == AngelScriptTypes.MIXIN ||
                type == AngelScriptTypes.LOCAL) {
                return true
            }

            // Special keywords
            if (type == AngelScriptTypes.THIS ||
                type == AngelScriptTypes.CAST ||
                type == AngelScriptTypes.ASSET ||
                type == AngelScriptTypes.OF ||
                type == AngelScriptTypes.FROM ||
                type == AngelScriptTypes.IN ||
                type == AngelScriptTypes.OUT ||
                type == AngelScriptTypes.INOUT) {
                return true
            }

            return false
        }

        private fun isPrimitiveType(type: IElementType): Boolean {
            return type == AngelScriptTypes.VOID ||
                    type == AngelScriptTypes.BOOL ||
                    type == AngelScriptTypes.INT ||
                    type == AngelScriptTypes.INT8 ||
                    type == AngelScriptTypes.INT16 ||
                    type == AngelScriptTypes.INT32 ||
                    type == AngelScriptTypes.INT64 ||
                    type == AngelScriptTypes.UINT ||
                    type == AngelScriptTypes.UINT8 ||
                    type == AngelScriptTypes.UINT16 ||
                    type == AngelScriptTypes.UINT32 ||
                    type == AngelScriptTypes.UINT64 ||
                    type == AngelScriptTypes.FLOAT ||
                    type == AngelScriptTypes.FLOAT32 ||
                    type == AngelScriptTypes.FLOAT64 ||
                    type == AngelScriptTypes.DOUBLE ||
                    type == AngelScriptTypes.AUTO
        }

        private fun isFString(type: IElementType): Boolean {
            return type == AngelScriptTypes.FSTRING_BEGIN ||
                    type == AngelScriptTypes.FSTRING_END ||
                    type == AngelScriptTypes.FSTRING_TEXT ||
                    type == AngelScriptTypes.FSTRING_ESCAPED_LBRACE ||
                    type == AngelScriptTypes.FSTRING_ESCAPED_RBRACE ||
                    type == AngelScriptTypes.FSTRING_EXPR_BEGIN ||
                    type == AngelScriptTypes.FSTRING_EXPR_END
        }

        private fun isNameString(type: IElementType): Boolean {
            return type == AngelScriptTypes.NAMESTRING_BEGIN ||
                    type == AngelScriptTypes.NAMESTRING_END ||
                    type == AngelScriptTypes.NAMESTRING_TEXT
        }

        private fun isOperator(type: IElementType): Boolean {
            return type == AngelScriptTypes.ASSIGNMENT ||
                    // Arithmetic operators
                    type == AngelScriptTypes.PLUS ||
                    type == AngelScriptTypes.MINUS ||
                    type == AngelScriptTypes.STAR ||
                    type == AngelScriptTypes.SLASH ||
                    type == AngelScriptTypes.PERCENT ||
                    type == AngelScriptTypes.INC ||
                    type == AngelScriptTypes.DEC ||
                    // Comparison operators
                    type == AngelScriptTypes.EQUAL ||
                    type == AngelScriptTypes.NOT_EQUAL ||
                    type == AngelScriptTypes.LESS_THAN ||
                    type == AngelScriptTypes.GREATER_THAN ||
                    type == AngelScriptTypes.LESS_THAN_OR_EQUAL ||
                    type == AngelScriptTypes.GREATER_THAN_OR_EQUAL ||
                    // Logical operators
                    type == AngelScriptTypes.AND ||
                    type == AngelScriptTypes.OR ||
                    type == AngelScriptTypes.XOR ||
                    type == AngelScriptTypes.NOT ||
                    // Bitwise operators
                    type == AngelScriptTypes.AMP ||
                    type == AngelScriptTypes.BIT_OR ||
                    type == AngelScriptTypes.BIT_XOR ||
                    type == AngelScriptTypes.BIT_NOT ||
                    type == AngelScriptTypes.BIT_SHIFT_LEFT ||
                    // Compound assignment operators
                    type == AngelScriptTypes.ADD_ASSIGN ||
                    type == AngelScriptTypes.SUB_ASSIGN ||
                    type == AngelScriptTypes.MUL_ASSIGN ||
                    type == AngelScriptTypes.DIV_ASSIGN ||
                    type == AngelScriptTypes.MOD_ASSIGN ||
                    type == AngelScriptTypes.AND_ASSIGN ||
                    type == AngelScriptTypes.OR_ASSIGN ||
                    type == AngelScriptTypes.XOR_ASSIGN ||
                    type == AngelScriptTypes.SHIFT_LEFT_ASSIGN ||
                    type == AngelScriptTypes.SHIFT_RIGHT_L_ASSIGN ||
                    type == AngelScriptTypes.SHIFT_RIGHT_A_ASSIGN ||
                    // Ternary operator
                    type == AngelScriptTypes.QUESTION ||
                    type == AngelScriptTypes.COLON ||
                    // Variadic operator
                    type == AngelScriptTypes.VARIADIC
        }

        private fun isParenthesis(type: IElementType): Boolean {
            return type == AngelScriptTypes.OPEN_PARENTHESIS ||
                    type == AngelScriptTypes.CLOSE_PARENTHESIS
        }

        private fun isBrace(type: IElementType): Boolean {
            return type == AngelScriptTypes.START_STATEMENT_BLOCK ||
                    type == AngelScriptTypes.END_STATEMENT_BLOCK
        }

        private fun isBracket(type: IElementType): Boolean {
            return type == AngelScriptTypes.OPEN_BRACKET ||
                    type == AngelScriptTypes.CLOSE_BRACKET
        }

        private fun isDotOrScope(type: IElementType): Boolean {
            return type == AngelScriptTypes.DOT ||
                    type == AngelScriptTypes.SCOPE
        }

        private fun pack(key: TextAttributesKey): Array<TextAttributesKey> {
            return arrayOf(key)
        }
    }
}
