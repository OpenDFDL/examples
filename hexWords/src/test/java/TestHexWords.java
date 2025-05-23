import hexWords.MessageParser;
import hexWords.MessageParser.Result;
import org.apache.daffodil.tdml.Runner;
import org.apache.daffodil.lib.util.Misc;
import org.apache.daffodil.lib.xml.XMLUtils;
import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;
import org.junit.Test;
import static org.junit.Assert.*;

import scala.xml.Node;
import scala.xml.XML;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class TestHexWords {
    URL schemaFileURL;
    MessageParser mp;
    XMLOutputter xout;

    static Runner runner = new Runner("/", "hexWords.tdml");

    public TestHexWords() throws MessageParser.CompileFailure, IOException, URISyntaxException {
        schemaFileURL = Objects.requireNonNull(Misc.getRequiredResource("hexWords.dfdl.xsd").toURL());
        mp = new MessageParser(schemaFileURL, "word", null);
        xout = new XMLOutputter();
    }

    @Test
    public void test_hexWords1() {
        runner.runOneTest("hexWords1");
    }

    @Test
    public void test_hexWords2() {
        runner.runOneTest("hexWords2");
    }

    @Test
    public void test_hexWords3() {
        runner.runOneTest("hexWords3");
    }

    @Test
    public void test_hexWords4() {
        runner.runOneTest("hexWords4");
    }

    /**
     * Convert to a scala.xml.Node from a JDOM Document.
     * @param doc
     * @return a scala.xml.Node
     * @throws IOException
     */
    private Node toNode(Document doc) throws IOException {
        StringWriter sw = new StringWriter();
        xout.output(doc, sw);
        Node root = (Node) scala.xml.XML.loadString(sw.toString());
        return root;
    }

    /**
     * Use Daffodil compare utility to compare XML results.
     *
     * Causes a junit assertion failure if the comparison finds any differences.
     * @param expectedXML as a string.
     * @param doc
     * @throws IOException
     */
    private void compare(String expectedXML, Document doc) throws IOException {
        // throws an exception if there are any differences
        XMLUtils.compareAndReport(
            (Node) XML.loadString(expectedXML),
            toNode(doc),
            true, // ignore proc instructions
            false, // do not check prefixes
            false, // do not check namespaces
            scala.Option.apply(null), // no float epsilon
            scala.Option.apply(null) // no double epsilon
        );
    }

    @Test public void testMessageParser1() throws IOException {
        //
        // our example data stream is just these bytes.
        // In a real system this would open a file or socket or other
        // source of an InputStream.
        //
        ByteArrayInputStream is = new ByteArrayInputStream(Misc.
                hex2Bytes("A4 BB 5A AB CD 40 CA CD".replace(" ", "")));
        mp.setInputStream(is);
        Result r = mp.parse();
        assertFalse(r.isProcessingError);
        assertFalse(r.isValidationError);
        compare("<word><len>4</len><text>ABBA</text></word>",r.message);
        r = mp.parse();
        assertFalse(r.isProcessingError);
        assertFalse(r.isValidationError);
        compare("<word><len>5</len><text>BADC0</text></word>", r.message);
        r = mp.parse();
        assertFalse(r.isProcessingError);
        assertFalse(r.isValidationError);
        compare("<word><len>4</len><text>ACDC</text></word>", r.message);
        assertFalse(mp.hasMoreData());
    }

    @Test public void testMessageParser2() throws IOException {
        //
        // our example data stream is just these bytes.
        // In a real system this would open a file or socket or other
        // source of an InputStream.
        //
        ByteArrayInputStream is = new ByteArrayInputStream(Misc.
                hex2Bytes("A4 BB 0A 50 AB CD 40 CA CD 05".replace(" ", "")));
        mp.setInputStream(is);
        Result r = mp.parse();
        assertFalse(r.isProcessingError);
        assertFalse(r.isValidationError);
        compare("<word><len>4</len><text>ABBA</text></word>",r.message);
        r = mp.parse();
        assertFalse(r.isProcessingError);
        assertTrue(r.isValidationError);
        compare("<word><illegal>0</illegal></word>", r.message);
        assertTrue(r.diags.stream().map(d -> d.toString() ).anyMatch( m -> m.contains("Validation Error") && m.contains("illegal")));
        r = mp.parse();
        assertFalse(r.isProcessingError);
        assertTrue(r.isValidationError);
        compare("<word><illegal>0</illegal></word>", r.message);
        assertTrue(r.diags.stream().map(d -> d.toString() ).anyMatch( m -> m.contains("Validation Error") && m.contains("illegal")));
        r = mp.parse();
        assertFalse(r.isProcessingError);
        assertFalse(r.isValidationError);
        compare("<word><len>5</len><text>BADC0</text></word>", r.message);
        r = mp.parse();
        assertFalse(r.isProcessingError);
        assertFalse(r.isValidationError);
        compare("<word><len>4</len><text>ACDC</text></word>", r.message);

        r = mp.parse();
        assertTrue(r.isProcessingError);
        assertTrue(r.diags.stream().anyMatch(d -> {
            String m = d.getMessage().toLowerCase();
            return m.contains("Parse Error".toLowerCase()) &&
                    m.contains("Insufficient bits in data".toLowerCase()) &&
                    m.contains("needed 20 bit".toLowerCase());
        }));

    }
}
