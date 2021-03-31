package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class Break: Stmt {
    public var stmt: Stmt? = null;
    constructor() {
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkBreakNode(this, arg);
    }
}