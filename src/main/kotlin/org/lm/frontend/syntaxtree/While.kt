package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class While: Stmt {

    public var expr: Expr? = null;
    public var stmt: Stmt? = null;

    constructor() {
    }

    public fun init(expr: Expr?, stmt: Stmt) {
        this.expr = expr;
        this.stmt = stmt;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkWhileNode(this, arg);
    }
}