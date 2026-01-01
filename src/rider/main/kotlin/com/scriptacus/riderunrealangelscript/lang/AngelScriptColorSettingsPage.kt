package com.scriptacus.riderunrealangelscript.lang

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class AngelScriptColorSettingsPage : ColorSettingsPage {

    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter = AngelScriptSyntaxHighlighter()

    override fun getDemoText(): String {
        return """
                <line_comment>// AngelScript Syntax Highlighting Demo</line_comment>
                <block_comment>/* Block comment example */</block_comment>

                <preprocessor>#if EDITOR</preprocessor>
                import void DebugLog(FString Message);
                <preprocessor>#endif</preprocessor>

                <unreal_macro>UCLASS</unreal_macro><parentheses>(</parentheses><parentheses>)</parentheses>
                <keyword>class</keyword> AMyActor <operator>:</operator> AActor
                <braces>{</braces>
                    <unreal_macro>UPROPERTY</unreal_macro><parentheses>(</parentheses>BlueprintReadWrite<parentheses>)</parentheses>
                    <type_keyword>int32</type_keyword> Health <operator>=</operator> <number>100</number><semicolon>;</semicolon>

                    <unreal_macro>UPROPERTY</unreal_macro><parentheses>(</parentheses><parentheses>)</parentheses>
                    <namestring>n"PlayerName"</namestring> PlayerTag<semicolon>;</semicolon>

                    <access>private</access> <type_keyword>float</type_keyword> Speed <operator>=</operator> <number>3.14f</number><semicolon>;</semicolon>
                    <type_keyword>bool</type_keyword> IsAlive <operator>=</operator> <boolean>true</boolean><semicolon>;</semicolon>
                    AActor Killer <operator>=</operator> <null>nullptr</null><semicolon>;</semicolon>

                    <unreal_macro>UFUNCTION</unreal_macro><parentheses>(</parentheses>BlueprintCallable<parentheses>)</parentheses>
                    <type_keyword>void</type_keyword> TakeDamage<parentheses>(</parentheses><type_keyword>int32</type_keyword> Amount<parentheses>)</parentheses>
                    <braces>{</braces>
                        Health <operator>-=</operator> Amount<semicolon>;</semicolon>

                        <fstring>f"Player took {Amount} damage! Health: {Health}"</fstring><dot>.</dot>Print<parentheses>(</parentheses><parentheses>)</parentheses><semicolon>;</semicolon>

                        <keyword>if</keyword> <parentheses>(</parentheses>Health <operator><=</operator> <number>0</number><parentheses>)</parentheses>
                        <braces>{</braces>
                            Die<parentheses>(</parentheses><parentheses>)</parentheses><semicolon>;</semicolon>
                        <braces>}</braces>
                    <braces>}</braces>

                    <type_keyword>void</type_keyword> Die<parentheses>(</parentheses><parentheses>)</parentheses>
                    <braces>{</braces>
                        Print<parentheses>(</parentheses><string>"Player died!"</string><parentheses>)</parentheses><semicolon>;</semicolon>
                    <braces>}</braces>
                <braces>}</braces>
                """.trimIndent()
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> {
        return mapOf(
            "keyword" to AngelScriptSyntaxHighlighter.KEYWORD,
            "type_keyword" to AngelScriptSyntaxHighlighter.TYPE_KEYWORD,
            "access" to AngelScriptSyntaxHighlighter.ACCESS_MODIFIER,
            "number" to AngelScriptSyntaxHighlighter.NUMBER,
            "string" to AngelScriptSyntaxHighlighter.STRING,
            "fstring" to AngelScriptSyntaxHighlighter.FSTRING,
            "namestring" to AngelScriptSyntaxHighlighter.NAMESTRING,
            "boolean" to AngelScriptSyntaxHighlighter.BOOLEAN,
            "null" to AngelScriptSyntaxHighlighter.NULL_KEYWORD,
            "line_comment" to AngelScriptSyntaxHighlighter.LINE_COMMENT,
            "block_comment" to AngelScriptSyntaxHighlighter.BLOCK_COMMENT,
            "operator" to AngelScriptSyntaxHighlighter.OPERATOR,
            "parentheses" to AngelScriptSyntaxHighlighter.PARENTHESES,
            "braces" to AngelScriptSyntaxHighlighter.BRACES,
            "brackets" to AngelScriptSyntaxHighlighter.BRACKETS,
            "dot" to AngelScriptSyntaxHighlighter.DOT,
            "comma" to AngelScriptSyntaxHighlighter.COMMA,
            "semicolon" to AngelScriptSyntaxHighlighter.SEMICOLON,
            "unreal_macro" to AngelScriptSyntaxHighlighter.UNREAL_MACRO,
            "preprocessor" to AngelScriptSyntaxHighlighter.PREPROCESSOR
        )
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "AngelScript"

    companion object {
        private val DESCRIPTORS = arrayOf(
            // Keywords
            AttributesDescriptor("Keyword", AngelScriptSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Type Keyword", AngelScriptSyntaxHighlighter.TYPE_KEYWORD),
            AttributesDescriptor("Access Modifier", AngelScriptSyntaxHighlighter.ACCESS_MODIFIER),

            // Literals
            AttributesDescriptor("Number", AngelScriptSyntaxHighlighter.NUMBER),
            AttributesDescriptor("String", AngelScriptSyntaxHighlighter.STRING),
            AttributesDescriptor("F-String", AngelScriptSyntaxHighlighter.FSTRING),
            AttributesDescriptor("Name String", AngelScriptSyntaxHighlighter.NAMESTRING),
            AttributesDescriptor("Boolean", AngelScriptSyntaxHighlighter.BOOLEAN),
            AttributesDescriptor("Null Keyword", AngelScriptSyntaxHighlighter.NULL_KEYWORD),

            // Comments
            AttributesDescriptor("Line Comment", AngelScriptSyntaxHighlighter.LINE_COMMENT),
            AttributesDescriptor("Block Comment", AngelScriptSyntaxHighlighter.BLOCK_COMMENT),

            // Operators and Punctuation
            AttributesDescriptor("Operator", AngelScriptSyntaxHighlighter.OPERATOR),
            AttributesDescriptor("Parentheses", AngelScriptSyntaxHighlighter.PARENTHESES),
            AttributesDescriptor("Braces", AngelScriptSyntaxHighlighter.BRACES),
            AttributesDescriptor("Brackets", AngelScriptSyntaxHighlighter.BRACKETS),
            AttributesDescriptor("Dot", AngelScriptSyntaxHighlighter.DOT),
            AttributesDescriptor("Comma", AngelScriptSyntaxHighlighter.COMMA),
            AttributesDescriptor("Semicolon", AngelScriptSyntaxHighlighter.SEMICOLON),

            // Unreal Specific
            AttributesDescriptor("Unreal Macro", AngelScriptSyntaxHighlighter.UNREAL_MACRO),
            AttributesDescriptor("Preprocessor Directive", AngelScriptSyntaxHighlighter.PREPROCESSOR)
        )
    }
}
