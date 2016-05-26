package web.xml.service.memory;

import static org.apache.xerces.jaxp.JAXPConstants.JAXP_SCHEMA_LANGUAGE;
import static org.apache.xerces.jaxp.JAXPConstants.W3C_XML_SCHEMA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBContext;
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

import jaxb.from.xsd.Clan;
import jaxb.from.xsd.Propis;
import jaxb.from.xsd.Clan.Sadrzaj;
import jaxb.from.xsd.Clan.Sadrzaj.Stav;
import jaxb.from.xsd.Propis.Deo;
import jaxb.from.xsd.Propis.Deo.Glava;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;

import web.xml.model.Propisi;
import web.xml.model.Users;
import web.xml.service.PropisService;

@Service
public class PropisServiceImpl implements PropisService
{
	
	public static int getID(){
		Random broj = new Random();
		return broj.nextInt(1000000);
	}
	
	@Override
	public Propisi findOne(Long id) { 
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Propisi findAll() throws JAXBException 
	{
		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37" ,"tim37", "tim37", Authentication.valueOf("DIGEST")); 
		
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();

		// A handle to receive the document's content.
		DOMHandle content = new DOMHandle();
		
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		
		// A document URI identifier. 
		String docId = "/propisi.xml";
		
		xmlManager.read(docId, metadata, content);

		// Retrieving a document node form DOM handle.
		Document doc = content.get();
		
		JAXBContext context = JAXBContext.newInstance(Propisi.class); 
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		
		return (Propisi) unmarshaller.unmarshal(doc);
		
		
	}

	@Override
	public void save(File f) throws FileNotFoundException  
	{ 
		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37" ,"tim37", "tim37", Authentication.valueOf("DIGEST")); 
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		
		String docId = "/propisi.xml";
		String collId = "/skupstina/propisi";
		
		InputStreamHandle handle = new InputStreamHandle(new FileInputStream(f.getAbsolutePath()));
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getCollections().add(collId);
		
		xmlManager.write(docId, metadata, handle);
		
		client.release();
	}

	@Override
	public void remove(Long id) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Propis dodajPropis(String requestData) {
		// TODO Auto-generated method stub
		
		JSONObject json = new JSONObject(requestData);

		String tekstClana = "";
		String redniBroj = "";
		String tekstStava = "";
		String nazivGlave = "";
		
		String propisNaziv = json.getString("nazivPropisa");
		String nazivDela = json.getString("nazivDeo");
		
		String nazivClana = json.getString("nazivClana");
		String opisClana = json.getString("opisClana");
		try
		{
			tekstClana = json.getString("tekstClana");
			
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			nazivGlave = json.getString("nazivGlave");
		}
		catch(org.json.JSONException e)
		{
			
		}
		
		Propis propis = new Propis();
		Deo deo = new Deo();
		Glava glava = new Glava();
		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();
		
		if(!redniBroj.equals(""))
		{
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}
		if(!tekstClana.equals(""))
		{
			sadrzaj.getTekst().add(tekstClana);
		}
		if(!nazivGlave.equals(""))
		{
			glava.getClan().add(clan);
			glava.setID(BigInteger.valueOf(getID()));
			glava.setNaziv(nazivGlave);
			deo.getGlava().add(glava);
		}
		
		
		clan.setID(BigInteger.valueOf(getID()));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);
		
		if(nazivGlave.equals(""))
		{
			deo.getClan().add(clan);
		}
		
		deo.setID(BigInteger.valueOf(getID()));
		deo.setNaziv(nazivDela);
		
		
		propis.setID(BigInteger.valueOf(getID()));
		propis.setNaziv(propisNaziv);
		propis.getDeo().add(deo);
		
		return propis;
	}

