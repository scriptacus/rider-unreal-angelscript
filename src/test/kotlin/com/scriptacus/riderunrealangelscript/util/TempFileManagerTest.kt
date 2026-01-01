package com.scriptacus.riderunrealangelscript.util

import com.intellij.testFramework.UsefulTestCase
import java.io.File

/**
 * Tests for TempFileManager.
 */
class TempFileManagerTest : UsefulTestCase() {

    private lateinit var manager: TempFileManager

    override fun setUp() {
        super.setUp()
        manager = TempFileManager()
    }

    override fun tearDown() {
        try {
            if (!manager.isDisposed()) {
                manager.dispose()
            }
        } finally {
            super.tearDown()
        }
    }

    fun testCreateTempFileCreatesFile() {
        val tempFile = manager.createTempFile("test", ".txt")

        assertTrue("Temp file should exist after creation", tempFile.exists())
        assertTrue("Temp file should have correct prefix", tempFile.name.startsWith("test"))
        assertTrue("Temp file should have correct suffix", tempFile.name.endsWith(".txt"))
        assertEquals("Should track one file", 1, manager.getTrackedFileCount())
    }

    fun testCreateTempFileTracksMultipleFiles() {
        val file1 = manager.createTempFile("test1", ".txt")
        val file2 = manager.createTempFile("test2", ".js")
        val file3 = manager.createTempFile("test3", ".log")

        assertEquals("Should track all three files", 3, manager.getTrackedFileCount())
        assertTrue(file1.exists())
        assertTrue(file2.exists())
        assertTrue(file3.exists())
    }

    fun testDisposeDeletesTrackedFiles() {
        val file1 = manager.createTempFile("test1", ".txt")
        val file2 = manager.createTempFile("test2", ".js")

        // Write some content to ensure files exist
        file1.writeText("test content 1")
        file2.writeText("test content 2")

        assertTrue(file1.exists())
        assertTrue(file2.exists())

        manager.dispose()

        assertFalse("File 1 should be deleted after disposal", file1.exists())
        assertFalse("File 2 should be deleted after disposal", file2.exists())
        assertTrue("Manager should be marked as disposed", manager.isDisposed())
        assertEquals("Should not track any files after disposal", 0, manager.getTrackedFileCount())
    }

    fun testDisposeIsIdempotent() {
        val file = manager.createTempFile("test", ".txt")
        file.writeText("content")

        manager.dispose()
        assertFalse(file.exists())

        // Disposing again should not throw
        manager.dispose()
        assertTrue(manager.isDisposed())
    }

    fun testCreateTempFileThrowsAfterDisposal() {
        manager.dispose()

        try {
            manager.createTempFile("test", ".txt")
            fail("Should throw IllegalStateException")
        } catch (e: IllegalStateException) {
            assertTrue("Exception message should mention disposed", e.message?.contains("disposed") == true)
        }
    }

    fun testDisposeHandlesAlreadyDeletedFilesGracefully() {
        val file = manager.createTempFile("test", ".txt")
        file.writeText("content")

        // Manually delete the file before disposal
        file.delete()
        assertFalse(file.exists())

        // Dispose should not throw
        manager.dispose()
        assertTrue(manager.isDisposed())
    }

    fun testCreateTempFileWithCustomDirectory() {
        val customDir = File(System.getProperty("java.io.tmpdir"), "test-custom-dir")
        customDir.mkdirs()

        try {
            val file = manager.createTempFile("test", ".txt", customDir)

            assertTrue(file.exists())
            assertEquals(customDir.absolutePath, file.parentFile.absolutePath)

            manager.dispose()
            assertFalse(file.exists())
        } finally {
            customDir.deleteRecursively()
        }
    }

    fun testEmptyManagerCanBeDisposed() {
        assertEquals(0, manager.getTrackedFileCount())
        manager.dispose()
        assertTrue(manager.isDisposed())
    }
}
