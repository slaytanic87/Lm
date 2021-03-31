package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Access
import org.lm.frontend.syntaxtree.Id
import org.lm.frontend.syntaxtree.Singleton

/**
 * This class describe a Three-Address-Instruction as leftSide = array[index]
 */
class ArrayRefCode: ArrayCode {
    var leftSide: Id;

    constructor(leftSide: Id, acc: Access): super(acc.array as Id, acc.index as Singleton) {
        this.leftSide = leftSide;
    }

    public override fun toString(): String {
        return (leftSide.toString() + " = " + array.toString() + "[" +index.toString()+ "]");
    }
}