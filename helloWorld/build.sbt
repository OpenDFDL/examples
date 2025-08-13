name := "dfdl-helloworld"

organization := "com.owlcyberdefense"

version := "0.2.0"

scalaVersion := "3.3.6"
// People use this project to study what the dependencies actually are needed
// so having them put into lib_managed is helpful.
retrieveManaged := true
useCoursier := false // Workaround becauuse retrieveManaged doesn't work in some sbt versions.

Compile / run / mainClass := Some("HelloWorld")

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-core" % daffodilVersion.value,
  "jaxen" % "jaxen" % "1.2.0"
)

enablePlugins(DaffodilPlugin)
