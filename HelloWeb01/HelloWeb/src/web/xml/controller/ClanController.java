package web.xml.controller;

import static org.apache.xerces.jaxp.JAXPConstants.JAXP_SCHEMA_LANGUAGE;
import static org.apache.xerces.jaxp.JAXPConstants.W3C_XML_SCHEMA;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import jaxb.from.xsd.Clan.Sadrzaj.Stav;
import jaxb.from.xsd.Propis;
import jaxb.from.xsd.Propis.Deo;
import jaxb.from.xsd.Propis.Deo.Glava;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

import jaxb.from.xsd.Clan;
import web.xml.crl.Sertifikati;
import web.xml.model.Clanovi;
import web.xml.model.Propisi;
import jaxb.from.xsd.Clan.Sadrzaj;
import web.xml.model.User;
import web.xml.model.Users;
import web.xml.service.PropisService;
import web.xml.service.UserService;

@Controller
@RequestMapping("/clan")
public class ClanController {

	@Autowired
	PropisService propisSer;
	
	@Autowired
	UserService userSer;


	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody ResponseEntity<String> getPropis(@PathVariable(value = "id") String id)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException {

		// ovim kreiramo taj propis.xml
		BigInteger idPropis = BigInteger.valueOf(Long.parseLong(id));
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		Document dokument = null;

		for (Propis p : propisi.getPropisi()) {
			if (p.getID().equals(idPropis)) {
				dokument = propisSer.findPropisById(p.getNaziv());
				break;
			}
		}

		String resultHtml = propisSer.generateHtmlFromXsl(dokument, new File("data/xml/propis.xsl"));

		return new ResponseEntity<String>(resultHtml, HttpStatus.OK);

	}
	
	
	@RequestMapping(value = "/propis/{nazivPropisa}/clan/{clanID}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody ResponseEntity<String> getClan(@PathVariable(value = "nazivPropisa") String nazivPropisa, @PathVariable(value = "clanID") String clanID)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException {

		JAXBContext context = JAXBContext.newInstance(Clan.class);

		Marshaller marshaller = context.createMarshaller();

		// Pode�avanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml-stylesheet type='text/xsl' href='clan.xsl' ?>");

		
		
		BigInteger idClana = BigInteger.valueOf(Long.parseLong(clanID));
		Document propis = propisSer.findPropisById(nazivPropisa);
		
		Propis propis2 = propisSer.unmarshallDocumentPropis(propis);
		
		for(int i=0; i<propis2.getDeo().size(); i++)
		{
			for(int j=0; j<propis2.getDeo().get(i).getClan().size(); j++)
			{
				if(propis2.getDeo().get(i).getClan().get(j).getID().equals(idClana))
				{
					marshaller.marshal(propis2.getDeo().get(i).getClan().get(j), new File("data\\xml\\clan.xml"));
					String resultHtml = propisSer.generateHtmlFromXsl(propisSer.loadDocument("data\\xml\\clan.xml"), new File("data/xml/clan.xsl"));
					return new ResponseEntity<String>(resultHtml, HttpStatus.OK);
				}
				
			}
			for(int a=0; a<propis2.getDeo().get(i).getGlava().size(); a++)
			{
				for(int b=0; b<propis2.getDeo().get(i).getGlava().get(a).getClan().size(); b++)
				{
					if(propis2.getDeo().get(i).getGlava().get(a).getClan().get(b).getID().equals(idClana))
					{
						marshaller.marshal(propis2.getDeo().get(i).getGlava().get(a).getClan().get(b), new File("data\\xml\\clan.xml"));
						String resultHtml = propisSer.generateHtmlFromXsl(propisSer.loadDocument("data\\xml\\clan.xml"), new File("data/xml/clan.xsl"));
						return new ResponseEntity<String>(resultHtml, HttpStatus.OK);
					}
				}
			}
		}
		

		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

	}
	
