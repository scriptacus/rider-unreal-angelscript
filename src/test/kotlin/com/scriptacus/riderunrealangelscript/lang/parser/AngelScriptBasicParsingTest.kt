package com.scriptacus.riderunrealangelscript.lang.parser

/**
 * Tests for basic AngelScript language features.
 *
 * Test data location: src/test/resources/testData/parser/basic/
 */
class AngelScriptBasicParsingTest : AngelScriptParsingTestBase("parser/basic") {
    fun testSimpleVariable() = doTest(true)
    fun testSimpleFunction() = doTest(true)
    fun testLineComment() = doTest(true)
    fun testBlockComment() = doTest(true)
    fun testNumericLiterals() = doTest(true)
    fun testStringLiterals() = doTest(true)
    fun testNamespace() = doTest(true)
    fun testPrimitiveTypes() = doTest(true)
}
