This is a Daffodil/DFDL hello-world program.

The build.sbt file is a build script for the sbt - simple build tool.

You will need version 0.13.x or higher.

You will also need Java 1.8 (aka Java 8) or higher JDK.

You will need an internet connection to pull down the dependent libraries, including daffodil itself.

With those installed you can type 'sbt run' and it will download all dependencies as jar files, and then compile and run the HelloWorld.java program.

If you would like the source and javadoc jars for the dependencies downloaded also, then type 'sbt updateClassifiers'.

The jar files are cached in the lib_managed/ directory (which is removed by 'sbt clean')
