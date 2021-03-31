package org.lm.frontend.syntaxtree

import org.lm.frontend.lexer.Token
import org.lm.frontend.treewalker.TreeWalker

/**
 * This class describes relational expression as x1 op x2
 */
class Rel: Logical {
    constructor(opToken: Token, x1: Expr?, x2: Expr?): super(opToken, x1, x2) {
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkRelNode(this, arg);
    }
}