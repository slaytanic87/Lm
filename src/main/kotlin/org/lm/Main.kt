package org.lm

import org.lm.frontend.treewalker.CodeGenTreeWalker
import org.lm.frontend.lexer.Lexer
import org.lm.frontend.parser.Parser
import org.lm.frontend.syntaxtree.Program
import org.lm.frontend.treewalker.SemanticTreeWalker
import org.lm.frontend.treewalker.TransformTreeWalker
import java.io.ByteArrayInputStream
import java.io.InputStream

fun main(args: Array<String>) {

    val inputString: String = "program {\n" +
                                         "int i; \n" +
                                         "int func test() {\n" +
                                             "i = 3;\n" +
                                         "}\n" +
                                      "}";
    val inputStream: InputStream = ByteArrayInputStream(inputString.toByteArray(Charsets.UTF_8));
    val lexer = Lexer(inputStream);
    val semanticTreeWalker: SemanticTreeWalker= SemanticTreeWalker();
    val transformTreeWalker: TransformTreeWalker = TransformTreeWalker();
    val threeAddrCodeGen: CodeGenTreeWalker =
        CodeGenTreeWalker();
    val parser = Parser(lexer);
    val program: Program = parser.program();
    semanticTreeWalker.walk(program, Unit);
    transformTreeWalker.walk(program, Unit);
    threeAddrCodeGen.walk(program, null);
}