package org.lm.frontend.parser

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lm.frontend.lexer.*
import org.lm.frontend.lexer.Array
import org.lm.frontend.symbol.Env
import org.lm.frontend.symbol.Type
import org.lm.frontend.syntaxtree.*
import org.lm.frontend.syntaxtree.Function
import kotlin.collections.ArrayList

class Parser {
    private var log: Logger = LogManager.getLogger(Parser::class.java);
    private val lexer : Lexer;
    private lateinit var lookahead: Token;
    var top: Env? = null;
    var usedAddrSpace: Int = 0;     // next free memoryaddress in memory

    constructor(lexer: Lexer) {
        this.lexer = lexer;
        move();
    }

    private fun move() {
        lookahead = lexer.scan();
        log.debug(lookahead);
    }

    private fun match(tag: Int) {
        if (tag == lookahead.tag) {
            move();
        } else {
            error("Syntax error expected " +tag+ " but was " +lookahead.tag);
        }
    }

    private fun error(errMsg: String) {
        throw Error("near line " + Lexer.line.toString() + ": " + errMsg);
    }

    public fun program(): Program {
        match(Tag.PROG.value);
        val scope: Scope = scope();
        return Program(scope);
    }

    private fun scope(): Scope {
        val savedUsed: Int = usedAddrSpace;
        match('{'.toInt());
        val savedEnv: Env? = top;
        top = Env(top);
        val s: Stmt = sdecls();
        match('}'.toInt());
        top = savedEnv;
        usedAddrSpace = savedUsed;
        return Scope(s);
    }

    private fun pblock(params: ArrayList<Id>): Block {
            val savedUsed: Int = usedAddrSpace;
            val savedEnv: Env? = top;
            top = Env(top);
            param(params);
            match('{'.toInt());
            decls();
            val s: Stmt = stmts();
            match('}'.toInt());
            top = savedEnv;
            usedAddrSpace = savedUsed;
            return Block(s);
    }

    private fun block(): Block {
        val savedUsed: Int = usedAddrSpace;
        match('{'.toInt());
        val savedEnv: Env? = top;
        top = Env(top);
        decls();
        val s: Stmt = stmts();
        match('}'.toInt());
        top = savedEnv;
        usedAddrSpace = savedUsed;
        return Block(s);
    }

    private fun param(params: ArrayList<Id>) {
        match('('.toInt());
        pdecls(params);
        match(')'.toInt());
    }

    private fun pdecls(params: ArrayList<Id>) {
        if (lookahead.tag == ')'.toInt()) {
            return;
        }
        var type: Type = type();
        var token: Token = lookahead;
        match(Tag.ID.value);
        var id: Id = Id(token as Word, type, usedAddrSpace);
        top?.put(token, id);
        params.add(id);
        usedAddrSpace += type.width;
        while (lookahead.tag == ','.toInt()) {
            move();
            type = type();
            token = lookahead;
            match(Tag.ID.value);
            id = Id(token as Word, type, usedAddrSpace);
            top?.put(token, id);
            params.add(id);
            usedAddrSpace += type.width;
        }
    }

    private fun function(returnType: Type): Stmt {
        match(Tag.FUNC.value);
        val token: Token = lookahead;
        match(Tag.ID.value);
        var id: Id? = top?.get(token);
        if (id != null) {
            error("identifier " + token + " for function already in use");
        }
        id = Id(token as Word, returnType, usedAddrSpace);
        id.isFuntion = true;
        usedAddrSpace += returnType.width;
        val block: Block = pblock(id.funcParams);
        top?.put(token, id);
        return Function(id, block);
    }

    private fun stmts(): Stmt {
        val stmtList: ArrayList<Stmt> = ArrayList();
        while (lookahead.tag != '}'.toInt()) {
            stmtList.add(stmt());
        }

        stmtList.add(EmptyStmt.Null);
        var stmt: Stmt = stmtList.get(stmtList.size - 1);
        for (i in stmtList.size - 2 downTo 0) {
            stmt = Seq(stmtList.get(i), stmt);
        }
        return stmt;
    }

