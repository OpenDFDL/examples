import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

import org.apache.daffodil.japi.Compiler;
import org.apache.daffodil.japi.Daffodil;
import org.apache.daffodil.japi.DataProcessor;
import org.apache.daffodil.japi.Diagnostic;
import org.apache.daffodil.japi.ParseResult;
import org.apache.daffodil.japi.ProcessorFactory;
import org.apache.daffodil.japi.infoset.JDOMInfosetOutputter;
import org.apache.daffodil.japi.io.InputSourceDataInputStream;

public class runDaffodil {

    public static String dfdlParse(String dfdl, String input) throws IOException, URISyntaxException {

        URL dfdlURL = runDaffodil.class.getResource(dfdl);
        URL inputURL = runDaffodil.class.getResource(input);

        //
        // First, compile the DFDL Schema
        //
        Compiler c = Daffodil.compiler();
        ProcessorFactory pf = c.compileSource(dfdlURL.toURI());
        DataProcessor dp = pf.onPath("/");
       
        //
        // Parse - parse data to XML
        //
        java.io.InputStream is = inputURL.openStream();
        InputSourceDataInputStream dis = new InputSourceDataInputStream(is);
       
        //
        // Setup JDOM outputter
        //
        JDOMInfosetOutputter outputter = new JDOMInfosetOutputter();

        //
        // Do the parse
        //
        ParseResult res = dp.parse(dis, outputter);
       
        //
        // Return the XML as a string
        //
        Document doc = outputter.getResult();
        return new XMLOutputter().outputString(doc);
    }
}
