package com.example.superexp

import org.junit.AfterClass
import org.junit.Test

import org.apache.daffodil.tdml.Runner

object TestSuperexp {
  lazy val runner = Runner("/", "TestSuperexp.tdml")

  @AfterClass def shutDown: Unit = {
    runner.reset
  }
}

class TestSuperexp {

  import TestSuperexp._

  @Test def test_superexp_1(): Unit = { runner.runOneTest("test_superexp_1") }

  @Test def test_superexp_2(): Unit = { runner.runOneTest("test_superexp_2") }

  @Test def test_superexp_3(): Unit = { runner.runOneTest("test_superexp_3") }

  @Test def test_superexp_4(): Unit = { runner.runOneTest("test_superexp_4") }

  @Test def test_superexp_5(): Unit = { runner.runOneTest("test_superexp_5") }

  @Test def test_superexp_6(): Unit = { runner.runOneTest("test_superexp_6") }

  // Gets out of memory in java heap even when run with sbt -J-Xmx48g test
  // @Test def test_superexp_7(): Unit = { runner.runOneTest("test_superexp_7") }

}
