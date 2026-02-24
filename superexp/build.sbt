val root = (project in file("."))
  .settings(
    name := "dfdl-superexp",

    organization := "com.example",

    version := "0.2.0-SNAPSHOT",

    daffodilFlatLayout := true
  )
  .daffodilProject()
