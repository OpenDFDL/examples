
//
// we must specify the scala version because it is used
// when determining the names of the daffodil jars, and
// since daffodil requires a scala runtime this specifies
// that version.
//

scalaVersion in ThisBuild := "2.11.7"

mainClass in (Compile,run) := Some("HelloWorld")

//
// Below, change 2.1.0 to the version of daffodil you want to use
// Notice double %% on the dependency for daffodil. Needed so that 
// daffodil matching the scalaVersion above is accessed. 


libraryDependencies in ThisBuild := Seq(
  "org.apache.daffodil" %% "daffodil-japi" % "2.1.0",
  "jaxen" % "jaxen" % "1.1.4"
)

// these lines arrange for a lib_managed/ subdirectory
// to be created that contains all the jars that are dependencies
// Note: sbt clean will delete this directory.
// This helps if you use the eclipse IDE. After an sbt test, the 
// lib_managed will be populated with everything needed and eclipse IDE will
// find this and setup its class path. 

retrieveManaged := true

exportJars in ThisBuild := true

// this line arranges for the lib_managed/ subdirectory
// to also contain all the source jars and javadoc jars
// for all the dependencies.
//
// this is optional however. Use sbt update-classifers
// to pull these sources and docs.
transitiveClassifiers := Seq("sources", "javadoc")




