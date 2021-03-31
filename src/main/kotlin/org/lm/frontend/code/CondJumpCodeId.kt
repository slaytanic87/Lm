package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Singleton

/**
 * This class describe conditional jumps as if (id) goto label
 */
class CondJumpCodeId: JumpCode {

    var ident: Singleton;

    constructor(id: Singleton, label: Int) {
        this.ident = id;
        super.setLabel(label);
    }

    public override fun toString(): String {
        return ("if " + ident.toString() + " goto " + getLabel());
    }
}