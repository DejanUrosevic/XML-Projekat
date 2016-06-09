<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0"
	xmlns:pp="http://www.parlament.gov.rs/propis" xmlns:ns2="http://www.parlament.gov.rs/clan">

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
						Grad Novi Sad
					</fo:block>
					<fo:block padding-left="30px" color="black" font-size="80%">
						Datum:
						<xsl:value-of select="pp:Propis/@Datum" />
					</fo:block>
					<fo:block text-align="center" color="black"
						padding-bottom="10px" font-size="200%" text-decoration="underline">
						<fo:inline>
							<xsl:value-of select="pp:Propis/pp:Naziv" />
						</fo:inline>
					</fo:block>
					<xsl:for-each select="pp:Propis/pp:Deo">
						<fo:block text-align="center" color="black"
							padding-bottom="10px" font-size="180%">
							<xsl:value-of select="pp:Naziv" />
						</fo:block>
						<xsl:for-each select="pp:Glava">
							<fo:block text-align="center" color="black"
								padding-bottom="10px" font-size="150%">
								<xsl:value-of select="pp:Naziv" />
							</fo:block>
							<xsl:for-each select="ns2:Clan">
								<fo:block text-align="center" color="black"
									padding-bottom="10px" font-size="110%">
									<xsl:value-of select="ns2:Naziv" />
								</fo:block>
								<fo:block text-align="center" color="black"
									padding-bottom="10px" font-size="70%">
									<xsl:value-of select="ns2:Opis" />
								</fo:block>
								<fo:block text-align="center" color="black"
									padding-bottom="10px" font-size="70%">
									<xsl:for-each select="ns2:Sadrzaj/ns2:Tekst/ns2:text">

										<xsl:variable name="clanID" select="../ns2:IDClana" />
										<xsl:variable name="clanNaziv" select="../ns2:NazivClana" />
										<xsl:variable name="propisNaziv" select="../ns2:NazivPropisa" />
										<xsl:variable name="propisID" select="../ns2:IDPropisa" />
										<xsl:variable name="url"
											select="concat('https://localhost:8443/HelloWeb/pages/index.html#/propis/', $propisNaziv,'/clan/',$clanID)" />
										<xsl:variable name="urlPropis"
											select="concat('https://localhost:8443/HelloWeb/pages/index.html#/propisi/propis/', $propisID)" />

										<xsl:choose>
											<xsl:when test="current() = $clanNaziv">
												<fo:basic-link color="blue" text-decoration="underline">
													<xsl:attribute name="external-destination"><xsl:value-of
														select="$url" /></xsl:attribute>
													<xsl:value-of select="$clanNaziv" />
												</fo:basic-link>
											</xsl:when>
											<xsl:when test="current() = $propisNaziv">
												<fo:basic-link color="blue" text-decoration="underline">
													<xsl:attribute name="external-destination"><xsl:value-of
														select="$urlPropis" /></xsl:attribute>
													<xsl:value-of select="$propisNaziv" />
												</fo:basic-link>
											</xsl:when>
											<xsl:otherwise>
												
												&#160; <xsl:value-of select="current()" /> &#160;
												
											</xsl:otherwise>
										</xsl:choose>

									</xsl:for-each>
								</fo:block>

								<xsl:for-each select="ns2:Sadrzaj/ns2:Stav">
									<fo:block text-align="center" color="black"
										padding-bottom="10px" font-size="70%">
										(
										<xsl:value-of select="ns2:Redni_broj" />
										) &#160;
										<xsl:for-each select="ns2:Tekst">
											
												<xsl:variable name="clanID" select="../ns2:IDClana" />
												<xsl:variable name="clanNaziv" select="../ns2:NazivClana" />
												<xsl:variable name="propisNaziv" select="../ns2:NazivPropisa" />
												<xsl:variable name="propisID" select="../ns2:IDPropisa" />
												<xsl:variable name="url"
													select="concat('https://localhost:8443/HelloWeb/pages/index.html#/propis/', $propisNaziv,'/clan/',$clanID)" />
												<xsl:variable name="urlPropis"
													select="concat('https://localhost:8443/HelloWeb/pages/index.html#/propisi/propis/', $propisID)" />
		
												<xsl:choose>
													<xsl:when test="current() = $clanNaziv">
														<fo:basic-link color="blue" text-decoration="underline">
															<xsl:attribute name="external-destination">
																<xsl:value-of select="$url" />
															</xsl:attribute>
															<xsl:value-of select="$clanNaziv" />
														</fo:basic-link>
													</xsl:when>
													<xsl:when test="current() = $propisNaziv">
														<fo:basic-link color="blue" text-decoration="underline">
															<xsl:attribute name="external-destination"><xsl:value-of
																select="$urlPropis" /></xsl:attribute>
															<xsl:value-of select="$propisNaziv" />
														</fo:basic-link>
													</xsl:when>
													<xsl:otherwise>
													 &#160;	<xsl:value-of select="current()" /> &#160;
													</xsl:otherwise>
												</xsl:choose>
										
										</xsl:for-each>
									</fo:block>
								</xsl:for-each>
							</xsl:for-each>
						</xsl:for-each>
						<xsl:for-each select="ns2:Clan">
							<fo:block text-align="center" color="black"
								padding-bottom="10px" font-size="110%">
								<xsl:value-of select="ns2:Naziv" />
							</fo:block>
							<fo:block text-align="center" color="black"
								padding-bottom="10px" font-size="70%">
								<xsl:value-of select="ns2:Opis" />
							</fo:block>
							<fo:block text-align="center" color="black"
								padding-bottom="10px" font-size="70%">
								<xsl:for-each select="ns2:Sadrzaj/ns2:Tekst/ns2:text">

									<xsl:variable name="clanID" select="../ns2:IDClana" />
									<xsl:variable name="clanNaziv" select="../ns2:NazivClana" />
									<xsl:variable name="propisNaziv" select="../ns2:NazivPropisa" />
									<xsl:variable name="propisID" select="../ns2:IDPropisa" />
									<xsl:variable name="url"
										select="concat('https://localhost:8443/HelloWeb/pages/index.html#/propis/', $propisNaziv,'/clan/',$clanID)" />
									<xsl:variable name="urlPropis"
										select="concat('https://localhost:8443/HelloWeb/pages/index.html#/propisi/propis/', $propisID)" />

									<xsl:choose>
										<xsl:when test="current() = $clanNaziv">
											<fo:basic-link color="blue" text-decoration="underline">
												<xsl:attribute name="external-destination"><xsl:value-of
													select="$url" /></xsl:attribute>
												<xsl:value-of select="$clanNaziv" />
											</fo:basic-link>
										</xsl:when>
										<xsl:when test="current() = $propisNaziv">
											<fo:basic-link color="blue" text-decoration="underline">
												<xsl:attribute name="external-destination"><xsl:value-of
													select="$urlPropis" /></xsl:attribute>
												<xsl:value-of select="$propisNaziv" />
											</fo:basic-link>
										</xsl:when>
										<xsl:otherwise>
											
										&#160;	<xsl:value-of select="current()" /> &#160;
											
										</xsl:otherwise>
									</xsl:choose>

								</xsl:for-each>
							</fo:block>

							<xsl:for-each select="ns2:Sadrzaj/ns2:Stav">
								<fo:block text-align="center" color="black"
									padding-bottom="10px" font-size="70%">
									(
									<xsl:value-of select="ns2:Redni_broj" />
									) &#160;					
									<xsl:for-each select="ns2:Tekst">
										<xsl:variable name="clanID" select="../ns2:IDClana" />
										<xsl:variable name="clanNaziv" select="../ns2:NazivClana" />
										<xsl:variable name="propisNaziv" select="../ns2:NazivPropisa" />
										<xsl:variable name="propisID" select="../ns2:IDPropisa" />
										<xsl:variable name="url"
											select="concat('https://localhost:8443/HelloWeb/pages/index.html#/propis/', $propisNaziv,'/clan/',$clanID)" />
										<xsl:variable name="urlPropis"
											select="concat('https://localhost:8443/HelloWeb/pages/index.html#/propisi/propis/', $propisID)" />
	
										<xsl:choose>
											<xsl:when test="current() = $clanNaziv">
												<fo:basic-link color="blue" text-decoration="underline">
													<xsl:attribute name="external-destination"><xsl:value-of
														select="$url" /></xsl:attribute>
													<xsl:value-of select="$clanNaziv" />
												</fo:basic-link>
											</xsl:when>
											<xsl:when test="current() = $propisNaziv">
												<fo:basic-link color="blue" text-decoration="underline">
													<xsl:attribute name="external-destination"><xsl:value-of
														select="$urlPropis" /></xsl:attribute>
													<xsl:value-of select="$propisNaziv" />
												</fo:basic-link>
											</xsl:when>
											<xsl:otherwise>
												
											&#160;	<xsl:value-of select="current()" /> &#160;
												
											</xsl:otherwise>
										</xsl:choose>
									</xsl:for-each>
								</fo:block>
							</xsl:for-each>
						</xsl:for-each>
					</xsl:for-each>
				</fo:flow>
			</fo:page-sequence>

		</fo:root>

	</xsl:template>
</xsl:stylesheet>