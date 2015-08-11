<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:mif="urn:hl7-org:v3/mif2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="text"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="text()|@*">
	</xsl:template>

	<xsl:template match="/">
		<xsl:text>Name&#9;Id&#10;</xsl:text>
		<xsl:apply-templates select="/mif:vocabularyModel/mif:codeSystem"/>
	</xsl:template>

	<xsl:template match="mif:codeSystem">
		<xsl:if test="count(mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
			<xsl:value-of select="@name" /><xsl:text>&#9;</xsl:text>
			<xsl:value-of select="@codeSystemId" /><xsl:text>&#10;</xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>

