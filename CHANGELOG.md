# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## 0.9.6 - 2026-01-07

### Fixed
- Improved AS script folder detection performance

## 0.9.5 - 2026-01-06

### Added
- **Improved File Search Priority**
  - AngelScript files now appear with proper priority in "Navigate to File" (Ctrl+Shift+N)
  - Script folders registered as module content roots instead of library roots
  - Files now rank above fuzzy C++ matches when they have exact or better name matches
  - Automatic detection of Script folders next to .uproject and .uplugin files
  - Dynamic updates when Script folders are created or deleted

### Performance
- **Eliminated UI Freezes During Plugin Initialization**
  - Converted library root detection to async background computation
  - getAdditionalProjectLibraries() now returns in <10ms (was blocking 2-5 seconds)
  - VFS traversal 30-50% faster by skipping build/cache directories
  - Implemented lazy cache pattern to avoid EDT blocking
  - Added automatic cancellation of stale computations

### Technical
- Modernized folder detection with Kotlin coroutines
- Implemented EDT thread safety for module modifications
- Content roots persist across IDE restarts (stored in .idea/)
- Defensive logging for cache hits and computation timing

## 0.9.4 - 2026-01-05

### Added
- **Method Separators**
  - Visual separators between class methods and functions in the editor
  - Improves code readability with horizontal lines between method declarations
  - Automatically enabled for AngelScript files

### Fixed
- **Parser Error Recovery**
  - Fixed handling of ternary operators in formatted strings (e.g., `f"{value:condition ? 'Y' : 'N'}"`)
  - Improved error recovery with pinning on expression start and format separator
  - Parser now gracefully handles complex format expressions

### Performance
- **Script Files Tool Window**
  - Optimized tree cell rendering with 50% reduction in VCS status queries
  - Single status check per cell instead of separate checks for text and icon

## 0.9.3 - 2026-01-04

### Added
- **C++ Navigation**
  - Navigate from AngelScript symbols to C++ source code
  - Two navigation strategies:
    - Rider text search: Direct navigation in Rider using header file search with UE naming convention support
    - Unreal Engine delegation: Opens C++ source in configured IDE via RiderLink
  - Configurable in Settings → Tools → AngelScript → Navigation
  - Intelligent header file ranking (Public/ > Classes/ > Runtime/ > Private/)
  - Performance caching for repeated lookups

- **Script Ignore Patterns**
  - Filter files from LSP server using glob patterns
  - Default patterns exclude build artifacts (**/Saved/**) and VCS metadata (**/.plastic/**)
  - Prevents duplicate modules and improves LSP performance
  - Blocks navigation to excluded directories

- **VCS Integration**
  - Script Files tool window now shows VCS status colors
  - Async loading prevents EDT blocking
  - Auto-updates when file status changes

### Changed
- **Completion Icons**
  - Updated completion item icons for scripting language semantics
  - Method → Function icon (matches AngelScript terminology)
  - Field → Property icon (matches AngelScript terminology)

- **Icon System**
  - Added light/dark theme icon support for project and file icons

### Fixed
- Custom completion proposal fixes lsp4ij's additionalTextEdits ordering bug with stable sort

## 0.9.2 - 2026-01-02

### Fixed
- **Code Formatter Improvements**
  - Fixed brace alignment for methods with access specifiers (private, protected, public)
    - Braces now correctly align to the start of the method declaration instead of the return type
  - Fixed block content indentation to use consistent indent levels
    - Content inside braces no longer aligns to arbitrary positions based on PSI tree structure
  - Example: `private void Foo() { }` now formats correctly with braces aligned to "private"

- **Scoped Identifier Completion**
  - Fixed autocomplete for scoped identifiers (e.g., `FVector::ZeroVector`)
  - Completion now correctly replaces only the identifier after `::` instead of the entire scoped path
  - Example: `FVector::Z<TAB>` now completes to `FVector::ZeroVector` (was incorrectly `FVectorZeroVector`)
  - Handles both complete (`::`) and incomplete (`:`) scope resolution operators

### Changed
- **Grammar and Parser Updates**
  - Updated grammar to better handle scoped identifiers and member access expressions
  - Improved PSI structure for identifier references and scope resolution
  - Regenerated parser and updated all test expectations to match new PSI structure

### Technical
- Excluded formatter tests from automated CI runs (requires full Rider infrastructure)
- Added test data files for manual formatter verification

## 0.9.1 - 2026-01-01

### Added
- **Tool Window Features**
  - 'Always Select Opened File' toggle in AngelScript Files tool window
    - Automatically scrolls to currently opened file in the tree
    - Persists setting across IDE restarts
  - 'Auto-Switch Tool Windows' feature
    - Automatically switches between AngelScript Files and Project windows
    - Switches to AngelScript Files when opening .as/.ash files
    - Switches to Project window when opening other files
    - Persists setting across IDE restarts
  - Both features accessible via Behavior submenu in gear menu
- Automated release workflow for CI/CD

### Fixed
- AngelScript Files tool window now correctly displays standalone Script folders (not part of .uproject)
- Autocomplete cursor positioning corrected
- Release workflow improvements with proper permissions and error handling

### Documentation
- Updated debugging documentation

## 0.9.0 - 2024-12-01
- First release
