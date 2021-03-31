package org.lm.frontend.syntaxtree

import org.lm.frontend.lexer.Lexer
import org.lm.frontend.treewalker.TreeWalker

abstract class Node {
    var lexline: Int = 0;

    constructor() {
        lexline = Lexer.line;
    }

    open fun error(s: String) {
        throw Error("near line $lexline: $s")
    }

    public abstract fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S;
}