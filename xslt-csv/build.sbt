name := "dfdl-xslt-csv"

organization := "com.example"

version := "0.0.2"

scalaVersion := "2.12.19"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-japi" % daffodilVersion.value,
  "xalan" % "xalan" % "2.7.3",
  "xalan" % "serializer" % "2.7.3",
  "commons-io" % "commons-io" % "2.16.1" % Test,
)

enablePlugins(DaffodilPlugin)
