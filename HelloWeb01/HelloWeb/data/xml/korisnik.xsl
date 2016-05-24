<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:template match="/">
    	
    	<table style="border: 1px">
    		<tr bgcolor="#9acd32">
                        <th>Ime</th>
                        <th>Prezime</th>
                        <th>Username</th>
                        <th>Vrsta</th>
            </tr>
			<xsl:for-each select="korisnici/korisnik">
				<tr>
					<td><xsl:value-of select="ime"/></td>
					<td><xsl:value-of select="prezime"/></td>
					<td><xsl:value-of select="username"/></td>
					<td><xsl:value-of select="vrsta"/></td>
				</tr>
			</xsl:for-each>
		
		</table>
  	</xsl:template>
</xsl:stylesheet>