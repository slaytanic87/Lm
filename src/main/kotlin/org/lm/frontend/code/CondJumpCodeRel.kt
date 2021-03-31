package org.lm.frontend.code

import org.lm.frontend.lexer.Token
import org.lm.frontend.syntaxtree.Logical
import org.lm.frontend.syntaxtree.Singleton

/**
 * This class describe conditional jumps as if id1 op id2 goto label
 */
class CondJumpCodeRel: JumpCode {
    var id1: Singleton;
    var op: Token?;
    var id2: Singleton;

    constructor(exp: Logical, label: Int) {
        id1 = exp.expr1 as Singleton;
        op = exp.op;
        id2 = exp.expr2 as Singleton;
        super.setLabel(label);
    }

    public override fun toString(): String {
        return ("if " + id1.toString() + op.toString() + id2.toString() +" goto " + getLabel());
    }
}