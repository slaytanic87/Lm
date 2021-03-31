package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class If:Stmt {
    public var expr : Expr?;
    public var stmt: Stmt;

    constructor(expr: Expr?, stmt: Stmt) {
        this.expr = expr;
        this.stmt = stmt;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkIfNode(this, arg);
    }
}