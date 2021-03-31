package org.lm.frontend.treewalker

import org.lm.frontend.lexer.Tag
import org.lm.frontend.lexer.Word
import org.lm.frontend.symbol.Type
import org.lm.frontend.syntaxtree.*
import org.lm.frontend.syntaxtree.Function
import org.lm.frontend.lexer.Array
import java.lang.Error
import kotlin.collections.ArrayList

class SemanticTreeWalker: TreeWalker<Type?, Unit> {

    constructor() {
    }

    private fun error(line: Int, str: String) {
        throw Error("Type error near line: "+ line + " " + str);
    }

    private fun isNumeric(p: Type?): Boolean {
        return (p == Type.char || p == Type.int || p == Type.float);
    }

    private fun castType(p1: Type?, p2: Type?): Type? {
        if (!isNumeric(p1) || !isNumeric(p2)) {
            return null;
        } else if (p1 == Type.float || p2 == Type.float) {
            return Type.float;
        } else if (p1 == Type.int || p2 == Type.int) {
            return Type.int;
        } else if (p1 == Type.long || p2 == Type.long) {
            return Type.long;
        } else {
            return Type.char;
        }
    }

    private fun coerceExpr(e: Expr?, t: Type?): Expr {
        val conv: Word = if (t == Type.int) Word.toInt else Word.toFloat;
        val node: Unary = Unary(conv, e);
        node.type = t;
        return node;
    }

    private fun checkLogical(b1: Type?, b2: Type?): Boolean {
        return (b1 == Type.bool) && (b2 == Type.bool);
    }

    private fun checkArrayAccessType(a: Type?, t: Type?): Type? {
        if (a?.tag == Tag.INDEX.value && (t == Type.int || t == Type.char)) {
            return (a as Array).of;
        } else {
            return null;
        }
    }

    override fun walkAccessNode(node: Access, arg: Unit): Type {
        val index: Type? = walk(node.index, Unit);
        val a: Type? = walk(node.array, Unit);
        val resType: Type? = checkArrayAccessType(a, index);
        if (resType == null) {
            error(node.lexline, "array type error");
        }
        node.type = resType;
        if (index == Type.char) {
            node.index = coerceExpr(node.index, Type.int);
        }
        return resType as Type;
    }

    override fun walkAndNode(node: And, arg: Unit): Type {
        val p1: Type? = walk(node.expr1, Unit);
        val p2: Type? = walk(node.expr2, Unit);

        if (checkLogical(p1, p2)) {
            error(node.lexline, "node: and");
        }
        node.type = Type.bool;
        return Type.bool;
    }

    override fun walkArithNode(node: Arith, arg: Unit): Type {
        val p1: Type? = walk(node.expr1, Unit);
        val p2: Type? = walk(node.expr2, Unit);
        val resType: Type? = castType(p1, p2);
        if (resType == null) {
            error(node.lexline, "node: Arith");
        }
        if (p1 != resType) {
            node.expr1 = coerceExpr(node.expr1, resType as Type);
        } else if (p2 != resType) {
            node.expr2 = coerceExpr(node.expr2, resType as Type);
        }
        node.type = resType;
        return resType as Type;
    }

    override fun walkAssignElemNode(node: AssignElem, arg: Unit): Type? {
        val accType: Type? = walk(node.acc, Unit);
        val exprType: Type? = walk(node.expr, Unit);
        val resType: Type? = castType(accType, exprType);
        if (accType == exprType && isNumeric(accType)) {
            return null;
        }
        if (checkLogical(accType, exprType)) {
            return null;
        }
        if (isNumeric(accType) && (accType == resType)) {
            node.expr = coerceExpr(node.expr, accType);
        } else {
            error(node.lexline, "incompatible array assignment between " +accType+ " and " + exprType);
        }
        return null;
    }

    override fun walkAssignIdNode(node: AssignId, arg: Unit): Type? {
        val identType: Type? = walk(node.ident, Unit);
        val exprType: Type? = walk(node.expr, Unit);
        val resType: Type? = castType(identType, exprType);
        if (identType == exprType && isNumeric(identType)) {
            return null;
        }
        if (checkLogical(identType, exprType)) {
            return null;
        }
        if (isNumeric(identType) && (identType == resType)) {
            node.expr = coerceExpr(node.expr, identType);
        } else {
            error(node.lexline, "incompatible array assignment between "+identType+ " and " + exprType);
        }
        return null;
    }

    override fun walkAssignStmtNode(node: AssignStmt, arg: Unit): Type? {
        return walk(node.assign, Unit);
    }

    override fun walkBlockNode(node: Block, arg: Unit): Type? {
        walk(node.stmts, Unit);
        return null;
    }

    override fun walkBreakNode(node: Break, arg: Unit): Type? {
        if (Stmt.enclosing == null) {
            error(node.lexline, "unenclosed break")
        }
        node.stmt = Stmt.enclosing;
        return null;
    }

    override fun walkConstantNode(node: Constant, arg: Unit): Type? {
        return node.type;
    }

    override fun walkDoNode(node: Do, arg: Unit): Type? {
        val exprType: Type? = walk(node.expr, Unit);
        if (exprType != Type.bool) {
            error(node.lexline, "Boolean expression required in do statement");
        }
        val savedStmt = Stmt.enclosing;
        walk(node.stmt, Unit);
        Stmt.enclosing = savedStmt;
        return null;
    }

