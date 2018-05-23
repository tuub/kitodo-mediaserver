<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mets="http://www.loc.gov/METS/"
                xmlns:mods="http://www.loc.gov/mods/v3">

    <xsl:output method="text" omit-xml-declaration="yes" indent="no" encoding="utf-8"/>

    <xsl:template match="/">
        <xsl:for-each select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/mods:mods/mods:recordInfo/mods:recordIdentifier">
            <xsl:value-of select="."/><xsl:text>&#xa;</xsl:text>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
