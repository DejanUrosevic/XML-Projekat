package web.xml.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
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

import jaxb.from.xsd.Amandman;
import jaxb.from.xsd.Propis;
import web.xml.model.Propisi;
import web.xml.model.User;
import web.xml.role.Role.Rola;
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
		
		String cleanPostPayload = Jsoup.clean(postPayload, Whitelist.basic());
		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){
			if(userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
				return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
			}	
			
			for(int q = 0; q < rola.getPermisije().size(); q++){
				if(q != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(q).getNaziv().equals("unos amandmana")){
						amandmanSer.dodajAmandman(cleanPostPayload, korisnik);
						
						return new ResponseEntity<String>(HttpStatus.OK);
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
				}
			}	
		}else{
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody ResponseEntity<String> getAmandman(@PathVariable(value="id") String id)
			throws IOException, JAXBException, ServletException, DatatypeConfigurationException, TransformerConfigurationException, ParserConfigurationException, SAXException, TransformerFactoryConfigurationError, TransformerException {
	
		Document dokument = amandmanSer.findAmandmanById(id);

		String resultHtml = propisSer.generateHtmlFromXsl(dokument, new File("data/xml/amandman.xsl"));
		return new ResponseEntity<String>(resultHtml, HttpStatus.OK);

	}
	
	@RequestMapping(value = "/potvrda", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> amandmanPotvrda(@RequestBody String postPayload, final HttpServletRequest req)
			throws IOException, JAXBException, ServletException, DatatypeConfigurationException, TransformerConfigurationException, ParserConfigurationException, SAXException, TransformerFactoryConfigurationError, TransformerException {
		
		String cleanPostPayload = Jsoup.clean(postPayload, Whitelist.basic());
		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){
			if(userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
				return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
			}	
			
			for(int q = 0; q < rola.getPermisije().size(); q++){
				if(q != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(q).getNaziv().equals("sednica")){
						amandmanSer.primeniAmandman(cleanPostPayload);
						
						propisSer.saveWithoutEncrypt(new File("data\\xml\\potpisPropis.xml"));
						//propisSer.encryptXml(new File("data\\xml\\potpisPropis.xml"), new File("data\\sertifikati\\iasgns.jks"), "iasgns");
						propisSer.signPropis(new File("data\\xml\\potpisPropis.xml"), korisnik.getJksPutanja(), korisnik.getAlias(), korisnik.getAlias(),
								"", korisnik.getAlias());
						propisSer.save(new File("data\\xml\\potpisPropis.xml"));
												
						return new ResponseEntity<String>(HttpStatus.OK);
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
				}
			}	
		}else{
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		
		

	}
	
	@RequestMapping(value = "/toPdf/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Amandman> toPdf(@PathVariable(value = "id") String id, final HttpServletRequest req)
			throws IOException, JAXBException, ServletException, TransformerConfigurationException, SAXException, TransformerException {
		
			try {
				amandmanSer.marshallAmandman(amandmanSer.unmarshallDocument(amandmanSer.findAmandmanById(id)), new File("data\\xml\\AmandmanForPdf.xml"));
				amandmanSer.toPdf(new File("data\\xml\\AmandmanForPdf.xml"), new File("data\\xml\\amandman_fo.xsl"));
				return new ResponseEntity<Amandman>(HttpStatus.OK);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new ResponseEntity<Amandman>(HttpStatus.NO_CONTENT);
			}	
	}
	
	@RequestMapping(value = "/propis/{id2}/delete/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> delete(@PathVariable(value = "id") String id, @PathVariable(value = "id2") String id2, final HttpServletRequest req) throws ServletException, JAXBException{
		
		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){
			if(userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
				return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
			}	
			
			for(int q = 0; q < rola.getPermisije().size(); q++){
				if(q != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(q).getNaziv().equals("sednica")){
						try {
							amandmanSer.removeAmandman(id);
							Propisi propisi = propisSer.unmarshall(new File("data\\xml\\propisi.xml"));
							Propis propis = null;
							
							for(Propis p : propisi.getPropisi()){
								if(p.getID().equals(BigInteger.valueOf(Long.parseLong(id2)))){
									propis = p;
									propisi.getPropisi().remove(p);
									for(int i = 0; i < p.getAmandman().size(); i++){
										if(propis.getAmandman().get(i).getID().equals(BigInteger.valueOf(Long.parseLong(id)))){
											propis.getAmandman().remove(i);
											break;
										}
									}
									break;
								}
							}
							
							propisi.getPropisi().add(propis);
							propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));
							propisSer.savePropisiXML();
							
							propisSer.marshallPropis(propis, new File("data\\xml\\potpisPropis.xml"));
							propisSer.signPropis(new File("data\\xml\\potpisPropis.xml"), korisnik.getJksPutanja(), korisnik.getAlias(), korisnik.getAlias(),
									"", korisnik.getAlias());
							propisSer.saveAgain(new File("data\\xml\\potpisPropis.xml"), propis.getID());
							
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
						}
						
						return new ResponseEntity<String>(HttpStatus.OK);
												
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
				}
			}	
		}else{
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
			
	}

}
