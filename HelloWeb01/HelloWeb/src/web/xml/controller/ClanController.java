package web.xml.controller;

import static org.apache.xerces.jaxp.JAXPConstants.JAXP_SCHEMA_LANGUAGE;
import static org.apache.xerces.jaxp.JAXPConstants.W3C_XML_SCHEMA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

import jaxb.from.xsd.Clan;
import web.xml.model.Clanovi;
import web.xml.model.Propisi;
import jaxb.from.xsd.Clan.Sadrzaj;
import web.xml.model.User;
import web.xml.model.Users;
import web.xml.service.PropisService;

@Controller
@RequestMapping("/clan")
public class ClanController 
{
	
	@Autowired
	PropisService propisSer;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public @ResponseBody ResponseEntity<String> getPropis(@PathVariable(value="id") String id) throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException, SAXException, TransformerFactoryConfigurationError, TransformerException {
		
		//ovim kreiramo taj propis.xml
		BigInteger idPropis = BigInteger.valueOf(Long.parseLong(id));
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		Document dokument = null;
		
		for(Propis p : propisi.getPropisi())
		{ 
			if(p.getID().equals(idPropis))
			{
				dokument = propisSer.findPropisById(p.getNaziv());
				break;
			}
		}
		
		
		String resultHtml = propisSer.generateHtmlFromXsl(dokument, new File("data/xml/propis.xsl"));
		
		return new ResponseEntity<String>(resultHtml, HttpStatus.OK);
		
	}
	
	/**
	 * Pregled svih dostupnih propisa
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces="application/json; charset=UTF-8")
	public @ResponseBody ResponseEntity<Propisi> getAll() throws IOException, JAXBException {
			
		return new ResponseEntity<Propisi>(propisSer.findAll(), HttpStatus.OK);
	}
	
	/**
	 * Dodavanja novog propisa, celokupnog.
	 * @param postPayload
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@RequestMapping(value = "/noviPropis", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviPropis(@RequestBody String postPayload) throws IOException, JAXBException {
		
		
		Propis noviPropis = propisSer.dodajPropis(postPayload);
	
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		propisi.getPropisi().add(noviPropis);
		
		propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));
	
		return new ResponseEntity<String>(HttpStatus.OK);
		
	}
	
	
	/**
	 * Dodavanje novog dela u okviru trenutnog propisa
	 * @param postPayload
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@RequestMapping(value = "/noviDeo", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviDeo(@RequestBody String postPayload) throws IOException, JAXBException {
			
		Propisi propisi = propisSer.dodajDeo(postPayload);

		propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));
		
		return new ResponseEntity<String>(HttpStatus.OK);
		
	}
	
	/**
	 * Dodavanje glave za izabrani deo.
	 * Ovo za izabrani deo jos nije odradjeno --> TO DO (napraviti da se izabere u koji DEO se zeli ubaciti glava)
	 * @param postPayload
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@RequestMapping(value = "/novaGlava", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> novaGlava(@RequestBody String postPayload) throws IOException, JAXBException {
		
		Propisi propisi = propisSer.dodajGlavu(postPayload);
		
		propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));
		
		return new ResponseEntity<String>(HttpStatus.OK);
		
	}
	
	/**
	 * Dodavanja novog clana za izabranu glavu
	 * TO DO isti kao i za glavu
	 * @param postPayload
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@RequestMapping(value = "/noviClan", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviClan(@RequestBody String postPayload) throws IOException, JAXBException{
		
		Propisi propisi = propisSer.dodajClan(postPayload);
		
		propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	
	/**
	 * Dodavanje novog stava za izabarni clan
	 * TO DO isti kao i za clan
	 * @param postPayload
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@RequestMapping(value = "/noviStav", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviStav(@RequestBody String postPayload) throws IOException, JAXBException{
		
		Propisi propisi = propisSer.dodajStav(postPayload);
		
		propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> saveToDatabase() throws IOException, JAXBException{
		
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		//jos uvek ne potpisan propis
		
		propisSer.marshallPropis(propisi.getPropisi().get(propisi.getPropisi().size()-1), new File("data\\xml\\potpisPropis.xml"));
		
		propisSer.signPropis(new File("data\\xml\\potpisPropis.xml"), "data\\sertifikati\\qq.jks", "qq", "qq", "data\\sertifikati\\qq.cer", "qq");
		
		propisSer.save(new File("data\\xml\\potpisPropis.xml"));
		
	//	propisSer.save(new File("./data/xml/propisi.xml"));
		
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	
}
