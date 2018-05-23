<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mets="http://www.loc.gov/METS/"
                xmlns:mods="http://www.loc.gov/mods/v3"
                xmlns:goobi="http://meta.goobi.org/v1.5.1/">

    <xsl:output method="text" omit-xml-declaration="yes" indent="no" encoding="utf-8"/>

    <xsl:variable name="dmdsec_id">
        <xsl:choose>
            <xsl:when test="/mets:mets/mets:structMap[@TYPE='LOGICAL']/mets:div/@DMDID">
                <xsl:value-of select="/mets:mets/mets:structMap[@TYPE='LOGICAL']/mets:div/@DMDID"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="/mets:mets/mets:structMap[@TYPE='LOGICAL']/mets:div/mets:div/@DMDID">
                        <xsl:value-of select="/mets:mets/mets:structMap[@TYPE='LOGICAL']/mets:div/mets:div/@DMDID"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>DMDLOG_0000</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:template match="/">

        <xsl:for-each select="//mets:structMap[@TYPE='LOGICAL']/mets:div">
            title:<xsl:value-of select="@LABEL"/>
            identifier:<xsl:value-of select="@CONTENTIDS"/>
        </xsl:for-each>

        <xsl:for-each select="//mets:dmdSec[@ID=$dmdsec_id]/mets:mdWrap/mets:xmlData/mods:mods/mods:recordInfo/mods:recordIdentifier">
            workid:<xsl:value-of select="."/>
        </xsl:for-each>

        <xsl:for-each select="//mets:dmdSec[@ID=$dmdsec_id]/mets:mdWrap/mets:xmlData/mods:mods/mods:identifier">
            identifier.<xsl:value-of select="@type"/>:<xsl:value-of select="."/>
        </xsl:for-each>

        <xsl:for-each select="//mets:dmdSec[@ID=$dmdsec_id]/mets:mdWrap/mets:xmlData/mods:mods/mods:identifier">
            identifier.<xsl:value-of select="@source"/>:<xsl:value-of select="."/>
        </xsl:for-each>

        <xsl:for-each select="//mets:dmdSec[@ID=$dmdsec_id]/mets:mdWrap/mets:xmlData/mods:mods/mods:titleInfo/mods:title">
            title:<xsl:value-of select="."/>
        </xsl:for-each>


    </xsl:template>

</xsl:stylesheet>