package web.xml.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jaxb.from.xsd.Propis;
import web.xml.model.Propisi;
import web.xml.model.User;
import web.xml.service.AmandmanService;
import web.xml.service.PropisService;
import web.xml.service.UserService;

@Controller
@RequestMapping("/amandman")
public class AmandmanController {

	@Autowired
	PropisService propisSer;
	
	@Autowired
	AmandmanService amandmanSer;
	
	@Autowired
	UserService userSer;
	
	@RequestMapping(value = "/novi", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviAmandman(@RequestBody String postPayload, final HttpServletRequest req)
			throws IOException, JAXBException, ServletException, DatatypeConfigurationException {
		
		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(korisnik.getVrsta().equals("gradjanin") || userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		
		amandmanSer.dodajAmandman(postPayload, korisnik);
		
//		Propis noviPropis = propisSer.dodajPropis(postPayload);
//
//		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
//		propisi.getPropisi().add(noviPropis);
//		
//		propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));

		return new ResponseEntity<String>(HttpStatus.OK);

	}

}
