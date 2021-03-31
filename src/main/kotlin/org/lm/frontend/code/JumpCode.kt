package org.lm.frontend.code

/**
 * This abstract subclass of a Three-Address-Code describes all jump instructions, where
 * every instruct have a goal label to jump.
 */
abstract class JumpCode: ThreeAddrCode {

    private var label: Int = 0

    constructor() {}

    public override fun setLabel(i: Int) {
        this.label = i;
    }

    public override fun getLabel(): Int {
        return this.label;
    }
}