package com.scriptacus.riderunrealangelscript.lang

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptTypes

/**
 * Formatting block for AngelScript language elements.
 *
 * Represents a node in the formatting tree that corresponds to a PSI element.
 * Provides indentation, spacing, and alignment rules for code formatting.
 *
 * Phase 1: Basic indentation for class/function/statement blocks
 */
class AngelScriptBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    private val spacingBuilder: SpacingBuilder?
) : AbstractBlock(node, wrap, alignment) {

    /**
     * Builds child blocks for this node's children.
     * Mirrors the PSI tree structure, creating a block for each child node.
     */
    override fun buildChildren(): List<Block> {
        val blocks = mutableListOf<Block>()
        var child = myNode.firstChildNode

        while (child != null) {
            // Skip whitespace tokens - they're handled by spacing rules
            if (child.elementType != TokenType.WHITE_SPACE) {
                blocks.add(
                    AngelScriptBlock(
                        node = child,
                        wrap = Wrap.createWrap(WrapType.NONE, false),
                        alignment = Alignment.createAlignment(),
                        spacingBuilder = spacingBuilder
                    )
                )
            }
            child = child.treeNext
        }

        return blocks
    }

    /**
     * Returns the indent for this block relative to its parent.
     *
     * Phase 1: Simple rules:
     * - NO indent for braces (they stay at parent's indent level)
     * - NORMAL indent for children of CLASS_BODY, FUNCTION_BODY, STATEMENT_BLOCK
     * - NONE indent for all other elements
     */
    override fun getIndent(): Indent? {
        // Braces should never be indented relative to their parent body
        // They stay at the same level as the declaration (class, function, etc.)
        if (myNode.elementType == AngelScriptTypes.START_STATEMENT_BLOCK ||
            myNode.elementType == AngelScriptTypes.END_STATEMENT_BLOCK) {
            return Indent.getNoneIndent()
        }

        val parent = myNode.treeParent?.psi
        return when (parent?.node?.elementType) {
            AngelScriptTypes.CLASS_BODY,
            AngelScriptTypes.STRUCT_BODY,
            AngelScriptTypes.FUNCTION_BODY,
            AngelScriptTypes.STATEMENT_BLOCK,
            AngelScriptTypes.NAMESPACE_BODY,
            AngelScriptTypes.ENUM_BODY,
            AngelScriptTypes.ASSET_BODY -> Indent.getNormalIndent()
            else -> Indent.getNoneIndent()
        }
    }

    /**
     * Returns spacing between two child blocks.
     *
     * Phase 1: Returns null (no custom spacing yet)
     * Phase 2: Will delegate to SpacingBuilder
     */
    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        // Phase 1: No custom spacing rules yet
        // Phase 2 will use: spacingBuilder?.getSpacing(this, child1, child2)
        return null
    }

    /**
     * Returns true if this block has no children (is a leaf node).
     */
    override fun isLeaf(): Boolean {
        return myNode.firstChildNode == null
    }

    /**
     * Returns child attributes for indentation when Enter is pressed.
     *
     * Phase 1: Simple implementation - indent children of block structures
     * Phase 3: Will add smart Enter handling based on isIncomplete()
     */
    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        return when (myNode.elementType) {
            AngelScriptTypes.CLASS_BODY,
            AngelScriptTypes.STRUCT_BODY,
            AngelScriptTypes.FUNCTION_BODY,
            AngelScriptTypes.STATEMENT_BLOCK,
            AngelScriptTypes.NAMESPACE_BODY,
            AngelScriptTypes.ENUM_BODY,
            AngelScriptTypes.ASSET_BODY -> ChildAttributes(Indent.getNormalIndent(), null)
            else -> ChildAttributes(Indent.getNoneIndent(), null)
        }
    }

    /**
     * Returns true if this block represents an incomplete construct.
     *
     * Phase 1: Always returns false
     * Phase 3: Will implement detection of missing closing braces, etc.
     */
    override fun isIncomplete(): Boolean {
        // Phase 1: No incomplete detection yet
        // Phase 3 will check for missing closing braces, semicolons, etc.
        return false
    }
}
