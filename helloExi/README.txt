This is a Daffodil/DFDL example program using Agile Delta's EXI implementation.

The build.sbt file is a build script for the sbt - simple build tool.

You will need version 0.13.x or higher.

You will also need Java 1.8 (aka Java 8) or higher JDK.

You will need an internet connection to pull down the dependent libraries, including daffodil itself.

With those installed you can type 'sbt run' and it will download all dependencies as jar files, and then compile and run the HelloWorld.java program.

You will need either an evaluation or full version of the Agile Delta Efficient XML SDK, with all of it's jar files placed in the "lib/" directory.
NOTE: The standard evaluation package from Agile Delta only includes command line tools and does not include access to the SDK. You must ask for the SDK.

If you would like the source and javadoc jars for the dependencies downloaded also, then type 'sbt updateClassifiers'.

The jar files are cached in the lib_managed/ directory (which is removed by 'sbt clean')
