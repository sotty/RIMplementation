<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:mif="urn:hl7-org:v3/mif2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="text"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="text()|@*">
	</xsl:template>

	<xsl:template match="/">
		<xsl:text>Name&#9;Code&#9;Class&#9;CodeSystemId&#10;</xsl:text>
		<xsl:apply-templates select="/mif:vocabularyModel/mif:codeSystem"/>
	</xsl:template>

	<xsl:template match="mif:codeSystem">
		<xsl:if test="count(mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
			<xsl:apply-templates select="mif:releasedVersion/mif:concept"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="mif:concept">
		<xsl:if test="count(mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
			<xsl:choose>
				<xsl:when test="mif:printName">
					<xsl:value-of select="mif:printName/@text" /><xsl:text>&#9;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="mif:code/@code" /><xsl:text>&#9;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="mif:code/@code" /><xsl:text>&#9;</xsl:text>
			<xsl:choose>
				<xsl:when test="@isSelectable='false'">
					<xsl:text>NonSelectableConcept&#9;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>Concept&#9;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="../../@codeSystemId" /><xsl:text>&#10;</xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>

