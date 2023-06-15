name := "dfdl-self-descriptive-data"

organization := "com.tresys"

version := "0.1.0"

scalaVersion := "2.12.15"

Compile / run / mainClass := Some("com.tresys.tscv.TypedCSV")

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-tdml-processor" % "3.5.0" % "test",
  "org.apache.daffodil" %% "daffodil-japi" % "3.5.0",
  "junit" % "junit" % "4.13.2" % "test",
  "com.github.sbt" % "junit-interface" % "0.13.2" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
