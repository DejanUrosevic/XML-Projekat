package web.xml.service.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;

import jaxb.from.xsd.Amandman;
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

		// Umesto System.out-a, mo�e se koristiti FileOutputStream
		marshaller.marshal(t, f);
		
	}

	@Override
	public Amandman unmarshall(File f) throws JAXBException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void dodajAmandman(String postPayLoad, User korisnik) throws DatatypeConfigurationException, JAXBException {
		JSONObject json = new JSONObject(postPayLoad);
		Amandman a = new Amandman();
		a.setResenje(json.getString("clanTekst"));
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
//		for (Propis p: propisi.getPropisi()) {
//			if(p.getID().equals(BigInteger.valueOf(propisid))){
////				a.setPropis(p);
//				propis = p;
//				p.getAmandman().add(a);
//			
//				break;
//			}
//		}
		for (int i = 0; i < propisi.getPropisi().size(); i++) {
			if(propisi.getPropisi().get(i).getID().equals(BigInteger.valueOf(propisid))){
				propisi.getPropisi().get(i).getAmandman().add(a);
				break;
			}
		}
		propisSer.marshall(propisi, new File("./data/xml/propisi.xml"));
		marshall(a, new File("./data/xml/amandman.xml"));
		
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
	

}