	@RequestMapping(value = "/naziv/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> getPropisNaziv(@PathVariable(value = "id") String id)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException {

		// ovim kreiramo taj propis.xml
		BigInteger idPropis = BigInteger.valueOf(Long.parseLong(id));
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		Document dokument = null;

		for (Propis p : propisi.getPropisi()) {
			if (p.getID().equals(idPropis)) {
				return new ResponseEntity<Propis>(p, HttpStatus.OK);
			}
		}

		return new ResponseEntity<Propis>(HttpStatus.NOT_FOUND);

	}
	
	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> getPropisById(@PathVariable(value = "id") String id)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException {

		// ovim kreiramo taj propis.xml
		BigInteger idPropis = BigInteger.valueOf(Long.parseLong(id));
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		Document dokument = null;

		for (Propis p : propisi.getPropisi()) {
			if (p.getID().equals(idPropis)) {
				dokument = propisSer.findPropisById(p.getNaziv());
				break;
			}
		}
	

		return new ResponseEntity<Propis>(propisSer.unmarshallDocumentPropis(dokument), HttpStatus.OK);

	}
	
	@RequestMapping(value = "/odbijen/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> odbijenPropis(@PathVariable(value = "id") String id)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException {

		// ovim kreiramo taj propis.xml
		BigInteger idPropis = BigInteger.valueOf(Long.parseLong(id));
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		Document dokument = null;

		for (Propis p : propisi.getPropisi()) {
			if (p.getID().equals(idPropis)) {
				propisi.getPropisi().remove(p);
				propisSer.removePropis(p.getNaziv());
				propisSer.marshall(propisi, new File("./data/xml/propisi.xml"));
				propisSer.savePropisiXML();
				return new ResponseEntity<Propis>(HttpStatus.OK);
			}
		}
		

		return new ResponseEntity<Propis>(HttpStatus.NOT_FOUND);

	}
	
