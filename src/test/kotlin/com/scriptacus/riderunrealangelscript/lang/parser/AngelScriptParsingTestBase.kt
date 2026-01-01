package com.scriptacus.riderunrealangelscript.lang.parser

import com.intellij.testFramework.ParsingTestCase
import com.scriptacus.riderunrealangelscript.lang.AngelScriptParserDefinition

/**
 * Base class for AngelScript parser tests.
 *
 * Test files are located in src/test/resources/testData/parser/<subdirectory>/
 * Each test consists of:
 * - <TestName>.as - Input AngelScript code
 * - <TestName>.txt - Expected PSI tree structure
 */
abstract class AngelScriptParsingTestBase(baseDir: String) :
    ParsingTestCase(baseDir, "as", true, AngelScriptParserDefinition()) {

    override fun getTestDataPath(): String = "src/test/resources/testData"

    override fun setUp() {
        super.setUp()
    }

    override fun includeRanges(): Boolean = true
}
