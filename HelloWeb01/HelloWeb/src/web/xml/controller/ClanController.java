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
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces="application/json; charset=UTF-8")
	public @ResponseBody ResponseEntity<Propis> getPropis(@PathVariable(value="id") String id) throws IOException, JAXBException {
			
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		
		BigInteger idPropisa = BigInteger.valueOf(Long.parseLong(id));
		
		for(Propis p : propisi.getPropisi()){
			if(idPropisa.equals(p.getID())){
				return new ResponseEntity<Propis>(p, HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<Propis>(HttpStatus.NOT_FOUND);
	}
	
	/**
	 * Pregled svih dostupnih propisa
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces="application/json; charset=UTF-8")
	public @ResponseBody ResponseEntity<Propisi> getAll() throws IOException, JAXBException {
			
		return new ResponseEntity<Propisi>(propisSer.unmarshall(new File("./data/xml/propisi.xml")), HttpStatus.OK);
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
}
