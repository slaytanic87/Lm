package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

/**
 * The AssignStmt describes the value assignment, which occurs as instruction.
 */
class AssignStmt: Stmt {
    public var assign: Assignment;

    constructor(assign: Assignment) {
        this.assign = assign;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkAssignStmtNode(this, arg);
    }
}