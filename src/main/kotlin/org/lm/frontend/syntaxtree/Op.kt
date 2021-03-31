package org.lm.frontend.syntaxtree

import org.lm.frontend.lexer.Token

abstract class Op: Expr {
    
    constructor(token: Token?): super(token, null) {
    }
}