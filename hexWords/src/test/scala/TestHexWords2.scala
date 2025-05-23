
import org.apache.daffodil.lib.util.Misc
import org.apache.daffodil.lib.xml.XMLUtils
import org.junit.Test
import org.junit.Assert._

import scala.xml.Node
import scala.xml.XML
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.Objects


class TestHexWords2 () {
  var schemaFileURL = Objects.requireNonNull(Misc.getRequiredResource("hexWords.dfdl.xsd").toURL)


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
  def testMessageParser1(): Unit = { //
    // our example data stream is just these bytes.
    // In a real system this would open a file or socket or other
    // source of an InputStream.
    val is = new ByteArrayInputStream(Misc.hex2Bytes("A4 BB 5A AB CD 40 CA CD".replace(" ", "")))
    var mp = new MessageParser2(is, schemaFileURL, "word", null)
    var r = mp.parse
    assertFalse(r.isProcessingError)
    assertFalse(r.isValidationError)
    compare("<word><len>4</len><text>ABBA</text></word>", r.message)
    r = mp.parse
    assertFalse(r.isProcessingError)
    assertFalse(r.isValidationError)
    compare("<word><len>5</len><text>BADC0</text></word>", r.message)
    r = mp.parse
    assertFalse(r.isProcessingError)
    assertFalse(r.isValidationError)
    compare("<word><len>4</len><text>ACDC</text></word>", r.message)
    r = mp.parse
    assertTrue(r.isProcessingError) // end of data
  }

  @Test
  @throws[IOException]
  def testMessageParser2(): Unit = {
    val is = new ByteArrayInputStream(Misc.hex2Bytes("A4 BB 0A 50 AB CD 40 CA CD 05".replace(" ", "")))
    var mp = new MessageParser2(is, schemaFileURL, "word", null)
    var r = mp.parse
    assertFalse(r.isProcessingError)
    assertFalse(r.isValidationError)
    compare("<word><len>4</len><text>ABBA</text></word>", r.message)
    r = mp.parse
    assertFalse(r.isProcessingError)
    assertTrue(r.isValidationError)
    compare("<word><illegal>0</illegal></word>", r.message)
    assertTrue(r.diags.map((d) => d.toString).filter((m) => m.contains("Validation Error") && m.contains("illegal")).nonEmpty)
    r = mp.parse
    assertFalse(r.isProcessingError)
    assertTrue(r.isValidationError)
    compare("<word><illegal>0</illegal></word>", r.message)
    assertTrue(r.diags.map((d) => d.toString).filter((m) => m.contains("Validation Error") && m.contains("illegal")).nonEmpty)
    r = mp.parse
    assertFalse(r.isProcessingError)
    assertFalse(r.isValidationError)
    compare("<word><len>5</len><text>BADC0</text></word>", r.message)
    r = mp.parse
    assertFalse(r.isProcessingError)
    assertFalse(r.isValidationError)
    compare("<word><len>4</len><text>ACDC</text></word>", r.message)
    r = mp.parse
    assertTrue(r.isProcessingError)
    assertTrue(r.diags.map( d => {
      val m = d.getMessage().toLowerCase
      m.contains("Parse Error".toLowerCase) && m.contains("Insufficient bits in data".toLowerCase) && m.contains("needed 20 bit".toLowerCase)
    }).nonEmpty)

  }
}
