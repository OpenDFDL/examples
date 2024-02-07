name := "dfdl-superexp"

organization := "com.example"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  "com.github.sbt" % "junit-interface" % "0.13.2" % "test",
  "junit" % "junit" % "4.13.2" % "test",
  "org.apache.daffodil" %% "daffodil-tdml-processor" % "3.6.0" % "test",
  "org.apache.logging.log4j" % "log4j-core" % "2.17.1" % "test",
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

crossPaths := false

// Use flat folder structure. This means *.java and *.scala files are in the
// same directory as resources files, and source/resource files are only
// differentiated by their file extension.
Compile / unmanagedSourceDirectories := Seq(baseDirectory.value / "src")
Compile / unmanagedResourceDirectories := Seq(baseDirectory.value / "src")
Compile / unmanagedSources / includeFilter := "*.java" | "*.scala"
Compile / unmanagedResources / excludeFilter := "*.java" | "*.scala"
Test / unmanagedSourceDirectories := Seq(baseDirectory.value / "test")
Test / unmanagedResourceDirectories := Seq(baseDirectory.value / "test")
Test / unmanagedSources / includeFilter := "*.java" | "*.scala"
Test / unmanagedResources / excludeFilter := "*.java" | "*.scala"
