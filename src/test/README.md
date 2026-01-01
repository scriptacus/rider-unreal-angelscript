# AngelScript Parser Tests

## Running Tests

```bash
# All tests
./gradlew test

# Specific test class
./gradlew test --tests AngelScriptBasicParsingTest

# Single test method
./gradlew test --tests AngelScriptBasicParsingTest.testSimpleVariable

# Run with verbose output
./gradlew test --info
```

## Adding New Tests

1. **Add test method** to corresponding test class:
   ```kotlin
   fun testMyNewFeature() = doTest(true)
   ```

2. **Create `.as` file** in appropriate `testData/parser/` subdirectory:
   - File name MUST be: `myNewFeature.as` (lowercase first letter!)
   - Note: `testMyNewFeature()` → `myNewFeature.as` (strip "test", lowercase first)

3. **Run test** to see actual PSI output (it will fail initially)

4. **Create `.txt` file** with expected PSI:
   - File name: `myNewFeature.txt` (must match `.as` file exactly)
   - Copy PSI output from test failure, or let framework generate it

5. **Re-run test** to verify it passes

## Test Data Structure

- **`testData/parser/basic/`** - Basic language features (variables, functions, comments, primitives)
- **`testData/parser/classes/`** - Class and struct declarations
- **`testData/parser/expressions/`** - Expression parsing (operators, calls, casts)
- **`testData/parser/statements/`** - Statement parsing (if, for, while, switch, etc.)
- **`testData/parser/unreal/`** - Unreal-specific features (macros, delegates, events, access)
- **`testData/parser/advanced/`** - Complex features (f-strings, preprocessor, complete files)
- **`testData/parser/strings/`** - String literal variations
- **`testData/parser/preprocessor/`** - Preprocessor directive parsing
- **`testData/parser/real/`** - Real-world Split Fiction files
- **`testData/parser/errors/`** - Error recovery tests
- **`testData/parser/edges/`** - Edge cases

## Test File Format

Each test consists of two files:

### Input File: `<testName>.as`
The AngelScript source code to parse.

Example `simpleVariable.as`:
```angelscript
int myVariable;
```

### Expected Output: `<testName>.txt`
The expected PSI tree structure.

Example `simpleVariable.txt`:
```
AngelScript File
  PsiWhiteSpace('\n')
  AngelScriptVariableDecl(VARIABLE_DECL)
    AngelScriptTypename(TYPENAME)
      PsiElement(INT)('int')
    PsiWhiteSpace(' ')
    AngelScriptVariableDeclarator(VARIABLE_DECLARATOR)
      PsiElement(IDENTIFIER)('myVariable')
    PsiElement(SEMICOLON)(';')
```

### IMPORTANT: File Naming Convention

The test framework converts method names to file names as follows:
- Test method: `fun testBinaryOperations() = doTest(true)`
- Strips "test" prefix: `BinaryOperations`
- **Lowercases first letter**: `binaryOperations`
- Looks for files: `binaryOperations.as` and `binaryOperations.txt`

**Critical for cross-platform compatibility:**
- File names MUST have lowercase first letter: `binaryOperations.as` (not `BinaryOperations.as`)
- Both `.as` and `.txt` files must use identical casing
- On Windows: Case-insensitive filesystem may mask this issue
- On Linux/macOS: Case-sensitive filesystem will cause test failures if casing is wrong

**Example mappings:**
- `testSimpleVariable()` → `simpleVariable.as` + `simpleVariable.txt`
- `testFStringWithFormat()` → `fStringWithFormat.as` + `fStringWithFormat.txt`
- `testUClassMacro()` → `uClassMacro.as` + `uClassMacro.txt`

## Generating Expected PSI Output

### Option 1: Let Test Generate It (Recommended for Initial Setup)

1. Create only the `.as` file
2. Run the test - it will fail
3. The test output will show the actual PSI tree
4. Copy that output to create the `.txt` file
5. Review to ensure it matches expectations
6. Re-run test to verify it passes

### Option 2: Use PSI Viewer (For Manual Verification)

1. Open IntelliJ IDEA (PSI Viewer may not be available in Rider)
2. Install your plugin in dev mode
3. Open an `.as` file
4. Go to **Tools → View PSI Structure**
5. Copy the PSI tree text
6. Paste into corresponding `.txt` file

## Test Organization

### AngelScriptBasicParsingTest
Tests fundamental language features:
- Variable declarations
- Function declarations
- Comments (line and block)
- Literals (numeric, string)
- Namespaces
- Primitive types

### AngelScriptUnrealParsingTest
Tests Unreal Engine-specific features:
- UCLASS, USTRUCT, UENUM macros
- UPROPERTY, UFUNCTION macros
- UMETA annotations
- Class inheritance
- Default statements
- Delegate and event declarations
- Asset declarations
- Access specifiers (simple and complex)
- Mixin functions

### AngelScriptExpressionParsingTest
Tests expression parsing:
- Binary operations (arithmetic, logical, bitwise)
- Unary operations
- Ternary conditional
- Function calls
- Method calls
- Cast expressions
- Constructor calls
- Array access
- Scoped identifiers (namespace::class)
- Template types
- Named arguments

### AngelScriptStatementParsingTest
Tests control flow statements:
- If/else statements
- While loops
- For loops
- Foreach loops
- Switch/case statements
- Return statements
- Break/continue
- Fallthrough
- Statement blocks

### AngelScriptAdvancedParsingTest
Tests advanced features:
- F-strings (simple and with expressions)
- F-string format specifiers
- Name literals (n"name")
- Preprocessor directives (#if EDITOR, #if TEST)
- Nested preprocessor blocks
- Complete class definitions
- Complete struct definitions

## Troubleshooting

### Test Fails with "Cannot find expected file"

**Most common cause: Incorrect file name casing**

The test framework expects files with lowercase first letter. For example:
- ❌ `BinaryOperations.as` (Wrong - uppercase B)
- ✅ `binaryOperations.as` (Correct - lowercase b)

This error may only appear on Linux/macOS due to case-sensitive filesystems.

**Other causes:**
- Ensure both `.as` and `.txt` files exist in the same directory
- Verify the file names exactly match the test method name (minus "test" prefix, lowercase first letter)

### PSI Tree Doesn't Match Expected

1. Check for whitespace differences (spaces vs tabs)
2. Verify line endings (CRLF vs LF)
3. Ensure the grammar has been regenerated: `./gradlew generateAngelScriptParser`
4. Check if the lexer was updated: `./gradlew generateAngelScriptLexer`

### Parser Generates Error Nodes

The grammar may be incomplete or incorrect for that construct. Check:
1. The BNF grammar definition in `src/main/bnf/AngelScript.bnf`
2. The lexer definition in `src/main/jflex/AngelScript.flex`
3. Token definitions match between lexer and parser

## Validation Strategy

1. **Unit Tests**: Each grammar rule tested individually
2. **Integration Tests**: Complex real-world files from Split Fiction
3. **Performance Tests**: Ensure parsing speed is acceptable
4. **Error Recovery**: Malformed code should produce useful error messages

## Success Criteria

- ✅ All unit tests pass
- ✅ 95%+ of Split Fiction sample files parse without errors
- ✅ No performance regressions
- ✅ Clean PSI tree structure (no unexpected error nodes)
- ✅ Good error recovery for malformed code
