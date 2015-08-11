<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="text" version="1.0" encoding="UTF-8" indent="no"/>
    <xsl:param name="selector">
        <xsl:text>ALL</xsl:text> <!-- Default value -->
    </xsl:param>
	
	<xsl:template match="/" priority="101">
        <xsl:if test="$selector='ALL' or $selector='RoleLinkType'">
            <xsl:text>Name:Code:Specializes:InboundLinkName:OutboundLinkName&#10;</xsl:text>
            <xsl:apply-templates select="/vocabularyModel/codeSystem[@name='RoleLinkType']/releasedVersion/concept" xpath-default-namespace="urn:hl7-org:v3/mif2"/>
            <xsl:text>&#10;&#10;</xsl:text>
        </xsl:if>
        <xsl:if test="$selector='ALL' or $selector='ParticipationType'">
            <xsl:text>Name:Code:Specializes:Name-Act-Participation:Name-Role-Participation&#10;</xsl:text>
            <xsl:apply-templates select="/vocabularyModel/codeSystem[@name='ParticipationType']/releasedVersion/concept" xpath-default-namespace="urn:hl7-org:v3/mif2"/>
            <xsl:text>&#10;&#10;</xsl:text>
        </xsl:if>
        <xsl:if test="$selector='ALL' or $selector='ActRelationshipType'">
            <xsl:text>Name:Code:SpecializesCount:Specializes:InboundName:OutboundName:SourceName:TargetName&#10;</xsl:text>
            <xsl:apply-templates select="/vocabularyModel/codeSystem[@name='ActRelationshipType']/releasedVersion/concept" xpath-default-namespace="urn:hl7-org:v3/mif2"/>
            <xsl:text>&#10;&#10;</xsl:text>
        </xsl:if>
        <xsl:if test="$selector='ALL' or $selector='ActEntityRoleCodes'">
            <xsl:text>Name:RelationshipCount:RelationshipName=Value/CodeSystem:Code&#10;</xsl:text>
            <xsl:apply-templates select="/vocabularyModel/codeSystem[index-of(('EntityCode','ActCode','RoleCode'),@name) > 0]/releasedVersion/concept" xpath-default-namespace="urn:hl7-org:v3/mif2"/>
        </xsl:if>
	</xsl:template>

	<xsl:template match="/|@*|node()" priority="10">
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>
	
	<xsl:template match="codeSystem[@name='RoleLinkType']/releasedVersion/concept[code/@status='active']" priority="100" xpath-default-namespace="urn:hl7-org:v3/mif2">
		<xsl:value-of select="parent::node()/parent::node()/@name"/><xsl:text>:</xsl:text>
		<xsl:value-of select="printName/@text"/><xsl:text>:</xsl:text>
		<xsl:value-of select="code/@code"/><xsl:text>:</xsl:text>
		<xsl:value-of select="conceptRelationship[@relationshipName='Specializes']/targetConcept/@code"/><xsl:text>:</xsl:text>
		<xsl:value-of select="conceptProperty[@name='Name:Role:inboundLink:RoleLink']/@value"/><xsl:text>:</xsl:text>
		<xsl:value-of select="conceptProperty[@name='Name:Role:outboundLink:RoleLink']/@value"/>
		<xsl:text>&#10;</xsl:text>
	</xsl:template>

	<xsl:template match="codeSystem[@name='ParticipationType']/releasedVersion/concept[code/@status='active']" priority="100" xpath-default-namespace="urn:hl7-org:v3/mif2">
		<xsl:value-of select="parent::node()/parent::node()/@name"/><xsl:text>:</xsl:text>
		<xsl:value-of select="printName/@text"/><xsl:text>:</xsl:text>
		<xsl:value-of select="code/@code"/><xsl:text>:</xsl:text>
		<xsl:value-of select="conceptRelationship[@relationshipName='Specializes']/targetConcept/@code"/><xsl:text>:</xsl:text>
		<xsl:value-of select="conceptProperty[@name='Name:Act:participation:Participation']/@value"/><xsl:text>:</xsl:text>
		<xsl:value-of select="conceptProperty[@name='Name:Role:participation:Participation']/@value"/>
		<xsl:text>&#10;</xsl:text>
	</xsl:template>

	<xsl:template match="codeSystem[@name='ActRelationshipType']/releasedVersion/concept[code/@status='active']" xpath-default-namespace="urn:hl7-org:v3/mif2" priority="100">
		<!--xsl:value-of select="parent::node()/parent::node()/@name"/><xsl:text>:</xsl:text-->
		<xsl:value-of select="printName/@text"/><xsl:text>:</xsl:text>
		<xsl:value-of select="code/@code"/><xsl:text>:</xsl:text>
		<xsl:value-of select="count(conceptRelationship)"/><xsl:text>:</xsl:text>
		<xsl:for-each select="conceptRelationship">
			<xsl:value-of select="targetConcept/@code"/><xsl:text>:</xsl:text>
		</xsl:for-each>
		<xsl:value-of select="conceptProperty[@name='Name:Act:inboundRelationship:ActRelationship']/@value"/><xsl:text>:</xsl:text>
		<xsl:value-of select="conceptProperty[@name='Name:Act:outboundRelationship:ActRelationship']/@value"/><xsl:text>:</xsl:text>
		<xsl:value-of select="conceptProperty[@name='Name:ActRelationship:source:Act']/@value"/><xsl:text>:</xsl:text>
		<xsl:value-of select="conceptProperty[@name='Name:ActRelationship:target:Act']/@value"/>
		<xsl:text>&#10;</xsl:text>
	</xsl:template>
	
	<xsl:template match="codeSystem[index-of(('EntityCode','ActCode','RoleCode'),@name) > 0]/releasedVersion/concept[code/@status='active']" priority="100" xpath-default-namespace="urn:hl7-org:v3/mif2">
		<xsl:value-of select="parent::node()/parent::node()/@name"/><xsl:text>:</xsl:text>
		<xsl:value-of select="replace(printName[1]/@text,':','-')"/><xsl:text>:</xsl:text>
		<xsl:value-of select="count(conceptRelationship)"/><xsl:text>:</xsl:text>
		<xsl:for-each select="conceptRelationship">
			<xsl:value-of select="@relationshipName"/><xsl:text>=</xsl:text>
			<xsl:value-of select="targetConcept/@code"/>
			<xsl:if test="@relationshipName='ClassifiesClassCode'">
				<xsl:text>/</xsl:text>
				<xsl:value-of select="targetConcept/@codeSystem"/>
			</xsl:if>
			<xsl:text>:</xsl:text>
		</xsl:for-each>
		<xsl:value-of select="code/@code"/>
		<xsl:text>&#10;</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
