package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Singleton

class ReturnCode: ThreeAddrCode {
    var ident: Singleton;

    constructor(id: Singleton) {
        this.ident = id;
    }

    override public fun toString(): String {
        return "return "+ this.ident.toString();
    }

}