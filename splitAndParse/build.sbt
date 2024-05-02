name := "splitAndParse"

organization := "com.owlcyberdefense"

version := "0.0.1"

scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-sapi" % daffodilVersion.value,
  "com.ibm" % "dfdl-edifact" % "0.0.1-SNAPSHOT" % "test"
)

enablePlugins(DaffodilPlugin)
