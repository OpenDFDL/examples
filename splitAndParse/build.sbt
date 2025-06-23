name := "splitAndParse"

organization := "com.owlcyberdefense"

version := "0.0.1"

libraryDependencies ++= Seq(
  "org.apache.daffodil" %% "daffodil-sapi" % daffodilVersion.value,
  "com.ibm" % "dfdl-edifact" % "0.0.1-SNAPSHOT" % "test",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
)

enablePlugins(DaffodilPlugin)
