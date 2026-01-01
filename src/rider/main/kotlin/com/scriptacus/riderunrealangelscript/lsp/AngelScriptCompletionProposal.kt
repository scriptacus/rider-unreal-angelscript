package com.scriptacus.riderunrealangelscript.lsp

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.redhat.devtools.lsp4ij.LSPIJUtils
import com.redhat.devtools.lsp4ij.client.features.LSPCompletionFeature
import com.redhat.devtools.lsp4ij.client.features.LSPCompletionProposal
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.TextEdit
import java.lang.reflect.Method

/**
 * Custom completion proposal that fixes lsp4ij's bug with additionalTextEdits ordering.
 *
 * ## The Bug
 *
 * lsp4ij's LSPIJUtils.applyEdits() sorts text edits with an unstable comparator that
 * returns 0 for same-position edits, violating the LSP spec requirement to preserve
 * array order for same-position insertions.
 *
 * **LSP Specification (v3.17, Section 3.16.6.4):**
 * > "The edits must be sorted in reverse document order (from end to start). Multiple edits
 * > at the same position are considered to be in the same order as they appear in the array."
 *
 * **lsp4ij Bug:** The comparator in LSPIJUtils.doApplyEdits() is:
 * ```java
 * public int compare(TextEdit a, TextEdit b) {
 *     int diff = a.getRange().getStart().getLine() - b.getRange().getStart().getLine();
 *     if (diff == 0) {
 *         return a.getRange().getStart().getCharacter() - b.getRange().getStart().getCharacter();
 *     }
 *     return diff;
 * }
 * ```
 * This returns 0 for same-position edits, which makes the sort unstable and can reorder
 * same-position edits arbitrarily, breaking completions that rely on specific ordering.
 *
 * ## The Fix
 *
 * Reimplements the parent's apply() method identically (lines 171-266), but replaces the call to
 * LSPIJUtils.applyEdits() with our own applyEditsWithStableSort() (line 260) that uses a
 * stable comparator (StableTextEditComparator, lines 72-89).
 *
 * The stable comparator preserves array order for same-position edits by tracking original
 * indices and using them as a tiebreaker.
 *
 * ## Why Reflection Is Necessary
 *
 * The parent class LSPCompletionProposal has several private fields and methods that are
 * needed to reimplement apply():
 * - `getInsertText()` - Gets the text to insert
 * - `getResolvedCompletionItem()` - Gets resolved completion with additionalTextEdits
 * - `prefixStartOffset` - Start offset of the prefix being completed
 * - `completionOffset` - Offset where completion was triggered
 * - `editor` - The editor instance
 *
 * We cannot override apply() without accessing these, and they are private. Reflection
 * is the only way to access them without modifying lsp4ij.
 *
 * ## How to Detect If Fixed Upstream
 *
 * To check if lsp4ij has fixed this bug:
 *
 * 1. Look for changes to LSPIJUtils.doApplyEdits() in lsp4ij releases
 * 2. Check if the comparator now preserves array order for same-position edits
 * 3. Test with a completion that has multiple additionalTextEdits at the same position
 * 4. If the edits are applied in LSP array order, the bug is fixed
 *
 * **Test case:** A completion that adds UFUNCTION() above a method declaration:
 * ```
 * additionalTextEdits: [
 *   { range: {line: 5, char: 0 - line: 5, char: 0}, newText: "UFUNCTION()\n" }
 *   { range: {line: 5, char: 0 - line: 5, char: 0}, newText: "\t" }
 * ]
 * ```
 * Expected result: `UFUNCTION()\n\t` (newline, then tab)
 * Bug result: Could be `\tUFUNCTION()\n` (reversed)
 *
 * ## Version Compatibility
 *
 * - **Affected versions:** lsp4ij 0.19.2-SNAPSHOT (as of 2024-12-30)
 * - **Testing needed:** Test with each lsp4ij update
 * - **Risk:** Low - Reflection API is stable, but lsp4ij could change field/method names
 *
 * ## Maintenance Notes
 *
 * See docs/LSP_COMPLETION_WORKAROUND.md for detailed maintenance guide including:
 * - How to remove this workaround if upstream fixes the bug
 * - How to update if lsp4ij changes private API
 * - Testing procedures for verifying the fix is still needed
 */
