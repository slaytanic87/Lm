package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class AssignId: Assignment {
    public var ident: Id;
    public var expr: Expr;

    constructor(ident: Id, expr: Expr) {
        this.ident = ident;
        this.expr = expr;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkAssignIdNode(this, arg);
    }
}