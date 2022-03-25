name := "splitAndParse"

organization := "com.owlcyberdefense"

version := "0.0.1"

scalaVersion := "2.12.15"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-sapi" % "3.3.0",
  "org.apache.daffodil" %% "daffodil-tdml-processor" % "3.3.0" % "test",
  "junit" % "junit" % "4.13.2" % "test",
  "com.github.sbt" % "junit-interface" % "0.13.2" % "test",
  "com.ibm" % "dfdl-edifact" % "0.0.1-SNAPSHOT" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
