package org.lm.frontend.lexer

class Real: Token {

    public val value: Float;

    constructor(v: Float): super(Tag.REAL.value) {
        this.value = v;
    }

    override fun toString(): String {
        return "" + value;
    }
}