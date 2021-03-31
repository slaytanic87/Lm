package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Id
import org.lm.frontend.syntaxtree.Singleton

/**
 * This abstract class describes an Three-Address-Code array access.
 */
abstract class ArrayCode: ThreeAddrCode {
    protected var array:Id;
    protected var index: Singleton;

    constructor(array: Id, index: Singleton) {
        this.array = array;
        this.index = index;
    }
}