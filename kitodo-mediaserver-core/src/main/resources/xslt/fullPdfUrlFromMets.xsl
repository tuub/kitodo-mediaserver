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

    <xsl:output method="text" omit-xml-declaration="yes" indent="no" encoding="utf-8"/>

    <xsl:param name="downloadGrpId"/>

    <xsl:template match="/">
        <!-- prints the URL of the full PDF file from a METS file -->

        <!-- get file ID of the PDF file -->
        <xsl:variable name="fileId">
            <xsl:value-of select="/mets:mets/mets:structMap[@TYPE='PHYSICAL']/mets:div/mets:fptr[last()]/@FILEID"/>
        </xsl:variable>

        <!-- print the URL from DOWNLOAD file group -->
        <xsl:if test="$fileId != ''">
            <xsl:text>fullPdfUrl:</xsl:text>
            <xsl:value-of select="/mets:mets/mets:fileSec/mets:fileGrp[@USE=$downloadGrpId]/mets:file[@ID=$fileId]/mets:FLocat/@xlink:href"/>
            <xsl:text>
</xsl:text>
        </xsl:if>

    </xsl:template>

</xsl:stylesheet>
