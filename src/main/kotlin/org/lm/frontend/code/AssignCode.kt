package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Id
import org.lm.frontend.syntaxtree.Singleton

/**
 * This class describes the Three-Address-Instruction as leftSide = rightSide
 */
class AssignCode: ArithCode {
    var rightSide: Singleton;

    constructor(left: Id, right: Singleton): super(left) {
        rightSide = right;
    }

    public override fun toString(): String {
        return (leftSide.toString() + " = " +rightSide.toString());
    }
}