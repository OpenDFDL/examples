package com.owlcyberdefense

import org.apache.daffodil.sapi.io.InputSourceDataInputStream

import java.io.ByteArrayOutputStream
import scala.xml.Node
import org.apache.daffodil.sapi.infoset.ScalaXMLInfosetInputter
import org.apache.daffodil.sapi.infoset.ScalaXMLInfosetOutputter
import org.apache.daffodil.sapi.DataProcessor
import org.apache.daffodil.sapi.Diagnostic

import java.nio.channels.Channels

/**
 * Class shared by both splitter and parser parts of this splitAndParse utility.
 *
 * @param dp
 */
final class Processor(dp: DataProcessor) {

  /**
   * Called to pull messages from the data stream.
   *
   * @return a Result object containing the results of the parse including diagnostic information.
   */
  def parse(dis: InputSourceDataInputStream): Processor.Result = {
    val outputter = new ScalaXMLInfosetOutputter()
    val res = dp.parse(dis, outputter)
    val procErr = res.isProcessingError
    val validationErr = res.isValidationError
    val diags = res.getDiagnostics
    val bitPos1bAfterParse = res.location().bitPos1b()
    val doc = if (!procErr) {
      outputter.getResult
    } else {
      null
    }
    val r = new Processor.Result(doc, diags, procErr, validationErr, bitPos1bAfterParse)
    outputter.reset() // Best practice is to do this even though it is not needed since we're about to discard the object.
    r
  }

  def unparse(n: Node): Array[Byte] = {
    val inputter = new ScalaXMLInfosetInputter(n)
    val bbos = new ByteArrayOutputStream()
    val wbc = Channels.newChannel(bbos)
    val res = dp.unparse(inputter, wbc)
    assert(!res.isError())
    bbos.toByteArray
  }

}

/**
 * Streaming message parser class.
 *
 * Illustrates proper use of Daffodil APIs to stream data.
 */
object Processor {

  /**
   * Result object for parse calls. Just a tuple.
   */
  case class Result(infoset: Node, // document that is the current parse result, or null
    diags: Seq[Diagnostic], // diagnostics.
    isProcessingError: Boolean,
    isValidationError: Boolean,
    bitPos1b: Long)
}