    private fun stmt(): Stmt {
        val x: Expr?;
        val s1: Stmt;
        val s2: Stmt;
        val a1: Assignment;
        val a2: Assignment;
        when (lookahead.tag) {
            ';'.toInt() -> {
                move();
                return EmptyStmt.Null;
            };
            Tag.IF.value -> {
                move();
                match('('.toInt());
                x = bool();
                match(')'.toInt());
                s1 = stmt();
                if (lookahead.tag != Tag.ELSE.value) {
                    return If(x, s1);
                }
                match(Tag.ELSE.value);
                s2 = stmt();
                return Else(x, s1, s2);
            };
            Tag.BREAK.value -> {
                match(Tag.BREAK.value);
                match(';'.toInt());
                return Break();
            };
            Tag.WHILE.value -> {
                val whileNode: While = While();
                match(Tag.WHILE.value);
                match('('.toInt());
                x = bool();
                match(')'.toInt());
                s1 = stmt();
                whileNode.init(x, s1);
                return whileNode;
            };
            Tag.DO.value -> {
                val doNode: Do = Do();
                match(Tag.DO.value);
                s1 = stmt();
                match(Tag.WHILE.value);
                match('('.toInt());
                x = bool();
                match(')'.toInt());
                match(';'.toInt());
                doNode.init(x, s1);
                return doNode;
            };
            Tag.FOR.value -> {
                val forNode : For = For();
                match(Tag.FOR.value);
                match('('.toInt());
                a1 = assign();
                match(';'.toInt());
                x = bool();
                match(';'.toInt());
                a2 = assign();
                match(')'.toInt());
                s1 = stmt();
                forNode.init(a1, x, a2, s1);
                return forNode;
            };
            Tag.RETURN.value -> {
                match(Tag.RETURN.value);
                x = bool();
                match(';'.toInt())
                return Return(x);
            };
            '{'.toInt() ->{
                return block();
            };
            Tag.ID.value -> {
                val id: Id? = top?.get(lookahead);
                if (id == null) {
                    error(lookahead.toString() + " is undeclared");
                }
                if (id?.isFuntion == true) {
                    x = funcall();
                    match(';'.toInt());
                    return ExprCall(x);
                }
                a1 = assign();
                match(';'.toInt());
                return AssignStmt(a1);
            }
            else -> {
                error("unexpected statement " + lookahead);
                return EmptyStmt.Null;
            }
        }
    }

    private fun assign(): Assignment {
        val assignment: Assignment;
        val token: Token = lookahead;
        match(Tag.ID.value);
        val id: Id? = top?.get(token);
        if (id == null) {
            error(token.toString() + " is undeclared");
        }
        if (lookahead.tag == '='.toInt()) {
            move();
            assignment = AssignId(id as Id, bool());
        } else {
            val access: Access = offset(id);
            match('='.toInt());
            assignment = AssignElem(access, bool());
        }
        return assignment;
    }

    private fun bool(): Expr {
        var e: Expr = join();
        while (lookahead.tag == Tag.OR.value) {
            val token: Token = lookahead;
            move();
            e = Or(token, e, join());
        }
        return e;
    }

    private fun join(): Expr {
        var e: Expr = equality();
        while (lookahead.tag == Tag.AND.value) {
            val token: Token = lookahead;
            move();
            e = And(token, e, equality());
        }
        return e;
    }

    private fun equality(): Expr {
        var e: Expr = rel();
        while (lookahead.tag == Tag.EQ.value  || lookahead.tag == Tag.NE.value) {
            val token: Token = lookahead;
            move();
            e = Rel(token, e, rel());
        }
        return e;
    }

    private fun rel(): Expr {
        val e: Expr = expr();
        when (lookahead.tag) {
            '<'.toInt(), Tag.LE.value, Tag.GE.value, '>'.toInt() -> {
                val token: Token = lookahead;
                move();
                return Rel(token, e, expr());
            };
            else -> {
                return e;
            }
        }
    }

    private fun expr(): Expr {
        var e: Expr = term();
        while (lookahead.tag == '+'.toInt() || lookahead.tag == '-'.toInt()) {
            val token: Token = lookahead;
            move();
            val e2 = term();
            e = Arith(token, e, e2);
        }
        return e;
    }

    private fun term(): Expr {
        var e: Expr = unary();
        while (lookahead.tag == '*'.toInt() || lookahead.tag == '/'.toInt()) {
            val token: Token = lookahead;
            move();
            val e2 = unary();
            e = Arith(token, e, e2);
        }
        return e;
    }

    private fun unary(): Expr {
        val unaryList: ArrayList<Expr> = ArrayList();
        while (lookahead.tag == '-'.toInt() || lookahead.tag == '!'.toInt()) {
            if (lookahead.tag == '-'.toInt()) {
                move();
                unaryList.add(Unary(Word.minus, null));
            } else if (lookahead.tag == '!'.toInt()) {
                unaryList.add(Not(lookahead, null));
                move();
            } else {
                throw Error("unexpected token " + lookahead);
            }
        }
        unaryList.add(factor());
        var expr: Expr = unaryList.get(unaryList.size - 1);
        for (i in unaryList.size - 2 downTo 0) {
            val tmp: Expr = unaryList.get(i);
            if (tmp is Unary) {
                tmp.expr = expr;
                expr = tmp;
            } else {
                (tmp as Not).expr1 = expr;
                tmp.expr2 = expr;
                expr = tmp;
            }
        }
        return expr;
    }

