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
                xmlns:mods="http://www.loc.gov/mods/v3">

    <xsl:output indent="yes" method="xml"/>

    <!--
        This stylesheet reduces the content of a mets/mods metadata file. It is used by the disablement of works,
        followed by a reindexing of the work, making sure that no sensitive metadata are indexed.
    -->

    <!-- Find out where the basic mods metadata are (different for monographs and periodical parts) -->
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

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>

    <!-- Get only selected mods metadata -->
    <xsl:template match="//mets:dmdSec">
        <xsl:if test="@ID=$dmdsec_id">
            <mets:dmdSec>
                <xsl:attribute name="ID"><xsl:value-of select="$dmdsec_id"/></xsl:attribute>
                <mets:mdWrap>
                    <mets:xmlData>
                        <mods:mods>
                            <xsl:for-each select="mets:mdWrap/mets:xmlData/mods:mods">
                                <xsl:for-each select="mods:titleInfo">
                                    <xsl:copy-of select="self::node()"/>
                                </xsl:for-each>
                                <xsl:for-each select="mods:originInfo">
                                    <xsl:copy-of select="self::node()"/>
                                </xsl:for-each>
                            </xsl:for-each>
                        </mods:mods>
                    </mets:xmlData>
                </mets:mdWrap>
            </mets:dmdSec>
        </xsl:if>
    </xsl:template>

    <!-- Remove certain mets sections -->
    <xsl:template match="//mets:structMap[@TYPE='PHYSICAL']"/>

    <xsl:template match="//mets:structLink"/>

    <xsl:template match="//mets:fileSec"/>

</xsl:stylesheet>
