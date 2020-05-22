name := "dfdl-self-descriptive-data"

organization := "com.tresys"

version := "0.1.0"

scalaVersion := "2.12.11"

libraryDependencies := Seq(
  "org.apache.daffodil" %% "daffodil-japi" % "2.6.0",
  "com.novocode" % "junit-interface" % "0.11" % "test",
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
