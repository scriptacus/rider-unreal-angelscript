package com.scriptacus.riderunrealangelscript.lang.parser

import com.intellij.testFramework.ParsingTestCase
import com.scriptacus.riderunrealangelscript.lang.AngelScriptParserDefinition
import java.io.File

/**
 * Utility to regenerate test expectations after grammar changes.
 * Run this test to update .txt files with current PSI structure.
 */
class UpdateTestExpectations : ParsingTestCase("parser", "as", true, AngelScriptParserDefinition()) {
    override fun getTestDataPath() = "src/test/resources"

    override fun includeRanges(): Boolean = true

    fun testUpdateCompleteStruct() {
        updateExpectation("advanced/CompleteStruct")
    }

    fun testUpdateShiftOperators() {
        updateExpectation("expressions/ShiftOperators")
    }

    fun testUpdateScientificNotation() {
        updateExpectation("basic/ScientificNotation")
    }

    fun testUpdateConstructorVariableDeclaration() {
        updateExpectation("declarations/ConstructorVariableDeclaration")
    }

    fun testUpdateFStringWithFormat() {
        updateExpectation("advanced/fStringWithFormat")
    }

    fun testUpdateFStringFormatsAndTernary() {
        updateExpectation("advanced/fStringFormatsAndTernary")
    }

    private fun updateExpectation(testName: String) {
        val testFile = "$testDataPath/testData/parser/$testName.as"
        val expectationFile = "$testDataPath/testData/parser/$testName.txt"

        println("Updating expectation for $testName")

        myFile = createFile("test.as", File(testFile).readText())
        ensureParsed(myFile)
        val psi = toParseTreeText(myFile, false, true)

        // Check for errors
        if (psi.contains("PsiErrorElement")) {
            println("WARNING: PSI contains errors for $testName")
            val errorLines = psi.lines().filter { it.contains("PsiErrorElement") }
            errorLines.forEach { println("  $it") }
        }

        // Write to expectation file
        File(expectationFile).writeText(psi.trimEnd() + "\n")
        println("Updated $expectationFile")
    }
}
