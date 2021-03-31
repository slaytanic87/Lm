package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class Scope: Stmt {
    public var stmts: Stmt;

    constructor(stmt: Stmt) {
        this.stmts = stmt;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkScopeNode(this, arg);
    }
}