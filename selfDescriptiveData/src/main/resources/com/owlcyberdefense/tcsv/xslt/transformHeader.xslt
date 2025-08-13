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

  <xsl:template match="/">
    <!-- Standard DFDL schema boilerplate defining delimited text data -->
    <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:dfdl="http://www.ogf.org/dfdl/dfdl-1.0/">

      <!-- DFDL default properties -->
      <xs:include schemaLocation="org/apache/daffodil/xsd/DFDLGeneralFormat.dfdl.xsd" />
      <xs:annotation>
        <xs:appinfo source="http://www.ogf.org/dfdl/">
          <dfdl:format ref="GeneralFormat" lengthKind="delimited" />
        </xs:appinfo>
      </xs:annotation>

      <!--
        TCSVPayload is an unbounded sequence or rows. Each row is separated by
        a newline. Each Row is a sequence of fields separated by a comma.
      -->
      <xs:element name="TCSVPayload">
        <xs:complexType>
          <xs:sequence dfdl:separator="%NL;">
            <xs:element name="Row" maxOccurs="unbounded">
              <xs:complexType>
                <xs:sequence dfdl:separator=",">
                  <xsl:apply-templates />
                </xs:sequence>
              </xs:complexType>
            </xs:element>
          </xs:sequence>
        </xs:complexType>
      </xs:element>
    </xs:schema>
  </xsl:template>

  <xsl:template match="/TCSVHeader/Column">
    <!--
      For each Column field in the TCSVHeader, create an element using the
      name and type. These elements are inserted in the Row sequence above
    -->
    <xs:element>
      <xsl:attribute name="name">
        <xsl:value-of select="Title" />
      </xsl:attribute>
      <xsl:attribute name="type">
        <xsl:value-of select="concat('xs:', Type)" />
      </xsl:attribute>
    </xs:element>
  </xsl:template>

</xsl:stylesheet>
