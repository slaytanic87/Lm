package org.lm.frontend.syntaxtree

import org.lm.frontend.symbol.Type

abstract class Stmt: Node {
    // Label number for the next attribut
    public var next: Int = 0;

    companion object {
        public var enclosing: Stmt? = null;
        public var returning: Type? = null;
    }

    constructor() {
    }

}