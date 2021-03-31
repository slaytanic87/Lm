package org.lm.frontend.lexer

import org.lm.frontend.symbol.Type

class Array: Type {

    public var of: Type;
    var size: Int;

    constructor(size: Int, type: Type):  super("[]", Tag.INDEX.value, size * type.width) {
        this.of = type;
        this.size = size;
    }

    companion object {
        public fun baseType(type: Type): Type {
            var t: Type = type;
            while (type.tag == Tag.INDEX.value) {
                t = (t as Array).of;
            }
            return t;
        }
    }


    override public fun toString(): String {
        return "[" + size + "] " + of.toString();
    }

}