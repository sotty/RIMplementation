<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:mif="urn:hl7-org:v3/mif2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="text"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="text()|@*">
	</xsl:template>

	<xsl:template match="/">
		<xsl:text>ConceptDomain&#9;Valueset&#9;CodingStrength&#10;</xsl:text>
		<xsl:apply-templates select="/mif:vocabularyModel/mif:contextBinding"/>
	</xsl:template>

	<xsl:template match="mif:contextBinding">
		<xsl:value-of select="@conceptDomain" /><xsl:text>&#9;</xsl:text>
		<xsl:value-of select="@valueSet" /><xsl:text>&#9;</xsl:text>
		<xsl:value-of select="@codingStrength" /><xsl:text>&#10;</xsl:text>
	</xsl:template>

</xsl:stylesheet>

