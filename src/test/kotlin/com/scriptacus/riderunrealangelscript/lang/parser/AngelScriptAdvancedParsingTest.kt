package com.scriptacus.riderunrealangelscript.lang.parser

/**
 * Tests for advanced AngelScript features.
 *
 * Test data location: src/test/resources/testData/parser/advanced/
 */
class AngelScriptAdvancedParsingTest : AngelScriptParsingTestBase("parser/advanced") {
    fun testFStringSimple() = doTest(true)
    fun testFStringWithExpression() = doTest(true)
    fun testFStringWithFormat() = doTest(true)
    fun testFStringFunctionCall() = doTest(true)  // Test f-strings with function calls (paren-depth tracking)
    fun testFStringMultiExpressionUserCase() = doTest(true)  // User-reported: multi-expression f-strings
    fun testNameLiteral() = doTest(true)
    fun testNameStringUserCase() = doTest(true)  // User-reported: name string literals
    fun testPreprocessorIfEditor() = doTest(true)
    fun testPreprocessorIfTest() = doTest(true)
    fun testPreprocessorNested() = doTest(true)
    fun testCompleteClass() = doTest(true)
    fun testCompleteStruct() = doTest(true)
}
