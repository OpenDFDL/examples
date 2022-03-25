package com.owlcyberdefense


import org.apache.daffodil.util.Misc
import org.apache.daffodil.xml.XMLUtils
import org.junit.Assert._
import org.junit.Test

import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.Objects
import scala.xml.Node
import scala.xml.XML


class TestSplitter () {
  var schemaFileURL = Objects.requireNonNull(Misc.getRequiredResource("int32Prefix.dfdl.xsd").toURL)


  /**
   * Use Daffodil compare utility to compare XML results.
   *
   * Causes a junit assertion failure if the comparison finds any differences.
   *
   * @param expectedXML as a string.
   * @param doc
   * @throws IOException
   */
  @throws[IOException]
  private def compare(expectedXML: String, doc: Node): Unit = { // throws an exception if there are any differences
    XMLUtils.compareAndReport(XML.loadString(expectedXML).asInstanceOf[Node], doc)
  }

  @Test
  @throws[IOException]
  def testSplitter1(): Unit = {
    val is = new ByteArrayInputStream(Misc.hex2Bytes("00 00 00 09 31 32 33 34 35".replace(" ", "")))
    var mp = new Splitter(schemaFileURL, "message", null).toResultIterator(is)
    var r = mp.next()
    assertFalse(r.isProcessingError)
    assertFalse(r.isValidationError)
    compare("<try><message><len>9</len><content>3132333435</content></message></try>", r.infoset)
    assertFalse(mp.hasNext)
  }

  @Test
  @throws[IOException]
  def testSplitter2(): Unit = {
    val is = new ByteArrayInputStream(Misc.hex2Bytes("00 00 00 09 31 32 33 34 35".replace(" ", "")))
    var mp = new Splitter(schemaFileURL, "message", null)
    val iter = mp.toResultIterator(is)
    val Seq(r) = iter.toStream.toList
    assertFalse(r.isProcessingError)
    assertFalse(r.isValidationError)
    compare("<try><message><len>9</len><content>3132333435</content></message></try>", r.infoset)
  }

  @Test
  @throws[IOException]
  def testSplitter3(): Unit = {
    val is = new ByteArrayInputStream(
      Misc.hex2Bytes("00 00 00 09 31 32 33 34 35 00 00 00 09 31 32 33 34 35".replace(" ", "")))
    var mp = new Splitter(schemaFileURL, "message", null)
    val iter = mp.toResultIterator(is)
    val Seq(r1, r2) = iter.toStream.toList
    assertFalse(r1.isProcessingError)
    assertFalse(r1.isValidationError)
    compare("<try><message><len>9</len><content>3132333435</content></message></try>", r1.infoset)
    assertFalse(r2.isProcessingError)
    assertFalse(r2.isValidationError)
    compare("<try><message><len>9</len><content>3132333435</content></message></try>", r2.infoset)
  }

  @Test
  @throws[IOException]
  def testSplitterBad1(): Unit = {
    val is = new ByteArrayInputStream(
      Misc.hex2Bytes("00".replace(" ", "")))
    var mp = new Splitter(schemaFileURL, "message", null)
    val iter = mp.toResultIterator(is)
    val list = iter.toList
    println(list)
    val hd :: tail = list
    assertFalse(hd.isProcessingError)
    assertTrue(hd.isValidationError)
  }

  @Test
  @throws[IOException]
  def testSplitterBad2(): Unit = {
    val is = new ByteArrayInputStream(
      Misc.hex2Bytes("00 00".replace(" ", "")))
    var mp = new Splitter(schemaFileURL, "message", null)
    val iter = mp.toResultIterator(is)
    val list = iter.toList
    println(list)
    val List(r1, r2) = list
    assertFalse(r1.isProcessingError)
    assertTrue(r1.isValidationError)
    assertFalse(r2.isProcessingError)
    assertTrue(r2.isValidationError)
  }

  @Test
  @throws[IOException]
  def testSplitter100x(): Unit = {
    val is = Misc.getRequiredResource("ORDERS_D.03B_Interchange.net.100x").toURL.openStream()
    val splitterSchemaFileURL = Misc.getRequiredResource("int32Prefix.dfdl.xsd").toURL
    var s = new Splitter(splitterSchemaFileURL, "try", null)
    val iter = s.toIterator(is)
    val list = iter.toList
    assertEquals(100, list.length)
  }
}
