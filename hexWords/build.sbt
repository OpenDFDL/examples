name := "dfdl-hexWords"

organization := "com.owlcyberdefense"

version := "0.0.1"

scalaVersion := "2.12.15"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-sapi" % "3.2.1",
  "org.apache.daffodil" %% "daffodil-tdml-processor" % "3.2.1" % "test",
  "org.apache.daffodil" %% "daffodil-japi" % "3.2.1",
  "junit" % "junit" % "4.13.2" % "test",
  "com.github.sbt" % "junit-interface" % "0.13.2" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
