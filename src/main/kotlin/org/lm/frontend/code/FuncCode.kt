package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Singleton

abstract class FuncCode: ThreeAddrCode {

    protected var ident: Singleton;

    constructor(identifier: Singleton) {
        this.ident = identifier;
    }

}