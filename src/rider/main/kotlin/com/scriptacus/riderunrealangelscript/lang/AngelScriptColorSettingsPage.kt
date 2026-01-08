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
                <line_comment>// Semantic highlighting shown below requires Unreal Engine connection in actual code.</line_comment>

                <sem_actor>AWeapon</sem_actor> <sem_global_var>GlobalWeaponInstance</sem_global_var><semicolon>;</semicolon>

                <keyword>const</keyword> <sem_primitive>float</sem_primitive> <sem_global_accessor>GetGlobalDamageMultiplier</sem_global_accessor><parentheses>(</parentheses><parentheses>)</parentheses>
                <braces>{</braces>
                    <keyword>return</keyword> <number>1.5f</number><semicolon>;</semicolon>
                <braces>}</braces>

                <preprocessor>#if EDITOR</preprocessor>
                <type_keyword>void</type_keyword> <sem_global_func>DebugLog</sem_global_func><parentheses>(</parentheses><sem_struct>FString</sem_struct> <sem_param>Message</sem_param><parentheses>)</parentheses> <braces>{</braces> <sem_global_func>Print</sem_global_func><parentheses>(</parentheses><sem_param>Message</sem_param><parentheses>)</parentheses><semicolon>;</semicolon> <braces>}</braces>
                <preprocessor>#endif</preprocessor>

                <keyword>namespace</keyword> <sem_namespace>Gameplay</sem_namespace>
                <braces>{</braces>
                    <unreal_macro>UENUM</unreal_macro><parentheses>(</parentheses><parentheses>)</parentheses>
                    <keyword>enum</keyword> <sem_typename>EEquipState</sem_typename>
                    <braces>{</braces>
                        None<comma>,</comma>
                        Equipping<comma>,</comma>
                        Equipped
                    <braces>}</braces>
                <braces>}</braces>

                <unreal_macro>USTRUCT</unreal_macro><parentheses>(</parentheses><parentheses>)</parentheses>
                <keyword>struct</keyword> <sem_struct>FWeaponData</sem_struct>
                <braces>{</braces>
                    <unreal_macro>UPROPERTY</unreal_macro><parentheses>(</parentheses><parentheses>)</parentheses>
                    <sem_primitive>float</sem_primitive> <sem_member_var>EquipDuration</sem_member_var> <operator>=</operator> <number>1.0f</number><semicolon>;</semicolon>

                    <unreal_macro>UPROPERTY</unreal_macro><parentheses>(</parentheses><parentheses>)</parentheses>
                    <sem_struct>FName</sem_struct> <sem_member_var>SocketName</sem_member_var> <operator>=</operator> <namestring>n"WeaponSocket"</namestring><semicolon>;</semicolon>
                <braces>}</braces>

                <keyword>event</keyword> <type_keyword>void</type_keyword> <sem_event>OnStateChanged</sem_event><parentheses>(</parentheses><sem_typename>EEquipState</sem_typename> <sem_param>NewState</sem_param><parentheses>)</parentheses><semicolon>;</semicolon>

                <keyword>delegate</keyword> <type_keyword>void</type_keyword> <sem_delegate>FOnEquipped</sem_delegate><parentheses>(</parentheses><parentheses>)</parentheses><semicolon>;</semicolon>

                <unreal_macro>UCLASS</unreal_macro><parentheses>(</parentheses>Abstract<parentheses>)</parentheses>
                <keyword>class</keyword> <sem_actor>AWeapon</sem_actor> <operator>:</operator> <sem_actor>AActor</sem_actor>
                <braces>{</braces>
                    <unreal_macro>UPROPERTY</unreal_macro><parentheses>(</parentheses>BlueprintReadWrite<parentheses>)</parentheses>
                    <sem_struct>FWeaponData</sem_struct> <sem_member_var>Data</sem_member_var><semicolon>;</semicolon>

                    <unreal_macro>UPROPERTY</unreal_macro><parentheses>(</parentheses><parentheses>)</parentheses>
                    <sem_component>USkeletalMeshComponent</sem_component> <sem_member_var>Mesh</sem_member_var><semicolon>;</semicolon>

                    <unreal_macro>UPROPERTY</unreal_macro><parentheses>(</parentheses><parentheses>)</parentheses>
                    <sem_template>TArray</sem_template><operator><</operator><sem_struct>FVector</sem_struct><operator>></operator> <sem_member_var>PathPoints</sem_member_var><semicolon>;</semicolon>

                    <access>private</access> <sem_actor>ACharacter</sem_actor> <sem_member_var>Owner</sem_member_var><semicolon>;</semicolon>
                    <access>private</access> <sem_typename>EEquipState</sem_typename> <sem_member_var>State</sem_member_var> <operator>=</operator> <sem_typename>EEquipState</sem_typename><dot>::</dot>None<semicolon>;</semicolon>

                    <sem_primitive>float</sem_primitive> <sem_member_accessor>GetDamageModifier</sem_member_accessor><parentheses>(</parentheses><parentheses>)</parentheses> <keyword>property</keyword>
                    <braces>{</braces>
                        <keyword>return</keyword> <sem_global_accessor>GetGlobalDamageMultiplier</sem_global_accessor><parentheses>(</parentheses><parentheses>)</parentheses> <operator>*</operator> <number>2.0f</number><semicolon>;</semicolon>
                    <braces>}</braces>

                    <unreal_macro>UFUNCTION</unreal_macro><parentheses>(</parentheses>BlueprintCallable<parentheses>)</parentheses>
                    <type_keyword>void</type_keyword> <sem_member_func>StartEquip</sem_member_func><parentheses>(</parentheses><parentheses>)</parentheses>
                    <braces>{</braces>
                        <sem_member_var>State</sem_member_var> <operator>=</operator> <sem_typename>EEquipState</sem_typename><dot>::</dot>Equipping<semicolon>;</semicolon>
                        <sem_event>OnStateChanged</sem_event><parentheses>(</parentheses><sem_member_var>State</sem_member_var><parentheses>)</parentheses><semicolon>;</semicolon>

                        <sem_member_var>Mesh</sem_member_var> <operator>=</operator> <sem_global_func>Cast</sem_global_func><operator><</operator><sem_component>USkeletalMeshComponent</sem_component><operator>></operator><parentheses>(</parentheses><sem_member_func>GetComponentByClass</sem_member_func><parentheses>(</parentheses><sem_component>USkeletalMeshComponent</sem_component><dot>::</dot><sem_member_func>StaticClass</sem_member_func><parentheses>(</parentheses><parentheses>)</parentheses><parentheses>)</parentheses><parentheses>)</parentheses><semicolon>;</semicolon>

                        <keyword>if</keyword> <parentheses>(</parentheses><sem_member_var>Owner</sem_member_var> <operator>==</operator> <null>nullptr</null><parentheses>)</parentheses>
                        <keyword>return</keyword><semicolon>;</semicolon>

                        <sem_struct>FVector</sem_struct> <sem_local_var>Location</sem_local_var> <operator>=</operator> <sem_struct>FVector</sem_struct><dot>::</dot>ZeroVector<semicolon>;</semicolon>
                        <sem_member_var>PathPoints</sem_member_var><dot>.</dot><sem_member_func>Add</sem_member_func><parentheses>(</parentheses><sem_local_var>Location</sem_local_var><parentheses>)</parentheses><semicolon>;</semicolon>
                        <sem_global_func>AttachToActor</sem_global_func><parentheses>(</parentheses><sem_member_var>Owner</sem_member_var><comma>,</comma> <sem_member_var>Data</sem_member_var><dot>.</dot><sem_member_var>SocketName</sem_member_var><parentheses>)</parentheses><semicolon>;</semicolon>

                        <sem_global_func>Print</sem_global_func><parentheses>(</parentheses><fstring>f"Equipped on {Owner.Name}"</fstring><parentheses>)</parentheses><semicolon>;</semicolon>
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
            "preprocessor" to AngelScriptSyntaxHighlighter.PREPROCESSOR,

            // Semantic token mappings
            "sem_namespace" to AngelScriptSyntaxHighlighter.SEMANTIC_NAMESPACE,
            "sem_typename" to AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME,
            "sem_actor" to AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_ACTOR,
            "sem_component" to AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_COMPONENT,
            "sem_struct" to AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_STRUCT,
            "sem_event" to AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_EVENT,
            "sem_delegate" to AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_DELEGATE,
            "sem_primitive" to AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_PRIMITIVE,
            "sem_template" to AngelScriptSyntaxHighlighter.SEMANTIC_TEMPLATE_BASE_TYPE,
            "sem_member_func" to AngelScriptSyntaxHighlighter.SEMANTIC_MEMBER_FUNCTION,
            "sem_global_func" to AngelScriptSyntaxHighlighter.SEMANTIC_GLOBAL_FUNCTION,
            "sem_param" to AngelScriptSyntaxHighlighter.SEMANTIC_PARAMETER,
            "sem_local_var" to AngelScriptSyntaxHighlighter.SEMANTIC_LOCAL_VARIABLE,
            "sem_member_var" to AngelScriptSyntaxHighlighter.SEMANTIC_MEMBER_VARIABLE,
            "sem_global_var" to AngelScriptSyntaxHighlighter.SEMANTIC_GLOBAL_VARIABLE,
            "sem_member_accessor" to AngelScriptSyntaxHighlighter.SEMANTIC_MEMBER_ACCESSOR,
            "sem_global_accessor" to AngelScriptSyntaxHighlighter.SEMANTIC_GLOBAL_ACCESSOR,
            "sem_access_spec" to AngelScriptSyntaxHighlighter.SEMANTIC_ACCESS_SPECIFIER,
            "sem_unimported" to AngelScriptSyntaxHighlighter.SEMANTIC_UNIMPORTED_SYMBOL,
            "sem_error" to AngelScriptSyntaxHighlighter.SEMANTIC_UNKNOWN_ERROR
        )
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "AngelScript"

    companion object {
        private val DESCRIPTORS = arrayOf(
            // Syntactic Highlighting - Keywords
            AttributesDescriptor("Keywords//Keyword", AngelScriptSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Keywords//Type Keyword", AngelScriptSyntaxHighlighter.TYPE_KEYWORD),
            AttributesDescriptor("Keywords//Access Modifier", AngelScriptSyntaxHighlighter.ACCESS_MODIFIER),

            // Syntactic Highlighting - Literals
            AttributesDescriptor("Literals//Number", AngelScriptSyntaxHighlighter.NUMBER),
            AttributesDescriptor("Literals//String", AngelScriptSyntaxHighlighter.STRING),
            AttributesDescriptor("Literals//F-String", AngelScriptSyntaxHighlighter.FSTRING),
            AttributesDescriptor("Literals//Name String", AngelScriptSyntaxHighlighter.NAMESTRING),
            AttributesDescriptor("Literals//Boolean", AngelScriptSyntaxHighlighter.BOOLEAN),
            AttributesDescriptor("Literals//Null Keyword", AngelScriptSyntaxHighlighter.NULL_KEYWORD),

            // Syntactic Highlighting - Comments
            AttributesDescriptor("Comments//Line Comment", AngelScriptSyntaxHighlighter.LINE_COMMENT),
            AttributesDescriptor("Comments//Block Comment", AngelScriptSyntaxHighlighter.BLOCK_COMMENT),

            // Syntactic Highlighting - Operators and Punctuation
            AttributesDescriptor("Operators and Punctuation//Operator", AngelScriptSyntaxHighlighter.OPERATOR),
            AttributesDescriptor("Operators and Punctuation//Parentheses", AngelScriptSyntaxHighlighter.PARENTHESES),
            AttributesDescriptor("Operators and Punctuation//Braces", AngelScriptSyntaxHighlighter.BRACES),
            AttributesDescriptor("Operators and Punctuation//Brackets", AngelScriptSyntaxHighlighter.BRACKETS),
            AttributesDescriptor("Operators and Punctuation//Dot", AngelScriptSyntaxHighlighter.DOT),
            AttributesDescriptor("Operators and Punctuation//Comma", AngelScriptSyntaxHighlighter.COMMA),
            AttributesDescriptor("Operators and Punctuation//Semicolon", AngelScriptSyntaxHighlighter.SEMICOLON),

            // Syntactic Highlighting - Unreal Specific
            AttributesDescriptor("Unreal Specific//Unreal Macro", AngelScriptSyntaxHighlighter.UNREAL_MACRO),
            AttributesDescriptor("Unreal Specific//Preprocessor Directive", AngelScriptSyntaxHighlighter.PREPROCESSOR),

            // Semantic Highlighting (LSP) - Types
            AttributesDescriptor("LSP//Types//Namespace", AngelScriptSyntaxHighlighter.SEMANTIC_NAMESPACE),
            AttributesDescriptor("LSP//Types//Type Name", AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME),
            AttributesDescriptor("LSP//Types//Actor Type", AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_ACTOR),
            AttributesDescriptor("LSP//Types//Component Type", AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_COMPONENT),
            AttributesDescriptor("LSP//Types//Struct Type", AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_STRUCT),
            AttributesDescriptor("LSP//Types//Event Type", AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_EVENT),
            AttributesDescriptor("LSP//Types//Delegate Type", AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_DELEGATE),
            AttributesDescriptor("LSP//Types//Primitive Type", AngelScriptSyntaxHighlighter.SEMANTIC_TYPENAME_PRIMITIVE),
            AttributesDescriptor("LSP//Types//Template Base Type", AngelScriptSyntaxHighlighter.SEMANTIC_TEMPLATE_BASE_TYPE),

            // Semantic Highlighting (LSP) - Functions
            AttributesDescriptor("LSP//Functions//Member Function", AngelScriptSyntaxHighlighter.SEMANTIC_MEMBER_FUNCTION),
            AttributesDescriptor("LSP//Functions//Global Function", AngelScriptSyntaxHighlighter.SEMANTIC_GLOBAL_FUNCTION),

            // Semantic Highlighting (LSP) - Variables
            AttributesDescriptor("LSP//Variables//Parameter", AngelScriptSyntaxHighlighter.SEMANTIC_PARAMETER),
            AttributesDescriptor("LSP//Variables//Local Variable", AngelScriptSyntaxHighlighter.SEMANTIC_LOCAL_VARIABLE),
            AttributesDescriptor("LSP//Variables//Member Variable", AngelScriptSyntaxHighlighter.SEMANTIC_MEMBER_VARIABLE),
            AttributesDescriptor("LSP//Variables//Global Variable", AngelScriptSyntaxHighlighter.SEMANTIC_GLOBAL_VARIABLE),

            // Semantic Highlighting (LSP) - Accessors
            AttributesDescriptor("LSP//Accessors//Member Accessor (Property)", AngelScriptSyntaxHighlighter.SEMANTIC_MEMBER_ACCESSOR),
            AttributesDescriptor("LSP//Accessors//Global Accessor", AngelScriptSyntaxHighlighter.SEMANTIC_GLOBAL_ACCESSOR),

            // Semantic Highlighting (LSP) - Other
            AttributesDescriptor("LSP//Other//Access Specifier", AngelScriptSyntaxHighlighter.SEMANTIC_ACCESS_SPECIFIER),
            AttributesDescriptor("LSP//Other//Unimported Symbol", AngelScriptSyntaxHighlighter.SEMANTIC_UNIMPORTED_SYMBOL),
            AttributesDescriptor("LSP//Other//Unknown Error", AngelScriptSyntaxHighlighter.SEMANTIC_UNKNOWN_ERROR)
        )
    }
}
