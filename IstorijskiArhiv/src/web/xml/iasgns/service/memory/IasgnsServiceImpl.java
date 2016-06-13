package web.xml.iasgns.service.memory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.keys.KeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;

import web.xml.iasgns.model.Propis;
import web.xml.iasgns.model.Propisi;
import web.xml.iasgns.service.IasgnsService;

@Service
public class IasgnsServiceImpl implements IasgnsService{

	@Override
	public Propisi findAll() throws JAXBException 
	{
		  DatabaseClient client =
		  DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37"
		  ,"tim37", "tim37", Authentication.valueOf("DIGEST"));
		  
		  XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		  
		  DOMHandle content = new DOMHandle();
		  
		  DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		  
		  String docId = "/propisi.xml";
		  
		  xmlManager.read(docId, metadata, content);
		  
		  Document doc = content.get();
		 
		 JAXBContext context = JAXBContext.newInstance(Propisi.class);

		// Unmarshaller je objekat zadu�en za konverziju iz XML-a u objektni
		// model
		 Unmarshaller unmarshaller = context.createUnmarshaller();

		 return (Propisi) unmarshaller.unmarshal(doc);
	}

	@Override
	public void sendToDatabase(String idPropis) throws JAXBException, IOException 
	{
		
		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37", "tim37", "tim37",
				Authentication.valueOf("DIGEST"));
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		
		String docId = "";
		//povlacimo trazeni propis sa baze, na osnovu poslatog ID-a
	//	Document propis = findPropisById(idPropis);
	//	Propis p = unmarshallDocumentPropis(propis);
		
		//marshallpropis(p, new File("F:\\My Documents\\Faks\\CETVRTA godina\\Internet Softverske Arhitekture\\IstorijskiArhiv\\data\\xml\\propisZaIstorijskiArhiv.xml"));
		encryptXml(findPropisById(idPropis), new File("data\\sertifikati\\iasgns.jks"), "iasgns");
		
		docId = idPropis.replaceAll("\\s", "") + "IASGNS.xml";
		String collId = "/istorijskiArhiv/propisi";

		InputStreamHandle handle = new InputStreamHandle(new FileInputStream(new File("data\\xml\\propisZaIstorijskiArhiv.xml").getAbsolutePath()));
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getCollections().add(collId);
		
		xmlManager.write(docId, metadata, handle);
	}

	@Override
	public Document findPropisById(String docId) {
		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37", "tim37", "tim37",
				Authentication.valueOf("DIGEST"));

		XMLDocumentManager xmlManager = client.newXMLDocumentManager();

		// A handle to receive the document's content.
		DOMHandle content = new DOMHandle();
		String nazivDoc = docId.replaceAll("\\s", "") + ".xml";
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		xmlManager.read(nazivDoc, metadata, content);
		Document doc = content.get();

		// Pozivi metoda za dekripciju
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();

		return doc;
	}

	@Override
	public Document loadDocument(String file) 
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document document = db.parse(new File(file));
			return document;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (FactoryConfigurationError e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public SecretKey generateDataEncryptionKey() {
		
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede"); // Triple
																			// DES
			return keyGenerator.generateKey();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Certificate readCertificate(String file, String certNaziv) {
		try {
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(new File(file)));

			ks.load(in, certNaziv.toCharArray());
			if (ks.isKeyEntry(certNaziv.toLowerCase())) {
				Certificate cert = ks.getCertificate(certNaziv);
				return cert;
			} else
				return null;
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
		//	NodeList odseci = doc.getElementsByTagName("Deo");
		//	Element odsek = (Element) odseci.item(0);
			NodeList odeseci = doc.getDocumentElement().getChildNodes();
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
	public void saveDocument(Document doc, String fileName) {
		try {
			File outFile = new File(fileName);
			FileOutputStream f = new FileOutputStream(outFile);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(f);

			transformer.transform(source, result);

			f.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void encryptXml(Document document, File jks, String nazivCert)
			throws IOException {
		
		// TODO Auto-generated method stub
		// inicijalizacija
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
 
		// generise tajni kljuc
		SecretKey secretKey = generateDataEncryptionKey();
		// ucitava sertifikat za kriptovanje tajnog kljuca
		Certificate cert = readCertificate(jks.getCanonicalPath(), nazivCert);
		// kriptuje se dokument

		document = encrypt(document, secretKey, cert);
		// snima se tajni kljuc
		// snima se dokument
		saveDocument(document, new File("data\\xml\\propisZaIstorijskiArhiv.xml").getCanonicalPath());
		
	}

	@Override
	public void marshallpropis(Propis p, File f) throws JAXBException 
	{
		JAXBContext context = JAXBContext.newInstance(Propis.class);

		Marshaller marshaller = context.createMarshaller();

		// Pode�avanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml-stylesheet type='text/xsl' href='propis.xsl' ?>");
		
		// Umesto System.out-a, mo�e se koristiti FileOutputStream
		marshaller.marshal(p, f);
		
	}

	@Override
	public Propis unmarshallDocumentPropis(Document propis) throws JAXBException 
	{
		JAXBContext context = JAXBContext.newInstance(Propis.class);

		// Unmarshaller je objekat zadu�en za konverziju iz XML-a u objektni
		// model
		 Unmarshaller unmarshaller = context.createUnmarshaller();
		
		return (Propis) unmarshaller.unmarshal(propis);
	}

}
