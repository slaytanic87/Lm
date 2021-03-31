package org.lm.frontend.lexer

open class Word: Token {

    public var lexeme: String = "";

    constructor(s: String, tag: Int): super(tag) {
        lexeme = s;
    }

    companion object {
        val and: Word = Word("&&", Tag.AND.value);
        val or: Word = Word("||", Tag.OR.value);
        val eq: Word = Word("==", Tag.EQ.value);
        val ne: Word = Word("!=", Tag.NE.value);
        val le: Word = Word("<=", Tag.LE.value);
        val ls: Word = Word("<", '<'.toInt());
        val gr: Word = Word(">", '>'.toInt());
        val ge: Word = Word(">=", Tag.GE.value);
        val minus: Word = Word("-", Tag.MINUS.value);
        val TRUE: Word = Word("true", Tag.TRUE.value);
        val FALSE: Word = Word("false", Tag.FALSE.value);
        val toInt: Word = Word("toInt", Tag.TOI.value);
        val toFloat: Word = Word("toFloat", Tag.TOF.value);
        val tmp: Word = Word("tmp", Tag.TEMP.value);
        val result: Word = Word("result", Tag.RESULT.value);
    }

    override fun toString(): String {
        return lexeme
    }
}