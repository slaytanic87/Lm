package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Access
import org.lm.frontend.syntaxtree.Id
import org.lm.frontend.syntaxtree.Singleton

/**
 * This class describe a Three-Address-Instruction as array[index] = rightSide
 */
class ArrayAssignCode: ArrayCode {
    var rightSide: Singleton;

    constructor(acc: Access, rightSide: Singleton): super(acc.array as Id, acc.index as Singleton) {
        this.rightSide = rightSide;
    }

    public override fun toString(): String {
        return (array.toString() + "[" +index.toString() + "] = " + rightSide.toString());
    }
}