# LSP Completion Workaround - Maintenance Guide

This document explains the reflection-based workaround in `AngelScriptCompletionProposal.kt` and how to maintain it.

## Table of Contents
- [The Problem](#the-problem)
- [The Solution](#the-solution)
- [Why Reflection](#why-reflection)
- [Testing the Workaround](#testing-the-workaround)
- [Detecting If Fixed Upstream](#detecting-if-fixed-upstream)
- [Removing the Workaround](#removing-the-workaround)
- [Updating After lsp4ij Changes](#updating-after-lsp4ij-changes)
- [Version History](#version-history)

## The Problem

### LSP Specification Requirement

The Language Server Protocol specification (v3.17, Section 3.16.6.4) states:

> "The edits must be sorted in reverse document order (from end to start). Multiple edits at the same position are considered to be in the same order as they appear in the array."

This means:
1. Edits must be applied from the end of the document toward the start (to avoid offset shifts)
2. When multiple edits are at the same position, their **array order must be preserved**

### The Bug in lsp4ij

lsp4ij's `LSPIJUtils.doApplyEdits()` uses this comparator:

```java
public int compare(TextEdit a, TextEdit b) {
    int diff = a.getRange().getStart().getLine() - b.getRange().getStart().getLine();
    if (diff == 0) {
        return a.getRange().getStart().getCharacter() - b.getRange().getStart().getCharacter();
    }
    return diff;
}
```

**Problem:** This returns `0` for same-position edits, making the sort unstable. Java's sort algorithm can then reorder same-position edits arbitrarily, violating the LSP spec.

### Real-World Impact

Example: AngelScript completion adds `UFUNCTION()` decorator:

```kotlin
// LSP server sends:
additionalTextEdits: [
  { range: {line: 5, char: 0 - line: 5, char: 0}, newText: "UFUNCTION()\n" },
  { range: {line: 5, char: 0 - line: 5, char: 0}, newText: "\t" }
]

// Expected result (preserving array order):
UFUNCTION()
	void MyMethod()

// Bug result (reversed order):
	UFUNCTION()
void MyMethod()  // Wrong indentation!
```

## The Solution

### Architecture

`AngelScriptCompletionProposal` extends `LSPCompletionProposal` and overrides `apply()`:

1. **Copy parent's apply() implementation** exactly (lines 233-328 in AngelScriptCompletionProposal.kt)
2. **Replace one line:** Change `LSPIJUtils.applyEdits()` to `applyEditsWithStableSort()`
3. **Stable comparator:** `StableTextEditComparator` tracks original indices and uses them as tiebreaker

### Key Components

**File:** `src/rider/main/kotlin/com/scriptacus/riderunrealangelscript/lsp/AngelScriptCompletionProposal.kt`

- **Lines 113-136:** Reflection setup to access private parent fields/methods
- **Lines 138-156:** `StableTextEditComparator` - stable sort with index preservation
- **Lines 162-230:** `applyEditsWithStableSort()` - stable sort implementation
- **Lines 233-328:** Overridden `apply()` method (copy of parent with one line changed)

## Why Reflection

The parent class `LSPCompletionProposal` has these private members that are essential for `apply()`:

| Member | Type | Purpose |
|--------|------|---------|
| `getInsertText()` | Method | Gets the text to insert |
| `getResolvedCompletionItem()` | Method | Gets completion with additionalTextEdits |
| `prefixStartOffset` | Field (int) | Start offset of prefix being completed |
| `completionOffset` | Field (int) | Offset where completion triggered |
| `editor` | Field (Editor) | Editor instance for caret adjustment |

**Why we can't avoid reflection:**
- These are private - not accessible to subclasses
- We must access them to reimplement `apply()`
- We can't modify lsp4ij source (it's a third-party library)
- Reflection is the only way to access private members

## Testing the Workaround

### Manual Test Procedure

1. **Open an AngelScript file in Rider**
2. **Trigger completion** for a function that adds UFUNCTION():
   ```angelscript
   void MyMeth|  // Cursor here, type to trigger completion
   ```
3. **Accept the completion** for a UFUNCTION-decorated method
4. **Verify result:**
   ```angelscript
   UFUNCTION()
   	void MyMethod()  // Tab AFTER UFUNCTION, not before
   ```

### Automated Test (Future)

Create `AngelScriptCompletionProposalTest.kt`:

```kotlin
fun testStableSortPreservesArrayOrder() {
    val edits = listOf(
        TextEdit(Range(Position(5, 0), Position(5, 0)), "UFUNCTION()\n"),
        TextEdit(Range(Position(5, 0), Position(5, 0)), "\t")
    )

    // Apply edits and verify order
    val result = applyEditsAndGetText(edits)
    assertEquals("UFUNCTION()\n\t", result)
}
```

## Detecting If Fixed Upstream

### When to Check

Check for upstream fix when:
- Updating to a new lsp4ij version
- lsp4ij release notes mention "text edit" or "completion" fixes
- You see a commit touching `LSPIJUtils.java`

### How to Check

1. **Check lsp4ij source:**
   ```bash
   cd third-party/lsp4ij-main
   git log --oneline --grep="text edit\|completion" src/main/java/com/redhat/devtools/lsp4ij/LSPIJUtils.java
   ```

2. **Look for comparator changes** in `LSPIJUtils.doApplyEdits()`:
   - If it now uses original index as tiebreaker → FIXED
   - If it still returns 0 for same-position → NOT FIXED

3. **Run manual test** (see Testing section above):
   - If indentation is correct → FIXED
   - If indentation is wrong → NOT FIXED

### Example of Fixed Code

A fixed comparator would look like:

```java
// FIXED VERSION (hypothetical)
public int compare(IndexedTextEdit a, IndexedTextEdit b) {
    int diff = a.edit.getRange().getStart().getLine() - b.edit.getRange().getStart().getLine();
    if (diff == 0) {
        diff = a.edit.getRange().getStart().getCharacter() - b.edit.getRange().getStart().getCharacter();
        if (diff == 0) {
            return a.originalIndex - b.originalIndex;  // Preserve array order!
        }
    }
    return diff;
}
```

## Removing the Workaround

### When to Remove

Remove this workaround when:
1. ✅ lsp4ij fixes the unstable comparator
2. ✅ Manual test passes without the workaround
3. ✅ Update is tested in production for at least one sprint

### Removal Steps

1. **Delete `AngelScriptCompletionProposal.kt`** entirely

2. **Update `AngelScriptLspCompletionFeature.kt`:**
   ```kotlin
   // BEFORE (with workaround):
   override fun createCompletionProposal(
       item: CompletionItem,
       context: LSPCompletionContext
   ): LSPCompletionProposal {
       return AngelScriptCompletionProposal(item, context, this)
   }

   // AFTER (without workaround):
   // Remove override - use default LSPCompletionProposal
   ```

3. **Test thoroughly:**
   - Run manual test (UFUNCTION completion)
   - Test all completion types (functions, properties, classes)
   - Verify no regressions

4. **Update this document:**
   - Add removal date to Version History
   - Mark as "REMOVED - bug fixed in lsp4ij X.Y.Z"

## Updating After lsp4ij Changes

### If Reflection Breaks

**Symptom:** `NoSuchMethodException` or `NoSuchFieldException` at runtime

**Cause:** lsp4ij renamed/removed a private field or method

**Solution:**

1. **Find the new name** in lsp4ij source:
   ```bash
   cd third-party/lsp4ij-main
   grep -r "prefixStartOffset\|completionOffset\|getInsertText" src/
   ```

2. **Update reflection code** in `AngelScriptCompletionProposal.kt`:
   ```kotlin
   // Example: if prefixStartOffset renamed to prefixOffset
   private val prefixStartOffsetField = LSPCompletionProposal::class.java
       .getDeclaredField("prefixOffset")  // Changed name
       .apply { isAccessible = true }
   ```

3. **Test and verify** it works

### If Parent apply() Changes

**Symptom:** Completions behave differently than expected

**Cause:** lsp4ij changed LSPCompletionProposal.apply() implementation

**Solution:**

1. **Compare implementations:**
   ```bash
   # View current lsp4ij version
   cat third-party/lsp4ij-main/src/main/java/com/redhat/devtools/lsp4ij/client/features/LSPCompletionProposal.java

   # Compare to our copy
   # Lines 233-328 in AngelScriptCompletionProposal.kt
   ```

2. **Update our apply()** to match new parent implementation:
   - Keep the stable sort fix (line 322: `applyEditsWithStableSort`)
   - Update everything else to match parent

3. **Test thoroughly**

## Version History

### 2024-12-30 - Initial Workaround
- **lsp4ij version:** 0.19.2-SNAPSHOT
- **Status:** Active workaround required
- **Issue:** Unstable comparator in LSPIJUtils.doApplyEdits()
- **Implementation:** AngelScriptCompletionProposal with reflection-based fix

### Future Updates

Document here when:
- lsp4ij versions are tested
- Workaround is updated
- Workaround is removed

---

## Quick Reference

**Workaround file:** `src/rider/main/kotlin/com/scriptacus/riderunrealangelscript/lsp/AngelScriptCompletionProposal.kt`

**Key line:** Line 322 - `applyEditsWithStableSort(editor, document, allEdits)`

**lsp4ij bug:** `third-party/lsp4ij-main/src/main/java/com/redhat/devtools/lsp4ij/LSPIJUtils.java` - `doApplyEdits()` method

**Test case:** UFUNCTION() completion should place decorator before indentation

**Remove when:** lsp4ij fixes comparator to preserve array order for same-position edits
