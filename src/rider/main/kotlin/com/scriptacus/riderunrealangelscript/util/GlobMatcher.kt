package com.scriptacus.riderunrealangelscript.util

import com.intellij.openapi.diagnostic.Logger
import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import java.nio.file.Paths

/**
 * Utility for matching file paths against glob patterns.
 * Supports standard glob syntax:
 * - asterisk matches any characters within a path segment
 * - double asterisk matches any number of directory levels
 * - question mark matches a single character
 *
 * Examples:
 * - Pattern asterisk asterisk /Saved/asterisk asterisk matches any file in Saved directories at any level
 * - Pattern asterisk asterisk /.plastic/asterisk asterisk matches any file in .plastic directories
 */
object GlobMatcher {

    private val LOG = Logger.getInstance(GlobMatcher::class.java)

    /**
     * Check if a file path matches any of the provided glob patterns.
     *
     * @param filePath The file path to test (can be absolute or relative)
     * @param patterns List of glob patterns to match against
     * @return true if the path matches any pattern, false otherwise
     */
    fun matchesAny(filePath: String, patterns: List<String>): Boolean {
        if (patterns.isEmpty()) return false

        // Normalize path separators to forward slashes for consistent matching
        val normalizedPath = filePath.replace('\\', '/')

        LOG.debug("GlobMatcher: Testing path: $normalizedPath against patterns: $patterns")

        val matched = patterns.any { pattern ->
            val result = matchesPattern(normalizedPath, pattern)
            if (result) {
                LOG.info("GlobMatcher: Path '$normalizedPath' MATCHED pattern '$pattern'")
            }
            result
        }

        if (!matched) {
            LOG.debug("GlobMatcher: Path '$normalizedPath' did NOT match any patterns")
        }

        return matched
    }

    /**
     * Check if a file path matches a single glob pattern.
     */
    private fun matchesPattern(filePath: String, pattern: String): Boolean {
        // Normalize pattern separators
        val normalizedPattern = pattern.replace('\\', '/')

        try {
            // Use Java NIO PathMatcher with glob syntax
            val matcher: PathMatcher = FileSystems.getDefault()
                .getPathMatcher("glob:$normalizedPattern")

            val path = Paths.get(filePath)
            val matches = matcher.matches(path)
            LOG.debug("GlobMatcher: Pattern '$normalizedPattern' vs path '$filePath' = $matches")
            return matches
        } catch (e: Exception) {
            LOG.warn("GlobMatcher: Exception matching pattern '$normalizedPattern' against '$filePath': ${e.message}")
            return false
        }
    }
}
