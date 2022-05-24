package com.owlcyberdefense

import org.apache.daffodil.sapi.Diagnostic
import org.apache.daffodil.sapi.io.InputSourceDataInputStream

import java.io.InputStream
import java.net.URL

/**
 * Splits data into byte arrays based on a splitter schema which is intended
 * to be a schema that contains only what is needed to carve a message off of
 * a stream of messages.
 *
 * Call the init() method in order to pre-compile the schema before first use.
 *
 * @param schemaFileURL URL of the splitter DFDL schema.
 * @param rootName Name of the root element.
 * @param rootNS Namespace of the root element, or can pass null if unambiguous.
 */
class Splitter(val schemaFileURL: URL, val rootName: String, val rootNS: String) {

  assert(schemaFileURL ne null)

  private lazy val (proc, compilationWarnings: Seq[Diagnostic]) = SchemaCompiler.initDP(schemaFileURL, rootName, rootNS)

  def init(): Seq[Diagnostic] = compilationWarnings

  private def split(dis: InputSourceDataInputStream) =
    proc.parse(dis)

  /**
   * Private but exposed as package private for testing purposes only.
   */
  private [owlcyberdefense] def toResultIterator(inputStream: InputStream): Iterator[Processor.Result] = {
    val dis = new InputSourceDataInputStream(inputStream)

    Stream.continually( split(dis) ).takeWhile{
      r =>
        val keepGoing = {
          if (r.isProcessingError && !dis.hasData)
            false // normal termination - we got error at end of data
          else {
            assert(!r.isProcessingError) // should only get proc-error at end of data
            true
          }
        }
        keepGoing
    }.toIterator
  }

  /**
   * Primary method for obtaining series of split results which are byte arrays.
   *
   * @param inputStream
   * @return Iterator of byte arrays. Each from one split of the input stream.
   */
  def toIterator(inputStream: InputStream): Iterator[Array[Byte]] = {
    //
    // This is fairly inefficient in that it unparses the data in order to obtain the byte array.
    //
    // There are possibly more efficient ways to do this, for example parsing the input just to obtain
    // the length, then pulling the necessary bytes directly from the input stream.
    //
    // Note however, that this splitter is intended to support methods of isolating a message from
    // a stream other than stored lengths. E.g., one example is textual data surrounded by markers like
    // the ascii start-of-message (SOM) and end-of-message (EOM) characters.
    //
    toResultIterator(inputStream).filter(r => !r.isValidationError).map{ r => proc.unparse(r.infoset) }
  }
}
