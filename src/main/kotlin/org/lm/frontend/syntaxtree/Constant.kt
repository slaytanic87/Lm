package org.lm.frontend.syntaxtree

import org.lm.frontend.lexer.Num
import org.lm.frontend.lexer.Token
import org.lm.frontend.lexer.Word
import org.lm.frontend.symbol.Type
import org.lm.frontend.treewalker.TreeWalker

class Constant: Singleton {

    companion object {
        public val True = Constant(Word.TRUE, Type.bool);
        public val False = Constant(Word.FALSE, Type.bool);
    }

    constructor(token: Token, type:Type): super(token, type) {
    }
    constructor(i:Int, type: Type): super(Num(i), type) {
    }
    constructor(i: Int): super(Num(i), Type.int) {
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkConstantNode(this, arg);
    }

    public override fun isConstant(): Boolean {
        return true;
    }
}