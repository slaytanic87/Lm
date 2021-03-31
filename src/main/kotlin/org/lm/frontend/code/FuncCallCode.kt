package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Singleton

class FuncCallCode: FuncCode {

    var numberParams: Int;

    constructor(identifier: Singleton, numberParams: Int): super(identifier) {
        this.numberParams = numberParams;
    }

    public override fun toString(): String {
        return "call " +ident.toString()+ ", "+this.numberParams;
    }
}