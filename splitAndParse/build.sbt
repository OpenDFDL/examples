val root = (project in file("."))
  .settings(
    name := "splitAndParse",

    organization := "com.owlcyberdefense",

    version := "0.1.0",

    libraryDependencies ++= Seq(
      "org.apache.daffodil" %% "daffodil-core" % daffodilVersion.value,
      "com.ibm" % "dfdl-edifact" % "0.0.1-SNAPSHOT" % "test",
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
    )
  )
  .daffodilProject()
