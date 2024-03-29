name := "dfdl-xslt-csv"

organization := "com.example"

version := "0.0.1"

scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-japi" % "3.5.0",
  "net.sf.saxon" % "Saxon-HE" % "10.6",
  "junit" % "junit" % "4.13.2" % "test",
  "com.github.sbt" % "junit-interface" % "0.13.2" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
