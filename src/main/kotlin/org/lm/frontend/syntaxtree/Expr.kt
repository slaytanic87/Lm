package org.lm.frontend.syntaxtree

import org.lm.frontend.code.ThreeAddrCode
import org.lm.frontend.lexer.Token
import org.lm.frontend.symbol.Type

abstract class Expr: Node {

    public var op: Token?;
    public var type: Type?;

    constructor(token: Token?, type:Type?) {
        this.op = token;
        this.type = type;
    }

    public open fun isConstant(): Boolean {
        return false;
    }

    public open fun isSingleton(): Boolean {
        return false;
    }

    public open fun codeForValueTo(left: Id): ThreeAddrCode {
        throw Error("codeForValueTo on incorrect node type: " + this.javaClass.toString());
    }

    public override fun toString(): String {
        return op.toString();
    }

}