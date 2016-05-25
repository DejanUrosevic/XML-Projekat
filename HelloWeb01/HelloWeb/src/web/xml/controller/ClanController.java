package web.xml.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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

import jaxb.from.xsd.Clan;
import web.xml.model.Clanovi;
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
	/*
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Clanovi> getAll() throws IOException, JAXBException {
		
		JAXBContext context = JAXBContext.newInstance(Clanovi.class);
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Clanovi clanovi = (Clanovi) unmarshaller.unmarshal(new File("./data/xml/clanovi.xml"));
		
		return new ResponseEntity<Clanovi>(clanovi, HttpStatus.OK);
		
		
	}
	*/
	
	/**
	 * Pregled svih dostupnih propisa
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces="application/json; charset=UTF-8")
	public @ResponseBody ResponseEntity<Propis> getAll() throws IOException, JAXBException {
			
		return new ResponseEntity<Propis>(propisSer.unmarshall(new File("./data/xml/probaPropis.xml")), HttpStatus.OK);
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
		
		propisSer.marshall(noviPropis, new File("data\\xml\\probaPropis.xml"));
	
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
		
		Propis propis = propisSer.dodajDeo(postPayload);
		
		propisSer.marshall(propis, new File("data\\xml\\probaPropis.xml"));
		
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
		
		Propis propis = propisSer.dodajGlavu(postPayload);
		
		
		propisSer.marshall(propis, new File("data\\xml\\probaPropis.xml"));
		
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
		
		Propis propis = propisSer.dodajClan(postPayload);
		
		propisSer.marshall(propis, new File("data\\xml\\probaPropis.xml"));
		
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
		
		Propis propis = propisSer.dodajStav(postPayload);
		
		propisSer.marshall(propis, new File("data\\xml\\probaPropis.xml"));
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
