package org.lm.frontend.syntaxtree

import org.lm.frontend.code.AssignCode
import org.lm.frontend.code.ThreeAddrCode
import org.lm.frontend.lexer.Token
import org.lm.frontend.symbol.Type

abstract class Singleton: Expr {
    constructor(t: Token, p: Type?): super(t, p) {
    }

    public override fun isSingleton(): Boolean {
        return true;
    }

    public override fun codeForValueTo(left: Id): ThreeAddrCode {
        return AssignCode(left, this);
    }
}