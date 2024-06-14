import org.apache.daffodil.japi.Compiler;
import org.apache.daffodil.japi.Daffodil;
import org.apache.daffodil.japi.DataProcessor;
import org.apache.daffodil.japi.Diagnostic;
import org.apache.daffodil.japi.ParseResult;
import org.apache.daffodil.japi.ProcessorFactory;
import org.apache.daffodil.japi.infoset.W3CDOMInfosetOutputter;
import org.apache.daffodil.japi.io.InputSourceDataInputStream;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.* ;

public class runDaffodil {

    public static Document dfdlParse(String dfdl, String input) throws IOException, URISyntaxException {

        URL dfdlURL = requireNonNull(runDaffodil.class.getResource(dfdl));
        URL inputURL = requireNonNull(runDaffodil.class.getResource(input));

        //
        // First, compile the DFDL Schema
        //
        // It would be good to do this once and save it, or to load a pre-compiled binary
        // of the DFDL schema, so that one could call this function
        // repeatedly without having to recompile the DFDL schema each time.
        //
        // The CSV schema is small, but some DFDL schemas are large and compilation takes some time.
        //
        Compiler c = Daffodil.compiler();
        ProcessorFactory pf = c.compileSource(dfdlURL.toURI());
        if (pf.isError()) {
            List<Diagnostic> diags = pf.getDiagnostics();
            diags.forEach(System.out::println);
            throw new RuntimeException("Processor Factory Compilation failed");
        }
        DataProcessor dp = pf.onPath("/");
        if (dp.isError()) throw new RuntimeException("Data Processor Creation failed");
        //
        // Parse - parse data to XML
        //
        java.io.InputStream is = inputURL.openStream();
        InputSourceDataInputStream dis = new InputSourceDataInputStream(is);
       
        //
        // Setup infoset outputter
        //
        W3CDOMInfosetOutputter outputter = new W3CDOMInfosetOutputter();

        //
        // Do the parse
        //
        ParseResult res = dp.parse(dis, outputter);

        if (res.isProcessingError()) {
            Stream<String> strings = res.getDiagnostics().stream().map(Diagnostic::getMessage);
            String allDiags = strings.collect(Collectors.joining("\n"));
            throw new RuntimeException(allDiags);
        }
        //
        // Return the XML as a w3c DOM Document
        //
        Document doc = outputter.getResult();
        return doc;
    }


}
