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

    <xsl:param name="request_url"/>
    <xsl:param name="original_id"/>

    <xsl:variable name="fileid">
        <xsl:value-of select="/mets:mets/mets:fileSec/mets:fileGrp/mets:file/mets:FLocat[@xlink:href=$request_url]/../@ID"/>
    </xsl:variable>

    <xsl:variable name="original_fileid">
        <xsl:value-of select="/mets:mets/mets:structMap[@TYPE='PHYSICAL']/mets:div/mets:div/mets:fptr[@FILEID=$fileid]/../mets:fptr[contains(@FILEID, $original_id)]/@FILEID"/>
    </xsl:variable>

    <xsl:template match="/">
source_url:    <xsl:value-of select="/mets:mets/mets:fileSec/mets:fileGrp/mets:file[@ID=$original_fileid]/mets:FLocat/@xlink:href"/>
source_mime:   <xsl:value-of select="/mets:mets/mets:fileSec/mets:fileGrp/mets:file[@ID=$original_fileid]/@MIMETYPE"/>
target_mime:   <xsl:value-of select="/mets:mets/mets:fileSec/mets:fileGrp/mets:file[@ID=$fileid]/@MIMETYPE"/>
    </xsl:template>

</xsl:stylesheet>
