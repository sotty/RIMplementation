<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:mif="urn:hl7-org:v3/mif2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:strip-space elements="*"/>

	<xsl:template match="text()|@*">
	</xsl:template>

	<xsl:template match="/">
		<xsl:text>AttributeName&#9;PropertyName&#9;PropertyValue&#9;ClassName&#10;</xsl:text>
		<xsl:apply-templates select="/mif:staticModel/mif:containedClass/mif:class"/>
	</xsl:template>

	<xsl:template match="mif:class">
		<xsl:if test="count(mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
			<xsl:apply-templates select="mif:attribute"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="mif:attribute">
		<xsl:if test="count(mif:annotations/mif:appInfo/mif:deprecationInfo) = 0">
			<xsl:variable name="attributeName" select="@name" />
			<xsl:variable name="className" select="../@name" />

			<xsl:if test="mif:annotations/mif:documentation/mif:definition/mif:text">
				<xsl:value-of select="$attributeName" /><xsl:text>&#9;</xsl:text>
				<xsl:text>Description&#9;</xsl:text>
		    	<xsl:apply-templates select="mif:annotations/mif:documentation/mif:definition/mif:text" /><xsl:text>&#9;</xsl:text>
				<xsl:value-of select="$className" /><xsl:text>&#10;</xsl:text>
			</xsl:if>

			<xsl:if test="mif:type">
				<xsl:value-of select="$attributeName" /><xsl:text>&#9;</xsl:text>
				<xsl:text>Type&#9;</xsl:text>
				<xsl:value-of select="mif:type/@name" /><xsl:text>&#9;</xsl:text>
				<xsl:value-of select="$className" /><xsl:text>&#10;</xsl:text>

				<xsl:value-of select="$attributeName" /><xsl:text>&#9;</xsl:text>
				<xsl:text>Min&#9;</xsl:text>
				<xsl:value-of select="@minimumMultiplicity" /><xsl:text>&#9;</xsl:text>
				<xsl:value-of select="$className" /><xsl:text>&#10;</xsl:text>

				<xsl:value-of select="$attributeName" /><xsl:text>&#9;</xsl:text>
				<xsl:text>Max&#9;</xsl:text>
				<xsl:value-of select="@maximumMultiplicity" /><xsl:text>&#9;</xsl:text>
				<xsl:value-of select="$className" /><xsl:text>&#10;</xsl:text>

				<xsl:for-each select="mif:type/mif:argumentDatatype">
					<xsl:value-of select="$attributeName" /><xsl:text>&#9;</xsl:text>
					<xsl:text>ArgumentDatatype&#9;</xsl:text>
					<xsl:value-of select="@name" /><xsl:text>&#9;</xsl:text>
					<xsl:value-of select="$className" /><xsl:text>&#10;</xsl:text>
				</xsl:for-each>
			</xsl:if>

			<xsl:if test="mif:vocabulary">
				<xsl:value-of select="$attributeName" /><xsl:text>&#9;</xsl:text>
				<xsl:text>Vocabulary&#9;</xsl:text>
				<xsl:value-of select="mif:vocabulary/mif:conceptDomain/@name"/><xsl:text>&#9;</xsl:text>
				<xsl:value-of select="$className" /><xsl:text>&#10;</xsl:text>
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

