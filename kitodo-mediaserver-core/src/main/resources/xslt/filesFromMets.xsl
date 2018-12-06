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
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:exsl="http://exslt.org/common">

    <!--
    Get source (master) files and its fulltext files.
    If $requestUrl is set, it selects only this one file. If the URL is not found, the output is empty.
    If $requestUrl is unset all pages are selected.

    Output syntax (targetMIME is only set if $requestUrl is set):
    order:masterURL|sourceMIME|targetMIME|fulltextURL

    for example
    1:http://.../image1.tif|image/tiff|image/jpeg|http://.../fulltext1.xml
    2:http://.../image2.tif|image/tiff|image/jpeg|http://.../fulltext2.xml
    -->

    <xsl:output method="text" omit-xml-declaration="yes" indent="no" encoding="utf-8"/>

    <xsl:param name="requestUrl"/>
    <xsl:param name="sourceGrpId"/>
    <xsl:param name="fulltextGrpId"/>

    <!-- If $requestUrl is set, search for the FILEID of the file -->
    <xsl:variable name="requestFileId">
        <xsl:if test="$requestUrl != ''">
            <xsl:value-of select="/mets:mets/mets:fileSec/mets:fileGrp/mets:file/mets:FLocat[@xlink:href=$requestUrl]/../@ID"/>
        </xsl:if>
    </xsl:variable>
    <xsl:variable name="targetMime" select="/mets:mets/mets:fileSec/mets:fileGrp/mets:file[@ID=$requestFileId]/@MIMETYPE"/>

    <xsl:template match="/">
        <!--
        If $requestUrl is set, choose the one physical page
        Otherwise choose all pages
        -->
        <xsl:for-each select="/mets:mets/mets:structMap[@TYPE='PHYSICAL']/mets:div/mets:div/mets:fptr[$requestUrl='' or @FILEID=$requestFileId]/..">

            <!-- Get the page order -->
            <xsl:variable name="order" select="@ORDER"/>

            <!-- Get the source file URL -->
            <xsl:variable name="sourceFileId">
                <xsl:call-template name="searchFileId">
                    <xsl:with-param name="groupId" select="$sourceGrpId"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="sourceUrl" select="//mets:file[@ID=$sourceFileId]/mets:FLocat/@xlink:href"/>
            <xsl:variable name="sourceMime" select="//mets:file[@ID=$sourceFileId]/@MIMETYPE"/>

            <!-- Get the fulltext file URL -->
            <xsl:variable name="fulltextFileId">
                <xsl:call-template name="searchFileId">
                    <xsl:with-param name="groupId" select="$fulltextGrpId"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="fulltextUrl" select="//mets:file[@ID=$fulltextFileId]/mets:FLocat/@xlink:href"/>

            <!-- print the whole line -->
            <xsl:value-of select="concat($order,':', $sourceUrl, '|', $sourceMime, '|', $targetMime, '|', $fulltextUrl)"/>
            <xsl:text>&#xa;</xsl:text>

        </xsl:for-each>
    </xsl:template>

    <!--
    Search for a file in a fileGroup with a matching FILEID
    param: groupId
    return: file href
    -->
    <xsl:template name="searchFileId">
        <xsl:param name="groupId"/>
        <xsl:for-each select="mets:fptr">
            <xsl:variable name="fileId" select="@FILEID"/>
            <xsl:value-of select="/mets:mets/mets:fileSec/mets:fileGrp[@USE=$groupId]/mets:file[@ID=$fileId]/@ID"/>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
