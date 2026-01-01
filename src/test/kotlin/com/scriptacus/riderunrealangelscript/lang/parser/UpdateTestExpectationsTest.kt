package com.scriptacus.riderunrealangelscript.lang.parser

import com.intellij.testFramework.ParsingTestCase
import com.scriptacus.riderunrealangelscript.lang.AngelScriptParserDefinition
import java.io.File

/**
 * Utility to regenerate test expectation files after grammar changes.
 *
 * Usage: Run this as a JUnit test with working directory set to project root.
 */
class UpdateTestExpectationsTest : ParsingTestCase("parser", "as", true, AngelScriptParserDefinition()) {
    override fun getTestDataPath(): String = "src/test/resources/testData"

    private fun generateExpectation(dir: String, testName: String) {
        val inputFile = "$testName.as"
        val outputFile = "$testName.txt"

        myFile = createFile(inputFile, loadFile("$dir/$inputFile"))
        ensureParsed(myFile)
        val psiTree = toParseTreeText(myFile, false, true).trim()

        val outputPath = File(getTestDataPath(), "parser/$dir/$outputFile")
        outputPath.writeText(psiTree)
        println("Updated: parser/$dir/$outputFile")
    }

    fun testGenerateAllExpectations() {
        val tests = mapOf(
            "basic" to listOf(
                "BlockComment", "LineComment", "Namespace", "NumericLiterals",
                "PrimitiveTypes", "SimpleFunction", "SimpleVariable", "StringLiterals"
            ),
            "advanced" to listOf(
                "CompleteClass", "CompleteStruct", "FStringFunctionCall",
                "FStringMultiExpressionUserCase", "FStringSimple", "FStringWithExpression",
                "FStringWithFormat", "NameLiteral", "NameStringUserCase",
                "PreprocessorIfEditor", "PreprocessorIfTest", "PreprocessorNested"
            ),
            "declarations" to listOf(
                "AssetWithBody", "DefaultParameters", "Destructor", "FunctionQualifiers",
                "ReferenceParameters", "StructConstructor", "TrailingCommaParameters"
            ),
            "expressions" to listOf(
                "ArrayAccess", "BinaryOperations", "BitwiseOperators", "CastExpression",
                "CompoundAssignment", "ConstructorCall", "FunctionCall", "MethodCall",
                "NamedArguments", "NestedTemplates", "ScopedIdentifier", "ShiftOperators",
                "TemplateType", "TernaryExpression", "UnaryOperations", "ExpressionPrecedence"
            ),
            "statements" to listOf(
                "BreakContinue", "Fallthrough", "ForeachLoop", "ForLoop",
                "IfElseStatement", "IfStatement", "ReturnStatement",
                "StatementBlock", "SwitchStatement", "WhileLoop"
            ),
            "unreal" to listOf(
                "AccessDeclaration", "AccessDeclarationComplex", "AssetDeclaration",
                "ClassInheritance", "DefaultStatement", "DelegateDeclaration",
                "EventDeclaration", "MixinFunction",
                "StructConstructorCopy", "StructConstructorWithUproperty",
                "UClassMacro", "UEnumMacro", "UFunctionMacro", "UMetaMacro",
                "UPropertyMacro", "UPropertyMacroAdvanced", "UStructMacro"
            ),
            "errorRecovery" to listOf(
                "comprehensiveErrors", "EnumValueErrors", "expressionErrors",
                "IncompleteBlock", "IncompleteControlFlow", "incompleteDeclarations",
                "MissingSemicolon", "MultipleErrors", "parameterErrors",
                "StructErrors", "templateErrors", "testControlFlowCascade"
            )
        )

        var successCount = 0
        var failCount = 0

        tests.forEach { (dir, testNames) ->
            println("\n=== Processing $dir ===")
            testNames.forEach { testName ->
                try {
                    generateExpectation(dir, testName)
                    successCount++
                } catch (e: Exception) {
                    println("ERROR generating $dir/$testName: ${e.message}")
                    e.printStackTrace()
                    failCount++
                }
            }
        }

        println("\n=== Summary ===")
        println("Success: $successCount")
        println("Failed: $failCount")
        println("Total: ${successCount + failCount}")
    }
}
