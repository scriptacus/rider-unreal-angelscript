# Contributing to Rider Unreal Angelscript

Thank you for your interest in contributing! This document provides guidelines and instructions for developing and contributing to this plugin.

## Development Setup

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21** - [Download from Adoptium](https://adoptium.net/)
- **Node.js** - [Download from nodejs.org](https://nodejs.org/)
- **.NET SDK** - [Download from Microsoft](https://dotnet.microsoft.com/download)
- **JetBrains Rider** (2024.2 or newer) - For testing the plugin

### Initial Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/scriptacus/rider-unreal-angelscript.git
   cd rider-unreal-angelscript
   ```

2. **Configure build properties**:
   ```bash
   cp gradle.properties.template gradle.properties
   ```

   Edit `gradle.properties` and set `org.gradle.java.home` to your Java 21 installation path:
   - Example (Windows): `C:/Program Files/Java/jdk-21`
   - Example (macOS): `/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home`
   - Example (Linux): `/usr/lib/jvm/java-21-openjdk`


3. **Build the plugin**:
   ```bash
   ./gradlew buildPlugin
   ```

## Project Structure

The plugin is a hybrid multi-platform project with three main components:

### 1. Kotlin/IntelliJ Platform (`src/rider/main/kotlin`)

The IDE integration layer providing:
- Language file type registration
- LSP client integration via lsp4ij
- Syntax highlighting and semantic token mapping
- Parser and lexer definitions

**Key files:**
- `AngelScriptFileType.kt` - File type registration
- `AngelScriptLanguage.kt` - Language definition
- `AngelScriptConnectionProvider.kt` - LSP server connection management
- `AngelScriptSemanticTokensColorsProvider.kt` - Syntax highlighting

### 2. .NET/ReSharper (`src/dotnet`)

Backend support for ReSharper and Rider:
- Zone definitions for ReSharper component system
- Built as separate .NET assemblies

**Key files:**
- `IAngelscriptRiderPluginZone.cs` - ReSharper zone definition
- `*.csproj` - Project files for each .NET component

### 3. Language Server (`third-party/vscode-unreal-angelscript`)

External LSP server bundled from the vscode-unreal-angelscript extension:
- TypeScript/JavaScript language server
- Bundled to single JS file via esbuild
- Modified for stdio communication

## Development Workflow

### Building Components

**Build everything:**
```bash
./gradlew buildPlugin
```

**Build only .NET components:**
```bash
./gradlew compileDotNet
```

**Bundle LSP server:**
```bash
npm run bundle
```

**Generate parser/lexer from grammar:**
```bash
./gradlew generateAngelScriptParser generateAngelScriptLexer
```

### Running and Testing

**Launch Rider with the plugin in a sandbox:**
```bash
./gradlew runIde
```

**Run Kotlin tests:**
```bash
./gradlew test
```

**Run .NET tests:**
```bash
./gradlew testDotNet
```
Or directly:
```bash
dotnet test Scriptacus.RiderUnrealAngelscript.sln
```

### Grammar Changes

If you modify the grammar files, you must regenerate the parser and lexer:

1. **Edit grammar files:**
   - `src/main/bnf/AngelScript.bnf` - Parser grammar (Grammar-Kit format)
   - `src/main/jflex/AngelScript.flex` - Lexer specification (JFlex format)

2. **Regenerate parser/lexer:**
   ```bash
   ./gradlew generateAngelScriptParser generateAngelScriptLexer
   ```

3. **Generated files** (do not edit manually):
   - `src/main/gen/com/scriptacus/riderunrealangelscript/lang/parser/`
   - `src/main/gen/com/scriptacus/riderunrealangelscript/lang/psi/`
   - `src/main/gen/com/scriptacus/riderunrealangelscript/lang/lexer/`

### LSP Server Changes

The bundled language server and debug adapter are built from the `third-party/vscode-unreal-angelscript` submodule (a fork of [vscode-unreal-angelscript](https://github.com/Hazelight/vscode-unreal-angelscript)). The submodule is **pinned to a specific commit** and **not updated** - all changes are made directly to our fork.

#### Patching Strategy

The build scripts (`scripts/bundle-lsp.js` and `scripts/bundle-dap.js`) automatically patch the bundled JavaScript after esbuild completes:

**LSP Server Patching** (`scripts/bundle-lsp.js`):
- **VSCode API Stubbing**: Replaces `import * as vscode` with stubs from `scripts/vscode-stub.js`
- **Stdio Communication**: Patches IPC-based communication to use stdin/stdout instead
- **Pattern**: `connection = server_1.createConnection(...)` → `connection = server_1.createConnection(process.stdin, process.stdout)`

**Debug Adapter Patching** (`scripts/bundle-dap.js`):
- **VSCode API Stubbing**: Same as LSP server
- **Unreal Engine Compatibility**: Injects `unreal.setDataBreakpoints([])` during initialization to match VSCode's DAP client behavior
- **Why needed**: Unreal Engine expects this message; LSP4IJ (Rider's DAP client) follows DAP spec strictly and only sends it when user creates data breakpoints

#### Making Changes

Since we maintain a fork and don't update the submodule:

1. **Modify the source** directly in `third-party/vscode-unreal-angelscript/`
2. **Rebuild the bundles**: `npm run bundle` (or `npm run bundle:lsp` / `npm run bundle:dap` individually)
3. **Bundled outputs**:
   - LSP: `src/rider/main/resources/js/angelscript-language-server.js`
   - DAP: `src/rider/main/resources/js/angelscript-debug-adapter.js`
4. **Commit both** the source changes and the bundled outputs

#### Best Practices

1. **Keep patches minimal**: Only patch what's necessary for Rider compatibility
2. **Document patches**: Add comments explaining why each patch is needed
3. **Test thoroughly**: Verify LSP features and debugging after rebundling
4. **Commit together**: Always commit source changes and bundled outputs in the same commit

## Code Style

### Kotlin

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Use IntelliJ IDEA's auto-formatting (Ctrl+Alt+L)

### C#/.NET

- Follow [C# coding conventions](https://docs.microsoft.com/en-us/dotnet/csharp/fundamentals/coding-style/coding-conventions)
- Use PascalCase for types and public members
- Use camelCase for private fields with `_` prefix
- Add XML documentation comments for public APIs

### Commit Messages

- Use clear, descriptive commit messages
- Start with a verb in imperative mood (e.g., "Add", "Fix", "Update", "Remove")
- Keep the first line under 72 characters
- Add detailed explanation in the body if needed

Example:
```
Add support for inlay hints configuration

Implemented user-configurable inlay hints for parameter names
and type information. Settings are synced with LSP server
configuration protocol.
```

## Submitting Changes

### Pull Request Process

1. **Fork the repository** and create a feature branch:
   ```bash
   git checkout -b feature/my-new-feature
   ```

2. **Make your changes** following the code style guidelines

3. **Test thoroughly**:
   - Run all tests: `./gradlew test testDotNet`
   - Test manually in sandbox: `./gradlew runIde`
   - Verify with a real Unreal Engine AngelScript project if possible

4. **Commit your changes** with clear commit messages

5. **Push to your fork**:
   ```bash
   git push origin feature/my-new-feature
   ```

6. **Create a Pull Request** with:
   - Clear description of changes
   - Reference any related issues
   - Screenshots/GIFs for UI changes
   - Test results

### Pull Request Guidelines

- Keep PRs focused on a single feature or fix
- Update documentation if you change user-facing behavior
- Add tests for new functionality
- Ensure all tests pass
- Respond to review feedback promptly

## Reporting Issues

When reporting bugs or requesting features, please include:

- **Plugin version** (from Settings → Plugins)
- **Rider version** (from Help → About)
- **Operating system** and version
- **Steps to reproduce** (for bugs)
- **Expected vs. actual behavior** (for bugs)
- **Logs** (if applicable, from Help → Show Log in Explorer)

## Questions?

- **Issues**: [GitHub Issues](https://github.com/scriptacus/rider-unreal-angelscript/issues)
- **Discussions**: [GitHub Discussions](https://github.com/scriptacus/rider-unreal-angelscript/discussions)

## License

By contributing to this project, you agree that your contributions will be licensed under the MIT License.
