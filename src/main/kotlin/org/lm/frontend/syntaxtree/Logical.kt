package org.lm.frontend.syntaxtree

import org.lm.frontend.lexer.Token

abstract class Logical: Expr {
    var expr1: Expr?;
    var expr2: Expr?;

    constructor(token: Token, x1: Expr?, x2: Expr?): super(token, null) {
        this.expr1 = x1;
        this.expr2 = x2;
    }
}