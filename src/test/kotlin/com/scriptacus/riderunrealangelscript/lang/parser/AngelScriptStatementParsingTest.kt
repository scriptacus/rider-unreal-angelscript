package com.scriptacus.riderunrealangelscript.lang.parser

/**
 * Tests for AngelScript statement parsing.
 *
 * Test data location: src/test/resources/testData/parser/statements/
 */
class AngelScriptStatementParsingTest : AngelScriptParsingTestBase("parser/statements") {
    fun testIfStatement() = doTest(true)
    fun testIfElseStatement() = doTest(true)
    fun testWhileLoop() = doTest(true)
    fun testForLoop() = doTest(true)
    fun testForeachLoop() = doTest(true)
    fun testSwitchStatement() = doTest(true)
    fun testReturnStatement() = doTest(true)
    fun testBreakContinue() = doTest(true)
    fun testFallthrough() = doTest(true)
    fun testStatementBlock() = doTest(true)
}
