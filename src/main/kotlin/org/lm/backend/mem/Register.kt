package org.lm.backend.mem

import org.lm.frontend.syntaxtree.Id

class Register: Store {

    public var bits: Int;
    public var id: Id? = null;

    constructor(name: String, bitLength: Int): super(name) {
        this.bits = bitLength;
    }
}