	@RequestMapping(value = "/prihvacenUNacelu/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> prihvacenUNacelu(@PathVariable(value = "id") String id, HttpServletRequest req) throws JAXBException, IOException, ServletException {
		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(korisnik.getVrsta().equals("gradjanin") || korisnik.getVrsta().equals("odbornik") || userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
			return new ResponseEntity<Propis>(HttpStatus.NOT_ACCEPTABLE);
		}		
		
		
		
		BigInteger idPropis = BigInteger.valueOf(Long.parseLong(id));
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		Document dokument = null;

		for (Propis p : propisi.getPropisi()) {
			if (p.getID().equals(idPropis)) {
				Propis propis = propisSer.unmarshallDocumentPropis(propisSer.findPropisById(p.getNaziv()));
				propis.setStatus("U_NACELU");
				
				/*
				if (propis.getAmandman().size() == 0) {
					propis.setStatus("U_CELINI");
				}
				*/
				
				propisSer.marshallPropis(propis, new File("./data/xml/potpisPropis.xml"));
				
				//radi pretrage po sadrzaju i metapodacima, pamtimo neenkrpitovan i nepotpisan propis
				propisSer.saveWithoutEncrypt(new File("data\\xml\\potpisPropis.xml"));
				
				
				propisSer.encryptXml(new File("data\\xml\\potpisPropis.xml"), new File("data\\sertifikati\\iasgns.jks"), "iasgns");
				propisSer.signPropis(new File("data\\xml\\potpisPropis.xml"), korisnik.getJksPutanja(), korisnik.getAlias(), korisnik.getAlias(),
						"", korisnik.getAlias());

				propisSer.save(new File("./data/xml/potpisPropis.xml"));
				
				return new ResponseEntity<Propis>(HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<Propis>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/prihvacenUCelini/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> prihvacenUCelini(@PathVariable(value = "id") String id, HttpServletRequest req) throws  JAXBException, ServletException, IOException {
		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
				
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(korisnik.getVrsta().equals("gradjanin") || korisnik.getVrsta().equals("odbornik") || userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
			return new ResponseEntity<Propis>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		
		
		BigInteger idPropis = BigInteger.valueOf(Long.parseLong(id));
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		Document dokument = null;
		
		for (Propis p : propisi.getPropisi()) {
			if (p.getID().equals(idPropis)) {
				Propis propis = propisSer.unmarshallDocumentPropis(propisSer.findPropisById(p.getNaziv()));
				propis.setStatus("U_CELINI");
				
				////////////////////////////
				
				propisSer.marshallPropis(propis, new File("./data/xml/potpisPropis.xml"));
				
				//radi pretrage po sadrzaju i metapodacima, pamtimo neenkrpitovan i nepotpisan propis
				propisSer.saveWithoutEncrypt(new File("data\\xml\\potpisPropis.xml"));
				
				
				propisSer.encryptXml(new File("data\\xml\\potpisPropis.xml"), new File("data\\sertifikati\\iasgns.jks"), "iasgns");
				propisSer.signPropis(new File("data\\xml\\potpisPropis.xml"), korisnik.getJksPutanja(), korisnik.getAlias(), korisnik.getAlias(),
						"", korisnik.getAlias());

				propisSer.save(new File("./data/xml/potpisPropis.xml"));
				
				return new ResponseEntity<Propis>(HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<Propis>(HttpStatus.NOT_FOUND);
	}

	/**
	 * Pregled svih dostupnih propisa
	 * 
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public @ResponseBody ResponseEntity<Propisi> getAll() throws IOException, JAXBException {

		return new ResponseEntity<Propisi>(propisSer.findAll(), HttpStatus.OK);
	}

	/**
	 * Preuzimanje akata tj. propisa koji zadovoljavaju uslov pretrage
	 * 
	 * @param requestBody 
	 * @return
	 * @throws JAXBException
	 */
	@RequestMapping(value = "/pretragaPropisa", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public @ResponseBody ResponseEntity<Propisi> pretragaPropisa(@RequestBody String reqBody) throws JAXBException {
		return new ResponseEntity<Propisi>(propisSer.pretrazi(reqBody), HttpStatus.OK);
	}
	
	/**
	 * Dodavanja novog propisa, celokupnog.
	 * 
	 * @param postPayload
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 * @throws ServletException 
	 * @throws DatatypeConfigurationException 
	 */
	@RequestMapping(value = "/noviPropis", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviPropis(@RequestBody String postPayload, final HttpServletRequest req)
			throws IOException, JAXBException, ServletException, DatatypeConfigurationException {
		
		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(korisnik.getVrsta().equals("gradjanin") || userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		Propis noviPropis = propisSer.dodajPropis(postPayload);

		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		propisi.getPropisi().add(noviPropis);
		
		propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));

		return new ResponseEntity<String>(HttpStatus.OK);

	}
	
	@RequestMapping(value = "/propisXml", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviPropisXml(@RequestBody String postPayload, final HttpServletRequest req)
			throws IOException, JAXBException, ServletException, DatatypeConfigurationException {
		
		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(korisnik.getVrsta().equals("gradjanin") || userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		
		propisSer.marshallPureXml(postPayload);
		propisSer.savePureXml(new File("data\\xml\\propisXml.xml"));

		return new ResponseEntity<String>(HttpStatus.OK);

	}

	/**
	 * Dodavanje novog dela u okviru trenutnog propisa
	 * 
	 * @param postPayload
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 * @throws ServletException 
	 */
	@RequestMapping(value = "/noviDeo", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviDeo(@RequestBody String postPayload, final HttpServletRequest req)
			throws IOException, JAXBException, ServletException {

		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
				
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(korisnik.getVrsta().equals("gradjanin") || userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		Propisi propisi = propisSer.dodajDeo(postPayload);

		propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));

		return new ResponseEntity<String>(HttpStatus.OK);

	}

	/**
	 * Dodavanje glave za izabrani deo. Ovo za izabrani deo jos nije odradjeno
	 * --> TO DO (napraviti da se izabere u koji DEO se zeli ubaciti glava)
	 * 
	 * @param postPayload
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 * @throws ServletException 
	 */
	@RequestMapping(value = "/novaGlava", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> novaGlava(@RequestBody String postPayload, final HttpServletRequest req)
			throws IOException, JAXBException, ServletException {
		
		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
						
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(korisnik.getVrsta().equals("gradjanin") || userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}

		Propisi propisi = propisSer.dodajGlavu(postPayload);

		propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));

		return new ResponseEntity<String>(HttpStatus.OK);

	}

	/**
	 * Dodavanja novog clana za izabranu glavu TO DO isti kao i za glavu
	 * 
	 * @param postPayload
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 * @throws ServletException 
	 */
	@RequestMapping(value = "/noviClan", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviClan(@RequestBody String postPayload, final HttpServletRequest req)
			throws IOException, JAXBException, ServletException {

		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
								
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(korisnik.getVrsta().equals("gradjanin") || userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		Propisi propisi = propisSer.dodajClan(postPayload);

		propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	/**
	 * Dodavanje novog stava za izabarni clan TO DO isti kao i za clan
	 * 
	 * @param postPayload
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 * @throws ServletException 
	 */
	@RequestMapping(value = "/noviStav", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviStav(@RequestBody String postPayload, final HttpServletRequest req)
			throws IOException, JAXBException, ServletException {
		
		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
								
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(korisnik.getVrsta().equals("gradjanin") || userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}

		Propisi propisi = propisSer.dodajStav(postPayload);

		propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/save", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> saveToDatabase(final HttpServletRequest req) throws IOException, JAXBException, ServletException {
		
		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(korisnik.getVrsta().equals("gradjanin") || userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		
		// jos uvek ne potpisan propis
		propisSer.marshallPropis(propisi.getPropisi().get(propisi.getPropisi().size() - 1),
				new File("data\\xml\\potpisPropis.xml"));
		
		//radi pretrage po sadrzaju i metapodacima, pamtimo neenkrpitovan i nepotpisan propis
		propisSer.saveWithoutEncrypt(new File("data\\xml\\potpisPropis.xml"));
		
		
	//	propisSer.encryptXml(new File("data\\xml\\potpisPropis.xml"), new File("data\\sertifikati\\iasgns.jks"), "iasgns");
		propisSer.signPropis(new File("data\\xml\\potpisPropis.xml"), korisnik.getJksPutanja(), korisnik.getAlias(), korisnik.getAlias(),
				"", korisnik.getAlias());

		//svaki put se kriptuje javnim kljucem istorijskog arhiva, posto se tamo salju propisi
		

		propisSer.save(new File("data\\xml\\potpisPropis.xml"));

		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	/*public static void main(String[] args) throws JAXBException, FileNotFoundException{
		JAXBContext context = JAXBContext.newInstance(Sertifikati.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		// Unmarshalling generi�e objektni model na osnovu XML fajla
		
		
		Sertifikati serti = (Sertifikati) unmarshaller.unmarshal(new File("data\\sertifikati\\crl.xml"));
		Certificate cert = null;
		try {
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(new File("data\\sertifikati\\odbornik1.jks")));

			ks.load(in, "odbornik1".toCharArray());
			if (ks.isKeyEntry("odbornik1")) {
				cert = ks.getCertificate("odbornik1");
	
			} else{}
			
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		X509Certificate sertif = (X509Certificate) cert;
		serti.getSerijskiBroj().add(sertif.getSerialNumber().toString(16));
		

		Marshaller marshaller = context.createMarshaller();

		// Pode�avanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		// Umesto System.out-a, mo�e se koristiti FileOutputStream
		marshaller.marshal(serti, new File("data\\sertifikati\\crl.xml"));
		
		
	}*/

}
