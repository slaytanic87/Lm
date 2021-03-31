package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class Seq: Stmt {
    var stmt1: Stmt;
    var stmt2: Stmt;

    constructor(s1: Stmt, s2: Stmt) {
        this.stmt1 = s1;
        this.stmt2 = s2;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkSeqNode(this, arg);
    }
}