package org.lm.frontend.syntaxtree

import org.lm.frontend.code.Arith1OpCode
import org.lm.frontend.code.ThreeAddrCode
import org.lm.frontend.lexer.Token
import org.lm.frontend.treewalker.TreeWalker

class Unary: Op {
    public var expr: Expr?;
    constructor(token: Token?, expr: Expr?): super (token) {
        this.expr = expr;
    }

    public override fun codeForValueTo(left: Id): ThreeAddrCode {
        return Arith1OpCode(left, this);
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkUnaryNode(this, arg);
    }
}