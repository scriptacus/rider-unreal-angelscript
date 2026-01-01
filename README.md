# Rider Unreal Angelscript

[![JetBrains Plugins](https://img.shields.io/badge/JetBrains-Plugin-blue)](https://plugins.jetbrains.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A JetBrains Rider plugin providing comprehensive AngelScript language support for Unreal Engine projects using the [UnrealEngine-Angelscript](https://github.com/Hazelight/UnrealEngine-Angelscript) plugin.

This plugin bundles and integrates the AngelScript language server from the excellent [vscode-unreal-angelscript](https://github.com/Hazelight/vscode-unreal-angelscript) VSCode extension by [Hazelight Studios](https://www.hazelight.se/).

## Features

### Language Support
- **Syntax Highlighting** - Basic syntax highlighting via custom parser/lexer, enhanced with semantic tokens when connected to Unreal Engine
- **Code Completion** - IntelliSense with context-aware suggestions (triggered by `.` and `:`)
- **Signature Help** - Function parameter hints and documentation
- **Hover Documentation** - View documentation for symbols on hover
- **Go to Definition** - Navigate to symbol definitions
- **Find References** - Find all usages of a symbol
- **Rename Refactoring** - Safely rename symbols across your project
- **Code Actions** - Quick fixes and refactorings
- **Inlay Hints** - Parameter names and type information displayed inline
- **Document/Workspace Symbols** - Search for symbols across files

### Unreal Engine Integration
- **Language Server Protocol (LSP)** integration with Unreal Engine
- **Semantic Highlighting** - Enhanced syntax highlighting when connected to Unreal Engine
- **API Browser Tool Window** - Browse and search Unreal/AngelScript API documentation
- **AngelScript File Window** - Browse .as files for your projects and plugins
- **Unreal-Specific Features** - Support for UPROPERTY, UFUNCTION, delegates, and more
- **Auto-Discovery** - Automatically finds Node.js runtime (if available)

### Debugging
- **Debug Adapter Protocol (DAP)** based debugging support
- **Concurrent Debugging** - Debug C++ and AngelScript simultaneously

See [docs/DEBUGGING.md](docs/DEBUGGING.md) for details and setup instructions.

## Requirements

- **JetBrains Rider** 2024.2 or newer
- **Node.js** - Required for the language server (auto-discovered from PATH or Rider settings)
- **Unreal Engine** with [UnrealEngine-Angelscript](https://github.com/Hazelight/UnrealEngine-Angelscript) plugin

## Installation

### From Disk
1. Download the latest `.zip` release from [Releases](https://github.com/scriptacus/rider-unreal-angelscript/releases)
2. In Rider, go to **Settings** → **Plugins**
3. Click ⚙️ → **Install Plugin from Disk...**
4. Select the downloaded `.zip` file
5. Restart Rider

## Configuration

### Node.js Setup
The plugin automatically discovers Node.js in this order:
1. Rider's configured Node.js interpreter (**Settings** → **Languages & Frameworks** → **Node.js**)
2. Node.js in system PATH
3. Common installation locations

If Node.js is not found, install it from [nodejs.org](https://nodejs.org/).

### Unreal Engine Connection
When you open an AngelScript file in a project with the UnrealEngine-Angelscript plugin:
- The language server will automatically start
- Full IntelliSense requires Unreal Engine to be running (default port: 27099)
- Basic syntax highlighting works without Unreal Engine

## Building from Source

See [BUILDING.md](BUILDING.md) for detailed build instructions, development tasks, and troubleshooting.

## Architecture

This is a hybrid multi-platform plugin with three components:

1. **Kotlin/IntelliJ Platform** (`src/rider/main/kotlin`) - IDE integration, LSP client, syntax highlighting
2. **.NET/ReSharper** (`src/dotnet`) - Backend support for ReSharper and Rider
3. **Language Server** (`third-party/vscode-unreal-angelscript`) - Bundled LSP server from the VSCode extension

The language server is bundled via esbuild from the [vscode-unreal-angelscript](https://github.com/Hazelight/vscode-unreal-angelscript) extension. See [THIRD-PARTY-LICENSES.md](THIRD-PARTY-LICENSES.md) for license information.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## Development
Portions of this codebase are written with AI coding tools.<br>
All code has been reviewed and tested by human maintainers.

## Support

- **Issues**: [GitHub Issues](https://github.com/scriptacus/rider-unreal-angelscript/issues)
- **Discussions**: [GitHub Discussions](https://github.com/scriptacus/rider-unreal-angelscript/discussions)
