import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class TestXSLTCSV {

    @Test
    public void testXSLTTransformation() throws Exception {
        // Get the transformer
        Transformer transformer = getTransformer();

        DOMResult domResult = new DOMResult();
        transformer.transform(dummyInput(), domResult);
        Document actualOutputDocument = (Document) domResult.getNode();

        // XmlUtils.writeDocumentToStream(actualOutputDocument, System.out);

        // Load and parse the expected output XML directly into a DOM Document
        Document expectedOutputDocument = getExpectedDOM();

        String actual = XmlUtils.documentToString(actualOutputDocument).replaceAll("\\s", "");
        String expected = XmlUtils.documentToString(expectedOutputDocument).replaceAll("\\s", "");
        assertEquals(expected, actual);
    }

    private StreamSource dummyInput() {
        String xmlInput = "<dummy/>";
        return new StreamSource(new StringReader(xmlInput));
    }

    private Document getExpectedDOM() throws Exception {
        InputStream expectedStream = getClass().getClassLoader().getResourceAsStream("expected.xml");
        if (expectedStream == null) {
            throw new RuntimeException("Cannot find expected.xml on the classpath");
        }

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(expectedStream);
    }

    private Transformer getTransformer() throws Exception {
        InputStream xsltStream = getClass().getClassLoader().getResourceAsStream("processCSV.xsl");
        if (xsltStream == null) {
            throw new RuntimeException("Cannot find processCSV.xsl on the classpath");
        }

        TransformerFactory factory = TransformerFactory.newInstance();
        Source xsltSource = new StreamSource(xsltStream);
        return factory.newTransformer(xsltSource);
    }
}


//      assertTrue(standardOutput.contains("<data recordCount=\"8\">"));
//      assertTrue(standardOutput.contains("<chevy model=\"E350\" year=\"1997\"/><chevy model=\"Venture Extended Edition\" year=\"1999\"/><chevy model=\"Venture Extended Edition\" year=\"2000\"/>"));
//
