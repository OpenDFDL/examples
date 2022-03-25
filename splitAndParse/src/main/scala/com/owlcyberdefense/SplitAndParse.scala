package com.owlcyberdefense

import org.apache.daffodil.sapi.io.InputSourceDataInputStream

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URL
import scala.xml.Node

/**
 * A utility class that takes two DFDL schemas. One is a lightweight splitter used to separate the
 * data into records or messages that are as yet, not parsed. The second schema is the detailed fine-grained
 * parser, which presumably has much more overhead.
 *
 * Call the init() method in advance to pre-compile the schemas before starting split-and-parse processing and
 * to obtain any compilation errors or warnings.
 *
 * The splitting is of course sequential, but the parsing occurs in parallel using Scala ".par" parallel collections.
 *
 * @param splitterSchemaURL URL for the lightweight splitting schema.
 * @param splitterRootName Root element name for splitting.
 * @param splitterRootNamespace Root element namespace for splitting root element, or null may be passed if unambiguous.
 * @param realSchemaFileURL URL for the heavier-weight parsing schema which will be run in parallel.
 * @param realRootName Root element name for parsing.
 * @param realRootNS Root element namespace for parsing root element, or null may be passed if unambiguous.
 */
final class SplitAndParse(
  splitterSchemaURL: URL,
  splitterRootName: String,
  splitterRootNamespace: String,
  realSchemaFileURL: URL, // expensive parser that we want to run in parallel.
  realRootName: String,
  realRootNS: String) {

  private def parseOne(ba: Array[Byte]): Processor.Result = {
    val bais = new ByteArrayInputStream(ba)
    val dis = new InputSourceDataInputStream(bais)
    val res = proc.parse(dis)
    res
  }

  def init() =
    splitter.init() ++ compilationWarnings

  private lazy val (proc, compilationWarnings) = SchemaCompiler.initDP(realSchemaFileURL, realRootName, realRootNS)

  private def parallelism = 16 // spins up this many threads (max)

  private val splitter = new Splitter(splitterSchemaURL, splitterRootName, splitterRootNamespace)

  /**
   * Creates an iterator of parsed scala.xml.Nodes that come from a parse of the data stream.
   *
   * The parse is carried out by first splitting the input stream into separate chunks using the
   * lightweight splitter schema, then the chunks are parsed, in parallel, using the full parsing
   * schema. If the splitting is only a small amount of work, and the parsing is substantial fine
   * grained parsing, then this parallelism can provide good speedup.
   *
   * Despite parallelism here, the returned iterator preserves the order of data from the input stream.
   * That is, this is all deterministic, and there should be no observable difference between this
   * parallel parsing and a simple sequential parse.
   *
   * Note that the returned XML Node here is
   *
   * @param inputStream
   * @return An iterator of the parsed nodes.
   */
  def dataIterator(inputStream: InputStream): Iterator[Node] = {

    val splitterIter = splitter.toIterator(inputStream)

    // Makes a bunch of parallel collections based on the parallelism member
    // which can be tuned above to larger/smaller.

    val players = splitterIter.sliding(parallelism, parallelism).map { _.par }

    val parsed = players.map { player => player.map { ba =>
      val r = parseOne(ba) // will happen in parallel
      r
    }}

    val res = parsed.flatMap { parsedPlayer => parsedPlayer.map { r =>
      val n = r.infoset
      n
    }}

    res
  }
}
