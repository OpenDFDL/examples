<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="http://xml.apache.org/xalan/java"
                exclude-result-prefixes="java">

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <xsl:variable name="csv-xml" select="java:runDaffodil.dfdlParse('csv.dfdl.xsd', 'csv.txt')" />
    <!-- Process the XML-formatted CSV here -->
    <xsl:variable name="numRecords" select="count($csv-xml//record)"/>
    <xsl:element name="data">
      <!-- Output the number of records in the CSV file -->
      <xsl:attribute name="recordCount">
        <xsl:value-of select="$numRecords"/>
      </xsl:attribute>
      <!-- Output the model (field 3) and year (field 1) of each Chevy auto -->
      <xsl:for-each select=" $csv-xml//record[field[2] = 'Chevy']" >
        <xsl:element name="chevy">
          <xsl:attribute name="model"><xsl:value-of select="field[3]"/></xsl:attribute>
          <xsl:attribute name="year"><xsl:value-of select="field[1]"/></xsl:attribute>
        </xsl:element>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
