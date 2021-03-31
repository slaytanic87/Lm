package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class Program: Stmt {

    public var scope: Scope;

    constructor(scope: Scope) {
        this.scope = scope;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkProgramNode(this, arg);
    }
}