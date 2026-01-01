package com.scriptacus.riderunrealangelscript.util

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import java.io.File

/**
 * Manages temporary files with proper lifecycle cleanup using the Disposable pattern.
 *
 * This class tracks all temporary files created during its lifetime and ensures they are
 * deleted when the manager is disposed. This is more reliable than File.deleteOnExit(),
 * which fails if the JVM crashes and can accumulate files over time.
 *
 * Usage:
 * ```kotlin
 * val manager = TempFileManager()
 * Disposer.register(parentDisposable, manager)
 *
 * val tempFile = manager.createTempFile("prefix", ".js")
 * // Use the file...
 * // File will be automatically deleted when manager is disposed
 * ```
 */
class TempFileManager : Disposable {
    private val LOG = Logger.getInstance(TempFileManager::class.java)

    private val tempFiles = mutableListOf<File>()
    private var disposed = false

    /**
     * Creates a temporary file that will be tracked and cleaned up on disposal.
     *
     * @param prefix The prefix string to be used in generating the file's name
     * @param suffix The suffix string to be used in generating the file's name (e.g., ".js")
     * @param directory The directory in which the file is to be created, or null for system temp directory
     * @return The created temporary file
     * @throws IllegalStateException if this manager has already been disposed
     */
    @Synchronized
    fun createTempFile(prefix: String, suffix: String, directory: File? = null): File {
        check(!disposed) { "TempFileManager has already been disposed" }

        val tempFile = File.createTempFile(prefix, suffix, directory)
        tempFiles.add(tempFile)
        LOG.debug("Created temp file: ${tempFile.absolutePath}")
        return tempFile
    }

    /**
     * Cleans up all tracked temporary files.
     *
     * Cleanup failures are logged but do not throw exceptions, ensuring that disposal
     * can complete even if some files cannot be deleted (e.g., locked files on Windows).
     */
    @Synchronized
    override fun dispose() {
        if (disposed) {
            return
        }

        disposed = true

        val filesToDelete = tempFiles.toList()
        tempFiles.clear()

        for (file in filesToDelete) {
            try {
                if (file.exists()) {
                    val deleted = file.delete()
                    if (deleted) {
                        LOG.debug("Deleted temp file: ${file.absolutePath}")
                    } else {
                        LOG.warn("Failed to delete temp file: ${file.absolutePath}")
                    }
                }
            } catch (e: Exception) {
                LOG.warn("Error deleting temp file: ${file.absolutePath}", e)
            }
        }
    }

    /**
     * Returns the number of temporary files currently being tracked.
     */
    @Synchronized
    fun getTrackedFileCount(): Int = tempFiles.size

    /**
     * Returns true if this manager has been disposed.
     */
    @Synchronized
    fun isDisposed(): Boolean = disposed
}
