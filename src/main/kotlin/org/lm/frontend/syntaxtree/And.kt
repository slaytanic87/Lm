package org.lm.frontend.syntaxtree

import org.lm.frontend.lexer.Token
import org.lm.frontend.treewalker.TreeWalker

class And: Logical {

    constructor(token: Token, x1: Expr?, x2: Expr?) : super(token, x1, x2) {
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkAndNode(this, arg);
    }
}