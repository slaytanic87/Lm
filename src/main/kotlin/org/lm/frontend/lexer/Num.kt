package org.lm.frontend.lexer

class Num: Token {

    public val value: Int;

    constructor(v: Int): super(Tag.NUM.value) {
        this.value = v;
    }

    override fun toString(): String {
        return "" + value;
    }
}