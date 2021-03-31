package org.lm.frontend.code

import org.lm.frontend.lexer.Token
import org.lm.frontend.syntaxtree.Id

/**
 * This is the abstract class for all simple arithmetic Three-Address-Instruction.
 * Every command have an identifier on the left side and max one operator on the
 * right side.
 */
abstract class ArithCode: ThreeAddrCode {

    protected var leftSide: Id;
    protected var operator: Token? = null;

    constructor(leftSide: Id) {
        this.leftSide = leftSide;
    }

    constructor(leftside: Id, operator: Token?) {
        this.leftSide = leftside;
        this.operator = operator;
    }
}