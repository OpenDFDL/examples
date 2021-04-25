name := "dfdl-hexWords"

organization := "com.owlcyberdefense"

version := "0.0.1"

scalaVersion := "2.12.11"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-tdml-processor" % "3.1.0-SNAPSHOT",
  "org.apache.daffodil" %% "daffodil-japi" % "3.1.0-SNAPSHOT",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "junit" % "junit" % "4.13.2" % "test",
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
