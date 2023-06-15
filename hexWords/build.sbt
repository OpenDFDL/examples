name := "dfdl-hexWords"

organization := "com.owlcyberdefense"

version := "0.0.1"

scalaVersion := "2.12.15"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-sapi" % "3.5.0",
  "org.apache.daffodil" %% "daffodil-tdml-processor" % "3.5.0" % "test",
  "org.apache.daffodil" %% "daffodil-japi" % "3.5.0",
  "junit" % "junit" % "4.13.2" % "test",
  "com.github.sbt" % "junit-interface" % "0.13.2" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
