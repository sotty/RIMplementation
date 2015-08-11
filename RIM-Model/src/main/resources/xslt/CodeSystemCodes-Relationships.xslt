<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:mif="urn:hl7-org:v3/mif2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="text"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="text()|@*">
	</xsl:template>

	<xsl:template match="/">
		<xsl:text>Source&#9;Relationship&#9;Target&#9;CodeSystemId&#9;TargetCodeSystemId&#10;</xsl:text>
		<xsl:apply-templates select="/mif:vocabularyModel/mif:codeSystem"/>
	</xsl:template>

	<xsl:template match="mif:codeSystem">
		<xsl:if test="count(mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
			<xsl:apply-templates select="mif:releasedVersion/mif:concept/mif:conceptRelationship/mif:targetConcept"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="mif:targetConcept">
		<xsl:if test="count(../../mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
			<xsl:if test="../@relationshipName != 'ClassifiesClassCode'">
				<xsl:value-of select="../../mif:code/@code" /><xsl:text>&#9;</xsl:text>
				<xsl:value-of select="../@relationshipName" /><xsl:text>&#9;</xsl:text>
				<xsl:value-of select="@code" /><xsl:text>&#9;</xsl:text>
				<xsl:value-of select="../../../../@codeSystemId" /><xsl:text>&#9;</xsl:text>
				<xsl:value-of select="@codeSystem" /><xsl:text>&#10;</xsl:text>
			</xsl:if>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>

