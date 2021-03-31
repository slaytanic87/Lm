package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

/**
 * AssignElem descibes the assignment with an array on the left side.
 * acc (id[index]) = expr
 */
class AssignElem : Assignment {
    public var acc: Access;
    public var expr: Expr?;

    constructor(acc: Access, expr: Expr?) {
        this.acc = acc;
        this.expr = expr;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkAssignElemNode(this, arg);
    }
}