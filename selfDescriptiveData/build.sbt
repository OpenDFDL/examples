name := "dfdl-self-descriptive-data"

organization := "com.tresys"

version := "0.1.1"

Compile / run / mainClass := Some("com.tresys.tscv.TypedCSV")

daffodilVersion := "3.11.0"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-japi" % daffodilVersion.value
)

enablePlugins(DaffodilPlugin)
