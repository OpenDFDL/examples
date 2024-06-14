import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import java.io.OutputStream;
import java.io.StringWriter;

public class XmlUtils {

    public static void writeDocumentToStream(Document doc, OutputStream outputStream) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(outputStream);
        transformer.transform(domSource, streamResult);
    }

    public static String documentToString(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult streamResult = new StreamResult(writer);

            transformer.transform(domSource, streamResult);

            return writer.toString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
