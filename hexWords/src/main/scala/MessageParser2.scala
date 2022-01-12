
import org.apache.daffodil.sapi.infoset.ScalaXMLInfosetOutputter
import org.apache.daffodil.sapi.Daffodil
import org.apache.daffodil.sapi.DataProcessor
import org.apache.daffodil.sapi.Diagnostic
import org.apache.daffodil.sapi.ValidationMode
import org.apache.daffodil.sapi.io.InputSourceDataInputStream

import java.io.InputStream
import java.net.URL
import scala.xml.Node


/**
 * Streaming message parser class.
 *
 * Illustrates proper use of Daffodil APIs to stream data.
 */
object MessageParser2 {
  case class CompileFailure (diags: Seq[Diagnostic]) extends Exception("DFDL Schema Compile Failure")

  /**
   * Result object for parse calls. Just a 4-tuple.
   */
  case class Result (message: Node, // document that is the current parse result, or null
    diags: Seq[Diagnostic],  // diagnostics.
    isProcessingError: Boolean,
    isValidationError: Boolean)
}

class MessageParser2(val schemaFileURL: URL, val rootName: String, val rootNS: String) {
  assert(schemaFileURL ne null)

  val outputter = new ScalaXMLInfosetOutputter()
  var dis: InputSourceDataInputStream = _
  var dp: DataProcessor = _
  //
  // First compile the DFDL Schema
  val c = Daffodil.compiler
  val pf = c.compileSource(schemaFileURL.toURI).withDistinguishedRootNode(rootName, rootNS)
  val pfDiags = pf.getDiagnostics
  if (pf.isError) throw new MessageParser2.CompileFailure(pfDiags)
  dp = pf.onPath("/").withValidationMode(ValidationMode.Limited)
  val dpDiags = dp.getDiagnostics
  if (dp.isError) throw new MessageParser2.CompileFailure(dpDiags)
  var compilationWarnings = if (!pfDiags.isEmpty) pfDiags else dpDiags // dpDiags might be empty. That's ok.

  def setInputStream(inputStream: InputStream): Unit = {
    dis = new InputSourceDataInputStream(inputStream)
  }

  // def hasMoreData = dis.hasData

  /**
   * Called to pull messages from the data stream.
   *
   * @return a Result object containing the results of the parse including diagnostic information.
   */
  def parse = {
    if (dis == null) throw new IllegalStateException("Input stream must be provided by setInputStream() call.")
    val res = dp.parse(dis, outputter)
    val procErr = res.isProcessingError
    val validationErr = res.isValidationError
    val diags = res.getDiagnostics
    val doc = if (!procErr) outputter.getResult else null
    val r = new MessageParser2.Result(doc, diags, procErr, validationErr)
    outputter.reset()
    r
  }
}
