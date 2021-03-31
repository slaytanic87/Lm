package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class ExprCall: Stmt {
    public var expr: Expr?;
    constructor(expr: Expr?) {
        this.expr = expr;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkExprCallNode(this, arg);
    }
}