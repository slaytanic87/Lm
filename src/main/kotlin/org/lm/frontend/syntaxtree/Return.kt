package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class Return: Stmt {
    var bool: Expr? = null;
    constructor(bool: Expr?) {
        this.bool = bool;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkReturnNode(this, arg);
    }
}