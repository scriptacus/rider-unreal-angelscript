package com.scriptacus.riderunrealangelscript.navigation

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptFile
import com.scriptacus.riderunrealangelscript.lsp.AngelScriptLanguageServer
import com.scriptacus.riderunrealangelscript.settings.AngelScriptLspSettings
import com.scriptacus.riderunrealangelscript.util.GlobMatcher
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.LSPIJUtils
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.DefinitionParams

/**
 * GotoDeclarationHandler that filters navigation to excluded files and provides C++ navigation.
 *
 * This handler runs BEFORE lsp4ij's default navigation handler and:
 * 1. Blocks navigation to files matching scriptIgnorePatterns (e.g., build artifacts in Saved/)
 * 2. Provides C++ navigation for Unreal Engine symbols (when enabled in settings)
 *
 * Navigation filtering:
 * - Queries LSP server for definition location before navigation occurs
 * - Checks target path against ignore patterns
 * - Returns empty array to block navigation to excluded files
 *
 * C++ navigation strategy (when target is a C++ symbol):
 * 1. Resolves AngelScript symbol to C++ class/method via LSP server
 * 2. Attempts text-based search in C++ header files (if sources are in project)
 * 3. Falls back to Unreal Engine navigation (opens in configured IDE via RiderLink)
 */
class AngelScriptCppNavigationHandler : GotoDeclarationHandler {

    private val LOG = Logger.getInstance(AngelScriptCppNavigationHandler::class.java)

