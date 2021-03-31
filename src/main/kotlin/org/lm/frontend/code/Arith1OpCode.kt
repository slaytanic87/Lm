package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Id
import org.lm.frontend.syntaxtree.Singleton
import org.lm.frontend.syntaxtree.Unary

/**
 * This class describes Three-Address-Instruction
 * as leftSide = operator rightSide
 */
class Arith1OpCode: ArithCode {

    var rightSide: Singleton;

    constructor(l: Id, u: Unary): super(l, u.op) {
        rightSide = u.expr as Singleton;
    }

    public override fun toString(): String {
        return (leftSide.toString() + " = " + operator.toString() + "" + rightSide.toString());
    }

}