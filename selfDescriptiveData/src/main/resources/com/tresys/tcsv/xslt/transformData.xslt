<?xml version="1.0" encoding="UTF-8"?> 

<!-- 
Copyright 2019 Tresys Technology

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  version="1.0">

  <xsl:output method="xml" indent="yes" />

  <!-- Sort all the Rows by the Name elements -->
  <xsl:template match="/TCSVData">
    <xsl:copy>
      <xsl:apply-templates>
        <xsl:sort select="Name" />
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <!-- Remove any Rows that have a Name element with the value "Eve" -->
  <xsl:template match="*[Name='Eve']" />

  <!-- Copy all other nodes -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
