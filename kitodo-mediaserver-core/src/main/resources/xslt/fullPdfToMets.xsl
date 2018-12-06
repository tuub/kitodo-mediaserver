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

    <!-- Add an file entry for a full PDF download which gets recognized by Kitodo.Presentation and DF-Viewer -->

    <xsl:param name="rootUrl"/>
    <xsl:param name="workId"/>
    <xsl:param name="downloadGrpId"/>

    <!-- The file ID used inside the METS file -->
    <xsl:variable name="fileid" select="'FILE_FULLPDF_DOWNLOAD'"/>

    <!-- If there already is a FullPDF entry, find it -->
    <xsl:variable name="presentFileId">
        <xsl:value-of select="/mets:mets/mets:structMap[@TYPE='PHYSICAL']/mets:div/mets:fptr[last()]/@FILEID"/>
    </xsl:variable>

    <!-- Replace string function -->
    <xsl:template name="replace">
        <xsl:param name="text" />
        <xsl:param name="search" />
        <xsl:param name="replace" />
        <xsl:choose>
            <xsl:when test="contains($text, $search)">
                <xsl:value-of select="substring-before($text, $search)" />
                <xsl:value-of select="$replace" />
                <xsl:call-template name="replace">
                    <xsl:with-param name="text" select="substring-after($text, $search)" />
                    <xsl:with-param name="search" select="$search" />
                    <xsl:with-param name="replace" select="$replace" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- The URL to the downloadable PDF file -->
    <xsl:variable name="url">
        <xsl:call-template name="replace">
            <xsl:with-param name="text" select="concat($rootUrl, '{workId}/{workId}.pdf')"/>
            <xsl:with-param name="search" select="'{workId}'"/>
            <xsl:with-param name="replace" select="$workId"/>
        </xsl:call-template>
    </xsl:variable>

    <!-- Copy whole document -->
    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Add entry to fileGroup -->
    <xsl:template match="//mets:mets/mets:fileSec/mets:fileGrp[@USE=$downloadGrpId]/*[1]">

        <!-- Add FullPDF entry, if there is non yet -->
        <xsl:if test="$presentFileId = ''">
            <mets:file ID="{$fileid}" MIMETYPE="application/pdf">
                <mets:FLocat LOCTYPE="URL" xlink:href="{$url}"/>
            </mets:file>
        </xsl:if>

        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Add entry to structMap -->
    <xsl:template match="//mets:mets/mets:structMap[@TYPE='PHYSICAL']/mets:div[1]/*[1]">

        <!-- Add FullPDF entry, if there is non yet -->
        <xsl:if test="$presentFileId = ''">
            <mets:fptr FILEID="{$fileid}"/>
        </xsl:if>

        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
