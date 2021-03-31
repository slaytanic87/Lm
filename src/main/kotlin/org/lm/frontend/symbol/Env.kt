package org.lm.frontend.symbol

import org.lm.frontend.lexer.Token
import org.lm.frontend.syntaxtree.Id
import java.util.*

/**
 * This class implements the symbol table,
 * which is represents as a linked list of hash tables.
 * The hash table can be access through the token as the key
 * and the return values are classes of the type Id.
 */
class Env {
    private var table: Hashtable<Token, Id> = Hashtable();
    protected var prev: Env?;

    constructor(prevEnv: Env?) {
        prev = prevEnv;
    }

    public fun put(w:Token, id:Id) {
        table.put(w, id);
    }

    public fun contains(w: Token): Boolean {
        return table.containsKey(w);
    }

    public fun get(w:Token): Id? {
        var current: Env? = this;
        while (current != null) {
            var found: Id? = current.table.get(w);
            if (found != null) {
                return found;
            }
            current = current.prev;
        }
        return null;
    }
}