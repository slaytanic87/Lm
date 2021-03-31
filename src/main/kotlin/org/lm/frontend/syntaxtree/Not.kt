package org.lm.frontend.syntaxtree

import org.lm.frontend.lexer.Token
import org.lm.frontend.treewalker.TreeWalker

class Not: Logical {
    constructor(token: Token, x2: Expr?): super(token, x2, x2) {

    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkNotNode(this, arg);
    }
}