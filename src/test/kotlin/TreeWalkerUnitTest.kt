import org.junit.jupiter.api.Test
import org.lm.frontend.lexer.Lexer
import org.lm.frontend.parser.Parser
import org.lm.frontend.treewalker.CodeGenTreeWalker
import org.lm.frontend.treewalker.LineTreeWalker
import org.lm.frontend.treewalker.SemanticTreeWalker
import org.lm.frontend.treewalker.TransformTreeWalker
import java.io.InputStream
import kotlin.test.assertNotEquals

class TreeWalkerUnitTest {

    @Test
    fun `should create tree from LineTreeWalker without error`() {
        val inputStream: InputStream = this.javaClass.getResourceAsStream("functionalProg.lm");
        val lexer = Lexer(inputStream);
        val parser = Parser(lexer);
        val program = parser.program();
        val ltw: LineTreeWalker = LineTreeWalker(true);
        ltw.walk(program, "");
        ltw.finish();
    }

    @Test
    fun `should check semantic correctly`() {
        val inputStream: InputStream = this.javaClass.getResourceAsStream("functionalProg.lm");
        val lexer = Lexer(inputStream);
        val parser = Parser(lexer);
        val program = parser.program();
        val stw: SemanticTreeWalker = SemanticTreeWalker();
        stw.walk(program, Unit);
    }

    @Test
    fun `should transform correctly`() {
        val inputStream: InputStream = this.javaClass.getResourceAsStream("functionalProg.lm");
        val lexer = Lexer(inputStream);
        val parser = Parser(lexer);
        val program = parser.program();
        val stw: SemanticTreeWalker = SemanticTreeWalker();
        stw.walk(program, Unit);
        val ttw: TransformTreeWalker = TransformTreeWalker();
        ttw.walk(program, Unit);
        val ltw: LineTreeWalker = LineTreeWalker(false);
        ltw.walk(program, "");
    }

    @Test
    fun `should generate three address code`() {
        val inputStream: InputStream = this.javaClass.getResourceAsStream("functionalProg.lm");
        val lexer = Lexer(inputStream);
        val parser = Parser(lexer);
        val ltw: LineTreeWalker = LineTreeWalker(true);
        val stw: SemanticTreeWalker = SemanticTreeWalker();
        val ttw: TransformTreeWalker = TransformTreeWalker();
        val ctw: CodeGenTreeWalker = CodeGenTreeWalker();
        val program = parser.program();
        stw.walk(program, Unit);
        ttw.walk(program, Unit);
        ltw.walk(program, "");
        ltw.finish();
        ctw.walk(program, null);
        ctw.exportCode();
        assertNotEquals(0, ctw.code.size, "generated code is empty!");
    }
}