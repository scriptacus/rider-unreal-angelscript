package com.scriptacus.riderunrealangelscript.lang.parser

import com.intellij.testFramework.ParsingTestCase
import com.scriptacus.riderunrealangelscript.lang.AngelScriptParserDefinition
import java.io.File

/**
 * Reusable utility for debugging parse errors in arbitrary AngelScript files.
 *
 * Usage: ./gradlew test --tests "ParseFileDebugger.testParseFile" -Dtest.file="path/to/file.as"
 *
 * Output shows:
 * - Total number of parse errors
 * - Each error with surrounding PSI context
 * - Location in the PSI tree where errors occur
 */
class ParseFileDebugger : ParsingTestCase("parser", "as", true, AngelScriptParserDefinition()) {
    override fun getTestDataPath() = "."

    fun testParseFile() {
        val filePath = System.getProperty("test.file")
            ?: "third-party/angelscript-reference/Script/Gameplay/Movement/Player/Component/PlayerMovementComponent.as"

        val file = File(filePath)
        require(file.exists()) { "File not found: $filePath" }

        val content = file.readText()
        myFile = createFile(file.name, content)
        ensureParsed(myFile)

        val psi = toParseTreeText(myFile, false, false)
        val psiLines = psi.lines()
        val errors = psiLines.withIndex().filter { it.value.contains("PsiErrorElement") }

        println("=== Parse Errors: ${file.name} ===")
        println("Total errors: ${errors.size}")

        if (errors.isEmpty()) {
            println("No parse errors found!")
            return
        }

        println("\nErrors with context:")
        errors.forEach { (idx, _) ->
            println("\n--- Error at PSI line ${idx + 1} ---")
            (maxOf(0, idx - 2)..minOf(psiLines.size - 1, idx + 2)).forEach { i ->
                val prefix = if (i == idx) ">>> " else "    "
                println("$prefix${psiLines[i]}")
            }
        }
    }
}
