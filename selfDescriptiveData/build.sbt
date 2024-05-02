name := "dfdl-self-descriptive-data"

organization := "com.tresys"

version := "0.1.0"

scalaVersion := "2.12.18"

Compile / run / mainClass := Some("com.tresys.tscv.TypedCSV")

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-japi" % daffodilVersion.value,
)

enablePlugins(DaffodilPlugin)
