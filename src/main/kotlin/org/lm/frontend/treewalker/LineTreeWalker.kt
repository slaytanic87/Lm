package org.lm.frontend.treewalker

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lm.frontend.syntaxtree.*
import org.lm.frontend.syntaxtree.Function
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class LineTreeWalker: TreeWalker<Unit, String> {

    private var log: Logger = LogManager.getLogger(LineTreeWalker::class.java);
    private var streamWriter: OutputStreamWriter;
    private var writeText: Boolean = false;
    companion object {
        var indent: String = "|  ";
    }

    constructor(writeText: Boolean) {
        this.writeText = writeText;
        streamWriter = OutputStreamWriter(FileOutputStream("syntaxtree.txt"));
    }

    override fun walkAccessNode(node: Access, arg: String): Unit {
        val str = arg + "Access";
        write(str)
        walk(node.array, arg + indent);
        walk(node.index, arg + indent);
    }

    override fun walkAndNode(node: And, arg: String) {
        val str = arg + "And";
        write(str)
        walk(node.expr1, arg + indent);
        walk(node.expr2, arg + indent);
    }

    override fun walkArithNode(node: Arith, arg: String) {
        val str = arg + "Arith (" + node.op.toString() + ")";
        write(str)
        walk(node.expr1, arg + indent);
        walk(node.expr2, arg + indent);
    }

    override fun walkAssignElemNode(node: AssignElem, arg: String) {
        val str = arg + "AssignElem";
        log.debug(arg + "AssignElem");
        streamWriter.write(str + "\n");
        walk(node.acc, arg + indent);
        walk(node.expr, arg + indent);
    }

    override fun walkAssignIdNode(node: AssignId, arg: String) {
        val str = arg + "AssignId";
        write(str)
        walk(node.ident, arg + indent);
        walk(node.expr, arg + indent);
    }

    override fun walkAssignStmtNode(node: AssignStmt, arg: String) {
        val str = arg + "AssignStmt";
        log.debug(str);
        streamWriter.write(str + "\n")
        walk(node.assign, arg + indent);
    }

    override fun walkBlockNode(node: Block, arg: String) {
        val str = arg + "Block";
        log.debug(arg + "Block");
        streamWriter.write(str + "\n");
        walk(node.stmts, arg + indent);
    }

    override fun walkBreakNode(node: Break, arg: String) {
        val str = arg + "Break";
        log.debug(arg + "Break");
        streamWriter.write(str + "\n");
    }

    override fun walkConstantNode(node: Constant, arg: String) {
        val str = arg + "Constant (" + node.op.toString() + ")";
        write(str)
    }

    override fun walkDoNode(node: Do, arg: String) {
        val str = arg + "Do";
        write(str)
        walk(node.stmt, arg + indent);
        walk(node.expr, arg + indent);
    }

    override fun walkElseNode(node: Else, arg: String) {
        val str = arg + "IfElse";
        write(str)
        walk(node.expr, arg + indent);
        walk(node.stmt1, arg + indent);
        walk(node.stmt2, arg + indent);
    }

    override fun walkEmptyStmtNode(node: EmptyStmt, arg: String) {
        val str = arg + "EmptyStmt";
        write(str)
    }

    override fun walkForNode(node: For, arg: String) {
        val str = arg + "For";
        write(str)
        walk(node.initAssig, arg + indent);
        walk(node.expr, arg + indent);
        walk(node.initAssig, arg + indent);
        walk(node.stmt, arg + indent);
    }

    override fun walkIdNode(node: Id, arg: String) {
        val str: String;
        if (node.isFuntion) {
            str = arg + "Return type ("+node.type.toString() + ")";
            write(str)
            return;
        }
        str = arg + "Id ("+node.op.toString() + ")";
        write(str)
    }

    override fun walkIfNode(node: If, arg: String) {
        val str = arg + "If";
        log.debug(str)
        streamWriter.write(str + "\n");
        walk(node.expr, arg + indent);
        walk(node.stmt, arg + indent);
    }

    override fun walkNotNode(node: Not, arg: String) {
        val str = arg + "Not";
        write(str)
        walk(node.expr1, arg + indent);
    }

    override fun walkOrNode(node: Or, arg: String) {
        val str = arg + "Or";
        write(str)
        walk(node.expr1, arg + indent);
        walk(node.expr2, arg + indent);
    }

    override fun walkProgramNode(node: Program, arg: String) {
        val str = arg + "Program";
        write(str)
        walk(node.scope, arg);
    }

    override fun walkRelNode(node: Rel, arg: String) {
        val str = arg + "Rel (" +node.op +")";
        write(str)
        walk(node.expr1, arg + indent);
        walk(node.expr2, arg + indent);
    }

    override fun walkSeqNode(node: Seq, arg: String) {
        val str = arg + "Seq";
        write(str)
        walk(node.stmt1, arg + indent);
        walk(node.stmt2, arg);
    }

    override fun walkUnaryNode(node: Unary, arg: String) {
        val str = arg + "Unary (" + node.op.toString() + ")";
        write(str)
        walk(node.expr, arg + indent);
    }

    override fun walkWhileNode(node: While, arg: String) {
        val str = arg + "While";
        write(str)
        walk(node.expr, arg + indent);
        walk(node.stmt, arg + indent);
    }

    override fun walkReturnNode(node: Return, arg: String) {
        val str = arg + "Return";
        write(str)
        walk(node.bool, arg + indent);
    }

    override fun walkFunctionNode(node: Function, arg: String) {
        val str = arg + "Function ("+ node.id.op + ")";
        write(str)
        walk(node.id, arg + indent);
        walk(node.block, arg + indent);
    }

    override fun walkScopeNode(node: Scope, arg: String) {
        val str = arg + "Scope";
        write(str)
        walk(node.stmts, arg + indent);
    }

    override fun walkFuncExprNode(node: FuncExpr, arg: String) {
        val str = arg + "FuncExpr (" + node.id.op + ")";
        write(str)
        walk(node.id, arg + indent);
        val paramLst: List<Expr> = node.params;
        for (expr in paramLst) {
            walk(expr, arg + indent);
        }
    }

    override fun walkExprCallNode(node: ExprCall, arg: String) {
        val str = arg + "ExprCall";
        write(str)
        walk(node.expr, arg + indent);
    }

    private fun write(str: String) {
        log.debug(str);
        if (writeText) {
            streamWriter.write(str + "\n");
        }
    }

    public fun finish() {
        if (writeText) {
            streamWriter.close();
        }
    }
}