package com.scriptacus.riderunrealangelscript.lang.parser

import java.io.File

/**
 * Utility to regenerate test expectation files after grammar changes.
 *
 * Usage: Run this as a JUnit test with working directory set to project root.
 *
 * This uses the EXACT same code path as actual tests (extends AngelScriptParsingTestBase,
 * uses loadFile()) to ensure expectations match test behavior.
 */
class UpdateTestExpectationsTest : AngelScriptParsingTestBase("parser") {

    fun testGenerateAllExpectations() {
        val testDataDir = File("$testDataPath/parser/")
        val updated = mutableListOf<String>()
        val failed = mutableListOf<String>()

        testDataDir.walkTopDown()
            .filter { it.extension == "as" }
            .forEach { asFile ->
                val relativePath = asFile.relativeTo(testDataDir).path.removeSuffix(".as").replace('\\', '/')
                try {
                    updateExpectation(relativePath)
                    updated.add(relativePath)
                } catch (e: Exception) {
                    failed.add("$relativePath: ${e.message}")
                    println("ERROR updating $relativePath: ${e.message}")
                }
            }

        println("\nSummary:")
        println("Updated: ${updated.size} files")
        println("Failed: ${failed.size} files")
        if (failed.isNotEmpty()) {
            println("\nFailed files:")
            failed.forEach { println("  $it") }
        }
    }

    private fun updateExpectation(testName: String) {
        val expectationFile = "$testDataPath/parser/$testName.txt"

        println("Updating expectation for $testName")

        // Use the EXACT same code path as doTest() - parseFile with loadFile
        parseFile(testName, loadFile("$testName.as"))
        val psi = toParseTreeText(myFile, false, true)

        if (psi.contains("PsiErrorElement")) {
            println("WARNING: PSI contains errors for $testName")
            val errorLines = psi.lines().filter { it.contains("PsiErrorElement") }
            errorLines.forEach { println("  $it") }
        }

        // Write PSI output exactly as-is (do NOT trim - doTest() doesn't trim either)
        File(expectationFile).writeBytes(psi.toByteArray(Charsets.UTF_8))
        println("Updated $expectationFile")
    }
}
