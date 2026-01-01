package com.scriptacus.riderunrealangelscript.lang.parser

/**
 * Tests for AngelScript declaration parsing.
 *
 * Test data location: src/test/resources/testData/parser/declarations/
 */
class AngelScriptDeclarationParsingTest : AngelScriptParsingTestBase("parser/declarations") {
    fun testDefaultParameters() = doTest(true)
    fun testReferenceParameters() = doTest(true)
    fun testFunctionQualifiers() = doTest(true)
    fun testStructConstructor() = doTest(true)
    fun testTrailingCommaParameters() = doTest(true)
    fun testDestructor() = doTest(true)
    fun testAssetWithBody() = doTest(true)
}
