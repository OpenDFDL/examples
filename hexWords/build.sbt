name := "dfdl-hexWords"

organization := "com.owlcyberdefense"

version := "0.1.0"

scalaVersion := "3.3.6"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-core" % daffodilVersion.value
)

enablePlugins(DaffodilPlugin)
