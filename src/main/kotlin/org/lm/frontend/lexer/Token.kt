package org.lm.frontend.lexer

open class Token {

    public val tag: Int;

    constructor(tag: Int) {
        this.tag = tag;
    }

    override fun toString(): String {
        return "" + tag.toChar()
    }
}