    private fun factor(): Expr {
        var e:Expr? = null;
        when (lookahead.tag) {
            '('.toInt() -> {
                move();
                e = bool();
                match(')'.toInt());
                return e;
            };
            Tag.NUM.value -> {
                e = Constant(lookahead, Type.int);
                move();
                return e;
            };
            Tag.REAL.value -> {
                e = Constant(lookahead, Type.float);
                move();
                return e;
            };
            Tag.TRUE.value -> {
                e = Constant.True;
                move();
                return e;
            };
            Tag.FALSE.value -> {
                e = Constant.False;
                move();
                return e;
            };
            Tag.ID.value -> {
                val id: Id? = top?.get(lookahead);
                if (id == null) {
                    error(lookahead.toString() + " is undeclared");
                }
                if ((id as Id).isFuntion == true) {
                    return funcall();
                }
                move();
                if (lookahead.tag != '['.toInt()) {
                    return id;
                } else {
                    return offset(id);
                }
            };
            else -> {
                error("syntax error");
                return e as Id;
            }
        }
    }

    private fun paramcall(): ArrayList<Expr> {
        val paramList: ArrayList<Expr> = ArrayList();
        if (lookahead.tag == ')'.toInt()) {
            return paramList;
        }
        var expr:Expr? = bool();
        paramList.add(expr as Expr);
        while (lookahead.tag == ','.toInt()) {
            move();
            expr = bool();
            paramList.add(expr);
        }
        return paramList;
    }

    private fun funcall(): FuncExpr {
        val token: Token = lookahead;
        match(Tag.ID.value);
        val id: Id? = top?.get(token);
        if (id == null) {
            error("undeclared function: " + token);
        }
        match('('.toInt());
        val params: ArrayList<Expr>  = paramcall();
        match(')'.toInt());
        return FuncExpr(id as Id, params);
    }

    private fun offset(a: Id?): Access {
        var varId: Expr?;
        match('['.toInt());
        varId = matchDim();
        match(']'.toInt());
        var acc: Access = Access(a, varId);
        while (lookahead.tag == '['.toInt()) {
            match('['.toInt());
            varId = matchDim();
            match(']'.toInt());
            acc = Access(acc, varId);
        }
        return acc;
    }

    private fun matchDim(): Expr? {
        val varDim: Expr?
        if (Tag.ID.value == lookahead.tag) {
            varDim = top?.get(lookahead);
            if (varDim == null) {
                error(lookahead.toString() + " is undeclared");
            }
            match(Tag.ID.value);
        } else {
            varDim = Constant((lookahead as Num).value, Type.int);
            match(Tag.NUM.value);
        }
        return varDim;
    }

    private fun decls() {
        while (lookahead.tag == Tag.BASIC.value) {
            val type = type();
            decl(type);
        }
    }

    private fun sdecls(): Stmt {
        val functionsList: ArrayList<Stmt> = ArrayList();
        while (lookahead.tag == Tag.BASIC.value) {
            val type:Type = type();
            if (lookahead.tag == Tag.FUNC.value) {
                functionsList.add(function(type));
                continue;
            }
            decl(type);
        }
        if (functionsList.isEmpty()) {
            return EmptyStmt.Null;
        }
        var seq: Seq = Seq(functionsList.get(functionsList.size - 1), EmptyStmt.Null);
        for (i in functionsList.size - 2 downTo 0) {
            seq = Seq(functionsList.get(i), seq);
        }
        return seq;
    }

    private fun decl(type: Type) {
        ids(type);
        match(';'.toInt());
    }

    private fun ids(type: Type) {
        var token: Token = lookahead;
        match(Tag.ID.value)
        checkIdentifierInScope(token, type)
        var id: Id = Id(token as Word, type, usedAddrSpace);
        top?.put(token, id);
        log.debug("in table "+id.op.toString() + "(" +id.type+")" + "\t rel.Addr: " +id.offset);
        usedAddrSpace += type.width;
        while (lookahead.tag == ','.toInt()) {
            move();
            token = lookahead;
            match(Tag.ID.value);
            checkIdentifierInScope(token, type)
            id = Id(token as Word, type, usedAddrSpace);
            top?.put(token, id);
            log.debug("in table "+id.op.toString() + "(" +id.type+")" + "\t rel.Addr: " +id.offset);
            usedAddrSpace += type.width;
        }
    }

    private fun checkIdentifierInScope(token: Token, type: Type) {
        if (top?.contains(token) == true) {
            error("identifier " + token + " for type " + type + "already declared");
        }
    }

    private fun type(): Type {
        val type: Type = lookahead as Type;
        match(Tag.BASIC.value);
        if (lookahead.tag != '['.toInt()) {
            return type;
        }
        return dims(type);
    }

    private fun dims(type: Type): Type {
        var p: Type = type;
        match('['.toInt());
        var token: Token = lookahead;
        match(Tag.NUM.value);
        match(']'.toInt());
        while (lookahead.tag == '['.toInt()) {
            match('['.toInt());
            token = lookahead;
            match(Tag.NUM.value);
            match(']'.toInt());
            p = Array((token as Num).value, p);
        }
        return Array((token as Num).value, p);
    }

}