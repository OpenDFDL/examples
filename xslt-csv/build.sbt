name := "dfdl-xslt-csv"

organization := "com.example"

version := "0.0.1"

scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-japi" % daffodilVersion.value,
  "net.sf.saxon" % "Saxon-HE" % "10.6",
)

enablePlugins(DaffodilPlugin)
