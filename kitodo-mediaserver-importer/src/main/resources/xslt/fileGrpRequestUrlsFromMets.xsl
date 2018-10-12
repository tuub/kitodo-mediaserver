<?xml version="1.0" encoding="utf-8"?>
<!--
 *
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * LICENSE file that was distributed with this source code.
 *
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mets="http://www.loc.gov/METS/"
                xmlns:xlink="http://www.w3.org/1999/xlink">

    <xsl:output method="text" omit-xml-declaration="yes" indent="no" encoding="utf-8"/>

    <xsl:param name="fileGrp"/>
    <xsl:param name="fileId"/>

    <xsl:template match="/">
        <xsl:for-each select="mets:mets/mets:fileSec/mets:fileGrp[@USE=$fileGrp]">
            <xsl:choose>
                <xsl:when test="$fileId">
request_url: <xsl:value-of select="mets:file[@ID=$fileId]/mets:FLocat/@xlink:href"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="mets:file">
request_url: <xsl:value-of select="mets:FLocat/@xlink:href"/>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
