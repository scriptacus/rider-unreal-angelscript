package com.scriptacus.riderunrealangelscript.lang.parser

/**
 * Tests for Unreal-specific AngelScript features.
 *
 * Test data location: src/test/resources/testData/parser/unreal/
 */
class AngelScriptUnrealParsingTest : AngelScriptParsingTestBase("parser/unreal") {
    fun testUClassMacro() = doTest(true)
    fun testUStructMacro() = doTest(true)
    fun testUEnumMacro() = doTest(true)
    fun testUPropertyMacro() = doTest(true)
    fun testUPropertyMacroAdvanced() = doTest(true)
    fun testUFunctionMacro() = doTest(true)
    fun testUMetaMacro() = doTest(true)
    fun testClassInheritance() = doTest(true)
    fun testDefaultStatement() = doTest(true)
    fun testDelegateDeclaration() = doTest(true)
    fun testEventDeclaration() = doTest(true)
    fun testAssetDeclaration() = doTest(true)
    fun testAccessDeclaration() = doTest(true)
    fun testAccessDeclarationComplex() = doTest(true)
    fun testMixinFunction() = doTest(true)
    fun testStructConstructorWithUproperty() = doTest(true)
    fun testStructConstructorCopy() = doTest(true)
}
