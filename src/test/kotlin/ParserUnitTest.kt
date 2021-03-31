import org.junit.jupiter.api.Test
import org.lm.frontend.lexer.Lexer
import org.lm.frontend.parser.SimpleParser
import org.lm.frontend.parser.Parser
import org.lm.frontend.syntaxtree.Program
import java.io.InputStream
import kotlin.test.assertNotNull

class ParserUnitTest {

    @Test
    fun `should parse correctly`() {
        val inputStream: InputStream = this.javaClass.getResourceAsStream("program.lm");
        val lexer = Lexer(inputStream);
        val parser = SimpleParser(lexer);
        parser.program();
    }

    @Test
    fun `should parse extended correctly`() {
        val inputStream: InputStream = this.javaClass.getResourceAsStream("functionalProg.lm");
        val lexer = Lexer(inputStream);
        val parser = Parser(lexer);
        var prog: Program = parser.program();
        assertNotNull(prog, "Could not parse program ");
    }

}