    override fun walkElseNode(node: Else, arg: Unit): Type? {
        val exprType: Type? = walk(node.expr, Unit);
        if (exprType != Type.bool) {
            error(node.lexline, "Boolean required in if statement");
        }
        walk(node.stmt1, Unit);
        walk(node.stmt2, Unit);
        return null;
    }

    override fun walkEmptyStmtNode(node: EmptyStmt, arg: Unit): Type? {
        return null;
    }

    override fun walkForNode(node: For, arg: Unit): Type? {
        val savedStmt: Stmt? = Stmt.enclosing;
        Stmt.enclosing = node;
        walk(node.initAssig, Unit);
        val typeExpr: Type? = walk(node.expr, Unit);
        if (typeExpr != Type.bool) {
            error(node.lexline, "Boolean expression required in for statement");
        }
        walk(node.iterAssign, Unit);
        walk(node.stmt, Unit);
        Stmt.enclosing = savedStmt;
        return null;
    }

    override fun walkIdNode(node: Id, arg: Unit): Type? {
        return node.type;
    }

    override fun walkIfNode(node: If, arg: Unit): Type? {
        val exprType: Type? = walk(node.expr, Unit);
        if (exprType != Type.bool) {
            error(node.lexline, "Boolean expression required in if statement");
        }
        walk(node.stmt, Unit);
        return null;
    }

    override fun walkNotNode(node: Not, arg: Unit): Type {
        val typeExpr: Type? = walk(node.expr1, Unit);
        if (typeExpr != Type.bool) {
            error(node.lexline, "Not operator expected a boolean expression but got " + typeExpr);
        }
        node.type = Type.bool;
        return Type.bool;
    }

    override fun walkOrNode(node: Or, arg: Unit): Type {
        val exprTypeLeft: Type? = walk(node.expr1, Unit);
        val exprTypeRight: Type? = walk(node.expr2, Unit);
        if (!checkLogical(exprTypeLeft, exprTypeRight)) {
            error(node.lexline, "Or operator expected boolean expression but got left: "
                    + exprTypeLeft + " right: " + exprTypeRight);
        }
        node.type = Type.bool;
        return Type.bool;
    }

    override fun walkProgramNode(node: Program, arg: Unit): Type? {
        walk(node.scope, Unit);
        return null;
    }

    override fun walkRelNode(node: Rel, arg: Unit): Type {
        val exprType1 = walk(node.expr1, Unit);
        val exprType2 = walk(node.expr2, Unit);
        val resType = castType(exprType1, exprType2);
        if (resType == null) {
            error(node.lexline, "Relation expressions have to be a numeric value");
        }
        if (exprType1 != resType) {
            node.expr1 = coerceExpr(node.expr1, resType);
        } else if (exprType2 != resType) {
            node.expr2= coerceExpr(node.expr2, resType);
        }
        node.type = Type.bool;
        return Type.bool;
    }

    override fun walkSeqNode(node: Seq, arg: Unit): Type? {
        val rType1: Type? = walk(node.stmt1, Unit);
        val rType2: Type? = walk(node.stmt2, Unit);
        return null;
    }

    override fun walkUnaryNode(node: Unary, arg: Unit): Type? {
        val exprType: Type? = walk(node.expr, Unit);
        if (!isNumeric(exprType)) {
            error(node.lexline, "Unary expression have to be a numeric");
        }
        node.type = exprType;
        return exprType;
    }

    override fun walkWhileNode(node: While, arg: Unit): Type? {
        val exprType: Type? = walk(node.expr, Unit);
        if (exprType != Type.bool) {
            error(node.lexline, "Boolean expression is required in while statement");
        }
        val savedStmt: Stmt? = Stmt.enclosing;
        Stmt.enclosing = node;
        walk(node.stmt, Unit);
        Stmt.enclosing = savedStmt;
        return null;
    }

    override fun walkReturnNode(node: Return, arg: Unit): Type? {
        val returnType: Type? = walk(node.bool, Unit);
        if (Stmt.returning != returnType) {
            error(node.lexline, "return type " + returnType
                    + " mismatch with function declaration type " +Stmt.returning);
        }
        return returnType;
    }

    override fun walkFunctionNode(node: Function, arg: Unit): Type? {
        val returnType: Type? = walk(node.id, Unit);
        val savedReturnStmt: Type? = Stmt.returning;
        Stmt.returning = returnType;
        // TODO check that every function should have a return statement if necessary
        val returnBlockType: Type? = walk(node.block, Unit);
        Stmt.returning = savedReturnStmt;
        return null;
    }

    override fun walkScopeNode(node: Scope, arg: Unit): Type? {
        walk(node.stmts, Unit);
        return null;
    }

    override fun walkFuncExprNode(node: FuncExpr, arg: Unit): Type? {
        val returnType: Type? = walk(node.id, Unit);
        val exprParamList: ArrayList<Expr> = node.params;
        val defParamList: ArrayList<Id> = node.id.funcParams;
        if (exprParamList.size != defParamList.size) {
            error(node.lexline, "the number of parameters does not match");
        }
        for (i in 0 until defParamList.size) {
            val exprType: Type? = walk(exprParamList.get(i), Unit);
            val defType: Type? = walk(defParamList.get(i), Unit);
            if (exprType  != defType) {
                error(node.lexline, "parameter type " + exprType +
                        " does not match with " + defType);
            }
        }
        return returnType;
    }

    override fun walkExprCallNode(node: ExprCall, arg: Unit): Type? {
        walk(node.expr, Unit);
        return null;
    }
}