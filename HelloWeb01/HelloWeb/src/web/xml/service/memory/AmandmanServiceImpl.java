package web.xml.service.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.keys.KeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;

import jaxb.from.xsd.Amandman;
import jaxb.from.xsd.Clan;
import jaxb.from.xsd.Clan.Sadrzaj;
import jaxb.from.xsd.Clan.Sadrzaj.Stav;
import jaxb.from.xsd.Propis;
import web.xml.model.Propisi;
import web.xml.model.User;
import web.xml.service.AmandmanService;
import web.xml.service.PropisService;

@Service
public class AmandmanServiceImpl implements AmandmanService{

	@Autowired
	PropisService propisSer;
	
	public static int getID() {
		Random broj = new Random();
		return broj.nextInt(1000000);
	}
	
	@Override
	public Amandman findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Amandman findAll() throws JAXBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(File f) throws FileNotFoundException, JAXBException {
//		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37", "tim37", "tim37",
//				Authentication.valueOf("DIGEST"));
//		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
//
//		Propisi propisi = unmarshall(new File("./data/xml/propisi.xml"));
//		// svaki novi propis ce imati svoje ime kao naziv xml fajla
//		String docId = propisi.getPropisi().get(propisi.getPropisi().size() - 1).getNaziv().replaceAll("\\s", "")
//				+ ".xml";
//		String collId = "/skupstina/safePropisi";
//
//		InputStreamHandle handle = new InputStreamHandle(new FileInputStream(f.getAbsolutePath()));
//		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
//		metadata.getCollections().add(collId);
//
//		xmlManager.write(docId, metadata, handle);
		
	}

