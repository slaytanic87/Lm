package org.lm.frontend.syntaxtree

import org.lm.frontend.treewalker.TreeWalker
import java.util.*

class FuncExpr: Expr {
    var id: Id;
    var params: ArrayList<Expr>;

    constructor(id: Id, params: ArrayList<Expr>): super(id.op, id.type) {
        this.id = id;
        this.params = params;
    }

    override fun <S, T> walk(walker: TreeWalker<S, T>, arg: T): S {
        return walker.walkFuncExprNode(this, arg);
    }
}