package org.lm.frontend.symbol

import org.lm.frontend.lexer.Tag
import org.lm.frontend.lexer.Word

open class Type: Word {

    public var width: Int;

    constructor(s: String, tag: Int, width: Int): super(s, tag) {
        this.width = width;
    }

    companion object {
        val int: Type =
            Type("int", Tag.BASIC.value, 4);
        val long: Type =
            Type("long", Tag.BASIC.value, 8);
        val float: Type =
            Type("float", Tag.BASIC.value, 4);
        val double: Type =
            Type("double", Tag.BASIC.value, 8);
        val char: Type =
            Type("char", Tag.BASIC.value, 1);
        val bool: Type =
            Type("bool", Tag.BASIC.value, 1);
        val void: Type =
            Type("void", Tag.BASIC.value, 1);
    }


}