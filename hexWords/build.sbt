name := "dfdl-hexWords"

organization := "com.owlcyberdefense"

version := "0.0.1"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-sapi" % daffodilVersion.value,
  "org.apache.daffodil" %% "daffodil-japi" % daffodilVersion.value,
)

enablePlugins(DaffodilPlugin)
