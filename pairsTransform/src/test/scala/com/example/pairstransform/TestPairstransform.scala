package com.example.pairstransform

import org.apache.daffodil.tdml.Runner
import org.junit.{AfterClass, Test}

object TestPairstransform {
  lazy val runner = Runner("/com/example/pairstransform/", "TestPairstransform.tdml")

  @AfterClass def shutDown = {
    runner.reset()
  }
}

class TestPairstransform {

  import TestPairstransform.*

  @Test def test_latLonLists01(): Unit = { runner.runOneTest("test_latlonLists01") }
  @Test def test_parseIntoPairs01(): Unit = { runner.runOneTest("test_parseIntoPairs01") }
  @Test def test_parseIntoNESW01(): Unit = { runner.runOneTest("test_parseIntoNESW01") }

  @Test def test_pairstransform_01p(): Unit = { runner.runOneTest("test_pairstransform_01p") }
  @Test def test_pairstransform_01u(): Unit = { runner.runOneTest("test_pairstransform_01u") }

}
