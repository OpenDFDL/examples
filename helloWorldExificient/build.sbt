name := "dfdl-helloworldexificient"

organization := "com.owlcyberdefense"

version := "0.1.0"

scalaVersion := "2.12.17"

// People use this project to study what the dependencies actually are needed
// so having them put into lib_managed is helpful.
retrieveManaged := true

Compile / run / mainClass := Some("HelloWorldExificient")

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-japi" % "3.4.0",
  "jaxen" % "jaxen" % "1.2.0",
  "junit" % "junit" % "4.13.2" % "test",
  "com.github.sbt" % "junit-interface" % "0.13.2" % "test",
  "com.siemens.ct.exi" % "exificient" % "1.0.4"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
