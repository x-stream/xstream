<?xml version="1.0"?>
<!--
Copyright (C) 2006, 2007, 2008 XStream Committers.
All rights reserved.

The software in this package is published under the terms of the BSD
style license a copy of which has been included with this distribution in
the LICENSE.txt file.

Created on 30. March 2006 by Joerg Schaible
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="immutableTypes">
	<xsl:copy>
	 	<xsl:apply-templates select="java-class">
			<xsl:sort/>
		</xsl:apply-templates>
	</xsl:copy>
</xsl:template>
<xsl:template match="typeToImpl|typeToName|classToName|packageToName">
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