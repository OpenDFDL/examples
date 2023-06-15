name := "dfdl-helloworld"

organization := "com.tresys"

version := "0.1.0"

scalaVersion := "2.12.15"

// People use this project to study what the dependencies actually are needed
// so having them put into lib_managed is helpful.
retrieveManaged := true
useCoursier := false // Workaround becauuse retrieveManaged doesn't work in some sbt versions.

Compile / run / mainClass := Some("HelloWorld")

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-japi" % "3.5.0",
  "jaxen" % "jaxen" % "1.2.0",
  "junit" % "junit" % "4.13.2" % "test",
  "com.github.sbt" % "junit-interface" % "0.13.2" % "test"

)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
