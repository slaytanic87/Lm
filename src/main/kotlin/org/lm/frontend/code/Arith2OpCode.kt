package org.lm.frontend.code

import org.lm.frontend.lexer.Token
import org.lm.frontend.syntaxtree.Arith
import org.lm.frontend.syntaxtree.Id
import org.lm.frontend.syntaxtree.Singleton

/**
 * This class describes Three-Address-Instruction
 * as leftSide = expr1 operator expr2
 */
class Arith2OpCode: ArithCode {

    var expr1: Singleton;
    var expr2: Singleton;

    constructor(left: Id, u: Arith): super(left, u.op as Token) {
        this.expr1 = u.expr1 as Singleton;
        this.expr2 = u.expr2 as Singleton;
    }

    public override fun toString(): String {
        return (leftSide.toString() + " = " + expr1.toString() + " " + operator.toString() + " " + expr2.toString());
    }
}