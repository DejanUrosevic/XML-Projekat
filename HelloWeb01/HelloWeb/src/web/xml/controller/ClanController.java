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
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Propis> getAll() throws IOException, JAXBException {
		
		JAXBContext context = JAXBContext.newInstance(jaxb.from.xsd.Clan.class);
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		jaxb.from.xsd.Clan clanovi = (jaxb.from.xsd.Clan) unmarshaller.unmarshal(new File("./data/xml/clan.xml"));
		
		
		/*
		//ovako dobijamo vrednosti iz ugnjezdnenih tagova, ne moze jednostavnije
		for( Serializable s: clanovi.getSadrzaj().getContent() ){
	        if( s instanceof String ){
	        }
	        else
	        {
	                String loremIpsum = (String)((JAXBElement)s).getValue();
	              //  return new ResponseEntity<String>(loremIpsum, HttpStatus.OK); 
	        
	        }
		}
		*/
		JAXBContext context2 = JAXBContext.newInstance(jaxb.from.xsd.Propis.class);
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshalle2r = context2.createUnmarshaller(); 
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		jaxb.from.xsd.Propis propis = (jaxb.from.xsd.Propis) unmarshalle2r.unmarshal(new File("./data/xml/probaPropis.xml"));
		
		return new ResponseEntity<Propis>(propis, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/noviPropis", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviPropis(@RequestBody String postPayload) throws IOException, JAXBException {
		
		
		Propis noviPropis = propisSer.dodajPropis(postPayload);
		
		
		JAXBContext context = JAXBContext.newInstance(Propis.class);
		
		Marshaller marshaller = context.createMarshaller();
		
		// Podešavanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Umesto System.out-a, može se koristiti FileOutputStream
		marshaller.marshal(noviPropis, new File("data\\xml\\probaPropis.xml"));

		
		return null;
		
	}
	
	@RequestMapping(value = "/noviDeo", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviDeo(@RequestBody String postPayload) throws IOException, JAXBException {
		
		Propis propis = propisSer.dodajDeo(postPayload);
		
		JAXBContext context = JAXBContext.newInstance(Propis.class);
		
		Marshaller marshaller = context.createMarshaller();
		
		// Podešavanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Umesto System.out-a, može se koristiti FileOutputStream
		marshaller.marshal(propis, new File("data\\xml\\probaPropis.xml"));

		return null;
		
	}
	
	
	@RequestMapping(value = "/novaGlava", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> novaGlava(@RequestBody String postPayload) throws IOException, JAXBException {
		
		Propis propis = propisSer.dodajGlavu(postPayload);
		
		
		JAXBContext context = JAXBContext.newInstance(Propis.class);
		Marshaller marshaller = context.createMarshaller();
		
		// Podešavanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Umesto System.out-a, može se koristiti FileOutputStream
		marshaller.marshal(propis, new File("data\\xml\\probaPropis.xml"));

		return null;
		
	}
}
