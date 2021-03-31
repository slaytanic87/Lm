package org.lm.frontend.lexer

import org.lm.frontend.symbol.Type
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.*

class Lexer {

    companion object {
        var line: Int = 1;
    }

    private var stream: BufferedInputStream;
    private var words: Hashtable<String, Word> = Hashtable();
    var peekChar: Char = ' ';

    constructor(inputStream: InputStream) {
        stream = BufferedInputStream(inputStream);
        reserve(Word("if", Tag.IF.value));
        reserve(Word("else", Tag.ELSE.value));
        reserve(Word("while", Tag.WHILE.value));
        reserve(Word("do", Tag.DO.value));
        reserve(Word("break", Tag.BREAK.value));
        reserve(Word("program", Tag.PROG.value));
        reserve(Word("func", Tag.FUNC.value));
        reserve(Word("return", Tag.RETURN.value));
        reserve(Word.TRUE);
        reserve(Word.FALSE);
        reserve(Type.int);
        reserve(Type.long);
        reserve(Type.float);
        reserve(Type.double);
        reserve(Type.char);
        reserve(Type.bool);
        reserve(Type.void);
    }

    fun reserve(w: Word) {
        words.put(w.lexeme, w);
    }

    private fun readChar() {
        peekChar =  stream.read().toChar();
    }

    private fun readChar(char: Char): Boolean {
        readChar();
        if (char == peekChar) {
            resetCharPointer();
            return true;
        }
        return false;
    }

    private fun resetCharPointer() {
        peekChar = ' ';
    }

    private fun checkLineEnd() {
        if (peekChar =='\n') {
            line++;
        }
    }

    private fun scanNextSymbol(): Token? {
        while (true) {
            if (peekChar == ' ' || peekChar == '\t' || peekChar == '\r') {
                readChar();
                continue;
            }
            if (peekChar == '/') {
                if (!readChar('/')) {
                    if (peekChar == '*') {
                        do {
                            checkLineEnd();
                            readChar();
                            checkLineEnd();
                        } while (peekChar != '*' && !readChar('/'));
                    } else {
                        return Token('/'.toInt());
                    }
                } else {
                    do {
                        readChar();
                    } while (peekChar != '\n');
                }
            }
            if (peekChar == '\n') {
                line++;
                readChar();
            } else {
                break;
            }
        }
        return null;
    }

    private fun getReservedWord(expectedChar: Char, expectedWord: Word, alternativeChar: Char): Token {
        if (readChar(expectedChar)) {
            return expectedWord;
        } else {
            return Token(alternativeChar.toInt());
        }
    }

    private fun scanWord(): Token? {
       when (peekChar) {
           '&' -> {
               return getReservedWord('&', Word.and, '&');
           };
           '|' -> {
               return getReservedWord('|', Word.or,'|');
           };
           '=' -> {
               return getReservedWord('=', Word.eq, '=');
           };
           '!' -> {
               return getReservedWord('=', Word.ne,'!');
           };
           '<' -> {
               return getReservedWord('=', Word.le,'<');
           };
           '>' -> {
               return getReservedWord('=', Word.ge,'>');
           };
           else -> {
                return null;
           }
       }
   }
    private fun scanNumber(): Token? {
        if (!Character.isDigit(peekChar)) {
            return null;
        }

        var v: Int = 0;
        do {
            v = 10 * v + Character.digit(peekChar, 10);
            readChar();
        } while (Character.isDigit(peekChar));

        if (peekChar != '.') {
            return Num(v);
        }

        var x: Float = v.toFloat();
        var d: Int = 10;

        while (true) {
            readChar();
            if (!Character.isDigit(peekChar)) {
                break;
            }
            x += Character.digit(peekChar, 10) / d;
            d *= 10;
        }
        return Real(x);
    }

    private fun scanSymbol(): Token? {
        if (!Character.isLetter(peekChar)) {
            return null;
        }

        val buffStr: StringBuffer = StringBuffer();

        do {
            buffStr.append(peekChar);
            readChar();
        } while (Character.isLetterOrDigit(peekChar));

        val s: String = buffStr.toString();
        var w: Word? = words.get(s);

        if (w != null) {
            return w;
        }

        w = Word(s, Tag.ID.value);
        words.put(s, w);

        return w;
    }

    public fun scan(): Token {
        var token: Token? = scanNextSymbol();
        if (token != null) {
            return token;
        }

        token = scanWord();
        if (token != null) {
            return token;
        }

        token = scanNumber();
        if (token != null) {
            return token;
        }

        token = scanSymbol();
        if (token != null) {
            return token;
        }

        token = Token(peekChar.toInt());
        resetCharPointer();

        return token;
    }

}