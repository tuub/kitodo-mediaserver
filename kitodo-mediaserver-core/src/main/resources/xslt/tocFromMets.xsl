<?xml version="1.0" encoding="utf-8"?>
<!--
  ~ (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
  ~
  ~ This file is part of the Kitodo project.
  ~
  ~ It is licensed under GNU General Public License version 3 or later.
  ~
  ~ For the full copyright and license information, please read the
  ~ LICENSE file that was distributed with this source code.
  -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mets="http://www.loc.gov/METS/"
                xmlns:xlink="http://www.w3.org/1999/xlink">

    <xsl:output indent="yes"/>

    <!--
    Output must be like:
    <toc>
        <tocItem name="xyz" type="chapter" pageNumber="123">
            <tocItem name="abc" type="..." pageNumber="456"/>
            ...
        <tocItem>
        ...
    </toc>
    -->

    <xsl:template match="/">
        <toc>
            <!--
            Get all TOC entries under the first child of the LOGICAL structMap containing a LABEL attribute
            -->
            <xsl:for-each select="//mets:structMap[@TYPE='LOGICAL']/mets:div[1]/mets:div[@LABEL]">
                <xsl:call-template name="tocItem"/>
            </xsl:for-each>
        </toc>
    </xsl:template>

    <xsl:template name="tocItem">
        <tocItem>
            <xsl:variable name="logId" select="@ID"/>
            <xsl:variable name="physId">
                <xsl:value-of select="//mets:structLink/mets:smLink[@xlink:from=$logId][1]/@xlink:to"/>
            </xsl:variable>

            <xsl:attribute name="name"><xsl:value-of select="@LABEL"/></xsl:attribute>
            <xsl:attribute name="type"><xsl:value-of select="@TYPE"/></xsl:attribute>
            <xsl:attribute name="pageNumber">
                <xsl:value-of select="//mets:structMap[@TYPE='PHYSICAL']/mets:div[1]/mets:div[@ID=$physId]/@ORDER"/>
            </xsl:attribute>

            <xsl:for-each select="mets:div">
                <xsl:call-template name="tocItem"/>
            </xsl:for-each>
        </tocItem>
    </xsl:template>

</xsl:stylesheet>
