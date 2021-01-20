import org.apache.daffodil.japi.*;
import org.apache.daffodil.japi.Compiler;
import org.apache.daffodil.japi.infoset.JDOMInfosetOutputter;
import org.apache.daffodil.japi.io.InputSourceDataInputStream;
import org.jdom2.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Streaming message parser class.
 *
 * Illustrates proper use of Daffodil APIs to stream data.
 */
public class MessageParser {

    static class CompileFailure extends Exception {
        List<Diagnostic> diags;

        CompileFailure(List<Diagnostic> diagnostics) {
            super("DFDL Schema Compile Failure");
            diags = diagnostics;
        }
    }

    List<Diagnostic> compilationWarnings;

    /**
     * Result object for parse calls. Just a 4-tuple.
     */
    static class Result {
         Document message; // JDOM document that is the current parse result, or null
         List<Diagnostic> diags; // diagnostics.
         boolean isProcessingError;
         boolean isValidationError;
         boolean isEOD; // if true, no more data is available

        Result(Document doc, List<Diagnostic> diagnostics, boolean isProcErr, boolean isValidationErr, boolean isAtEnd) {
            message = doc;
            diags = diagnostics;
            isProcessingError = isProcErr;
            isValidationError = isValidationErr;
            isEOD = isAtEnd;
        }
    }
    private InputSourceDataInputStream dis;
    private JDOMInfosetOutputter outputter = new JDOMInfosetOutputter();
    private DataProcessor dp;

    MessageParser(URL schemaFileURL, boolean validate) throws IOException, URISyntaxException, CompileFailure {
        //
        // First compile the DFDL Schema
        //
        Compiler c = Daffodil.compiler();
        ProcessorFactory pf = c.compileSource(schemaFileURL.toURI())
                .withDistinguishedRootNode("word", null);

        List<Diagnostic> pfDiags = pf.getDiagnostics();
        if (pf.isError()) {
            throw new CompileFailure(pfDiags);
        }
        dp = pf.onPath("/");
        if (validate) {
            try {
                dp = dp.withValidationMode(ValidationMode.Limited);
            } catch (InvalidUsageException e) {
                // impossible
                throw new Error(e);
            }
        }
        List<Diagnostic> dpDiags = dp.getDiagnostics();
        if (dp.isError()) {
            throw new CompileFailure(dpDiags);
        }
        if (!pfDiags.isEmpty()) {
            compilationWarnings = pfDiags;
            compilationWarnings.addAll(dpDiags); // dpDiags might be empty. That's ok.
        } else {
            compilationWarnings = dpDiags; // dpDiags might be empty. That's ok.
        }
    }

    void setInputStream(InputStream inputStream) {
        dis = new InputSourceDataInputStream(inputStream);
    }

    /**
     * Called to pull messages from the data stream.
     * @return a Result object containing the results of the parse including diagnostic information.
     */
    public Result parse() {
        if (dis == null)
            throw new IllegalStateException("Input stream must be provided by setInputStream() call.");

        ParseResult res = dp.parse(dis, outputter);

        boolean procErr = res.isProcessingError();
        boolean validationErr = res.isValidationError();
        List<Diagnostic> diags = res.getDiagnostics();

        boolean atEnd = res.location().isAtEnd();

        Document doc = null;
        if (!procErr)
            doc = outputter.getResult();
        Result r = new Result(doc, diags, procErr, validationErr, atEnd);
        outputter.reset();
        return r;
    }

}
