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

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:alto="http://www.loc.gov/standards/alto/ns-v2#">

    <xsl:output indent="yes"/>

    <xsl:template match="/">
        <ocrPage>
            <xsl:for-each select="//alto:TextBlock">
                <ocrParagraph>
                    <xsl:for-each select="alto:TextLine">
                        <ocrLine>
                            <xsl:for-each select="alto:String">
                                <xsl:if test="@HPOS!='' and @VPOS!='' and @WIDTH!='' and @HEIGHT!='' and @CONTENT!=''">
                                    <ocrWord>
                                        <xsl:attribute name="x"><xsl:value-of select="@HPOS"/></xsl:attribute>
                                        <xsl:attribute name="y"><xsl:value-of select="@VPOS"/></xsl:attribute>
                                        <xsl:attribute name="width"><xsl:value-of select="@WIDTH"/></xsl:attribute>
                                        <xsl:attribute name="height"><xsl:value-of select="@HEIGHT"/></xsl:attribute>
                                        <xsl:value-of select="@CONTENT"/>
                                    </ocrWord>
                                </xsl:if>
                            </xsl:for-each>
                        </ocrLine>
                    </xsl:for-each>
                </ocrParagraph>
            </xsl:for-each>
        </ocrPage>
    </xsl:template>

</xsl:stylesheet>
