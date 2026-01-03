package com.scriptacus.riderunrealangelscript.lang

import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.LightPlatformTestCase
import java.io.File

/**
 * Tests for AngelScript code formatter.
 *
 * Test files are located in src/test/resources/testData/formatter/
 * Each test consists of:
 * - <TestName>.as - Input AngelScript code (before formatting)
 * - <TestName>_after.as - Expected formatted output
 *
 * NOTE: These tests are currently excluded from automated test runs (see build.gradle.kts)
 * because they require full Rider infrastructure (protocol/solution) which isn't available
 * in the test environment. The formatter functionality should be tested manually by:
 * 1. Running the plugin in Rider sandbox: ./gradlew runIde
 * 2. Creating AngelScript test files
 * 3. Using Code > Reformat Code to verify formatting behavior
 */
class AngelScriptFormatterTest : LightPlatformTestCase() {

    private val testDataPath = "src/test/resources/testData/formatter"

    private fun doTest() {
        val testName = getTestName(false)
        val inputFile = File(testDataPath, "$testName.as")
        val expectedFile = File(testDataPath, "${testName}_after.as")

        assertTrue("Input file not found: ${inputFile.absolutePath}", inputFile.exists())
        assertTrue("Expected file not found: ${expectedFile.absolutePath}", expectedFile.exists())

        val input = inputFile.readText()
        val expected = expectedFile.readText()

        val psiFile = createFile("test.as", input)

        ApplicationManager.getApplication().runWriteAction {
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

    fun testAccessSpecifierMethod() {
        doTest()
    }

    fun testBlockIndentation() {
        doTest()
    }
}
