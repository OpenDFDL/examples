name := "dfdl-xslt-csv"

organization := "com.example"

version := "0.0.3"

daffodilVersion := "3.11.0"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-japi" % daffodilVersion.value,
  "xalan" % "xalan" % "2.7.3",
  "xalan" % "serializer" % "2.7.3",
  "commons-io" % "commons-io" % "2.16.1" % Test
)

enablePlugins(DaffodilPlugin)