	@Override
	public void remove(Long id) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void marshall(Amandman t, File f) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Amandman.class);

		Marshaller marshaller = context.createMarshaller();

		// Pode�avanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml-stylesheet type='text/xsl' href='amandman.xsl' ?>");

		// Umesto System.out-a, mo�e se koristiti FileOutputStream
		marshaller.marshal(t, f);
		
	}

	@Override
	public Amandman unmarshall(File f) throws JAXBException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void dodajAmandman(String postPayLoad, User korisnik) throws DatatypeConfigurationException, JAXBException, IOException {
		JSONObject json = new JSONObject(postPayLoad);
		
		
		Amandman a = new Amandman();
		
		if(json.getString("clanTekst").equals(""))
		{
			Clan clan = new Clan();
			Sadrzaj sadrzaj = new Sadrzaj();
			Stav stav = new Stav();
			stav.setRedniBroj(json.getInt("stavRedniBroj"));
			stav.setTekst(json.getString("stavTekst"));
			a.setResenje(json.getString("stavTekst"));
			
			sadrzaj.getStav().add(stav);
			clan.setSadrzaj(sadrzaj);
			clan.setID(BigInteger.valueOf(json.getInt("clanId")));
			clan.setNaziv(json.getString("clanNaziv"));
			a.setClan(clan);
			
		}
		else
		{
			Clan clan = new Clan();
			Sadrzaj sadrzaj = new Sadrzaj();
			a.setResenje(json.getString("clanTekst"));
			
		//	sadrzaj.getTekst().add(a.getResenje());
			clan.setSadrzaj(sadrzaj);
			clan.setID(BigInteger.valueOf(json.getInt("clanId")));
			clan.setNaziv(json.getString("clanNaziv"));
			a.setClan(clan);
			
		}
		
		
		a.setObrazlozenje(json.getString("amandmanObrazlozenje"));
		
		GregorianCalendar c = new GregorianCalendar(); 
		c.setTime(new Date()); 
		XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		
		a.setDatum(date2);
		Integer id = getID();
		a.setID(BigInteger.valueOf(Long.parseLong(id.toString())));
		a.setPodnosilac(korisnik.getIme() + " " + korisnik.getPrezime());
		a.setMesto("Novi Sad");
		Propisi propisi = propisSer.unmarshall(new File("./data/xml/propisi.xml"));
		int propisid = json.getInt("propisId");
		Propis propis = null;
		for (int i = 0; i < propisi.getPropisi().size(); i++) {
			if(propisi.getPropisi().get(i).getID().equals(BigInteger.valueOf(propisid))){
				propis = propisSer.unmarshallDocumentPropis(propisSer.findPropisById(propisi.getPropisi().get(i).getNaziv()));
				propis.getAmandman().add(a);
				
				propisSer.marshallPropis(propis, new File("data\\xml\\potpisPropis.xml"));
				propisSer.encryptXml(new File("data\\xml\\potpisPropis.xml"), new File("data\\sertifikati\\iasgns.jks"), "iasgns");
				propisSer.signPropis(new File("data\\xml\\potpisPropis.xml"), korisnik.getJksPutanja(), korisnik.getAlias(), korisnik.getAlias(),
						"", korisnik.getAlias());
				propisSer.save(new File("data\\xml\\potpisPropis.xml"));
				break;
			}
		}

		propisSer.marshall(propisi, new File("./data/xml/propisi.xml"));
		marshall(a, new File("./data/xml/amandman.xml"));
		propisSer.signPropis(new File("data\\xml\\amandman.xml"), korisnik.getJksPutanja(), korisnik.getAlias(), korisnik.getAlias(),
				"", korisnik.getAlias());

		saveAmandman(new File("data\\xml\\amandman.xml"), Long.parseLong(a.getID().toString()));
		
	}
	
	@Override
	public Document encrypt(Document doc, SecretKey key, Certificate certificate) {
		// TODO Auto-generated method stub
		try {
			// cipher za kriptovanje tajnog kljuca,
			// Koristi se Javni RSA kljuc za kriptovanje
			XMLCipher keyCipher = XMLCipher.getInstance(XMLCipher.RSA_v1dot5);
			// inicijalizacija za kriptovanje tajnog kljuca javnim RSA
			// kljucem(1024 bita!!!!)
			keyCipher.init(XMLCipher.WRAP_MODE, certificate.getPublicKey());
			EncryptedKey encryptedKey = keyCipher.encryptKey(doc, key);

			// cipher za kriptovanje XML-a(AES 128 bita!!!!)
			XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES);
			// inicijalizacija za kriptovanje
			xmlCipher.init(XMLCipher.ENCRYPT_MODE, key);

			// u EncryptedData elementa koji se kriptuje kao KeyInfo stavljamo
			// kriptovan tajni kljuc
			EncryptedData encryptedData = xmlCipher.getEncryptedData();
			// kreira se KeyInfo
			KeyInfo keyInfo = new KeyInfo(doc);
			keyInfo.addKeyName("Kriptovani tajni kljuc");
			// postavljamo kriptovani kljuc
			keyInfo.add(encryptedKey);
			// postavljamo KeyInfo za element koji se kriptuje
			encryptedData.setKeyInfo(keyInfo);

			// trazi se element ciji sadrzaj se kriptuje
			NodeList odseci = doc.getDocumentElement().getChildNodes();
			Element odsek = (Element) doc.getDocumentElement();

			xmlCipher.doFinal(doc, odsek, true); // kriptuje sa sadrzaj

			return doc;

		} catch (XMLEncryptionException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void encryptXml(File xmlDocument, File jks, String nazivCert) throws IOException {
		// TODO Auto-generated method stub
		// inicijalizacija
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();

		Document doc = propisSer.loadDocument(xmlDocument.getCanonicalPath());
		// generise tajni kljuc

		SecretKey secretKey = propisSer.generateDataEncryptionKey();
		// ucitava sertifikat za kriptovanje tajnog kljuca
		Certificate cert = propisSer.readCertificate(jks.getCanonicalPath(), nazivCert);
		// kriptuje se dokument

		doc = encrypt(doc, secretKey, cert);
		// snima se tajni kljuc
		// snima se dokument
		propisSer.saveDocument(doc, xmlDocument.getCanonicalPath());

	}

	@Override
	public void saveAmandman(File f, long amandmanId) throws FileNotFoundException 
	{
		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37", "tim37", "tim37",
				Authentication.valueOf("DIGEST"));
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();

		// svaki novi propis ce imati svoje ime kao naziv xml fajla
		String docId = amandmanId+ "Amandman.xml";
		String collId = "/skupstina/amandmani";

		InputStreamHandle handle = new InputStreamHandle(new FileInputStream(f.getAbsolutePath()));
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getCollections().add(collId);

		xmlManager.write(docId, metadata, handle);
		
	}

	@Override
	public Document findAmandmanById(String docId) {
		// TODO Auto-generated method stub
		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37", "tim37", "tim37",
				Authentication.valueOf("DIGEST"));

		XMLDocumentManager xmlManager = client.newXMLDocumentManager();

		// A handle to receive the document's content.
		DOMHandle content = new DOMHandle();
		String nazivDoc = docId + "Amandman.xml";
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		xmlManager.read(nazivDoc, metadata, content);
		Document doc = content.get();

		// Pozivi metoda za dekripciju
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();

	//	PrivateKey pk = readPrivateKey("data\\sertifikati\\iasgns.jks", "iasgns", "iasgns");
	//	doc = decryptXml(doc, pk);

		return doc;
	}

	@Override
	public void primeniAmandman(String data) throws JAXBException 
	{
		JSONObject json = new JSONObject(data);
		
		int propisId = json.getInt("propisId");
		Integer amandmanId = json.getInt("amandmanId");
		String docIdPropis = "";
		
		Amandman amandman = unmarshallDocument(findAmandmanById(amandmanId.toString()));
		
		Propisi propisi = propisSer.unmarshall(new File("data\\xml\\propisi.xml"));
		for(Propis p : propisi.getPropisi())
		{
			if(p.getID().equals(BigInteger.valueOf(propisId)))
			{
				docIdPropis = p.getNaziv();
			}
		}
		
		Propis propis = propisSer.unmarshallDocumentPropis(propisSer.findPropisById(docIdPropis));
		
		for(int i=0; i<propis.getDeo().size(); i++)
		{
			for(int j=0; j<propis.getDeo().get(i).getGlava().size(); j++)
			{
				for(int z=0; z<propis.getDeo().get(i).getGlava().get(j).getClan().size(); z++)
				{
					if(propis.getDeo().get(i).getGlava().get(j).getClan().get(z).getID().equals(amandman.getClan().getID()))
					{
						propis.getDeo().get(i).getGlava().get(j).getClan().get(z).setSadrzaj(amandman.getClan().getSadrzaj());
						break;
					}
				}
			}
			for(int a=0; a<propis.getDeo().get(i).getClan().size(); a++)
			{
				if(propis.getDeo().get(i).getClan().get(a).getID().equals(amandman.getClan().getID()))
				{
					propis.getDeo().get(i).getClan().get(a).setSadrzaj(amandman.getClan().getSadrzaj());
					break;
				}
			}
		}
		
		propisSer.marshallPropis(propis, new File("data\\xml\\potpisPropis.xml"));
	}

	@Override
	public Amandman unmarshallDocument(Document document) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Amandman.class);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		
		return (Amandman) unmarshaller.unmarshal(document);
	}
	

}
