package org.lm.frontend.syntaxtree

import org.lm.frontend.lexer.Word
import org.lm.frontend.symbol.Type

/**
 * This class describe a temporary assigned identifier
 */
class Temp: Id {
    companion object {
        @Volatile
        var count: Int = 0;
        // in case when more code generators are running one after another
        public fun resetCounter() {
            count = 0;
        }
    }
    var number: Int;

    constructor(p: Type?): super(Word.tmp, p, 0) {
        number = ++count;
    }

    public override fun toString(): String {
        return "t" + number;
    }
}