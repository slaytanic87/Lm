package org.lm.frontend.treewalker

import org.lm.frontend.syntaxtree.*
import org.lm.frontend.syntaxtree.Function

abstract class TreeWalker<S, T> {

    public fun walk(node: Node?, arg: T): S? {
        return node?.walk(this, arg);
    }

    public abstract fun walkAccessNode(node: Access, arg: T): S;
    public abstract fun walkAndNode(node: And, arg: T): S;
    public abstract fun walkArithNode(node: Arith, arg: T): S;
    public abstract fun walkAssignElemNode(node: AssignElem, arg: T): S;
    public abstract fun walkAssignIdNode(node: AssignId, arg: T): S;
    public abstract fun walkAssignStmtNode(node: AssignStmt, arg: T): S;
    public abstract fun walkBlockNode(node: Block, arg: T): S;
    public abstract fun walkBreakNode(node: Break, arg: T): S;
    public abstract fun walkConstantNode(node: Constant, arg: T): S;
    public abstract fun walkDoNode(node: Do, arg: T): S;
    public abstract fun walkElseNode(node: Else, arg: T): S;
    public abstract fun walkEmptyStmtNode(node: EmptyStmt, arg: T): S;
    public abstract fun walkForNode(node: For, arg: T): S;
    public abstract fun walkIdNode(node: Id, arg: T): S;
    public abstract fun walkIfNode(node: If, arg: T): S;
    public abstract fun walkNotNode(node: Not, arg: T): S;
    public abstract fun walkOrNode(node: Or, arg: T): S;
    public abstract fun walkProgramNode(node: Program, arg: T): S;
    public abstract fun walkRelNode(node: Rel, arg: T): S;
    public abstract fun walkSeqNode(node: Seq, arg: T): S;
    public abstract fun walkUnaryNode(node: Unary, arg: T): S;
    public abstract fun walkWhileNode(node: While, arg: T): S;
    public abstract fun walkReturnNode(node: Return, arg: T): S;
    public abstract fun walkFunctionNode(node: Function, arg: T): S;
    public abstract fun walkScopeNode(node: Scope, arg: T): S;
    public abstract fun walkFuncExprNode(node: FuncExpr, arg: T): S;
    public abstract fun walkExprCallNode(node: ExprCall, arg: T): S;
}