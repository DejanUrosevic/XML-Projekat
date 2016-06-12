<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0"
				xmlns:aman="http://www.parlament.gov.rs/amandman"
    			xmlns:clan="http://www.parlament.gov.rs/clan">

	<xsl:template match="/">
		
		<fo:root>

			<fo:layout-master-set>
				<fo:simple-page-master master-name="propis-page">
					<fo:region-body margin="1in" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			
			<fo:page-sequence master-reference="propis-page">
				<fo:flow flow-name="xsl-region-body">
					<fo:block>
				        <fo:external-graphic  src="url(http://www.aycongress.org/images/novi-sad-grb.png)" content-height="scale-to-fit" height="1.20in"  content-width="1.20in" scaling="non-uniform" ></fo:external-graphic>
				    </fo:block>				   
					<fo:block padding-left="30px" color="black" font-size="80%">
						<xsl:value-of select="aman:Amandman/@Mesto"/>
					</fo:block>
					<fo:block padding-left="30px" color="black" font-size="80%">
						Datum:
						<xsl:value-of select="aman:Amandman/@Datum"/>
					</fo:block>
					
					<fo:block text-align="center" color="black"
						padding-bottom="10px" font-size="200%" text-decoration="underline">
					 &#xA; &#xA;	Amandman
					</fo:block>
					<fo:block text-align="center" color="black"
						padding-bottom="10px" font-size="70%">
						Predlazem da se <xsl:value-of select="aman:Amandman/clan:Clan/clan:Naziv"/> 
						zameni sa  <xsl:value-of select="aman:Amandman/clan:Clan/clan:Sadrzaj/clan:Tekst/clan:text"/> 
						<xsl:value-of select="aman:Amandman/clan:Clan/clan:Sadrzaj/clan:Stav/clan:Tekst"/>
					</fo:block> 
					
					<fo:block text-align="center" color="black"
						padding-bottom="10px" font-size="160%" text-decoration="underline">
					&#xA;&#xA;	Obrazlozenje
					</fo:block>
					<fo:block text-align="center" color="black"
						padding-bottom="10px" font-size="70%">
						<xsl:value-of select="aman:Amandman/aman:Obrazlozenje"/>
					</fo:block>
					<fo:block text-align="right" color="black" padding-right="50px" text-decoration="underline">
						 Narodni Poslanik
					</fo:block>
					<fo:block text-align="right" color="black" padding-right="82px" font-size="70%">
						 <xsl:value-of select="aman:Amandman/@Podnosilac"/>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>

		</fo:root> 

	</xsl:template>
	
</xsl:stylesheet>