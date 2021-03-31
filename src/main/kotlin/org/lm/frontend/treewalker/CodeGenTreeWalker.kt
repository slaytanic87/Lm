package org.lm.frontend.treewalker

import org.lm.frontend.code.*
import org.lm.frontend.lexer.Tag
import org.lm.frontend.lexer.Token
import org.lm.frontend.lexer.Word
import org.lm.frontend.symbol.Type
import org.lm.frontend.syntaxtree.*
import org.lm.frontend.syntaxtree.Function
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*
import kotlin.collections.ArrayList


class CodeGenTreeWalker:
    TreeWalker<Expr?, Labels?> {

    private var nextCode: Int;
    private var labels: Int;
    public var code: ArrayList<ThreeAddrCode>;
    private var labelIsOnOption: Hashtable<Int, Int>;

    constructor() {
        Temp.resetCounter();
        Result.resetCounter();
        nextCode = 1;
        labels = 0;
        code = ArrayList<ThreeAddrCode>();
        labelIsOnOption = Hashtable<Int, Int>();
    }

    public fun exportCode() {
        val streamWriter: OutputStreamWriter = OutputStreamWriter(FileOutputStream("intercode.code"));
        var i: Int = 1;
        for (c in code) {
            streamWriter.write("${i++}:\t$c\n");
        }
        streamWriter.close();
    }

    private fun createNewLabel(): Int {
        return ++labels;
    }

    private fun emitLabel(label: Int) {
        labelIsOnOption.put(label, nextCode);
    }

    private fun emitCode(c: ThreeAddrCode) {
        code.add(c);
        nextCode++;
    }

    private fun adjustLabels() {
        var k: Int;
        for (c in code) {
            k = c.getLabel();
            if (k != 0) {
                c.setLabel(labelIsOnOption.get(k) as Int);
            }
        }
    }

    private fun chooseJumps(exp: Expr, label: Int) {
        if (exp.isSingleton()) {
            emitCode(CondJumpCodeId(exp as Singleton, label));
        } else {
            emitCode(CondJumpCodeRel(exp as Logical, label));
        }
    }

    /**
     * We need to invert the boolean operator to emit conditional jumps
     */
    private fun changeBooleanValue(exp: Expr): Logical {
        val token: Token? = exp.op;
        val tokenNew: Token;
        when (token?.tag) {
            '<'.toInt() -> {
                tokenNew = Word.ge;
            };
            '>'.toInt() -> {
                tokenNew = Word.le;
            };
            Tag.GE.value -> {
                tokenNew = Word.ls;
            };
            Tag.LE.value -> {
                tokenNew = Word.gr;
            };
            Tag.EQ.value -> {
                tokenNew = Word.ne;
            };
            else -> {
                throw Error("Cannot invert boolean operator for " + token?.tag)
            }
        }
        exp.op = tokenNew;
        return exp as Logical;
    }

    /**
     * Boolean expressions are evaluate with the short-cut method.
     * The label which was given through the arg parameter will be hand over to the
     * correspond boolean function. The label 0 stands for continue the program
     * sequence without a jump.
     */
    private fun emitGotos(exp: Expr, args: Labels) {
        val trueLabel: Int = args.trueLabel();
        val falseLabel: Int = args.falseLabel();
        if (trueLabel != 0 && falseLabel != 0) {
            chooseJumps(exp, trueLabel);           // if exp goto trueLabel
            emitCode(UncondJumpCode(falseLabel));  // goto falseLabel
        } else if (trueLabel != 0) {
            chooseJumps(exp, trueLabel);           // if exp goto trueLabel
        } else if (falseLabel != 0) {
            /* Special case when simulation of iffalse by switching the
             * relation operator does not work
             */
            if (exp.isSingleton()) {
                val label: Int = createNewLabel();
                emitCode(CondJumpCodeId(exp as Singleton, label)); // if exp goto label
                emitCode(UncondJumpCode(falseLabel));              // goto falseLabel
                emitLabel(label);                                  // label
            } else { // for the case when exp is a Rel
                emitCode(CondJumpCodeRel(changeBooleanValue(exp), falseLabel));
            }
        }
    }

    /**
     * If the expression a type of boolean, then it must be process in an extra case.
     * Because Three-Address-Instruction does not allow boolean operation.
     * So a new temporary variable is set on true and one on false and
     * jumps will be generated.
     */
    private fun processBooleanExpr(booleanExpr: Expr): Expr {
        if (booleanExpr.isConstant()) {
            return booleanExpr as Singleton;
        }
        val trueLabel: Int = createNewLabel();
        val falseLabel: Int  = createNewLabel();
        val tmp: Temp = Temp(Type.bool);
        walk(booleanExpr, Labels(0, falseLabel));
        emitCode(AssignCode(tmp, Constant.True)); // tmp = true
        emitCode(UncondJumpCode(trueLabel));      // goto trueLabel
        emitLabel(falseLabel);                     // falseLabel
        emitCode(AssignCode(tmp, Constant.False)); // tmp = false
        emitLabel(trueLabel);                      // trueLabel
        return tmp;
    }

    /**
     * This function returns the name of a variable, which value was fetch from exp
     * after the evaluation. In case when exp is not a constant nor a variable then
     * the class of the nodes decide which instruction will be generated.
     */
    private fun reduce(exp: Expr): Singleton {
        if (exp.isSingleton()) {
            return exp as Singleton;
        }
        val toBeAssigned: Temp = Temp(exp.type);
        emitCode(exp.codeForValueTo(toBeAssigned));
        return toBeAssigned;
    }

    override fun walkAccessNode(node: Access, arg: Labels?): Expr {
        val expr: Expr? = walk(node.index, arg);
        val acc: Access = Access(node.array, reduce(expr as Expr), node.type);
        if (node.type == Type.bool) {
            val condExpr: Expr = reduce(acc);
            emitGotos(condExpr, arg as Labels);
            return condExpr;
        }
        return acc;
    }

    override fun walkAndNode(node: And, arg: Labels?): Expr? {
        val trueLabel: Int = arg?.trueLabel() as Int;
        val falseLabel: Int = arg.falseLabel();
        val label: Int = if (falseLabel != 0) falseLabel else createNewLabel();
        walk(node.expr1, Labels(0, label));
        walk(node.expr2, Labels(trueLabel, falseLabel));
        if (falseLabel == 0) {
            emitLabel(label);
        }
        return null;
    }

    override fun walkArithNode(node: Arith, arg: Labels?): Expr {
        val expr1: Expr? = walk(node.expr1, arg);
        val expr2: Expr? = walk(node.expr2, arg);
        return Arith(node.op as Token, reduce(expr1 as Expr), reduce(expr2 as Expr));
    }

    override fun walkAssignElemNode(node: AssignElem, arg: Labels?): Expr? {
        val expr1: Expr;
        if (node.expr?.type == Type.bool) {
            expr1 = processBooleanExpr(node.expr as Expr);
        } else {
            expr1 = reduce(walk(node.expr, arg) as Expr);
        }
        val acc: Access = node.acc;
        val index: Expr = reduce(walk(acc.index, null) as Expr);
        emitCode(ArrayAssignCode(Access(acc.array, index), expr1 as Singleton));
        return null;
    }

    override fun walkAssignIdNode(node: AssignId, arg: Labels?): Expr? {
        val exprCode: Expr;
        if (node.expr.type  == Type.bool) {
            exprCode = processBooleanExpr(node.expr);
        } else {
            exprCode = walk(node.expr, arg) as Expr;
        }
        emitCode(exprCode.codeForValueTo(node.ident))
        return null;
    }

    override fun walkAssignStmtNode(node: AssignStmt, arg: Labels?): Expr? {
        walk(node.assign, arg);
        return null;
    }

    override fun walkBlockNode(node: Block, arg: Labels?): Expr? {
        walk(node.stmts, arg);
        return null;
    }

    override fun walkBreakNode(node: Break, arg: Labels?): Expr? {
        emitCode(UncondJumpCode(node.stmt?.next as Int));
        return null;
    }

    override fun walkConstantNode(node: Constant, arg: Labels?): Expr {
        if (node == Constant.True && arg?.trueLabel() != 0) {
            emitCode(UncondJumpCode(arg?.trueLabel() as Int));
        }
        if (node == Constant.False && arg?.falseLabel() != 0) {
            emitCode(UncondJumpCode(arg?.falseLabel() as Int));
        }
        return node;
    }

    override fun walkDoNode(node: Do, arg: Labels?): Expr? {
        val label: Int = createNewLabel();
        val beginLabel: Int = createNewLabel();
        node.next = arg?.nextLabel() as Int;
        emitLabel(beginLabel);
        walk(node.stmt, Labels(label));
        emitLabel(label);
        walk(node.expr, Labels(beginLabel, 0));
        return null;
    }

    override fun walkElseNode(node: Else, arg: Labels?): Expr? {
        val label: Int = createNewLabel();
        val next: Int = arg?.nextLabel() as Int;
        walk(node.expr, Labels(0, label));
        walk(node.stmt1, arg);
        emitCode(UncondJumpCode(next));
        emitLabel(label);
        walk(node.stmt2, arg);
        return null;
    }

    override fun walkEmptyStmtNode(node: EmptyStmt, arg: Labels?): Expr? {
        return null;
    }

    override fun walkForNode(node: For, arg: Labels?): Expr? {
        val label1: Int = createNewLabel();
        val label2: Int = createNewLabel();
        val next: Int = arg?.nextLabel() as Int;
        node.next = next;
        walk(node.initAssig, arg);
        emitLabel(label1);
        walk(node.expr, Labels(0, next));
        walk(node.stmt, Labels(label2));
        emitLabel(label2);
        walk(node.iterAssign, arg);
        emitCode(UncondJumpCode(label1));
        return null;
    }

    override fun walkIdNode(node: Id, arg: Labels?): Expr {
        if (node.type == Type.bool) {
            emitGotos(node, arg as Labels);
        }
        return node;
    }

    override fun walkIfNode(node: If, arg: Labels?): Expr? {
        val next: Int = arg?.nextLabel() as Int;
        walk(node.expr, Labels(0, next));
        walk(node.stmt, Labels(next));
        return null;
    }

    override fun walkNotNode(node: Not, arg: Labels?): Expr? {
        walk(node.expr1,
            Labels(arg?.falseLabel() as Int, arg.trueLabel())
        );
        return null;
    }

    override fun walkOrNode(node: Or, arg: Labels?): Expr? {
        val trueLabel: Int = arg?.trueLabel() as Int;
        val falseLabel: Int = arg.falseLabel();
        val label = if (trueLabel != 0) trueLabel else createNewLabel();
        walk(node.expr1, Labels(label, 0));
        walk(node.expr2, Labels(trueLabel, falseLabel));
        if (trueLabel == 0) {
            emitLabel(label);
        }
        return null;
    }

    override fun walkProgramNode(node: Program, arg: Labels?): Expr? {
        val end: Int = createNewLabel();
        walk(node.scope, Labels(end));
        emitLabel(end);
        // convert the jump label to code lines
        adjustLabels();
        return null;
    }

    override fun walkRelNode(node: Rel, arg: Labels?): Expr? {
        val expr1: Expr = reduce(walk(node.expr1, null) as Expr);
        val expr2: Expr = reduce(walk(node.expr2, null) as Expr);
        emitGotos(Rel(node.op as Token, expr1, expr2), arg as Labels);
        return null;
    }

    override fun walkSeqNode(node: Seq, arg: Labels?): Expr? {
        if (node.stmt1 == EmptyStmt.Null) {
            walk(node.stmt2, arg);
        } else if (node.stmt2 == EmptyStmt.Null) {
            walk(node.stmt1, arg);
        } else {
            val label: Int = createNewLabel();
            walk(node.stmt1, Labels(label));
            emitLabel(label);
            walk(node.stmt2, arg);
        }
        return null;
    }

    override fun walkUnaryNode(node: Unary, arg: Labels?): Expr {
        val expr: Expr = walk(node.expr, arg) as Expr;
        return Unary(node.op, reduce(expr));
    }

    override fun walkWhileNode(node: While, arg: Labels?): Expr? {
        val next: Int = arg?.nextLabel() as Int;
        val beginLabel: Int = createNewLabel();
        node.next = next;
        emitLabel(beginLabel);
        walk(node.expr, Labels(0, next));
        walk(node.stmt, Labels(beginLabel));
        emitCode(UncondJumpCode(beginLabel));
        return null;
    }

    override fun walkReturnNode(node: Return, arg: Labels?): Expr? {
        val expr: Expr = walk(node.bool, arg) as Expr;
        emitCode(ReturnCode(reduce(expr)));
        return null;
    }

    override fun walkFunctionNode(node: Function, arg: Labels?): Expr? {
        emitCode(FuncBeginCode(node.id));
        walk(node.block, arg);
        emitCode(FuncEndCode(node.id as Singleton));
        return null;
    }

    override fun walkScopeNode(node: Scope, arg: Labels?): Expr? {
        walk(node.stmts, arg);
        return null;
    }

    override fun walkFuncExprNode(node: FuncExpr, arg: Labels?): Expr? {
        val reducedParams: LinkedList<Expr> = LinkedList();
        for (param in node.params) {
            val reduced: Expr = reduce(walk(param, arg) as Expr);
            reducedParams.add(reduced);
        }
        for (reduced in reducedParams) {
            emitCode(ParamCallCode(node.id as Singleton, reduced));
        }
        val result: Expr = Result(node.id.type);
        emitCode(ParamCallCode(node.id as Singleton, result));
        var numParams: Int = node.params.size;
        numParams = if (node.id.type != Type.void) ++numParams else numParams;
        emitCode(FuncCallCode(node.id, numParams));
        return result;
    }

    override fun walkExprCallNode(node: ExprCall, arg: Labels?): Expr? {
        return walk(node.expr, arg);
    }
}