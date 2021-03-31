package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker

class Function: Stmt {

    public var id: Id;
    public var block: Block;

    constructor(returnId: Id, block: Block) {
        this.id = returnId;
        this.block = block;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkFunctionNode(this, arg);
    }
}