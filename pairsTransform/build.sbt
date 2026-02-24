val root = (project in file("."))
  .settings(
    name := "dfdl-pairs-transform",

    organization := "com.example",

    version := "0.2.0-SNAPSHOT"
  )
  .daffodilProject()
