<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="text"/>
	<xsl:strip-space elements="*"/>
	<xsl:template match="/" xpath-default-namespace="urn:hl7-org:v3/mif2">
		<xsl:apply-templates select="/vocabularyModel/codeSystem[index-of(('ActClass','EntityClass','RoleClass'),@name) > 0]/releasedVersion/concept" xpath-default-namespace="urn:hl7-org:v3/mif2"/>
	</xsl:template>
	
	<xsl:template match="/|@*|node()" priority="1">
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>
	
	<xsl:template match="codeSystem[index-of(('ActClass','EntityClass','RoleClass'),@name) > 0]/releasedVersion/concept[code/@status='active']" xpath-default-namespace="urn:hl7-org:v3/mif2" priority="100">
    	<xsl:variable name="codeSystem" select="parent::node()/parent::node()"/>
		<xsl:choose>
			<xsl:when test="$codeSystem/@name='ActClass'"><xsl:text>Act:</xsl:text></xsl:when>
			<xsl:when test="$codeSystem/@name='EntityClass'"><xsl:text>Entity:</xsl:text></xsl:when>
			<xsl:otherwise><xsl:text>Role:</xsl:text></xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="conceptProperty[@name='Name:Class']/@value"/><xsl:text>:</xsl:text>
		<xsl:value-of select="printName/@text"/><xsl:text>:</xsl:text>
		<xsl:value-of select="conceptRelationship/@relationshipName"/><xsl:text>:</xsl:text><xsl:value-of select="conceptRelationship/targetConcept/@code"/><xsl:text>:</xsl:text>
		<xsl:value-of select="code/@code"/><xsl:text>&#10;</xsl:text>
	</xsl:template>

</xsl:stylesheet>
