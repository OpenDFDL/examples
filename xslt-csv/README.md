xslt-csv - Transforming Data using XSLT that Calls Daffodil for DFDL Parsing

This example is an XSLT program that processes CSV. 

The XSLT program calls an external Java program, passing it the name of a DFDL file (csv.dfdl.xsd) and the name of a CSV file (csv.txt). 

The Java program calls Daffodil which produces XML-formatted CSV. The Java program returns the XML as a string. 

The XSLT program uses the parse-xml() function to convert the string to XML. It then transforms that data into a different piece of XML. 

The XSLT program uses XSLT version 3.0. You can use any version except 1.0 (the parse-xml() function is not present in XSLT 1.0). 

== Testing 

This was tested using Saxon-EE as the XSLT processor. saxonica.com sells Saxon-EE. An evaluation license is available which can be used to try this out. Put the saxon jars into the xslt-csv/lib directory. 

Obtain sbt (www.sbt.org)

    sbt test

Runs a simple test that queries a CSV file parsed using DFDL, and constructs XML with the result.

== Running from the Command Line

If you install java (version 8 or higher), and setup the classpath properly then a command line like this (for MS-Windows) will work

    java -classpath %CLASSPATH% net.sf.saxon.Transform Dummy.xml -xsl:processCSV.xsl 

== Q & A

Q: Do I need to use saxon? Will the Java built-in XSLT processor not work?
A: Any XSLT processor that supports XSLT 2.0 will work. Verify that the XSLT processor in Java is XSLT 2.0 capable. The way the extension function is defined and provided to the XSLT processor may be different from the way Saxon does it. 