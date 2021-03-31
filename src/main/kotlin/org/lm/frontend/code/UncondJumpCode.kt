package org.lm.frontend.code

/**
 * This class describe unconditional jumps as goto label
 */
class UncondJumpCode: JumpCode {

    constructor(i: Int) {
        super.setLabel(i);
    }

    public override fun toString(): String {
        return ("goto " + getLabel());
    }
}