package web.xml.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import jaxb.from.xsd.Propis;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

import jaxb.from.xsd.Clan;
import web.xml.model.Propisi;
import web.xml.model.User;
import web.xml.role.Role.Rola;
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
	public @ResponseBody ResponseEntity<String> getPropis(@PathVariable(value = "id") String id, final HttpServletRequest req)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException, org.apache.xml.security.exceptions.XMLSecurityException, ServletException {

		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){
			for(int i = 0; i < rola.getPermisije().size(); i++){
				if(i != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(i).getNaziv().equals("pregled propisa") || rola.getPermisije().get(i).getNaziv().equals("pretraga propisa")){
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
						
						try
						{
							if(!propisSer.verifySignature(dokument))
							{
								return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
							}
						}
						catch(Exception e)
						{
							return new ResponseEntity<String>(HttpStatus.NOT_IMPLEMENTED);
						}
						
						String resultHtml = propisSer.generateHtmlFromXsl(dokument, new File("data/xml/propis.xsl"));

						return new ResponseEntity<String>(resultHtml, HttpStatus.OK);
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
	
	
	@RequestMapping(value = "/propis/{nazivPropisa}/clan/{clanID}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody ResponseEntity<String> getClan(@PathVariable(value = "nazivPropisa") String nazivPropisa, @PathVariable(value = "clanID") String clanID, final HttpServletRequest req)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException, ServletException {

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
					if(rola.getPermisije().get(q).getNaziv().equals("pregled propisa") || rola.getPermisije().get(q).getNaziv().equals("pretraga propisa")){
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
	
	@RequestMapping(value = "/naziv/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> getPropisNaziv(@PathVariable(value = "id") String id, final HttpServletRequest req)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException, ServletException {

		
		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){	
			
			for(int q = 0; q < rola.getPermisije().size(); q++){
				if(q != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(q).getNaziv().equals("pregled propisa") || rola.getPermisije().get(q).getNaziv().equals("pretraga propisa")){
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
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
				}
			}	
		}else{
			return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<Propis>(HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> getPropisById(@PathVariable(value = "id") String id, final HttpServletRequest req)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException, ServletException {

		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){	
			
			for(int q = 0; q < rola.getPermisije().size(); q++){
				if(q != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(q).getNaziv().equals("pregled propisa") || rola.getPermisije().get(q).getNaziv().equals("pretraga propisa")){
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
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
				}
			}	
		}else{
			return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<Propis>(HttpStatus.NO_CONTENT);
		

	}
	
	@RequestMapping(value = "/odbijen/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> odbijenPropis(@PathVariable(value = "id") String id, final HttpServletRequest req)
			throws IOException, JAXBException, TransformerConfigurationException, ParserConfigurationException,
			SAXException, TransformerFactoryConfigurationError, TransformerException, ServletException {

		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){
			if(userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
				return new ResponseEntity<Propis>(HttpStatus.NOT_ACCEPTABLE);
			}	
			
			for(int q = 0; q < rola.getPermisije().size(); q++){
				if(q != rola.getPermisije().size()-1){
					/*Samo sednica zato sto odbijanju moze da pristupi samo predsednik skupstine, ako bi stavili i brisanje propisa
					 * mogao bi da pristupi i odbornik
					 */
					if(rola.getPermisije().get(q).getNaziv().equals("sednica")){ 
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
						
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
				}
			}	
		}else{
			return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<Propis>(HttpStatus.NO_CONTENT);	
	}
	
	@RequestMapping(value = "/prihvacenUNacelu/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> prihvacenUNacelu(@PathVariable(value = "id") String id, HttpServletRequest req) throws JAXBException, IOException, ServletException {
		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){
			if(userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
				return new ResponseEntity<Propis>(HttpStatus.NOT_ACCEPTABLE);
			}	
			
			for(int q = 0; q < rola.getPermisije().size(); q++){
				if(q != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(q).getNaziv().equals("sednica")){
						BigInteger idPropis = BigInteger.valueOf(Long.parseLong(id));
						Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
						Document dokument = null;
						Propis propis = null;
						for (Propis p : propisi.getPropisi()) {
							if (p.getID().equals(idPropis)) {
								propis = propisSer.unmarshallDocumentPropis(propisSer.findPropisById(p.getNaziv()));
								propis.setStatus("U_NACELU");
								break;
							}
						}
						
						if(propis == null){
							return new ResponseEntity<Propis>(HttpStatus.NOT_FOUND);
						}
						
						for(int i = 0; i < propisi.getPropisi().size(); i++){
							if(propisi.getPropisi().get(i).getID().equals(propis.getID())){
								propisi.getPropisi().remove(i);
								break;
							}
						}
						
						propisi.getPropisi().add(propis);
						propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));
						propisSer.savePropisiXML();
						
						propisSer.marshallPropis(propis, new File("./data/xml/potpisPropis.xml"));
								
						//radi pretrage po sadrzaju i metapodacima, pamtimo neenkrpitovan i nepotpisan propis
						propisSer.saveWithoutEncrypt(new File("data\\xml\\potpisPropis.xml"));
								
								
					//	propisSer.encryptXml(new File("data\\xml\\potpisPropis.xml"), new File("data\\sertifikati\\iasgns.jks"), "iasgns");
						propisSer.signPropis(new File("data\\xml\\potpisPropis.xml"), korisnik.getJksPutanja(), korisnik.getAlias(), korisnik.getAlias(),
								"", korisnik.getAlias());

						propisSer.saveAgain(new File("./data/xml/potpisPropis.xml"), propis.getID());
								
						return new ResponseEntity<Propis>(HttpStatus.OK);
						
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
				}
			}	
		}else{
			return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<Propis>(HttpStatus.NO_CONTENT);	
			
	}
	
	@RequestMapping(value = "/prihvacenUCelini/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Propis> prihvacenUCelini(@PathVariable(value = "id") String id, HttpServletRequest req) throws  JAXBException, ServletException, IOException, DatatypeConfigurationException {
		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){
			if(userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
				return new ResponseEntity<Propis>(HttpStatus.NOT_ACCEPTABLE);
			}	
			
			for(int q = 0; q < rola.getPermisije().size(); q++){
				if(q != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(q).getNaziv().equals("sednica")){
						BigInteger idPropis = BigInteger.valueOf(Long.parseLong(id));
						Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
						Document dokument = null;
						Propis propis = null;

						for (Propis p : propisi.getPropisi()) {
							if (p.getID().equals(idPropis)) {
								propis = propisSer.unmarshallDocumentPropis(propisSer.findPropisById(p.getNaziv()));
								propis.setStatus("U_CELINI");
								
								// Postavljanje datuma prihvaćanja
								GregorianCalendar calender = new GregorianCalendar();
								calender.setTime(new Date());
								
								XMLGregorianCalendar xmlCalender = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
								
								Propis.DatumUsvajanja datumUsvajanjaPropisa = new Propis.DatumUsvajanja();
								datumUsvajanjaPropisa.setProperty(datumUsvajanjaPropisa.getProperty());
								datumUsvajanjaPropisa.setDatatype(datumUsvajanjaPropisa.getDatatype());
								datumUsvajanjaPropisa.setValue(xmlCalender);
								propis.setDatumUsvajanja(datumUsvajanjaPropisa);
							}
						}
								
						if(propis == null){
							return new ResponseEntity<Propis>(HttpStatus.NOT_FOUND);
						}
						
						for(int i = 0; i < propisi.getPropisi().size(); i++){
							if(propisi.getPropisi().get(i).getID().equals(propis.getID())){
								propisi.getPropisi().remove(i);
								break;
							}
						}
						
						propisi.getPropisi().add(propis);
						propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));
						propisSer.savePropisiXML();
								
						propisSer.marshallPropis(propis, new File("./data/xml/potpisPropis.xml"));
								
						//radi pretrage po sadrzaju i metapodacima, pamtimo neenkrpitovan i nepotpisan propis
						propisSer.saveWithoutEncrypt(new File("data\\xml\\potpisPropis.xml"));
								
								
						//propisSer.encryptXml(new File("data\\xml\\potpisPropis.xml"), new File("data\\sertifikati\\iasgns.jks"), "iasgns");
						propisSer.signPropis(new File("data\\xml\\potpisPropis.xml"), korisnik.getJksPutanja(), korisnik.getAlias(), korisnik.getAlias(),
								"", korisnik.getAlias());

						propisSer.saveAgain(new File("./data/xml/potpisPropis.xml"), propis.getID());
						
						return new ResponseEntity<Propis>(HttpStatus.OK);
						
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
				}
			}	
		}else{
			return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<Propis>(HttpStatus.NO_CONTENT);
		
	}

	/**
	 * Pregled svih dostupnih propisa
	 * 
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 * @throws ServletException 
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public @ResponseBody ResponseEntity<Propisi> getAll(final HttpServletRequest req) throws IOException, JAXBException, ServletException {

		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){	
			
			for(int i = 0; i < rola.getPermisije().size(); i++){
				if(i != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(i).getNaziv().equals("pregled propisa") || rola.getPermisije().get(i).getNaziv().equals("pretraga propisa")){
						return new ResponseEntity<Propisi>(propisSer.findAll(), HttpStatus.OK);
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<Propisi>(HttpStatus.UNAUTHORIZED);
				}
			}
			
		}else{
			return new ResponseEntity<Propisi>(HttpStatus.UNAUTHORIZED);
		}
		
		return null;
	}

	/**
	 * Preuzimanje akata tj. propisa koji zadovoljavaju uslov pretrage po sadržaju
	 * 
	 * @param reqBody 
	 * @return
	 * @throws JAXBException
	 * @throws ServletException 
	 */
	@RequestMapping(value = "/pretragaPoSadrzaju", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public @ResponseBody ResponseEntity<Propisi> pretragaAkataPoSadrzaju(@RequestBody String reqBody, final HttpServletRequest req) throws JAXBException, ServletException {	
		String cleanPostPayload = Jsoup.clean(reqBody, Whitelist.basic());
		
		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){	
			for(int q = 0; q < rola.getPermisije().size(); q++){
				if(q != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(q).getNaziv().equals("pregled propisa") || rola.getPermisije().get(q).getNaziv().equals("pretraga propisa")){
						return new ResponseEntity<Propisi>(propisSer.pretraziPoSadrzaju(cleanPostPayload), HttpStatus.OK);	
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<Propisi>(HttpStatus.UNAUTHORIZED);
				}
			}	
		}else{
			return new ResponseEntity<Propisi>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<Propisi>(HttpStatus.NO_CONTENT);
		
	}
	
	/**
	 * Preuzimanje akata tj. propisa koji zadovoljavaju uslove pretrage po metapodacima
	 * 
	 * @param reqBody
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException 
	 * @throws ServletException 
	 */
	@RequestMapping(value = "/pretragaPoMetapodacima", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public @ResponseBody ResponseEntity<Propisi> pretragaAkataPoMetapodacima(@RequestBody String reqBody, final HttpServletRequest req) throws JAXBException, FileNotFoundException, ServletException {
		String cleanPostPayload = Jsoup.clean(reqBody, Whitelist.basic());
		
		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){	
			for(int q = 0; q < rola.getPermisije().size(); q++){
				if(q != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(q).getNaziv().equals("pregled propisa") || rola.getPermisije().get(q).getNaziv().equals("pretraga propisa")){
						return new ResponseEntity<Propisi>(propisSer.pretraziPoMetapodacima(cleanPostPayload), HttpStatus.OK);
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<Propisi>(HttpStatus.UNAUTHORIZED);
				}
			}	
		}else{
			return new ResponseEntity<Propisi>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<Propisi>(HttpStatus.NO_CONTENT);
		
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
					if(rola.getPermisije().get(q).getNaziv().equals("unos propisa")){
						Propis noviPropis = propisSer.dodajPropis(cleanPostPayload);

						Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
						propisi.getPropisi().add(noviPropis);
						
						propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));

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
	
	@RequestMapping(value = "/propisXml", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> noviPropisXml(@RequestBody String postPayload, final HttpServletRequest req)
			throws IOException, JAXBException, ServletException, DatatypeConfigurationException {
		
		String cleanPostPayload = Jsoup.clean(postPayload, Whitelist.basic());
		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
		User korisnik = userSer.getUserFromJWT(req);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(korisnik.getVrsta().equals("gradjanin") || userSer.isValidCertificate(userSer.getCertificateSerialNumber(propisSer.readCertificate(korisnik.getJksPutanja(), korisnik.getAlias())))){
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		
		propisSer.marshallPureXml(cleanPostPayload);
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
					if(rola.getPermisije().get(q).getNaziv().equals("unos propisa")){
						Propisi propisi = propisSer.dodajDeo(cleanPostPayload);

						propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));

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
	public @ResponseBody ResponseEntity<Propis> toPdf(@PathVariable(value = "id") String id, final HttpServletRequest req)
			throws IOException, JAXBException, ServletException, TransformerConfigurationException, SAXException, TransformerException {
		
		User korisnik = userSer.getUserFromJWT(req);
		Rola rola = userSer.getRolaPermisije(korisnik);
		
		//ova metoda ne sme da bude koriscena od strane gradjanina
		//provera da li taj korisnika ima validan sertifikat iz CRL liste.
		if(rola != null){		
			for(int q = 0; q < rola.getPermisije().size(); q++){
				if(q != rola.getPermisije().size()-1){
					if(rola.getPermisije().get(q).getNaziv().equals("pregled propisa") || rola.getPermisije().get(q).getNaziv().equals("pretraga propisa") || rola.getPermisije().get(q).equals("sednica")){
						Propisi propisi = propisSer.findAll();
						for(Propis p : propisi.getPropisi())
						{
							if(p.getID().equals(BigInteger.valueOf(Long.parseLong(id))))
							{
								propisSer.marshallPropis(propisSer.unmarshallDocumentPropis(propisSer.findPropisById(p.getNaziv())), new File("data\\xml\\propisForPdf.xml"));
								propisSer.toPdf(new File("data\\xml\\propisForPdf.xml"), new File("data\\xml\\propis_fo.xsl"));
								return new ResponseEntity<Propis>(p, HttpStatus.OK);
							}
						}
						
						return new ResponseEntity<Propis>(HttpStatus.NO_CONTENT);
						
					}else{
						continue;
					}
				}else{
					return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
				}
			}	
		}else{
			return new ResponseEntity<Propis>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<Propis>(HttpStatus.NO_CONTENT);
	
	}
	
	/**
	 * Vraća pdf dokumenat sa zadataim imenom 
	 * 
	 * @param fileName ime dokumenta bez ekstenzije .pdf
	 * @return
	 */
	@RequestMapping(value = "/file/pdf/{file_name}", method = RequestMethod.GET, produces = "application/pdf")
	public ResponseEntity<byte[]> getPdfFile(@PathVariable("file_name") String fileName) {
		fileName += ".pdf";
		
		FileInputStream fileStream;
		try {
			fileStream = new FileInputStream(new File("data\\pdf\\"+fileName));
			byte[] contents = IOUtils.toByteArray(fileStream);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("application/pdf"));
			headers.setContentDispositionFormData(fileName, fileName);
			
			ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
			
			return response;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
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
		
		
		String cleanPostPayload = Jsoup.clean(postPayload, Whitelist.basic());
		//provera da li postoji JWT, ako postoji, vratice tog korisnika,
		//ako ne postoji korisnik tj. JWT bacice exception
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
					if(rola.getPermisije().get(q).getNaziv().equals("unos propisa")){
						Propisi propisi = propisSer.dodajGlavu(cleanPostPayload);

						propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));

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
					if(rola.getPermisije().get(q).getNaziv().equals("unos propisa")){
						Propisi propisi = propisSer.dodajClan(cleanPostPayload);

						propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));

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
					if(rola.getPermisije().get(q).getNaziv().equals("unos propisa")){
						Propisi propisi = propisSer.dodajStav(cleanPostPayload);

						propisSer.marshall(propisi, new File("data\\xml\\propisi.xml"));

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
	//	propisSer.saveWithoutEncrypt(new File("data\\xml\\potpisPropis.xml"));
		
		//svaki put se kriptuje javnim kljucem istorijskog arhiva, posto se tamo salju propisi
		//propisSer.encryptXml(new File("data\\xml\\potpisPropis.xml"), new File("data\\sertifikati\\iasgns.jks"), "iasgns");
		propisSer.signPropis(new File("data\\xml\\potpisPropis.xml"), korisnik.getJksPutanja(), korisnik.getAlias(), korisnik.getAlias(),
				"", korisnik.getAlias());
		
		

		propisSer.save(new File("data\\xml\\potpisPropis.xml"));

		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	/*public static void main(String[] args) throws JAXBException, FileNotFoundException{
		JAXBContext context = JAXBContext.newInstance(Role.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		// Unmarshalling generi�e objektni model na osnovu XML fajla
		Role role = (Role) unmarshaller.unmarshal(new File("data\\role\\role.xml"));
		
		Rola rola1 = new Rola();
		Rola rola2 = new Rola();
		Rola rola3 = new Rola();
		
		rola1.setNaziv("predsednik skupstine");
		rola2.setNaziv("odbornik");
		rola3.setNaziv("gradjanin");
		
		Permisije permisija1 = new Permisije();
		Permisije permisija2 = new Permisije();
		Permisije permisija3 = new Permisije();
		Permisije permisija4 = new Permisije();
		Permisije permisija5 = new Permisije();
		Permisije permisija6 = new Permisije();
		Permisije permisija7 = new Permisije();
		
		permisija1.setNaziv("unos propisa");
		permisija2.setNaziv("unos amandmana");
		permisija3.setNaziv("unos odbornika");
		permisija4.setNaziv("sednica");
		permisija5.setNaziv("pregled propisa");
		permisija6.setNaziv("pretraga propisa");
		permisija7.setNaziv("brisanje propisa");
		
		rola1.getPermisije().add(permisija1);
		rola1.getPermisije().add(permisija2);
		rola1.getPermisije().add(permisija3);
		rola1.getPermisije().add(permisija4);
		rola1.getPermisije().add(permisija5);
		rola1.getPermisije().add(permisija6);
		rola1.getPermisije().add(permisija7);
	
		rola2.getPermisije().add(permisija1);
		rola2.getPermisije().add(permisija2);
		rola2.getPermisije().add(permisija5);
		rola2.getPermisije().add(permisija6);
		rola2.getPermisije().add(permisija7);
		
		rola3.getPermisije().add(permisija5);
		rola3.getPermisije().add(permisija6);
		
		role.getRola().add(rola1);
		role.getRola().add(rola2);
		role.getRola().add(rola3);
		
		Marshaller marshaller = context.createMarshaller();

		// Pode�avanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Umesto System.out-a, mo�e se koristiti FileOutputStream
		marshaller.marshal(role, new File("data\\role\\role.xml"));
		
		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37", "tim37", "tim37",
				Authentication.valueOf("DIGEST"));
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();

		String docId = "/role.xml";
		String collId = "/korisnici/role";

		InputStreamHandle handle = new InputStreamHandle(new FileInputStream("data\\role\\role.xml"));
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getCollections().add(collId);

		xmlManager.write(docId, metadata, handle);

		client.release();
		
		
	}*/

}
