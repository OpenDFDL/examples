val root = (project in file("."))
  .settings(
    name := "dfdl-hexWords",

    organization := "com.owlcyberdefense",

    version := "0.1.0",

    libraryDependencies ++= Seq(
      "org.apache.daffodil" %% "daffodil-core" % daffodilVersion.value
    )
  )
  .daffodilProject()
