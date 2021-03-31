package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Singleton

class FuncEndCode: FuncCode {

    constructor(id: Singleton): super(id) {
    }

    public override fun toString(): String {
        return "end func";
    }

}