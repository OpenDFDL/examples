
//
// we must specify the scala version because it is used
// when determining the names of the daffodil jars, and
// since daffodil requires a scala runtime this specifies
// that version.
//

scalaVersion in ThisBuild := "2.11.7"

mainClass in (Compile,run) := Some("HelloWorld")
//
// This is where daffodil jars are resolved
//
resolvers in ThisBuild += "NCSA Sonatype Releases" at "https://opensource.ncsa.illinois.edu/nexus/content/repositories/releases"

//
// Below, change 2.0.0-rc3 to the version of daffodil you want to use
//

libraryDependencies in ThisBuild := Seq(
  "edu.illinois.ncsa" %% "daffodil-japi" % "2.0.0",
  "jaxen" % "jaxen" % "1.1.4"
)

// these lines arrange for a lib_managed/ subdirectory
// to be created that contains all the jars that are dependencies
// Note: sbt clean will delete this directory.
retrieveManaged := true

exportJars in ThisBuild := true

// this line arranges for the lib_managed/ subdirectory
// to also contain all the source jars and javadoc jars
// for all the dependencies.
//
// this is optional however. Use sbt update-classifers
// to pull these sources and docs.
transitiveClassifiers := Seq("sources", "javadoc")




