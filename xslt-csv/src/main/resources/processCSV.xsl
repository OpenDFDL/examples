<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:dfdl="java:runDaffodil"
    version="3.0">
   
    <xsl:variable name="dfdl" select="'csv.dfdl.xsd'"/>
    <xsl:variable name="input" select="'csv.txt'"/>
   
    <xsl:template match="/">
        <!-- Convert the CSV text file to XML using DFDL -->
        <xsl:variable name="csv-string" select="dfdl:dfdlParse($dfdl, $input)" as="xs:string"/>
        <xsl:variable name="csv-xml" select="parse-xml($csv-string)" as="document-node()"/>
        <!-- Process the XML-formatted CSV here -->
        <xsl:variable name="numRecords" select="count($csv-xml//record)" as="xs:integer"/>
        <xsl:element name="data">
        <!-- Output the number of records in the CSV file -->
        <xsl:attribute name="recordCount">
          <xsl:value-of select="$numRecords"/>
        </xsl:attribute>
        <!-- Output the model (field 3) and year (field 1) of each Chevy auto -->
        <xsl:for-each select="$csv-xml//record[field[2] eq 'Chevy']">
            <xsl:element name="chevy">
              <xsl:attribute name="model"><xsl:value-of select="field[3]/data()"/></xsl:attribute>
              <xsl:attribute name="year"><xsl:value-of select="field[1]/data()"/></xsl:attribute>
            </xsl:element>
        </xsl:for-each>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
