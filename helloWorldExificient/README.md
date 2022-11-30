This is a Daffodil/DFDL hello-world program using the Exificient library to support EXI infosets.

It uses the EXI and SAX APIs to demonstrate how we can parse, XSLT transform, and unparse data without ever creating any large/expensive XML text.

You will need an internet connection to pull down the dependent libraries, including daffodil itself.

With those installed you can type 'sbt run' and it will download all dependencies as jar files, and then compile and run the HelloWorldExificient.java program.

If you would like the source and javadoc jars for the dependencies downloaded also, then type 'sbt updateClassifiers'.

The jar files are cached in the lib_managed/ directory (which is removed by 'sbt clean')
