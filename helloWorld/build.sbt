name := "dfdl-helloworld"

organization := "com.tresys"

version := "0.1.1"

// People use this project to study what the dependencies actually are needed
// so having them put into lib_managed is helpful.
retrieveManaged := true
useCoursier := false // Workaround becauuse retrieveManaged doesn't work in some sbt versions.

Compile / run / mainClass := Some("HelloWorld")

daffodilVersion := "3.11.0"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-japi" % daffodilVersion.value,
  "jaxen" % "jaxen" % "1.2.0",

)

enablePlugins(DaffodilPlugin)
