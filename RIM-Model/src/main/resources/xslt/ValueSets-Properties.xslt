<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:mif="urn:hl7-org:v3/mif2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:strip-space elements="*"/>

	<xsl:template match="text()|@*">
	</xsl:template>

	<xsl:template match="/">
		<xsl:text>Id&#9;PropertyName&#9;Value&#10;</xsl:text>
		<xsl:apply-templates select="/mif:vocabularyModel/mif:valueSet"/>
	</xsl:template>

	<xsl:template match="mif:valueSet">
		<xsl:if test="count(mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
			<xsl:if test="mif:annotations/mif:documentation/mif:description/mif:text">
				<xsl:value-of select="@id" /><xsl:text>&#9;</xsl:text>
				<xsl:text>Description&#9;</xsl:text>
				<xsl:apply-templates select="mif:annotations/mif:documentation/mif:description/mif:text" /><xsl:text>&#10;</xsl:text>
			</xsl:if>

			<xsl:if test="@isImmutable">
				<xsl:value-of select="@id" /><xsl:text>&#9;</xsl:text>
				<xsl:text>IsImmutable&#9;</xsl:text>
				<xsl:value-of select="@isImmutable" /><xsl:text>&#10;</xsl:text>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Copy elements beneath text elements, without their namespace -->
	<xsl:template match="mif:text//*">
		<xsl:element name="{local-name()}">
			<xsl:value-of select="normalize-space()"/>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>

