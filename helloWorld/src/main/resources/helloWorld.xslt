<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 version="1.0"
 xmlns:hw="http://example.com/dfdl/helloworld/"
>
 <xsl:output method="xml" />
 <xsl:template match="/hw:helloWorld" >
   <xsl:copy>
     <xsl:apply-templates/>
   </xsl:copy>
 </xsl:template>
 
 <xsl:template match="word[1]">
  <xsl:message>
   matched word 1
  </xsl:message>
  <xsl:choose>
   <xsl:when test="text() = 'Hello'">
    <xsl:message>
     Changing Hello to Goodbye
    </xsl:message>
    <word>Goodbye</word>
   </xsl:when>
   <xsl:otherwise>
    <xsl:copy-of select="." />
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>
 
 <xsl:template match="word[2]">
  <xsl:message>
   matched word 2
  </xsl:message>
  <xsl:choose>
   <xsl:when test="text() = 'world!'">
    <xsl:message>
     Changing world to Mars
    </xsl:message>
    <word>Mars!</word>
   </xsl:when>
   <xsl:otherwise>
    <xsl:copy-of select="." />
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>
</xsl:stylesheet>
