
import org.junit.Test
import org.junit.AfterClass
import org.apache.daffodil.tdml.Runner

object TestHexWords {
  lazy val runner = Runner("/", "hexWords.tdml")

  @AfterClass def shutdown: Unit = { runner.reset }

}

class TestHexWords {
  import TestHexWords._

  @Test def test_hexWords1() { runner.runOneTest("hexWords1") }
  @Test def test_hexWords2() { runner.runOneTest("hexWords2") }
  @Test def test_hexWords3() { runner.runOneTest("hexWords3") }
  @Test def test_hexWords4() { runner.runOneTest("hexWords4") }

}
