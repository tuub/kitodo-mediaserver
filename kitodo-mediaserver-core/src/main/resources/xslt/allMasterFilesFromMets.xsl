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

    <xsl:output method="text" omit-xml-declaration="yes" indent="no" encoding="utf-8"/>

    <xsl:param name="fileGrp"/>

    <xsl:template match="/">
        <!--
        output should be a list with page order and file URL.
        for example:
        1:http://domain.de/files/UATUB_717-0001/flugblatt_717_0001_tif/717_0001_0001.tif
        2:http://domain.de/files/UATUB_717-0001/flugblatt_717_0001_tif/717_0001_0002.tif
        -->

        <!-- for each file in choosen file group -->
        <xsl:for-each select="/mets:mets/mets:fileSec/mets:fileGrp[@USE=$fileGrp]/mets:file">

            <!-- save the file id -->
            <xsl:variable name="id">
                <xsl:value-of select="@ID"/>
            </xsl:variable>

            <!-- get the page order from structMap for this file id -->
            <xsl:variable name="order">
                <xsl:value-of select="/mets:mets/mets:structMap[@TYPE='PHYSICAL']/mets:div/mets:div[@TYPE='page']/mets:fptr[@FILEID=$id]/../@ORDER"/>
            </xsl:variable>

            <!-- only add the file if it is a page and has an order -->
            <xsl:if test="$order != ''">

                <!-- print page order and file url -->
                <xsl:value-of select="$order"/>:<xsl:value-of select="./mets:FLocat/@xlink:href"/>

                <!-- line break -->
                <xsl:text>
</xsl:text>
            </xsl:if>

        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
