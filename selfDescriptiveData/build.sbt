name := "dfdl-self-descriptive-data"

organization := "com.owlcyberdefense"

version := "0.2.0"

scalaVersion := "3.3.6"

Compile / run / mainClass := Some("com.owlcyberdefense.tscv.TypedCSV")

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-core" % daffodilVersion.value
)

enablePlugins(DaffodilPlugin)
