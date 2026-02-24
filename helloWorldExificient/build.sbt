val root = (project in file("."))
  .settings(
    name := "dfdl-helloworldexificient",

    organization := "com.owlcyberdefense",

    version := "0.2.0",

    // People use this project to study what the dependencies actually are needed
    // so having them put into lib_managed is helpful.
    retrieveManaged := true,

    Compile / run / mainClass := Some("HelloWorldExificient"),

    libraryDependencies ++= Seq(
      "org.apache.daffodil" %% "daffodil-core" % daffodilVersion.value,
      "jaxen" % "jaxen" % "1.2.0",
      "com.siemens.ct.exi" % "exificient" % "1.0.4"
    )
  )
  .daffodilProject()
