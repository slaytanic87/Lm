package org.lm.frontend.syntaxtree

import org.lm.frontend.code.Arith2OpCode
import org.lm.frontend.code.ThreeAddrCode
import org.lm.frontend.lexer.Token
import org.lm.frontend.symbol.Type
import org.lm.frontend.treewalker.TreeWalker

class Arith: Op {
    public var expr1: Expr?;
    public var expr2: Expr?;

    constructor(token: Token, expr1: Expr?, expr2: Expr?): super(token) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    constructor(token: Token, expr1: Expr, expr2: Expr, type: Type): super(token) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        super.type = type;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkArithNode(this, arg);
    }

    public override fun codeForValueTo(left: Id): ThreeAddrCode {
        return Arith2OpCode(left, this);
    }
}