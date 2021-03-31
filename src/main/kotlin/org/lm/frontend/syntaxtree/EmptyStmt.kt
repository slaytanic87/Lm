package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class EmptyStmt: Stmt {

    companion object {
        public var Null : EmptyStmt = EmptyStmt();
    }

    constructor() {
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkEmptyStmtNode(this, arg);
    }
}