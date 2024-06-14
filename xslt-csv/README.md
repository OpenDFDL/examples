# xslt-csv - Transforming Data using XSLT that Calls Daffodil for DFDL Parsing

This example is an XSLT program that processes CSV data. 

The XSLT program calls an external Java program, passing it the name of a DFDL file (csv.dfdl.xsd) and the name of a CSV file (csv.txt). 

The Java program calls Daffodil which produces XML from the CSV. The Java program returns the XML as a w3c.dom.Document. 

The XSLT then transforms that data into a different piece of XML. 

The XSLT program uses XSLT version 1.0 (built into Java).

## Testing

Obtain sbt (www.sbt.org)

    sbt test

Runs a simple test that queries a CSV file parsed using DFDL, and constructs XML with the result.

## Running from the Command Line

If you install java (version 8 or higher), and setup the classpath properly then a command line like this (for MS-Windows) will work

    java -classpath %CLASSPATH% net.sf.saxon.Transform Dummy.xml -xsl:processCSV.xsl 

## Q & A

Q: Do I need to use saxon? Will the Java built-in XSLT processor not work?
A: An earlier version of this used saxon. This version uses the XSLT built in to Java. Saxon EE provides a more powerful
XSLT transformer and implements version 2.0 (and beyond) of the XSLT specification, so if you really want to use this 
idea, you should convert to using Saxon EE. 

## Thanks/Credits

This idea of calling Daffodil directly from XSLT and the original version of this example come from Roger Costello of MITRE Corp.
