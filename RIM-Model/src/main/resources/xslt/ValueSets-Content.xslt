<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:mif="urn:hl7-org:v3/mif2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="text"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="text()|@*"/>

	<xsl:template match="/">
		<xsl:text>ValueSetId&#9;Name&#9;Member&#9;Type&#9;MemberNS&#9;Transitive&#10;</xsl:text>
		<xsl:apply-templates select="/mif:vocabularyModel/mif:valueSet"/>
	</xsl:template>

	<xsl:template match="mif:valueSet">
		<xsl:if test="count(mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
			<xsl:apply-templates select="mif:version/mif:content"/>
		</xsl:if>
	</xsl:template>


	<xsl:template match="mif:content">
		<xsl:variable name="valId" select="../../@id"/>
		<xsl:variable name="valName" select="../../@name"/>
			<xsl:apply-templates select="mif:codeBasedContent">
				<xsl:with-param name="vsId" select="$valId"/>
				<xsl:with-param name="vsName" select="$valName"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="mif:combinedContent">
				<xsl:with-param name="vsId" select="$valId"/>
				<xsl:with-param name="vsName" select="$valName"/>
			</xsl:apply-templates>
	</xsl:template>


	<xsl:template match="mif:combinedContent">
		<xsl:param name="vsId"/>
		<xsl:param name="vsName"/>
		<xsl:for-each select="mif:unionWithContent">
			<xsl:apply-templates>
				<xsl:with-param name="vsId" select="$vsId"/>
				<xsl:with-param name="vsName" select="$vsName"/>
			</xsl:apply-templates>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="mif:codeBasedContent">
		<xsl:param name="vsId"/>
		<xsl:param name="vsName"/>
		<xsl:value-of select="$vsId" /><xsl:text>&#9;</xsl:text>
		<xsl:value-of select="$vsName" /><xsl:text>&#9;</xsl:text>

		<xsl:value-of select="@code" /><xsl:text>&#9;</xsl:text>

		<xsl:text>Code&#9;</xsl:text>

		<xsl:choose>
			<xsl:when test="@codeSystem">
				<xsl:value-of select="@codeSystem" /><xsl:text>&#9;</xsl:text>
			</xsl:when>
			<xsl:when test="../@codeSystem">
				<xsl:value-of select="../@codeSystem" /><xsl:text>&#9;</xsl:text>
			</xsl:when>
		</xsl:choose>

		<xsl:choose>
			<xsl:when test="mif:includeRelatedCodes">
				<xsl:value-of select="mif:includeRelatedCodes/@relationshipTraversal" /><xsl:text>&#10;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>NONE&#10;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="mif:valueSetRef">
		<xsl:param name="vsId"/>
		<xsl:param name="vsName"/>
		<xsl:value-of select="$vsId" /><xsl:text>&#9;</xsl:text>
		<xsl:value-of select="$vsName" /><xsl:text>&#9;</xsl:text>

		<xsl:value-of select="@name" /><xsl:text>&#9;</xsl:text>

		<xsl:text>ValueSet&#9;</xsl:text>

		<xsl:value-of select="@id" /><xsl:text>&#9;</xsl:text>

		<!--xsl:choose>
			<xsl:when test="@codeSystem">
				<xsl:value-of select="@codeSystem" /><xsl:text>&#9;</xsl:text>
			</xsl:when>
			<xsl:when test="../@codeSystem">
				<xsl:value-of select="../@codeSystem" /><xsl:text>&#9;</xsl:text>
			</xsl:when>
		</xsl:choose-->


		<xsl:text>NONE&#10;</xsl:text>

	</xsl:template>


</xsl:stylesheet>

