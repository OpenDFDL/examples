package com.example.pairstransform

import org.junit.AfterClass
import org.junit.Test

import org.apache.daffodil.tdml.Runner

object TestPairstransform {
  lazy val runner = Runner("/com/example/pairstransform/", "TestPairstransform.tdml")

  @AfterClass def shutDown {
    runner.reset
  }
}

class TestPairstransform {

  import TestPairstransform._

  @Test def test_latLonLists01(): Unit = { runner.runOneTest("test_latlonLists01") }
  @Test def test_parseIntoPairs01(): Unit = { runner.runOneTest("test_parseIntoPairs01") }
  @Test def test_parseIntoNESW01(): Unit = { runner.runOneTest("test_parseIntoNESW01") }

  @Test def test_pairstransform_01p(): Unit = { runner.runOneTest("test_pairstransform_01p") }
  @Test def test_pairstransform_01u(): Unit = { runner.runOneTest("test_pairstransform_01u") }

}
