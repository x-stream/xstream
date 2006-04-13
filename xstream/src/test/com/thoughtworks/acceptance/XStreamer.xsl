<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="immutableTypes">
	<xsl:copy>
	 	<xsl:apply-templates select="java-class">
			<xsl:sort/>
		</xsl:apply-templates>
	</xsl:copy>
</xsl:template>
<xsl:template match="typeToImpl|typeToName|classToName">
	<xsl:copy>
	 	<xsl:apply-templates select="entry">
			<xsl:sort select="java-class[1]|string[1]"/>
		</xsl:apply-templates>
	</xsl:copy>
</xsl:template>
<xsl:template match="@*|node()">
	<xsl:copy>
		<xsl:apply-templates select="@*|node()"/>
	</xsl:copy>
</xsl:template>
</xsl:stylesheet>