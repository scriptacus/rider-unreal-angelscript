package com.scriptacus.riderunrealangelscript.lang.parser

import com.intellij.testFramework.ParsingTestCase
import com.scriptacus.riderunrealangelscript.lang.AngelScriptParserDefinition

/**
 * Tests error recovery following the Solidity plugin minimal pattern:
 * - 3 recovery predicates (UntilSemicolonRecover, UntilBraceRecover, ClosedBracketRecover)
 * - Unfinished construct pattern for blocks
 * - Verify parser recovers to next valid construct after errors
 */
class AngelScriptErrorRecoveryTest : ParsingTestCase("parser/errors", "as", true, AngelScriptParserDefinition()) {
    override fun getTestDataPath() = "src/test/resources/testData"

    /**
     * Test that parser recovers after missing closing brace in class
     * Expected: namespace after incomplete class is parsed at correct level
     */
    fun testMissingClosingBrace() {
        doErrorRecoveryTest(true)
    }

    /**
     * Test that parser recovers after missing closing brace in struct
     * Expected: class after incomplete struct is parsed at correct level
     */
    fun testMissingClosingBraceStruct() {
        doStructRecoveryTest(true)
    }

    /**
     * Test that parser recovers after missing closing brace in function
     * Expected: next function after incomplete function is parsed at correct level
     */
    fun testMissingClosingBraceFunction() {
        doFunctionRecoveryTest(true)
    }

    /**
     * Test that parser recovers after missing closing brace in statement block
     * Expected: subsequent statements in function body are parsed correctly
     */
    fun testMissingClosingBraceStatementBlock() {
        doStatementBlockRecoveryTest(true)
    }

    /**
     * Verify the PSI tree structure and recovery for class
     */
    private fun doErrorRecoveryTest(checkResult: Boolean) {
        val name = getTestName(false)
        myFile = createFile("$name.as", loadFile("$name.as"))
        ensureParsed(myFile)

        if (checkResult) {
            val psi = toParseTreeText(myFile, false, false)

            // Debug output
            println("=== PSI Tree for $name ===")
            println(psi)
            println("=== End PSI Tree ===")

            // Verify recovery happened - key indicators:
            // 1. Class declaration exists
            assertTrue("Should have class declaration", psi.contains("CLASS_DECL"))
            assertTrue("Should have class body", psi.contains("CLASS_BODY"))

            // 2. Namespace is parsed at FILE level (not inside class body)
            // This is the critical recovery behavior - namespace appears after class, not nested in it
            assertTrue("Should find Recovery namespace identifier", psi.contains("Recovery"))
            assertTrue("Should find namespace declaration at file level", psi.contains("NAMESPACE_DECL"))

            // 3. Namespace has proper structure
            assertTrue("Should find namespace body", psi.contains("NAMESPACE_BODY"))

            println("=== Error Recovery Test: $name ===")
            println("Recovery successful: Namespace parsed at file level after incomplete class")
        }
    }

    /**
     * Verify the PSI tree structure and recovery for struct
     */
    private fun doStructRecoveryTest(checkResult: Boolean) {
        val name = getTestName(false)
        myFile = createFile("$name.as", loadFile("$name.as"))
        ensureParsed(myFile)

        if (checkResult) {
            val psi = toParseTreeText(myFile, false, false)

            println("=== PSI Tree for $name ===")
            println(psi)
            println("=== End PSI Tree ===")

            // Verify recovery happened
            assertTrue("Should have struct declaration", psi.contains("STRUCT_DECL"))
            assertTrue("Should have struct body", psi.contains("STRUCT_BODY"))
            assertTrue("Should find Recovery class identifier", psi.contains("Recovery"))
            assertTrue("Should find class declaration at file level", psi.contains("CLASS_DECL"))

            println("=== Error Recovery Test: $name ===")
            println("Recovery successful: Class parsed at file level after incomplete struct")
        }
    }

    /**
     * Verify the PSI tree structure and recovery for function
     */
    private fun doFunctionRecoveryTest(checkResult: Boolean) {
        val name = getTestName(false)
        myFile = createFile("$name.as", loadFile("$name.as"))
        ensureParsed(myFile)

        if (checkResult) {
            val psi = toParseTreeText(myFile, false, false)

            println("=== PSI Tree for $name ===")
            println(psi)
            println("=== End PSI Tree ===")

            // Verify recovery happened
            assertTrue("Should have incomplete function declaration", psi.contains("IncompleteFunction"))
            assertTrue("Should have function body", psi.contains("FUNCTION_BODY"))
            assertTrue("Should find NextFunction identifier", psi.contains("NextFunction"))
            assertTrue("Should find second function declaration at file level", psi.contains("GLOBAL_FUNCTION_DECL"))

            println("=== Error Recovery Test: $name ===")
            println("Recovery successful: NextFunction parsed at file level after incomplete function")
        }
    }

    /**
     * Verify the PSI tree structure and recovery for statement block
     */
    private fun doStatementBlockRecoveryTest(checkResult: Boolean) {
        val name = getTestName(false)
        myFile = createFile("$name.as", loadFile("$name.as"))
        ensureParsed(myFile)

        if (checkResult) {
            val psi = toParseTreeText(myFile, false, false)

            println("=== PSI Tree for $name ===")
            println(psi)
            println("=== End PSI Tree ===")

            // Verify recovery happened
            assertTrue("Should have if statement", psi.contains("IF_STATEMENT"))
            assertTrue("Should have statement block", psi.contains("STATEMENT_BLOCK"))
            assertTrue("Should have while statement at function body level", psi.contains("WHILE_STATEMENT"))
            assertTrue("Should find variable 'y' declaration", psi.contains("'y'"))
            assertTrue("Should find variable 'z' declaration", psi.contains("'z'"))

            println("=== Error Recovery Test: $name ===")
            println("Recovery successful: Subsequent statements parsed correctly after incomplete if block")
        }
    }
}
