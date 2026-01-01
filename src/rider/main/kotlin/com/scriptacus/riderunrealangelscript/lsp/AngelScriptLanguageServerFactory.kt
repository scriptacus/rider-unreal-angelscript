package com.scriptacus.riderunrealangelscript.lsp

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.client.features.FileUriSupport
import com.redhat.devtools.lsp4ij.client.features.FileUriSupportBase
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import org.eclipse.lsp4j.services.LanguageServer
import java.net.URI

class AngelScriptLanguageServerFactory : LanguageServerFactory {
    private val LOG = Logger.getInstance(AngelScriptLanguageServerFactory::class.java)

    override fun createConnectionProvider(project: Project): StreamConnectionProvider {
        return AngelScriptConnectionProvider(project)
    }

    override fun createLanguageClient(project: Project): LanguageClientImpl {
        return AngelScriptLanguageClient(project)
    }

    override fun getServerInterface(): Class<out LanguageServer> {
        return AngelScriptLanguageServer::class.java
    }

    override fun createClientFeatures(): com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures {
        LOG.info("Creating client features with AngelScriptCompletionFeature")
        return com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures()
            .setFileUriSupport(AngelScriptFileUriSupport())
            .setCompletionFeature(AngelScriptCompletionFeature())
    }

    /**
     * Custom FileUriSupport that normalizes Windows drive letters to lowercase.
     *
     * The AngelScript LSP server (vscode-unreal-angelscript) normalizes all URIs to lowercase
     * using NormalizeUri() function. On Windows, this means drive letters are always lowercase
     * (e.g., file:///d:/project/file.as instead of file:///D:/project/file.as).
     *
     * Without this normalization, URI lookups fail because:
     * 1. Rider sends file:///D:/project/file.as (uppercase D)
     * 2. LSP server normalizes to file:///d:/project/file.as (lowercase d)
     * 3. LSP server sends diagnostics/responses with lowercase URI
     * 4. Rider tries to look up VirtualFile using uppercase URI -> MISMATCH
     *
     * This implementation ensures bidirectional normalization:
     * - Outgoing: VirtualFile -> lowercase URI -> LSP server
     * - Incoming: LSP server lowercase URI -> VirtualFile lookup succeeds
     */
    private class AngelScriptFileUriSupport : FileUriSupportBase() {
        override fun getFileUri(file: VirtualFile): URI? {
            val baseUri = super.getFileUri(file) ?: return null

            // Only normalize on Windows
            if (!SystemInfo.isWindows) {
                return baseUri
            }

            // Normalize drive letter to lowercase
            val uriString = baseUri.toString()
            val normalizedUri = normalizeDriveLetter(uriString)

            return if (normalizedUri != uriString) {
                URI.create(normalizedUri)
            } else {
                baseUri
            }
        }

        override fun toString(file: VirtualFile): String {
            val baseString = super.toString(file)

            // Only normalize on Windows
            if (!SystemInfo.isWindows || baseString == null) {
                return baseString
            }

            return normalizeDriveLetter(baseString)
        }

        override fun findFileByUri(fileUri: String): VirtualFile? {
            // Only denormalize on Windows
            if (!SystemInfo.isWindows) {
                return super.findFileByUri(fileUri)
            }

            // Convert lowercase drive letter back to uppercase for Rider's VirtualFile lookup
            // Incoming: file:///d:/UE5/Main/... → Outgoing: file:///D:/UE5/Main/...
            val denormalizedUri = denormalizeDriveLetter(fileUri)
            val result = super.findFileByUri(denormalizedUri)

            return result
        }

        /**
         * Converts lowercase drive letters back to uppercase for Rider's VirtualFile lookup.
         *
         * Examples:
         * - file:///d:/project/file.as -> file:///D:/project/file.as
         * - file:///c:/Users/... -> file:///C:/Users/...
         */
        private fun denormalizeDriveLetter(uriString: String): String {
            // Match pattern: file:///{drive}:/...
            if (uriString.startsWith("file:///") && uriString.length >= 11) {
                val driveLetterPos = 8 // Position after "file:///"
                val charAfterDrive = uriString.getOrNull(driveLetterPos + 1)

                if (charAfterDrive == ':') {
                    val driveLetter = uriString[driveLetterPos]
                    if (driveLetter.isLowerCase()) {
                        // Replace lowercase drive letter with uppercase
                        return uriString.substring(0, driveLetterPos) +
                                driveLetter.uppercaseChar() +
                                uriString.substring(driveLetterPos + 1)
                    }
                }
            }

            return uriString
        }

        /**
         * Normalizes Windows drive letters to lowercase in a URI string.
         *
         * Examples:
         * - file:///D:/project/file.as -> file:///d:/project/file.as
         * - file:///C:/Users/... -> file:///c:/Users/...
         * - file:///d:/already/lower -> file:///d:/already/lower (no change)
         */
        private fun normalizeDriveLetter(uriString: String): String {
            // Match pattern: file:///{DRIVE}:/...
            // Look for file:/// followed by a single letter and colon
            if (uriString.startsWith("file:///") && uriString.length >= 11) {
                val driveLetterPos = 8 // Position after "file:///"
                val charAfterDrive = uriString.getOrNull(driveLetterPos + 1)

                if (charAfterDrive == ':') {
                    val driveLetter = uriString[driveLetterPos]
                    if (driveLetter.isUpperCase()) {
                        // Replace uppercase drive letter with lowercase
                        return uriString.substring(0, driveLetterPos) +
                                driveLetter.lowercaseChar() +
                                uriString.substring(driveLetterPos + 1)
                    }
                }
            }

            return uriString
        }
    }
}
