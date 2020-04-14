lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.tresys",
      version      := "0.1.0",
      scalaVersion := "2.12.11",
    )),
    name := "HelloWorld",
    libraryDependencies := Seq(
      "org.apache.daffodil" %% "daffodil-japi" % "2.5.0",
      "jaxen" % "jaxen" % "1.1.4"
    )
  )
