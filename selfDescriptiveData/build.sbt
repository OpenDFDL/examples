lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.tresys",
      version      := "0.1.0",
      scalaVersion := "2.12.6",
    )),
    name := "self-descriptive-data",
    libraryDependencies := Seq(
      "org.apache.daffodil" %% "daffodil-japi" % "2.3.0",
    )
  )
