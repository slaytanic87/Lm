package org.lm.frontend.syntaxtree

import org.lm.frontend.lexer.Word
import org.lm.frontend.symbol.Type
import org.lm.frontend.treewalker.TreeWalker
import java.util.*

open class Id: Singleton {
    var offset: Int;
    var isFuntion: Boolean = false;
    var funcParams: ArrayList<Id> = ArrayList<Id>();

    constructor(id: Word, type: Type?, offset: Int): super(id, type) {
        this.offset = offset;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkIdNode(this, arg);
    }

}