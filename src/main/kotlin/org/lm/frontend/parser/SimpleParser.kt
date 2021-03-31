package org.lm.frontend.parser

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lm.frontend.lexer.Lexer
import org.lm.frontend.lexer.Tag
import org.lm.frontend.lexer.Token


public class SimpleParser {

    private var log: Logger = LogManager.getLogger(SimpleParser::class.java);
    private val lexer : Lexer;
    private lateinit var lookahead: Token;

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
            error("Syntax error: expected " + tag + " but was " + lookahead.tag);
        }
    }

    private fun error(errMsg: String) {
        throw Error("near line " + Lexer.line.toString() + ": " + errMsg);
    }

    private fun block() {
        match('{'.toInt());
        decls();
        stmts();
        match('}'.toInt());
    }

    private fun decls() {
        if (lookahead.tag != Tag.BASIC.value) {
            return;
        }
        decl();
        decls();
    }

    private fun decl() {
        type();
        ids();
        match(';'.toInt());
    }

    private fun ids() {
        match(Tag.ID.value)
        while (lookahead.tag == ','.toInt()) {
            move();
            match(Tag.ID.value);
        }
    }

    private fun type() {
        match(Tag.BASIC.value);
        if (lookahead.tag != '['.toInt()) {
            return;
        }
        dims();
    }

    private fun dims() {
        match('['.toInt());
        match(Tag.NUM.value);
        match(']'.toInt());
        if (lookahead.tag == '['.toInt()) {
            dims();
        }
    }

    private fun stmts() {
        if (lookahead.tag == '}'.toInt()) {
            return;
        }
        stmt();
        stmts();
    }

    private fun stmt() {
        when (lookahead.tag) {
            ';'.toInt() -> {
                move();
            };
            Tag.IF.value -> {
                match(Tag.IF.value);
                match('('.toInt());
                bool();
                match(')'.toInt());
                stmt();
                if (lookahead.tag == Tag.ELSE.value) {
                    move();
                    stmt();
                }
            };
            Tag.BREAK.value -> {
                match(Tag.BREAK.value);
                match(';'.toInt());
            };
            Tag.WHILE.value -> {
                match(Tag.WHILE.value);
                match('('.toInt());
                bool();
                match(')'.toInt());
                stmt();
            };
            Tag.DO.value -> {
                match(Tag.DO.value);
                stmt();
                match(Tag.WHILE.value);
                match('('.toInt());
                bool();
                match(')'.toInt());
                match(';'.toInt());
            };
            Tag.FOR.value -> {
                match(Tag.FOR.value);
                match('('.toInt());
                assign();
                match(';'.toInt());
                bool();
                match(';'.toInt());
                assign();
                match(')'.toInt());
                stmt();
            };
            '{'.toInt() -> {
                block();
            };
            else -> {
                assign();
                match(';'.toInt());
            }
        }
    }

    private fun assign() {
        match(Tag.ID.value);
        offset();
        match('='.toInt());
        bool();
    }

    private fun bool() {
        join();
        while (lookahead.tag == Tag.OR.value) {
            move();
            join();
        }
    }

    private fun join() {
        equality();
        while(lookahead.tag == Tag.AND.value) {
            move();
            equality();
        }
    }

    private fun equality() {
        rel();
        while (lookahead.tag == Tag.EQ.value || lookahead.tag == Tag.NE.value) {
            move();
            rel();
        }
    }

    private fun rel() {
        expr();
        when (lookahead.tag) {
            '<'.toInt(),
            '>'.toInt(),
            Tag.GE.value,
            Tag.LE.value -> {
                move();
                expr();
            };
        }
    }

    private fun expr() {
        term();
        while (lookahead.tag == '+'.toInt() || lookahead.tag == '-'.toInt()) {
            move();
            term();
        }
    }

    private fun term() {
        unary();
        while (lookahead.tag == '*'.toInt() || lookahead.tag == '/'.toInt()) {
            move();
            unary();
        }
    }

    private fun unary() {
        if (lookahead.tag == '-'.toInt()) {							// unary -> - unary
            move();
            unary();
            return;
        } else if (lookahead.tag == '!'.toInt()) {					// unary -> ! unary
            move();
            unary();
            return;
        } else {										// unary -> factor
            factor();
            return;
        }
    }

    private fun factor() {
        when (lookahead.tag) {
            '('.toInt() -> {
                move();
                bool();
                match(')'.toInt());
            };
            Tag.ID.value -> {
                move();
                offset();
            };
            Tag.NUM.value, Tag.REAL.value, Tag.BASIC.value, Tag.TRUE.value, Tag.FALSE.value -> {
                move();
            }
        }
    }

    private fun offset() {
        if (lookahead.tag != '['.toInt()) {
            return;
        }
        match('['.toInt());
        if (lookahead.tag == Tag.ID.value) {
            match(Tag.ID.value);
        } else {
            match(Tag.NUM.value);
        }
        match(']'.toInt());
        offset();
    }

    public fun program() {
        match(Tag.PROG.value);
        block();
        log.debug("Parsing successful completed!")
    }
}