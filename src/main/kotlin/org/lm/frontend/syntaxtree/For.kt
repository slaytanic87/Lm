package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class For: Stmt {

    public var expr: Expr? = null;
    public var initAssig: Assignment? = null;
    public var iterAssign: Assignment? = null;
    public var stmt: Stmt? = null;

    constructor() {
    }

    public fun init(as1: Assignment, expr: Expr?, as2: Assignment, stmt: Stmt) {
        this.expr = expr;
        initAssig = as1;
        iterAssign = as2;
        this.stmt = stmt;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkForNode(this, arg);
    }
}