name := "dfdl-helloworld"

organization := "com.tresys"

version := "0.1.0"

scalaVersion := "2.12.11"

// People use this project to study what the dependencies actually are needed
// so having them put into lib_managed is helpful.
retrieveManaged := true
useCoursier := false // Workaround becauuse retrieveManaged doesn't work in some sbt versions.

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-japi" % "3.1.0",
  "jaxen" % "jaxen" % "1.1.4",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "junit" % "junit" % "4.12" % "test",
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
