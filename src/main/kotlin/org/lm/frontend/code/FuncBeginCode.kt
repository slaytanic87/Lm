package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Singleton

class FuncBeginCode: FuncCode {

    constructor(id: Singleton): super(id) {
    }

    public override fun toString(): String {
        return "func begin " + super.ident.toString()
    }

}