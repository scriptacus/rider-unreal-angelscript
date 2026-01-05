# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

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
