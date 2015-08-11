<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:mif="urn:hl7-org:v3/mif2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="text"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="text()|@*">
	</xsl:template>

	<xsl:template match="/">
		<xsl:text>AttributeName&#9;ClassName&#9;Vocabulary&#10;</xsl:text>
		<xsl:apply-templates select="/mif:staticModel/mif:containedClass/mif:class"/>
	</xsl:template>

	<xsl:template match="mif:class">
		<xsl:if test="count(mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
			<xsl:for-each select="mif:attribute">
				<xsl:if test="count(mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
					<xsl:value-of select="@name" /><xsl:text>&#9;</xsl:text>
					<xsl:value-of select="../@name" /><xsl:text>&#9;</xsl:text>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="mif:vocabulary">
						<xsl:value-of select="mif:vocabulary/mif:conceptDomain/@name"/><xsl:text>&#10;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>&#10;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>

