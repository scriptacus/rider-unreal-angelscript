package com.scriptacus.riderunrealangelscript.lang

import com.intellij.application.options.CodeStyle
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File

/**
 * Tests for AngelScript code formatter.
 *
 * Test files are located in src/test/resources/testData/formatter/
 * Each test consists of:
 * - <TestName>.as - Input AngelScript code (before formatting)
 * - <TestName>_after.as - Expected formatted output
 */
class AngelScriptFormatterTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/resources/testData/formatter"

    private fun doTest() {
        val testName = getTestName(false)
        val inputFile = File(testDataPath, "$testName.as")
        val expectedFile = File(testDataPath, "${testName}_after.as")

        assertTrue("Input file not found: ${inputFile.absolutePath}", inputFile.exists())
        assertTrue("Expected file not found: ${expectedFile.absolutePath}", expectedFile.exists())

        val input = inputFile.readText()
        val expected = expectedFile.readText()

        val psiFile = myFixture.configureByText("test.as", input)

        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).reformat(psiFile)
        }

        val actual = psiFile.text
        assertEquals("Formatted output does not match expected", expected, actual)
    }

    // Phase 1 Tests: Basic Indentation

    fun testSimpleClass() {
        doTest()
    }

    fun testNestedBlocks() {
        doTest()
    }

    fun testFunctionIndentation() {
        doTest()
    }
}
