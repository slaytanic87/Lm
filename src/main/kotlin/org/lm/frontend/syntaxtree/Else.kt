package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class Else: Stmt {
    public var expr: Expr?;
    public var stmt1: Stmt;
    public var stmt2: Stmt;

    constructor(expr: Expr?, s1: Stmt, s2: Stmt) {
        this.expr = expr;
        stmt1 = s1;
        stmt2 = s2;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkElseNode(this, arg);
    }
}