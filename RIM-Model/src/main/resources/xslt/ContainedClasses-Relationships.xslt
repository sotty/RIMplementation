<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:mif="urn:hl7-org:v3/mif2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="text"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="text()|@*">
	</xsl:template>

	<xsl:template match="/">
		<xsl:text>Source&#9;Relationship&#9;Target&#10;</xsl:text>
		<xsl:apply-templates select="/mif:staticModel/mif:containedClass/mif:class"/>
	</xsl:template>

	<xsl:template match="mif:class">
		<xsl:if test="count(mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
			<xsl:if test="mif:parentClass">
				<xsl:value-of select="@name" /><xsl:text>&#9;</xsl:text>
				<xsl:text>ParentClass&#9;</xsl:text>
				<xsl:value-of select="mif:parentClass/@name" /><xsl:text>&#10;</xsl:text>
			</xsl:if>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>

