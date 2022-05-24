package com.owlcyberdefense

import org.apache.daffodil.util.Misc
import org.apache.daffodil.xml.XMLUtils
import org.junit.Assert.assertEquals
import org.junit.Test

import java.io.IOException
import java.io.SequenceInputStream


class TestSplitEdifact () {
  var splitterSchemaFileURL = Misc.getRequiredResource("int32Prefix.dfdl.xsd").toURL
  val realSchemaFileURL = Misc.getRequiredResource("edifactWithLength.dfdl.xsd").toURL

  @Test
  @throws[IOException]
  def testEDIFACT_OneMessageOnly(): Unit = {
    val oneEdifactRecord = Misc.getRequiredResource("ORDERS_D.03B_Interchange.net").toURL
    val oneEdifactRecordXML = Misc.getRequiredResource("ORDERS_D.03B_Interchange.net.xml").toURL

    val sp = new SplitAndParse(splitterSchemaFileURL, "try", null, realSchemaFileURL, "edifact", null)
    println("Starting compilation")
    sp.init()
    println("Ending compilation")
    val mp = sp.dataIterator(oneEdifactRecord.openStream())
    val Seq(n) = mp.toList
    val xml = scala.xml.XML.load(oneEdifactRecordXML)
    XMLUtils.compareAndReport(xml, n)
  }

  @Test
  @throws[IOException]
  def testEDIFACT_StreamOf2Messages(): Unit = {
    val edifactRecords = Misc.getRequiredResource("ORDERS_D.03B_Interchange.net.2x").toURL
    val oneEdifactRecordXML = Misc.getRequiredResource("ORDERS_D.03B_Interchange.net.xml").toURL

    val sp = new SplitAndParse(splitterSchemaFileURL, "try", null, realSchemaFileURL, "edifact", null)
    println("Starting compilation")
    sp.init()
    println("Ending compilation")
    val mp = sp.dataIterator(edifactRecords.openStream())
    val n1 = mp.next()
    val xml = scala.xml.XML.load(oneEdifactRecordXML)
     XMLUtils.compareAndReport(xml, n1)
    val n2 = mp.next()
    XMLUtils.compareAndReport(xml, n2)
  }

  @Test
  @throws[IOException]
  def testEDIFACT_Stream10x100Messages_ParseInParallel(): Unit = {
    import collection.JavaConverters._

    val bulkFactor = 10 // increase for longer running test where you can really see the parallelism.
    val streams = (1 to bulkFactor).toIterator map { _ =>
      Misc.getRequiredResource("ORDERS_D.03B_Interchange.net.100x")
      .toURL.openStream()
    }
    val edifactRecords = new SequenceInputStream(streams.asJavaEnumeration)
    val oneEdifactRecordXML = Misc.getRequiredResource("ORDERS_D.03B_Interchange.net.xml").toURL

    val sp = new SplitAndParse(splitterSchemaFileURL, "try", null, realSchemaFileURL, "edifact", null)
    println("Starting compilation")
    sp.init()
    println("Ending compilation")
    val mp = sp.dataIterator(edifactRecords)
    val xml = scala.xml.XML.load(oneEdifactRecordXML)
    val all = mp.toList.par // parallel collection
    assertEquals(100 * bulkFactor, all.length)
    all.foreach { n1 =>
      XMLUtils.compareAndReport(xml, n1)
    }
  }
}