class AngelScriptCompletionProposal(
    item: CompletionItem,
    private val context: LSPCompletionFeature.LSPCompletionContext,
    completionFeature: LSPCompletionFeature
) : LSPCompletionProposal(item, context, completionFeature) {

    companion object {
        private val LOG = Logger.getInstance(AngelScriptCompletionProposal::class.java)

        // Reflection to access private parent methods/fields
        // These are required to reimplement apply() with stable sort fix

        /** Accesses LSPCompletionProposal.getInsertText() to get the completion text */
        private val getInsertTextMethod: Method = LSPCompletionProposal::class.java.getDeclaredMethod("getInsertText").apply {
            isAccessible = true
        }

        /** Accesses LSPCompletionProposal.getResolvedCompletionItem() to get additionalTextEdits */
        private val getResolvedCompletionItemMethod: Method = LSPCompletionProposal::class.java.getDeclaredMethod("getResolvedCompletionItem").apply {
            isAccessible = true
        }

        /** Accesses LSPCompletionProposal.prefixStartOffset field for range calculation */
        private val prefixStartOffsetField = LSPCompletionProposal::class.java.getDeclaredField("prefixStartOffset").apply {
            isAccessible = true
        }

        /** Accesses LSPCompletionProposal.completionOffset field for range adjustment */
        private val completionOffsetField = LSPCompletionProposal::class.java.getDeclaredField("completionOffset").apply {
            isAccessible = true
        }

        /** Accesses LSPCompletionProposal.editor field to adjust caret position after edits */
        private val editorField = LSPCompletionProposal::class.java.getDeclaredField("editor").apply {
            isAccessible = true
        }

        /**
         * Stable comparator for TextEdits - DESCENDING order (end to start of document).
         *
         * Edits must be applied from END to START of document to avoid offset shifts.
         * When edits are at the same position, LSP array order must be preserved.
         */
        private class StableTextEditComparator : Comparator<IndexedTextEdit> {
            override fun compare(a: IndexedTextEdit, b: IndexedTextEdit): Int {
                val aStart = a.edit.range.start
                val bStart = b.edit.range.start

                // Sort by start line (DESCENDING - from end to start)
                val lineDiff = bStart.line - aStart.line
                if (lineDiff != 0) return lineDiff

                // Then by start character (DESCENDING - from end to start)
                val charDiff = bStart.character - aStart.character
                if (charDiff != 0) return charDiff

                // At same position: preserve LSP array order
                // Since we're processing in reverse, we need to REVERSE the index order too
                return b.originalIndex - a.originalIndex
            }
        }

        private data class IndexedTextEdit(
            val edit: TextEdit,
            val originalIndex: Int
        )

        /**
         * Apply text edits with stable sorting.
         * This is a copy of LSPIJUtils.doApplyEdits() but with a stable comparator.
         *
         * IMPORTANT: For completion, the first edit in the list is ALWAYS the MAIN textEdit
         * that replaces the completion prefix. The caret should be positioned at the END of
         * this main edit. Additional edits (like adding imports or decorators) should NOT
         * affect caret positioning.
         */
        private fun applyEditsWithStableSort(
            editor: Editor?,
            document: Document,
            edits: List<TextEdit>
        ) {
            if (edits.isEmpty()) return

            // Index edits to track original order
            val indexedEdits = edits.mapIndexed { index, edit -> IndexedTextEdit(edit, index) }

            // Sort with stable comparator (descending - from end to start)
            val sortedIndexed = indexedEdits.sortedWith(StableTextEditComparator())
            val sortedEdits = sortedIndexed.map { it.edit }

            // Create pairs of (TextEdit, RangeMarker)
            val pairs = mutableListOf<Pair<TextEdit, RangeMarker>>()
            for (textEdit in sortedEdits) {
                val range = textEdit.range
                if (range != null) {
                    val start = LSPIJUtils.toOffset(range.start, document)
                    val end = LSPIJUtils.toOffset(range.end, document)
                    if (end >= start) {
                        val marker = document.createRangeMarker(start, end)
                        pairs.add(Pair(textEdit, marker))
                    }
                }
            }

            if (pairs.isEmpty()) return

            // Track the main textEdit (always first) to position caret at its end
            val mainTextEdit = edits.firstOrNull()
            var caretMarker: RangeMarker? = null

            // Apply each edit
            for ((edit, marker) in pairs) {
                val startOffset = marker.startOffset
                val endOffset = marker.endOffset
                val newText = edit.newText ?: ""

                document.replaceString(startOffset, endOffset, newText)

                // If this is the main textEdit, create a marker to track the caret position
                // The marker will automatically adjust as subsequent edits are applied
                if (mainTextEdit != null && edit === mainTextEdit) {
                    val caretPosition = startOffset + newText.length
                    caretMarker = document.createRangeMarker(caretPosition, caretPosition)
                }

                marker.dispose()
            }

            // Move caret to end of main textEdit (marker has been adjusted for all edits)
            if (caretMarker != null && editor != null) {
                editor.caretModel.moveToOffset(caretMarker.startOffset)
                caretMarker.dispose()
            }
        }
    }

    override fun apply(document: Document, offset: Int) {
        // This is a complete copy of the parent's apply() method
        // The ONLY difference is we call applyEditsWithStableSort instead of LSPIJUtils.applyEdits

        var insertText: String? = null
        var textEdit: TextEdit? = null

        try {
            val eitherTextEdit = item.textEdit
            if (eitherTextEdit != null) {
                textEdit = if (eitherTextEdit.isLeft) {
                    eitherTextEdit.left
                } else {
                    val insertReplaceEdit = eitherTextEdit.right
                    TextEdit(insertReplaceEdit.insert, insertReplaceEdit.newText)
                }
            }

            if (textEdit == null) {
                insertText = getInsertTextMethod.invoke(this) as String?
                val prefixStartOffset = prefixStartOffsetField.getInt(this)
                val startOffset = prefixStartOffset
                val endOffset = offset
                val start = LSPIJUtils.toPosition(startOffset, document)
                val end = LSPIJUtils.toPosition(endOffset, document)
                textEdit = TextEdit(Range(start, end), insertText)
            } else {
                val completionOffset = completionOffsetField.getInt(this)
                if (offset > completionOffset) {
                    val shift = offset - completionOffset
                    textEdit.range.end.character = textEdit.range.end.character + shift
                }
            }

            // Workaround https://github.com/Microsoft/vscode/issues/17036
            run {
                val start = textEdit.range.start
                val end = textEdit.range.end
                if (start.line > end.line || (start.line == end.line && start.character > end.character)) {
                    textEdit.range.end = start
                    textEdit.range.start = end
                }
            }

            // Allow completion items to be wrong with too wide range
            run {
                val documentEnd = LSPIJUtils.toPosition(document.textLength, document)
                val textEditEnd = textEdit.range.end
                if (documentEnd.line < textEditEnd.line ||
                    (documentEnd.line == textEditEnd.line && documentEnd.character < textEditEnd.character)) {
                    textEdit.range.end = documentEnd
                }
            }

            // Reuse existing characters
            if (insertText != null) {
                val prefixStartOffset = prefixStartOffsetField.getInt(this)
                val shift = offset - prefixStartOffset
                var commonSize = 0
                while (commonSize < insertText.length - shift &&
                    document.textLength > offset + commonSize &&
                    document.text[prefixStartOffset + shift + commonSize] == insertText[commonSize + shift]) {
                    commonSize++
                }
                textEdit.range.end.character = textEdit.range.end.character + commonSize
            }

            // Get additional edits
            var additionalEdits = item.additionalTextEdits
            if ((additionalEdits == null || additionalEdits.isEmpty()) && context.isResolveCompletionSupported) {
                val resolved = getResolvedCompletionItemMethod.invoke(this) as CompletionItem?
                if (resolved != null) {
                    additionalEdits = resolved.additionalTextEdits
                }
            }

            // Combine all edits
            val allEdits = if (additionalEdits != null && additionalEdits.isNotEmpty()) {
                val list = mutableListOf<TextEdit>()
                list.add(textEdit)
                list.addAll(additionalEdits)
                list
            } else {
                listOf(textEdit)
            }

            // THE FIX: Use our stable sort instead of LSPIJUtils.applyEdits
            val editor = editorField.get(this) as Editor?
            WriteAction.run<RuntimeException> {
                applyEditsWithStableSort(editor, document, allEdits)
            }

        } catch (ex: RuntimeException) {
            LOG.warn(ex.localizedMessage, ex)
        }
    }

    /**
     * Override handleInsert to add auto-formatting after completion.
     * This is called by IntelliJ after the completion is inserted.
     */
    override fun handleInsert(context: InsertionContext) {
        // Let parent handle the insertion (applies edits, snippets, commands, etc.)
        super.handleInsert(context)

        // Auto-format the inserted code
        try {
            val document = context.document

            // Get all text edits that were applied
            val resolved = try {
                getResolvedCompletionItemMethod.invoke(this) as CompletionItem?
            } catch (ex: Exception) {
                null
            }
            val additionalEdits = resolved?.additionalTextEdits ?: item.additionalTextEdits

            LOG.info("=== Auto-format after completion ===")
            LOG.info("Context startOffset: ${context.startOffset}, tailOffset: ${context.tailOffset}")
            LOG.info("Context range in document: ${LSPIJUtils.toPosition(context.startOffset, document)} - ${LSPIJUtils.toPosition(context.tailOffset, document)}")

            if (additionalEdits != null && additionalEdits.isNotEmpty()) {
                LOG.info("Additional text edits (${additionalEdits.size}):")
                additionalEdits.forEachIndexed { index, edit ->
                    LOG.info("  [$index] range: ${edit.range.start.line}:${edit.range.start.character} - ${edit.range.end.line}:${edit.range.end.character}, text: ${edit.newText.replace("\n", "\\n").replace("\t", "\\t")}")
                }
            }

            // Commit document to sync PSI with document changes
            context.commitDocument()

            val psiFile = context.file
            val project = context.project

            // Calculate the full range affected by all edits (main edit + additional edits)
            var minOffset = context.startOffset
            var maxOffset = context.tailOffset

            if (additionalEdits != null && additionalEdits.isNotEmpty()) {
                for (edit in additionalEdits) {
                    val editStart = LSPIJUtils.toOffset(edit.range.start, document)
                    val editEnd = LSPIJUtils.toOffset(edit.range.end, document)
                    minOffset = minOf(minOffset, editStart)
                    maxOffset = maxOf(maxOffset, editEnd + edit.newText.length)
                }
            }

            LOG.info("Combined edit range: $minOffset - $maxOffset")
            LOG.info("Combined edit range in document: ${LSPIJUtils.toPosition(minOffset, document)} - ${LSPIJUtils.toPosition(maxOffset, document)}")

            // Just format the full edited range - the LSP server has already provided properly formatted code
            val formatStart = minOffset
            val formatEnd = maxOffset

            LOG.info("Formatting full edited range: $formatStart - $formatEnd")
            LOG.info("Formatting range in document: ${LSPIJUtils.toPosition(formatStart, document)} - ${LSPIJUtils.toPosition(formatEnd, document)}")

            // Format the range
            WriteCommandAction.runWriteCommandAction(project) {
                CodeStyleManager.getInstance(project).reformatText(psiFile, formatStart, formatEnd)
            }
            LOG.info("=== Auto-format complete ===")
        } catch (ex: Exception) {
            LOG.warn("Failed to auto-format after completion", ex)
        }
    }
}