	@Override
	public Propisi dodajDeo(String requestData) throws JAXBException {
		// TODO Auto-generated method stub
		
		JSONObject json = new JSONObject(requestData);

		String tekstClana = "";
		String redniBroj = "";
		String tekstStava = "";
		String nazivGlave = "";
		
		String nazivDela = json.getString("nazivDeo");
		
		String nazivClana = json.getString("nazivClana");
		String opisClana = json.getString("opisClana");
		try
		{
			tekstClana = json.getString("tekstClana");
			
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			nazivGlave = json.getString("nazivGlave");
		}
		catch(org.json.JSONException e)
		{
			
		}
		
		Deo deo = new Deo();
		Glava glava = new Glava();
		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();
		
		if(!redniBroj.equals(""))
		{
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}
		if(!tekstClana.equals(""))
		{
			sadrzaj.getTekst().add(tekstClana);
		}
		if(!nazivGlave.equals(""))
		{
			glava.getClan().add(clan);
			glava.setID(BigInteger.valueOf(getID()));
			glava.setNaziv(nazivGlave);
			deo.getGlava().add(glava);
		}
		
		
		clan.setID(BigInteger.valueOf(getID()));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);
		
		if(nazivGlave.equals(""))
		{
			deo.getClan().add(clan);
		}
		
		
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));
		
		
		
		deo.setID(BigInteger.valueOf(getID()));
		deo.setNaziv(nazivDela);
		propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().add(deo);

		
		return propisi;
	}

	@Override
	public Propisi dodajGlavu(String requestData) throws JAXBException {
		// TODO Auto-generated method stub
		
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));
		

		JSONObject json = new JSONObject(requestData);

		String tekstClana = "";
		String redniBroj = "";
		String tekstStava = "";
		String nazivGlave = "";
		
		String nazivClana = json.getString("nazivClana");
		String opisClana = json.getString("opisClana");
		try
		{
			tekstClana = json.getString("tekstClana");
			
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
		}
		catch(org.json.JSONException e)
		{
			//e.printStackTrace();
		}
		try
		{
			nazivGlave = json.getString("nazivGlave");
		}
		catch(org.json.JSONException e)
		{
			
		}
		
		Glava glava = new Glava();
		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();
		
		if(!redniBroj.equals(""))
		{
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}
		
		if(!tekstClana.equals(""))
		{
			sadrzaj.getTekst().add(tekstClana);
		}
		
		if(!nazivGlave.equals(""))
		{
			glava.getClan().add(clan);
			glava.setID(BigInteger.valueOf(getID()));
			glava.setNaziv(nazivGlave);
			int brDelova = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().size();
			propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().add(glava);
		}
		
		
		clan.setID(BigInteger.valueOf(getID()));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);
		
		return propisi;
	}

	@Override
	public Propisi dodajClan(String requestData) throws JAXBException {
		
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));

		JSONObject json = new JSONObject(requestData);

		String tekstClana = "";
		String redniBroj = "";
		String tekstStava = "";
		
		String nazivClana = json.getString("nazivClana");
		String opisClana = json.getString("opisClana");
		
		try{
			
			tekstClana = json.getString("tekstClana");
			
		}catch(org.json.JSONException e){
			//e.printStackTrace();
		}
		
		try{
			
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
			
		}catch(org.json.JSONException e){
			//e.printStackTrace();
		}
		
		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();
		
		if(!redniBroj.equals(""))
		{
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}
		if(!tekstClana.equals(""))
		{
			sadrzaj.getTekst().add(tekstClana);
		}
		
		int brDelova = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().size();
		int brGlava = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().size();
		
		if(brGlava != 0 && propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava() != null ){
			clan.setID(BigInteger.valueOf(getID()));
			clan.setNaziv(nazivClana);
			clan.setOpis(opisClana);
			clan.setSadrzaj(sadrzaj);
			propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().get(brGlava-1).getClan().add(clan);
		}else {
			clan.setID(BigInteger.valueOf(getID()));
			clan.setNaziv(nazivClana);
			clan.setOpis(opisClana);
			clan.setSadrzaj(sadrzaj);
			propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getClan().add(clan);
		}
				
		return propisi;
	}

	@Override
	public Propisi dodajStav(String requestData) throws JAXBException {
		 
		// Unmarshalling generiše objektni model na osnovu XML fajla 
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));	

		JSONObject json = new JSONObject(requestData);


		String redniBroj = "";
		String tekstStava = "";		
		
		try{
			
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
			
		}catch(org.json.JSONException e){
			//e.printStackTrace();
		}

		Stav stav = new Stav();
		try {
			Long rbrStava = Long.parseLong(redniBroj);
			if(!redniBroj.equals("")){
				stav.setRedniBroj(rbrStava);
				stav.setTekst(tekstStava);
				int brGlava = 0;
				int brClan = 0;
				int brStav = 0;
				int brDelova = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().size();
				
				/**
				 * Dodavanje stava na poslednje dodati clan koji u sebi sadrzi stavove
				 */
				if(brDelova != 0){ //provera da li ima delove
					brGlava = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().size();
					if(brGlava != 0){ //provera da li deo ima glave
						brClan = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().get(brGlava-1).getClan().size();
						if(brClan != 0){ //provera da li glava ima clanove
							if(propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().get(brGlava-1).getClan().get(brClan-1).getSadrzaj().getStav().size() != 0 
									&& propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().get(brGlava-1).getClan().get(brClan-1).getSadrzaj().getStav() != null){ 
								//provera da li clan ima u sebi stavove, ako ima na njega dodajemo
								propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getGlava().get(brGlava-1).getClan().get(brClan-1).getSadrzaj().getStav().add(stav);
							}
						}
					}else{
						brClan = propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova - 1).getClan().size();
						if(brClan != 0){ //provera da li deo ima clanove 
							if(propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getClan().get(brClan-1).getSadrzaj().getStav().size() != 0
									&& propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getClan().get(brClan-1).getSadrzaj().getStav() != null){
								propisi.getPropisi().get(propisi.getPropisi().size()-1).getDeo().get(brDelova-1).getClan().get(brClan-1).getSadrzaj().getStav().add(stav);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		return propisi;
	}



	@Override
	public Propisi unmarshall(File f) throws JAXBException {
		// TODO Auto-generated method stub
		
		JAXBContext context = JAXBContext.newInstance(Propisi.class); 
		
		// Unmarshaller je objekat zadužen za konverziju iz XML-a u objektni model
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		
		// Unmarshalling generiše objektni model na osnovu XML fajla
		return (Propisi) unmarshaller.unmarshal(f);
	}

	@Override
	public void marshall(Propisi t, File f) throws JAXBException {
		// TODO Auto-generated method stub
		JAXBContext context = JAXBContext.newInstance(Propisi.class);
		
		Marshaller marshaller = context.createMarshaller();
		
		// Podešavanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		// Umesto System.out-a, može se koristiti FileOutputStream
	    marshaller.marshal(t, f);
	}

	@Override
	public Propis findPropisById(Long id) throws JAXBException 
	{
		//ideja ovde je da kada pronadjemo trazeni propis,
		//da se napravi samo njegov xml, da bi posle mogli prikazati njegove podatke
		//pomocu xsl-a
		JAXBContext context = JAXBContext.newInstance(Propis.class);
		
		Marshaller marshaller = context.createMarshaller();
		
		// Podešavanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		marshaller.setProperty("com.sun.xml.bind.xmlHeaders", 
				    "<?xml-stylesheet type='text/xsl' href='propis.xsl' ?>");
		
		Propisi propisi = findAll();
		
		BigInteger idPropis = BigInteger.valueOf(id);
		
		for(Propis p : propisi.getPropisi())
		{
			if(idPropis.equals(p.getID()))
			{
				marshaller.marshal(p, new File("./data/xml/propis.xml"));   
				return p;
			}
		}
		return null;
	}

	@Override
	public String generateHtmlFromXsl(File fileForXml, File fileForXsl) throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException 
	{
		 DocumentBuilderFactory factory;
		
		 Document document;
		

		 factory = DocumentBuilderFactory.newInstance();
			
			/* Ukljuèuje validaciju. */ 
		 factory.setValidating(true);
			
		 factory.setNamespaceAware(true);
		 factory.setIgnoringComments(true);
		 factory.setIgnoringElementContentWhitespace(true);
			
			/* Validacija u odnosu na XML šemu. */
		 factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		
			
		 DocumentBuilder builder = factory.newDocumentBuilder();
		 document = builder.parse(fileForXml);
		
		 //ovde se generise html kao string, koji se u stvari sastoji od podataka iz xml-a spojenog sa xsl-om.
		 StreamSource streamSource = new StreamSource(fileForXsl);
	     Transformer transformer = TransformerFactory.newInstance().newTransformer(streamSource);
	     StringWriter writer = new StringWriter();
	     transformer.transform(new DOMSource(document), new StreamResult(writer));
	      
		 return writer.getBuffer().toString();
	}

	

}
