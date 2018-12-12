<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mets="http://www.loc.gov/METS/"
                xmlns:mods="http://www.loc.gov/mods/v3">

    <xsl:output method="xml" indent="yes" encoding="utf-8"/>

    <xsl:param name="pattern"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="//mods:titleInfo/mods:title/text()">
        <xsl:value-of select="replace(., $pattern, '$1')"/>
    </xsl:template>

    <xsl:template match="//mods:titleInfo/mods:subTitle/text()">
        <xsl:value-of select="replace(., $pattern, '$1')"/>
    </xsl:template>

    <xsl:template match="//mets:div/@LABEL">
        <xsl:attribute name="LABEL">
            <xsl:value-of select="replace(., $pattern, '$1')"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="//mets:div/@ORDERLABEL">
        <xsl:attribute name="ORDERLABEL">
            <xsl:value-of select="replace(., $pattern, '$1')"/>
        </xsl:attribute>
    </xsl:template>

</xsl:stylesheet>
