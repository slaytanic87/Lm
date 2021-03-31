package org.lm.frontend.syntaxtree

import org.lm.frontend.code.ArrayRefCode
import org.lm.frontend.code.ThreeAddrCode
import org.lm.frontend.lexer.Tag
import org.lm.frontend.lexer.Word
import org.lm.frontend.symbol.Type
import org.lm.frontend.treewalker.TreeWalker

/**
 * array[index]
 */
class Access: Op {
    var array: Expr?;
    var index: Expr?;
    constructor(array: Expr?, index: Expr?): super(Word("[]", Tag.INDEX.value)) {
        this.array = array;
        this.index = index;
    }

    constructor(array: Expr?, index: Expr, type: Type?): super(Word("[]", Tag.INDEX.value)) {
        this.array = array;
        this.index = index;
        this.type = type;
    }

    public override fun codeForValueTo(left: Id): ThreeAddrCode {
        return ArrayRefCode(left, this);
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkAccessNode(this, arg);
    }
}