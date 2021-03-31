package org.lm.frontend.treewalker

import org.lm.frontend.lexer.Array
import org.lm.frontend.lexer.Token
import org.lm.frontend.symbol.Type
import org.lm.frontend.syntaxtree.*
import org.lm.frontend.syntaxtree.Function
import java.util.*

/**
 * This class describe a Treewalker, which walk through the syntax tree and transform
 * multiple dimension array to one dimension array with the respect to the size of the
 * elements.
 * The transformed access are represents as startaddress[offset]
 */
class TransformTreeWalker: TreeWalker<Expr?, Unit> {

    constructor() {}

    private fun transformNestedAccess(node: Access): Expr {
        var nodeTmp: Access = node;
        val tmpNodeList: ArrayList<Expr> = ArrayList();
        var t1: Expr? = null;
        while (nodeTmp.array?.javaClass == Access::javaClass) {
            tmpNodeList.add(nodeTmp.array as Expr);
            nodeTmp = nodeTmp.array as Access;
        }
        tmpNodeList.add(nodeTmp.index as Expr);
        if (tmpNodeList.size == 1) {
            return tmpNodeList.get(0);
        }
        for (i in tmpNodeList.size - 1 downTo 1) {
            val tmpNode: Expr = tmpNodeList.get(i);
            val acc: Access = tmpNodeList.get(i - 1) as Access;
            val type: Type = acc.type as Type;

            val word: Constant = Constant((type as Array).size);
            t1 = Arith(Token('*'.toInt()), tmpNode, word, Type.int);
            t1 = Arith(Token('+'.toInt()), t1, acc.index as Expr, Type.int); // t1 = tmpNode * arraysize + index
        }
        return t1 as Expr;
    }

    private fun getArrayName(acc: Access): Id {
        var arrExpr: Expr? = acc.array;
        while (arrExpr?.javaClass == Access::class) {
            arrExpr = (arrExpr as Access).array;
        }
        return arrExpr as Id;
    }

    private fun transformArray(acc: Access): Access {
        val aName: Id = getArrayName(acc);
        val type: Type = acc.array?.type as Type;
        val rNode: Expr = transformNestedAccess(acc);
        val w: Expr = Constant((type as Array).of.width);
        val t: Expr = Arith(Token('*'.toInt()), rNode, w, Type.int);
        return Access(aName, t, acc.type);
    }

    override fun walkAccessNode(node: Access, arg: Unit): Expr {
        val tNode: Access = transformArray(node);
        tNode.index = walk(tNode.index, Unit);
        return tNode;
    }

    override fun walkAndNode(node: And, arg: Unit): Expr {
        node.expr1 = walk(node.expr1, Unit);
        node.expr2 = walk(node.expr2, Unit);
        return node;
    }

    override fun walkArithNode(node: Arith, arg: Unit): Expr {
        node.expr1 = walk(node.expr1, Unit);
        node.expr2 = walk(node.expr2, Unit);
        return node;
    }

    override fun walkAssignElemNode(node: AssignElem, arg: Unit): Expr? {
        node.acc = walk(node.acc, Unit) as Access;
        node.expr = walk(node.expr, Unit);
        return null;
    }

    override fun walkAssignIdNode(node: AssignId, arg: Unit): Expr? {
        node.expr = walk(node.expr, Unit) as Expr;
        return null;
    }

    override fun walkAssignStmtNode(node: AssignStmt, arg: Unit): Expr? {
        walk(node.assign, Unit);
        return null;
    }

    override fun walkBlockNode(node: Block, arg: Unit): Expr? {
        walk(node.stmts, Unit);
        return null;
    }

    override fun walkBreakNode(node: Break, arg: Unit): Expr? {
        return null;
    }

    override fun walkConstantNode(node: Constant, arg: Unit): Expr {
        return node;
    }

    override fun walkDoNode(node: Do, arg: Unit): Expr? {
        node.expr = walk(node.expr, Unit);
        walk(node.stmt, Unit);
        return null;
    }

    override fun walkElseNode(node: Else, arg: Unit): Expr? {
        node.expr = walk(node.expr, Unit);
        walk(node.stmt1, Unit);
        walk(node.stmt2, Unit);
        return null;
    }

    override fun walkEmptyStmtNode(node: EmptyStmt, arg: Unit): Expr? {
        return null;
    }

    override fun walkForNode(node: For, arg: Unit): Expr? {
        node.expr = walk(node.expr, Unit);
        walk(node.initAssig, Unit);
        walk(node.iterAssign, Unit);
        walk(node.stmt, Unit);
        return null;
    }

    override fun walkIdNode(node: Id, arg: Unit): Expr {
        return node;
    }

    override fun walkIfNode(node: If, arg: Unit): Expr? {
        node.expr = walk(node.expr, Unit);
        walk(node.stmt, Unit);
        return null;
    }

    override fun walkNotNode(node: Not, arg: Unit): Expr {
        node.expr1 = walk(node.expr1, Unit);
        return node;
    }

    override fun walkOrNode(node: Or, arg: Unit): Expr {
        node.expr1 = walk(node.expr1, Unit);
        node.expr2 = walk(node.expr2, Unit);
        return node;
    }

    override fun walkProgramNode(node: Program, arg: Unit): Expr? {
        walk(node.scope, Unit);
        return null;
    }

    override fun walkRelNode(node: Rel, arg: Unit): Expr {
        node.expr1 = walk(node.expr1, Unit);
        node.expr2 = walk(node.expr2, Unit);
        return node;
    }

    override fun walkSeqNode(node: Seq, arg: Unit): Expr? {
        walk(node.stmt1, Unit);
        walk(node.stmt2, Unit);
        return null;
    }

    override fun walkUnaryNode(node: Unary, arg: Unit): Expr {
        node.expr = walk(node.expr, Unit);
        return node;
    }

    override fun walkWhileNode(node: While, arg: Unit): Expr? {
        node.expr = walk(node.expr, Unit);
        walk(node.stmt, Unit);
        return null;
    }

    override fun walkReturnNode(node: Return, arg: Unit): Expr? {
        node.bool = walk(node.bool, Unit);
        return null;
    }

    override fun walkFunctionNode(node: Function, arg: Unit): Expr? {
        walk(node.block, Unit);
        return null;
    }

    override fun walkScopeNode(node: Scope, arg: Unit): Expr? {
        walk(node.stmts, Unit);
        return null;
    }

    override fun walkFuncExprNode(node: FuncExpr, arg: Unit): Expr? {
        for (i in 0 until node.params.size) {
            node.params[i] = walk(node.params.get(i), Unit) as Expr;
        }
        return node;
    }

    override fun walkExprCallNode(node: ExprCall, arg: Unit): Expr? {
        node.expr = walk(node.expr, Unit);
        return null;
    }
}