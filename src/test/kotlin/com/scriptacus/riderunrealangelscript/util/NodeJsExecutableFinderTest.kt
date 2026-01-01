package com.scriptacus.riderunrealangelscript.util

import com.intellij.testFramework.UsefulTestCase

/**
 * Tests for NodeJsExecutableFinder.
 *
 * Note: These tests interact with the real environment and may behave differently
 * depending on whether Node.js is installed and how it's configured.
 *
 * Tests without a project reference since platform initialization is complex.
 */
class NodeJsExecutableFinderTest : UsefulTestCase() {

    override fun setUp() {
        super.setUp()
        // Clear cache before each test
        NodeJsExecutableFinder.clearCache()
    }

    override fun tearDown() {
        try {
            NodeJsExecutableFinder.clearCache()
        } finally {
            super.tearDown()
        }
    }

    fun testFindCanWorkWithoutProject() {
        // Should be able to find Node.js even without a project (PATH and common locations only)
        val nodePath = NodeJsExecutableFinder.find(null, useCache = false)

        // Test passes either way, just verify it doesn't throw
        // The result depends on whether Node.js is installed
        if (nodePath != null) {
            assertTrue("Path should not be empty when found", nodePath.isNotEmpty())
        }
    }

    fun testCachingWorks() {
        val firstResult = NodeJsExecutableFinder.find(null, useCache = true)
        val secondResult = NodeJsExecutableFinder.find(null, useCache = true)

        // Both calls should return the same result (cached)
        assertEquals("Cached result should match first result", firstResult, secondResult)
    }

    fun testClearCacheForcesNewSearch() {
        val firstResult = NodeJsExecutableFinder.find(null, useCache = true)

        NodeJsExecutableFinder.clearCache()

        // After clearing cache, should search again
        // We can't easily verify it searched again, but we can verify it still works
        val secondResult = NodeJsExecutableFinder.find(null, useCache = true)

        // Results should still be equal (same Node.js installation)
        assertEquals("Result after cache clear should match", firstResult, secondResult)
    }

    fun testUseCacheFalseBypassesCache() {
        // First call with cache
        val cachedResult = NodeJsExecutableFinder.find(null, useCache = true)

        // Second call without cache
        val uncachedResult = NodeJsExecutableFinder.find(null, useCache = false)

        // Results should be equal (same Node.js installation)
        assertEquals("Uncached result should match cached result", cachedResult, uncachedResult)
    }

    fun testFindReturnsNullOrPathConsistently() {
        // The method should not throw, and should consistently return the same result
        val result1 = NodeJsExecutableFinder.find(null, useCache = false)
        val result2 = NodeJsExecutableFinder.find(null, useCache = false)

        // Either null (not found) or non-null (found) is acceptable
        // But it should be consistent
        assertEquals("Multiple calls should return consistent results", result1, result2)

        // If found, path should not be empty
        if (result1 != null) {
            assertTrue("Found path should not be empty", result1.isNotEmpty())
        }
    }
}