    companion object {
        /**
         * Cache mapping class names to their resolved header file paths.
         * Key: className, Value: VirtualFile path string
         * This avoids repeated file system searches for the same class.
         */
        private val headerFileCache = java.util.concurrent.ConcurrentHashMap<String, String>()

        /**
         * Cache mapping UE module names to their base directories.
         * Key: module name (e.g., "Engine", "CoreUObject"), Value: module base path
         */
        private val modulePathCache = java.util.concurrent.ConcurrentHashMap<String, String>()

        /**
         * Deduplication cache to prevent processing the same navigation request multiple times.
         * Key: "uri:line:character", Value: timestamp of last processing
         */
        private val lastNavigationRequest = java.util.concurrent.ConcurrentHashMap<String, Long>()
        private const val NAVIGATION_DEBOUNCE_MS = 100L // Ignore duplicate requests within 100ms

        /**
         * Clear all caches. Called when project structure changes.
         */
        fun clearCaches() {
            headerFileCache.clear()
            modulePathCache.clear()
            lastNavigationRequest.clear()
        }
    }

    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor?
    ): Array<PsiElement>? {
        // Only handle AngelScript files
        if (sourceElement == null || editor == null) return null
        val psiFile = sourceElement.containingFile
        if (psiFile !is AngelScriptFile) return null

        // Don't compete with LSP navigation if indices aren't ready
        val project = editor.project ?: return null
        if (DumbService.isDumb(project)) {
            return null
        }

        try {
            // First, check if navigation target is an excluded file
            val virtualFile = psiFile.virtualFile ?: return null
            val document = editor.document
            val lineNumber = document.getLineNumber(offset)
            val lineStartOffset = document.getLineStartOffset(lineNumber)
            val character = offset - lineStartOffset
            val position = Position(lineNumber, character)
            val lspUri = LSPIJUtils.toUri(virtualFile).toString()

            if (shouldBlockNavigation(project, lspUri, position)) {
                // Block navigation by returning empty array
                LOG.info("Blocked navigation to excluded file")
                return emptyArray()
            }

            // Try to get C++ symbol info with a short timeout
            // This blocks briefly to check if this is a C++ symbol before returning null
            val cppSymbol = getCppSymbolWithTimeout(project, lspUri, position, 300)

            if (cppSymbol != null) {
                // This is a C++ symbol - handle navigation
                val logMsg = if (cppSymbol.symbolName.isEmpty()) {
                    "C++ class: ${cppSymbol.className}"
                } else {
                    "C++ symbol: ${cppSymbol.className}.${cppSymbol.symbolName}"
                }
                LOG.info(logMsg)

                // Offload navigation to background thread to avoid blocking EDT
                ApplicationManager.getApplication().executeOnPooledThread {
                    performCppNavigation(project, lspUri, position, cppSymbol)
                }

                // Return null - we're handling navigation ourselves asynchronously
                // The brief wait above ensures we know it's a C++ symbol, so no "Cannot navigate" message
                return null
            }

        } catch (e: Exception) {
            LOG.debug("Error in C++ navigation handler", e)
        }

        // Not a C++ symbol or error occurred - let lsp4ij's default navigation handler take over
        return null
    }

    /**
     * Check if navigation should be blocked because the target is in an excluded directory.
     * Queries the LSP server for the definition location and checks against ignore patterns.
     *
     * @return true if navigation should be blocked, false otherwise
     */
    private fun shouldBlockNavigation(
        project: com.intellij.openapi.project.Project,
        uri: String,
        position: Position
    ): Boolean {
        try {
            // Check if we have any ignore patterns configured
            val settings = AngelScriptLspSettings.getInstance()
            val ignorePatterns = settings.state.scriptIgnorePatterns
            if (ignorePatterns.isEmpty()) {
                return false
            }

            // Query LSP server for definition location with a short timeout
            val manager = LanguageServerManager.getInstance(project)
            val serverItemFuture = manager.getLanguageServer("angelscript-lsp")
            val serverItem = serverItemFuture.get(200, java.util.concurrent.TimeUnit.MILLISECONDS)
                ?: return false

            val server = serverItem.server as? org.eclipse.lsp4j.services.LanguageServer ?: return false

            // Request definition
            val textDocumentIdentifier = TextDocumentIdentifier(uri)
            val definitionParams = DefinitionParams(textDocumentIdentifier, position)
            val definitionFuture = server.textDocumentService.definition(definitionParams)
            val result = definitionFuture.get(200, java.util.concurrent.TimeUnit.MILLISECONDS)
                ?: return false

            // Check if any of the returned locations are in excluded directories
            // The result is Either<List<Location>, List<LocationLink>>
            val locationsAny: List<*> = if (result.isLeft) {
                // Left side: List<Location>
                result.left as? List<*> ?: emptyList<Any>()
            } else {
                // Right side: List<LocationLink> - we ignore these since we only need URIs
                emptyList<Any>()
            }
            val locations: List<org.eclipse.lsp4j.Location> = locationsAny.mapNotNull { it as? org.eclipse.lsp4j.Location }

            // Check each location against ignore patterns
            for (location in locations) {
                val targetUri = location.uri
                // Convert file:// URI to path
                val targetPath = java.net.URI(targetUri).path

                if (GlobMatcher.matchesAny(targetPath, ignorePatterns)) {
                    LOG.info("Navigation target matches ignore pattern: $targetPath")
                    return true
                }
            }

            return false

        } catch (e: java.util.concurrent.TimeoutException) {
            LOG.debug("Timeout checking navigation target, allowing navigation")
            return false
        } catch (e: Exception) {
            LOG.debug("Error checking navigation target: ${e.message}, allowing navigation")
            return false
        }
    }

    /**
     * Get C++ symbol information with a timeout.
     * Blocks for up to timeoutMs waiting for the LSP server response.
     * Returns null if timeout expires or no C++ symbol found.
     */
    private fun getCppSymbolWithTimeout(
        project: com.intellij.openapi.project.Project,
        uri: String,
        position: Position,
        timeoutMs: Long
    ): CppSymbolInfo? {
        try {
            val manager = LanguageServerManager.getInstance(project)
            val serverItemFuture = manager.getLanguageServer("angelscript-lsp")

            // Wait for server with timeout
            val serverItem = serverItemFuture.get(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)
                ?: return null

            val server = serverItem.server as? AngelScriptLanguageServer ?: return null

            val params = mapOf(
                "uri" to uri,
                "position" to mapOf(
                    "line" to position.line,
                    "character" to position.character
                )
            )

            // Wait for C++ symbol info with timeout
            val cppSymbolResponse = server.getCppSymbol(params)
                .get(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)
                ?: return null

            val className = cppSymbolResponse["className"] as? String ?: ""
            val symbolName = cppSymbolResponse["symbolName"] as? String ?: ""

            // If LSP returns className="" for class navigation, swap values
            return if (className.isEmpty() && symbolName.isNotEmpty()) {
                CppSymbolInfo(symbolName, "")
            } else {
                CppSymbolInfo(className, symbolName)
            }

        } catch (e: java.util.concurrent.TimeoutException) {
            LOG.debug("Timeout waiting for C++ symbol info")
            return null
        } catch (e: Exception) {
            LOG.debug("Error getting C++ symbol info", e)
            return null
        }
    }

    /**
     * Perform C++ navigation based on the configured strategy.
     */
    private fun performCppNavigation(
        project: com.intellij.openapi.project.Project,
        uri: String,
        position: Position,
        cppSymbol: CppSymbolInfo
    ) {
        // Get navigation strategy from settings
        val settings = AngelScriptLspSettings.getInstance()
        val strategy = settings.state.cppNavigationStrategy

        when (strategy) {
            AngelScriptLspSettings.CppNavigationStrategy.TEXT_SEARCH -> {
                LOG.info("Using text search navigation strategy")
                attemptTextSearchNavigation(project, cppSymbol)
            }
            AngelScriptLspSettings.CppNavigationStrategy.UNREAL_ENGINE -> {
                LOG.info("Using Unreal Engine navigation strategy")
                delegateToUnrealEngine(project, uri, position)
            }
        }
    }

    /**
     * Delegate navigation to Unreal Engine via LSP implementation request.
     * The server's onImplementation handler will send GoToDefinition to UE.
     */
    private fun delegateToUnrealEngine(
        project: com.intellij.openapi.project.Project,
        uri: String,
        position: Position
    ) {
        try {
            val manager = LanguageServerManager.getInstance(project)
            val serverItemFuture = manager.getLanguageServer("angelscript-lsp")

            serverItemFuture.thenAccept { serverItem ->
                if (serverItem != null) {
                    val server = serverItem.server as? org.eclipse.lsp4j.services.LanguageServer
                    if (server != null) {
                        val textDocumentIdentifier = org.eclipse.lsp4j.TextDocumentIdentifier(uri)
                        val implementationParams = org.eclipse.lsp4j.ImplementationParams(textDocumentIdentifier, position)

                        server.textDocumentService.implementation(implementationParams).thenAccept { result ->
                            LOG.info("Implementation request completed, result: $result")
                        }.exceptionally { throwable ->
                            LOG.warn("Implementation request failed: ${throwable.message}")
                            null
                        }
                    }
                }
            }
        } catch (e: Exception) {
            LOG.warn("Failed to delegate to Unreal Engine", e)
        }
    }


    /**
     * Attempt to navigate directly to C++ symbol in Rider using text-based search.
     * Returns true if navigation succeeded, false if C++ sources not found.
     */
    private fun attemptTextSearchNavigation(
        project: com.intellij.openapi.project.Project,
        cppSymbol: CppSymbolInfo
    ): Boolean {
        // Execute search on background thread with read access, then navigate on UI thread
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                // File index access requires read action
                val targetFile = ApplicationManager.getApplication().runReadAction<com.intellij.openapi.vfs.VirtualFile?> {
                    findCppHeaderFile(project, cppSymbol.className)
                }

                if (targetFile != null) {
                    LOG.info("Found C++ header file: ${targetFile.path}")

                    // Document/file access also requires read action
                    val offset = ApplicationManager.getApplication().runReadAction<Int> {
                        // If symbolName is empty, we're navigating to the class itself
                        if (cppSymbol.symbolName.isEmpty()) {
                            findClassDeclarationInFile(project, targetFile, cppSymbol.className)
                        } else {
                            // Search for the symbol within the file
                            findSymbolInFile(project, targetFile, cppSymbol.symbolName)
                        }
                    }

                    if (offset >= 0) {
                        // Navigate on UI thread
                        ApplicationManager.getApplication().invokeLater {
                            val navigation = PsiNavigationSupport.getInstance()
                            val descriptor = navigation.createNavigatable(project, targetFile, offset)
                            descriptor.navigate(true)
                            val symbolDesc = if (cppSymbol.symbolName.isEmpty()) {
                                cppSymbol.className
                            } else {
                                "${cppSymbol.className}.${cppSymbol.symbolName}"
                            }
                            LOG.info("Navigated to C++ symbol via text search: $symbolDesc")
                        }
                    } else {
                        val symbolDesc = if (cppSymbol.symbolName.isEmpty()) "class ${cppSymbol.className}" else cppSymbol.symbolName
                        LOG.debug("Symbol $symbolDesc not found in ${targetFile.name}")
                    }
                } else {
                    LOG.debug("C++ header file not found for class: ${cppSymbol.className}")
                }
            } catch (e: Exception) {
                LOG.warn("Error during text search navigation", e)
            }
        }

        // Return false to indicate async navigation (caller should not wait)
        // We return false so the fallback navigation can also execute
        return false
    }

    /**
     * Find the C++ header file for a given class name.
     * Handles Unreal Engine naming conventions (e.g., "AActor" -> "Actor.h", "UObject" -> "Object.h")
     * Follows UE's search patterns: Public/, Classes/, then Private/ as fallback.
     * Uses caching to avoid repeated file system searches.
     * Also handles AngelScript binding files for global functions.
     */
    private fun findCppHeaderFile(
        project: com.intellij.openapi.project.Project,
        className: String
    ): com.intellij.openapi.vfs.VirtualFile? {
        // If className is empty, this might be a global function (binding)
        if (className.isEmpty()) {
            return findBindingFile(project, null)
        }

        // Check cache first
        val cachedPath = headerFileCache[className]
        if (cachedPath != null) {
            // Check if cached path is in an excluded directory
            val isExcluded = cachedPath.contains("/Saved/") || cachedPath.contains("\\Saved\\") ||
                             cachedPath.contains("/Intermediate/") || cachedPath.contains("\\Intermediate\\") ||
                             cachedPath.contains("/Binaries/") || cachedPath.contains("\\Binaries\\") ||
                             cachedPath.contains("/DerivedDataCache/") || cachedPath.contains("\\DerivedDataCache\\")

            if (isExcluded) {
                LOG.debug("Cached path is in excluded directory, invalidating: $cachedPath")
                headerFileCache.remove(className)
            } else {
                val cachedFile = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByPath(cachedPath)
                if (cachedFile != null && cachedFile.exists()) {
                    LOG.debug("Using cached header file for $className: $cachedPath")
                    return cachedFile
                } else {
                    // Cached file no longer exists, remove from cache
                    headerFileCache.remove(className)
                }
            }
        }

        // Strip UE prefix (A, U, F, E, I, T) to get base class name
        val baseName = when {
            className.startsWith("A") || className.startsWith("U") ||
            className.startsWith("F") || className.startsWith("E") ||
            className.startsWith("I") || className.startsWith("T") -> className.substring(1)
            else -> className
        }

        val scope = GlobalSearchScope.allScope(project)

        // Try to find header file by name
        // UE convention: BaseName.h is the standard, sometimes FullName.h is used
        val candidateNames = listOf(
            "$baseName.h",           // Actor.h (most common)
            "$className.h",          // AActor.h (less common, but used for some classes)
            "$baseName.generated.h"  // Actor.generated.h (generated code, last resort)
        )

        for (fileName in candidateNames) {
            val files = FilenameIndex.getVirtualFilesByName(fileName, scope)
            if (files.isEmpty()) continue

            LOG.debug("Found ${files.size} candidates for $fileName")

            // UE directory preference order (matches SourceCodeNavigation.cpp):
            // 1. Public/ - Public API headers
            // 2. Classes/ - Legacy location for headers
            // 3. Runtime/ paths - Engine runtime modules
            // 4. Editor/ paths - Editor-only modules
            // 5. Private/ - Private implementation headers (fallback)
            // 6. Any other location

            val rankedFiles = files
                .filter { file ->
                    val path = file.path
                    val excluded = path.contains("/Saved/") || path.contains("\\Saved\\") ||
                                   path.contains("/Intermediate/") || path.contains("\\Intermediate\\") ||
                                   path.contains("/Binaries/") || path.contains("\\Binaries\\") ||
                                   path.contains("/DerivedDataCache/") || path.contains("\\DerivedDataCache\\")

                    if (excluded) {
                        LOG.debug("Excluding build artifact: $path")
                    }
                    !excluded
                }
                .map { file ->
                    val path = file.path
                    val rank = when {
                        // Prefer Public/ directories (standard UE location for headers)
                        path.contains("/Public/") || path.contains("\\Public\\") -> 0

                        // Classes/ is legacy but still used
                        path.contains("/Classes/") || path.contains("\\Classes\\") -> 1

                        // Runtime modules (Engine core)
                        path.contains("/Runtime/") || path.contains("\\Runtime\\") -> 2

                        // Editor modules
                        path.contains("/Editor/") || path.contains("\\Editor\\") -> 3

                        // Private/ as fallback (implementation headers)
                        path.contains("/Private/") || path.contains("\\Private\\") -> 4

                        // Anything else gets lower priority
                        else -> 5
                    }
                    file to rank
                }
                .sortedBy { it.second }

            if (rankedFiles.isNotEmpty()) {
                val bestFile = rankedFiles.first().first
                // Cache the result for future lookups
                headerFileCache[className] = bestFile.path
                LOG.debug("Cached header file for $className: ${bestFile.path}")
                return bestFile
            }
        }

        return null
    }

    /**
     * Find AngelScript binding file for global functions or specific classes.
     * Searches for Bind_*.cpp files in the AngelScript plugin directory.
     */
    private fun findBindingFile(
        project: com.intellij.openapi.project.Project,
        symbolName: String?
    ): com.intellij.openapi.vfs.VirtualFile? {
        val scope = GlobalSearchScope.allScope(project)

        // Common binding file patterns
        val bindingFilePatterns = listOf(
            "Bind_Logging.cpp",        // Print, Log, etc.
            "Bind_CoreGlobals.cpp",    // Global utility functions
            "Bind_Console.cpp",         // Console commands
            "Bind_Math.cpp"            // Math functions
        )

        for (pattern in bindingFilePatterns) {
            val files = FilenameIndex.getVirtualFilesByName(pattern, scope)
            if (files.isNotEmpty()) {
                // Prefer files in Angelscript plugin directory
                val preferredFile = files.firstOrNull { file ->
                    file.path.contains("/Angelscript/") || file.path.contains("\\Angelscript\\")
                } ?: files.first()

                // If symbolName provided, check if it's in this file
                if (symbolName != null) {
                    if (symbolExistsInFile(project, preferredFile, symbolName)) {
                        return preferredFile
                    }
                } else {
                    return preferredFile
                }
            }
        }

        return null
    }

    /**
     * Quick check if a symbol name exists in a file (used for binding file selection).
     */
    private fun symbolExistsInFile(
        project: com.intellij.openapi.project.Project,
        file: com.intellij.openapi.vfs.VirtualFile,
        symbolName: String
    ): Boolean {
        try {
            val document = com.intellij.openapi.fileEditor.FileDocumentManager.getInstance().getDocument(file)
                ?: return false
            return document.text.contains(symbolName)
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Search for a symbol definition within a file and return its offset.
     * Looks for patterns like:
     * - UFUNCTION() FVector GetActorLocation() const;
     * - virtual FVector GetActorLocation() const;
     * - FVector GetActorLocation();
     *
     * Filters out matches in comments and prioritizes actual declarations.
     */
    private fun findSymbolInFile(
        project: com.intellij.openapi.project.Project,
        file: com.intellij.openapi.vfs.VirtualFile,
        symbolName: String
    ): Int {
        try {
            val document = com.intellij.openapi.fileEditor.FileDocumentManager.getInstance().getDocument(file)
                ?: return -1

            val text = document.text

            // Strategy 1: Look for UFUNCTION macro followed by the symbol
            // Pattern: UFUNCTION(...) [newlines/whitespace] [modifiers] ReturnType SymbolName(
            val ufunctionPattern = """UFUNCTION\s*\([^)]*\)\s*(?:\w+\s+)*\w+\s+$symbolName\s*\(""".toRegex(RegexOption.MULTILINE)
            val ufunctionMatch = ufunctionPattern.find(text)
            if (ufunctionMatch != null) {
                // Find the actual symbol position within the match
                val symbolInMatch = Regex("\\b$symbolName\\s*\\(").find(ufunctionMatch.value)
                if (symbolInMatch != null) {
                    return ufunctionMatch.range.first + symbolInMatch.range.first
                }
            }

            // Strategy 2: Look for function declarations (avoid comments)
            // Match line-based patterns that look like declarations:
            // - Must have valid C++ identifier characters before the symbol name
            // - Should have return type or modifiers (virtual, static, etc.)
            // - Should NOT be inside /* */ or after //

            val allMatches = mutableListOf<Pair<Int, String>>()
            val symbolPattern = "\\b$symbolName\\s*\\(".toRegex()

            symbolPattern.findAll(text).forEach { match ->
                val lineStart = text.lastIndexOf('\n', match.range.first).let { if (it == -1) 0 else it + 1 }
                val lineEnd = text.indexOf('\n', match.range.first).let { if (it == -1) text.length else it }
                val line = text.substring(lineStart, lineEnd).trim()

                // Filter out obvious non-declarations
                val isLikelyDeclaration = when {
                    // Skip if line starts with comment markers
                    line.startsWith("//") -> false
                    line.startsWith("*") -> false
                    line.startsWith("/*") -> false

                    // Skip if inside a block comment (check backwards from match)
                    isInsideBlockComment(text, match.range.first) -> false

                    // Prefer lines with typical declaration keywords
                    line.contains(Regex("\\b(virtual|static|inline|explicit|constexpr|ENGINE_API|UFUNCTION)\\b")) -> true

                    // Prefer lines with typical return types (class names, primitive types)
                    line.contains(Regex("\\b(void|bool|int|float|double|[UFA]\\w+)\\s+$symbolName\\s*\\(")) -> true

                    // Skip if it looks like a comment (contains common comment words before the symbol)
                    line.substringBefore(symbolName).contains(Regex("\\b(cached|called|see|note|todo|fixme)\\b", RegexOption.IGNORE_CASE)) -> false

                    else -> false
                }

                if (isLikelyDeclaration) {
                    allMatches.add(match.range.first to line)
                }
            }

            // Return the first likely declaration
            return allMatches.firstOrNull()?.first ?: -1

        } catch (e: Exception) {
            LOG.warn("Error searching for symbol in file", e)
            return -1
        }
    }

    /**
     * Check if a position in text is inside a block comment.
     */
    private fun isInsideBlockComment(text: String, position: Int): Boolean {
        // Find the last /* before position
        val lastCommentStart = text.lastIndexOf("/*", position)
        if (lastCommentStart == -1) return false

        // Find the first */ after that /*
        val commentEnd = text.indexOf("*/", lastCommentStart)

        // If there's no closing */ or it's after our position, we're inside a comment
        return commentEnd == -1 || commentEnd > position
    }

    /**
     * Find the class/struct declaration within a C++ header file.
     * Looks for patterns like:
     * - class AActor : public UObject
     * - struct FHitResult
     * - UCLASS() class AActor : public UObject
     * - USTRUCT() struct FVector
     *
     * Filters out forward declarations (e.g., "class AActor;", "struct FHitResult;")
     */
    private fun findClassDeclarationInFile(
        project: com.intellij.openapi.project.Project,
        file: com.intellij.openapi.vfs.VirtualFile,
        className: String
    ): Int {
        try {
            val document = com.intellij.openapi.fileEditor.FileDocumentManager.getInstance().getDocument(file)
                ?: return -1

            val text = document.text

            // Strategy 1: Look for UCLASS/USTRUCT macro followed by the type declaration
            // Pattern: UCLASS(...) [newlines/whitespace] class ClassName
            // Pattern: USTRUCT(...) [newlines/whitespace] struct StructName
            val umacroPattern = """U(?:CLASS|STRUCT)\s*\([^)]*\)\s*(?:class|struct)\s+\b$className\b""".toRegex(RegexOption.MULTILINE)
            val umacroMatch = umacroPattern.find(text)
            if (umacroMatch != null) {
                // Find the actual class/struct keyword position within the match
                val typeInMatch = Regex("\\b(?:class|struct)\\s+$className\\b").find(umacroMatch.value)
                if (typeInMatch != null) {
                    return umacroMatch.range.first + typeInMatch.range.first
                }
            }

            // Strategy 2: Look for class/struct declarations (avoid forward declarations)
            // Match: class ClassName : (inheritance) or struct ENGINE_API StructName
            // Also matches custom API macros like ALS_API, MYMODULE_API, etc.
            val allMatches = mutableListOf<Pair<Int, String>>()
            val classPattern = "\\b(?:class|struct)\\s+(?:\\w+_API\\s+)?\\b$className\\b".toRegex()

            classPattern.findAll(text).forEach { match ->
                val lineStart = text.lastIndexOf('\n', match.range.first).let { if (it == -1) 0 else it + 1 }
                val lineEnd = text.indexOf('\n', match.range.first).let { if (it == -1) text.length else it }
                val line = text.substring(lineStart, lineEnd).trim()

                // Filter out forward declarations and comments
                val isActualDeclaration = when {
                    // Skip forward declarations (end with semicolon on same line)
                    line.endsWith(";") -> false

                    // Skip if line starts with comment markers
                    line.startsWith("//") -> false
                    line.startsWith("*") -> false
                    line.startsWith("/*") -> false

                    // Skip if inside a block comment
                    isInsideBlockComment(text, match.range.first) -> false

                    // Skip if it looks like a parameter (e.g., "class AActor* Actor")
                    line.contains(Regex("\\*|&")) -> false

                    // Prefer lines with inheritance (: public/protected/private)
                    line.contains(Regex(":\\s*(public|protected|private)")) -> true

                    // Prefer lines with UCLASS/USTRUCT or _API macros
                    line.contains(Regex("\\b(UCLASS|USTRUCT|\\w+_API)\\b")) -> true

                    // Accept lines that have opening brace (actual declarations often have { on same or next line)
                    // Check a bit ahead for opening brace
                    text.substring(match.range.first, (match.range.first + 200).coerceAtMost(text.length))
                        .contains("{") -> true

                    // If none of the above, it's likely not a full declaration
                    else -> false
                }

                if (isActualDeclaration) {
                    allMatches.add(match.range.first to line)
                }
            }

            // Return the first actual declaration
            return allMatches.firstOrNull()?.first ?: -1

        } catch (e: Exception) {
            LOG.warn("Error searching for class declaration in file", e)
            return -1
        }
    }

    /**
     * Reconstruct a source file path for relocated builds.
     *
     * When UE is compiled on a different machine (e.g., from Epic, or moved after compilation),
     * PDB/dSYM files may contain absolute paths from the original build machine.
     * This method attempts to reconstruct the correct local path by:
     * 1. Splitting the path into components
     * 2. Searching for the file starting from known UE roots (Engine/, Project/)
     * 3. Progressively building paths from right to left until file is found
     *
     * Based on FSourceCodeNavigation::NavigateToFunctionSource (SourceCodeNavigation.cpp:567-620)
     *
     * @param originalPath The path from debug symbols (may be from different machine)
     * @param fileName The file name to search for
     * @return The reconstructed local path, or null if not found
     */
    private fun reconstructSourcePath(
        project: com.intellij.openapi.project.Project,
        originalPath: String,
        fileName: String
    ): com.intellij.openapi.vfs.VirtualFile? {
        // Split path on both forward slash and backslash
        val tokens = originalPath.split(Regex("[/\\\\]"))

        // Common UE base paths to search
        val basePaths = mutableListOf<String>()

        // Try to find Engine directory (UE5/Main/Engine or UE5/Engine)
        val engineMarkers = listOf("Engine", "UE5", "UE4", "UnrealEngine")
        var enginePath: String? = null

        // Search for Engine directory in project content roots
        val contentRoots = com.intellij.openapi.roots.ProjectRootManager.getInstance(project).contentRoots
        for (root in contentRoots) {
            var current = root
            for (i in 0..5) { // Search up to 5 levels
                if (engineMarkers.any { current.path.contains(it) }) {
                    // Found a potential engine path
                    val pathStr = current.path
                    if (pathStr.contains("/Engine") || pathStr.contains("\\Engine")) {
                        enginePath = pathStr.substringBefore("/Engine") + "/Engine"
                        break
                    } else if (pathStr.contains("/UE5") || pathStr.contains("\\UE5")) {
                        enginePath = pathStr.substringBefore("/UE5") + "/UE5"
                        break
                    }
                }
                current = current.parent ?: break
            }
            if (enginePath != null) break
        }

        if (enginePath != null) {
            basePaths.add(enginePath)
        }

        // Add project base path
        project.basePath?.let { basePaths.add(it) }

        // Try each base path
        for (basePath in basePaths) {
            // Build path from right to left (fileName first, then parent dirs)
            var pathTail = fileName

            for (i in tokens.size - 2 downTo 0) { // -2 because fileName is already included
                val token = tokens[i]
                if (token.isEmpty()) continue

                // Try the current reconstruction
                val candidatePath = "$basePath/$pathTail"
                val candidateFile = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByPath(candidatePath)

                if (candidateFile != null && candidateFile.exists()) {
                    LOG.info("Reconstructed path for relocated build: $originalPath -> $candidatePath")
                    return candidateFile
                }

                // Prepend next directory component
                pathTail = "$token/$pathTail"
            }
        }

        return null
    }
}
