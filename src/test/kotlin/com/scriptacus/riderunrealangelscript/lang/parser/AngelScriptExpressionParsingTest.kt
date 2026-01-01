package com.scriptacus.riderunrealangelscript.lang.parser

/**
 * Tests for AngelScript expression parsing.
 *
 * Test data location: src/test/resources/testData/parser/expressions/
 */
class AngelScriptExpressionParsingTest : AngelScriptParsingTestBase("parser/expressions") {
    fun testBinaryOperations() = doTest(true)
    fun testUnaryOperations() = doTest(true)
    fun testTernaryExpression() = doTest(true)
    fun testFunctionCall() = doTest(true)
    fun testMethodCall() = doTest(true)
    fun testCastExpression() = doTest(true)
    fun testConstructorCall() = doTest(true)
    fun testArrayAccess() = doTest(true)
    fun testScopedIdentifier() = doTest(true)
    fun testTemplateType() = doTest(true)
    fun testNestedTemplates() = doTest(true)
    fun testNamedArguments() = doTest(true)
    fun testShiftOperators() = doTest(true)
    fun testCompoundAssignment() = doTest(true)
    fun testBitwiseOperators() = doTest(true)
    fun testExpressionPrecedence() = doTest(true)
}
