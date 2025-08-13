package hexWords;

import org.apache.daffodil.api.Compiler;
import org.apache.daffodil.api.Daffodil;
import org.apache.daffodil.api.DataProcessor;
import org.apache.daffodil.api.Diagnostic;
import org.apache.daffodil.api.InputSourceDataInputStream;
import org.apache.daffodil.api.ParseResult;
import org.apache.daffodil.api.ProcessorFactory;
import org.apache.daffodil.api.exceptions.InvalidUsageException;
import org.apache.daffodil.api.infoset.JDOMInfosetOutputter;
import org.apache.daffodil.api.validation.ValidatorInitializationException;
import org.apache.daffodil.api.validation.ValidatorNotRegisteredException;
import org.jdom2.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

/**
 * Streaming message parser class.
 * <p>
 * Illustrates proper use of Daffodil APIs to stream data.
 */
public class MessageParser {

  List<Diagnostic> compilationWarnings;
  private InputSourceDataInputStream dis;
  private JDOMInfosetOutputter outputter = Daffodil.newJDOMInfosetOutputter();
  private DataProcessor dp;
  public MessageParser(URL schemaFileURL, String rootName, String rootNS) throws IOException, URISyntaxException, CompileFailure {
    Objects.requireNonNull(schemaFileURL);
    //
    // First compile the DFDL Schema
    //
    Compiler c = Daffodil.compiler();
    ProcessorFactory pf = c.compileSource(schemaFileURL.toURI())
      .withDistinguishedRootNode(rootName, rootNS);

    List<Diagnostic> pfDiags = pf.getDiagnostics();
    if (pf.isError()) {
      throw new CompileFailure(pfDiags);
    }
    dp = pf.onPath("/");
    try {
      dp = dp.withValidation("daffodil");
    } catch (InvalidUsageException | ValidatorNotRegisteredException | ValidatorInitializationException e) {
      // impossible
      throw new Error(e);
    }
    List<Diagnostic> dpDiags = dp.getDiagnostics();
    if (dp.isError()) {
      throw new CompileFailure(dpDiags);
    }

    // dpDiags contains warnings from both pf an dp
    compilationWarnings = dpDiags;
  }

  public void setInputStream(InputStream inputStream) {
    dis = Daffodil.newInputSourceDataInputStream(inputStream);
  }

  public boolean hasMoreData() {
    return dis.hasData();
  }

  /**
   * Called to pull messages from the data stream.
   *
   * @return a Result object containing the results of the parse including diagnostic information.
   */
  public Result parse() {
    if (dis == null)
      throw new IllegalStateException("Input stream must be provided by setInputStream() call.");

    ParseResult res = dp.parse(dis, outputter);

    boolean procErr = res.isProcessingError();
    boolean validationErr = res.isValidationError();
    List<Diagnostic> diags = res.getDiagnostics();

    Document doc = null;
    if (!procErr)
      doc = outputter.getResult();
    Result r = new Result(doc, diags, procErr, validationErr);
    outputter.reset();
    return r;
  }

  public static class CompileFailure extends Exception {
    List<Diagnostic> diags;

    CompileFailure(List<Diagnostic> diagnostics) {
      super("DFDL Schema Compile Failure");
      diags = diagnostics;
    }
  }

  /**
   * Result object for parse calls. Just a 4-tuple.
   */
  public static class Result {
    public Document message; // JDOM document that is the current parse result, or null
    public List<Diagnostic> diags; // diagnostics.
    public boolean isProcessingError;
    public boolean isValidationError;

    Result(Document doc, List<Diagnostic> diagnostics, boolean isProcErr, boolean isValidationErr) {
      message = doc;
      diags = diagnostics;
      isProcessingError = isProcErr;
      isValidationError = isValidationErr;
    }
  }

}
