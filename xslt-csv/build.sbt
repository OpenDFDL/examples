val root = (project in file("."))
  .settings(
    name := "dfdl-xslt-csv",

    organization := "com.example",

    version := "0.1.0",

    libraryDependencies ++= Seq(
      "org.apache.daffodil" %% "daffodil-core" % daffodilVersion.value,
      "xalan" % "xalan" % "2.7.3",
      "xalan" % "serializer" % "2.7.3",
      "commons-io" % "commons-io" % "2.16.1" % Test
    )
  )
  .daffodilProject()
