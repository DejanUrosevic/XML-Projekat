<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
				xmlns="http://www.parlament.gov.rs/propis"
			    xmlns:ns2="http://www.parlament.gov.rs/clan">
  
    <xsl:template match="/">
    	
    	 <html>
            <head>
                <title>Bookstore (XSLT)</title>
                <style type="text/css">
                    table {
                        font-family: serif;
                        border-collapse: collapse;
                        margin: 50px auto 50px auto;
                        width: 90%;
                    }
			        td {
			          	border: 0;
			          }
			        th, td
			        {
			          	text-align: center;
			            font-size: 14px;
			            padding: 8px;
			        }

                </style>
            </head>
            <body>
            	
            	<u><h1 class="tekst_sredina"><xsl:value-of select="Propis/Naziv"/></h1></u>
					<br/><br/>
					<xsl:for-each select="Propis/Deo">
						<h3 class="tekst_sredina"><xsl:value-of select="Naziv"/></h3> <br/>
						<xsl:for-each select="//Deo/Glava">
							<h4 class="tekst_sredina"><xsl:value-of select="Naziv"/></h4><br/>
							<xsl:for-each select="//Glava/ns2:Clan">
								<h4 class="tekst_sredina"><xsl:value-of select="ns2:Naziv"/></h4>
								<p class="tekst_sredina"><xsl:value-of select="ns2:Opis"/></p>
								<div>
									<xsl:for-each select="//ns2:Clan/ns2:Sadrzaj/ns2:Tekst"> 
										<xsl:value-of select="ns2:Naziv"/> 
									</xsl:for-each>
									<xsl:for-each select="//ns2:Clan/ns2:Sadrzaj/ns2:Stav">
										(<xsl:value-of select="ns2:Redni_broj"/>) &#160; <xsl:value-of select="ns2:Tekst"/>
									</xsl:for-each>
								</div>
							</xsl:for-each> 
						</xsl:for-each>
						<xsl:for-each select="//Deo/ns2:Clan">
								<h4 class="tekst_sredina"><xsl:value-of select="ns2:Naziv"/></h4>
								<p class="tekst_sredina"><xsl:value-of select="ns2:Opis"/></p>
								<div>
									<xsl:for-each select="//ns2:Clan/ns2:Sadrzaj/ns2:Tekst"> 
										<xsl:value-of select="ns2:Naziv"/> 
									</xsl:for-each>
									<xsl:for-each select="//ns2:Clan/ns2:Sadrzaj/ns2:Stav">
										(<xsl:value-of select="ns2:Redni_broj"/>) &#160; <xsl:value-of select="ns2:Tekst"/>
									</xsl:for-each>
								</div>
					  </xsl:for-each> 
					</xsl:for-each>	
            </body>
          </html>
    
    </xsl:template>
    
    
</xsl:stylesheet>
    