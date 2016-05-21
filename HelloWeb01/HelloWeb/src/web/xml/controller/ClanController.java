package web.xml.controller;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import web.xml.model.Clan;
import web.xml.model.Clanovi;
import web.xml.model.User;
import web.xml.model.Users;

@Controller
@RequestMapping("/clan")
public class ClanController 
{

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Clanovi> getAll() throws IOException, JAXBException {
		
		JAXBContext context = JAXBContext.newInstance(Clanovi.class);
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Clanovi clanovi = (Clanovi) unmarshaller.unmarshal(new File("./data/xml/clanovi.xml"));
		
		return new ResponseEntity<Clanovi>(clanovi, HttpStatus.OK);
		
		
	}
	
}
