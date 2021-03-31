package org.lm.frontend.code

abstract class ThreeAddrCode {

    public open fun getLabel(): Int {
        return 0;
    }

    public open fun setLabel(i: Int) {
        throw Error("setLabel was set on a non jump command");
    }
}