package web.xml.service.memory;

import static org.apache.xerces.jaxp.JAXPConstants.JAXP_SCHEMA_LANGUAGE;
import static org.apache.xerces.jaxp.JAXPConstants.W3C_XML_SCHEMA;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Random;

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
import javax.xml.transform.stream.StreamSource;

import jaxb.from.xsd.Clan;
import jaxb.from.xsd.Propis;
import jaxb.from.xsd.Clan.Sadrzaj;
import jaxb.from.xsd.Clan.Sadrzaj.Stav;
import jaxb.from.xsd.Propis.Deo;
import jaxb.from.xsd.Propis.Deo.Glava;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.utils.Constants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignature;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;

import web.xml.model.Propisi;
import web.xml.model.Users;
import web.xml.service.PropisService;

@Service
public class PropisServiceImpl implements PropisService {

	public static int getID() {
		Random broj = new Random();
		return broj.nextInt(1000000);
	}

	@Override
	public Propisi findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Propisi findAll() throws JAXBException {
		/*
		 * DatabaseClient client =
		 * DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37"
		 * ,"tim37", "tim37", Authentication.valueOf("DIGEST"));
		 * 
		 * XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		 * 
		 * // A handle to receive the document's content. DOMHandle content =
		 * new DOMHandle();
		 * 
		 * DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		 * 
		 * // A document URI identifier. String docId = "/propisi.xml";
		 * 
		 * xmlManager.read(docId, metadata, content);
		 * 
		 * // Retrieving a document node form DOM handle. Document doc =
		 * content.get();
		 */
		JAXBContext context = JAXBContext.newInstance(Propisi.class);

		// Unmarshaller je objekat zadu�en za konverziju iz XML-a u objektni
		// model
		Unmarshaller unmarshaller = context.createUnmarshaller();

		return (Propisi) unmarshall(new File("./data/xml/propisi.xml"));

	}

	@Override
	public void save(File f) throws FileNotFoundException, JAXBException {
		DatabaseClient client = DatabaseClientFactory.newClient("147.91.177.194", 8000, "Tim37", "tim37", "tim37",
				Authentication.valueOf("DIGEST"));
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();

		Propisi propisi = unmarshall(new File("./data/xml/propisi.xml"));
		// svaki novi propis ce imati svoje ime kao naziv xml fajla
		String docId = propisi.getPropisi().get(propisi.getPropisi().size() - 1).getNaziv().replaceAll("\\s", "")
				+ ".xml";
		String collId = "/skupstina/safePropisi";

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
		try {
			tekstClana = json.getString("tekstClana");

		} catch (org.json.JSONException e) {
			// e.printStackTrace();
		}
		try {
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
		} catch (org.json.JSONException e) {
			// e.printStackTrace();
		}
		try {
			nazivGlave = json.getString("nazivGlave");
		} catch (org.json.JSONException e) {

		}

		Propis propis = new Propis();
		Deo deo = new Deo();
		Glava glava = new Glava();
		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();

		if (!redniBroj.equals("")) {
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}
		if (!tekstClana.equals("")) {
			sadrzaj.getTekst().add(tekstClana);
		}
		if (!nazivGlave.equals("")) {
			glava.getClan().add(clan);
			glava.setID(BigInteger.valueOf(getID()));
			glava.setNaziv(nazivGlave);
			deo.getGlava().add(glava);
		}

		clan.setID(BigInteger.valueOf(getID()));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);

		if (nazivGlave.equals("")) {
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
		try {
			tekstClana = json.getString("tekstClana");

		} catch (org.json.JSONException e) {
			// e.printStackTrace();
		}
		try {
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
		} catch (org.json.JSONException e) {
			// e.printStackTrace();
		}
		try {
			nazivGlave = json.getString("nazivGlave");
		} catch (org.json.JSONException e) {

		}

		Deo deo = new Deo();
		Glava glava = new Glava();
		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();

		if (!redniBroj.equals("")) {
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}
		if (!tekstClana.equals("")) {
			sadrzaj.getTekst().add(tekstClana);
		}
		if (!nazivGlave.equals("")) {
			glava.getClan().add(clan);
			glava.setID(BigInteger.valueOf(getID()));
			glava.setNaziv(nazivGlave);
			deo.getGlava().add(glava);
		}

		clan.setID(BigInteger.valueOf(getID()));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);

		if (nazivGlave.equals("")) {
			deo.getClan().add(clan);
		}

		// Unmarshalling generi�e objektni model na osnovu XML fajla
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));

		deo.setID(BigInteger.valueOf(getID()));
		deo.setNaziv(nazivDela);
		propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().add(deo);

		return propisi;
	}

	@Override
	public Propisi dodajGlavu(String requestData) throws JAXBException {
		// TODO Auto-generated method stub

		// Unmarshalling generi�e objektni model na osnovu XML fajla
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));

		JSONObject json = new JSONObject(requestData);

		String tekstClana = "";
		String redniBroj = "";
		String tekstStava = "";
		String nazivGlave = "";

		String nazivClana = json.getString("nazivClana");
		String opisClana = json.getString("opisClana");
		try {
			tekstClana = json.getString("tekstClana");

		} catch (org.json.JSONException e) {
			// e.printStackTrace();
		}
		try {
			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");
		} catch (org.json.JSONException e) {
			// e.printStackTrace();
		}
		try {
			nazivGlave = json.getString("nazivGlave");
		} catch (org.json.JSONException e) {

		}

		Glava glava = new Glava();
		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();

		if (!redniBroj.equals("")) {
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}

		if (!tekstClana.equals("")) {
			sadrzaj.getTekst().add(tekstClana);
		}

		if (!nazivGlave.equals("")) {
			glava.getClan().add(clan);
			glava.setID(BigInteger.valueOf(getID()));
			glava.setNaziv(nazivGlave);
			int brDelova = propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().size();
			propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1).getGlava().add(glava);
		}

		clan.setID(BigInteger.valueOf(getID()));
		clan.setNaziv(nazivClana);
		clan.setOpis(opisClana);
		clan.setSadrzaj(sadrzaj);

		return propisi;
	}

	@Override
	public Propisi dodajClan(String requestData) throws JAXBException {

		// Unmarshalling generi�e objektni model na osnovu XML fajla
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));

		JSONObject json = new JSONObject(requestData);

		String tekstClana = "";
		String redniBroj = "";
		String tekstStava = "";

		String nazivClana = json.getString("nazivClana");
		String opisClana = json.getString("opisClana");

		try {

			tekstClana = json.getString("tekstClana");

		} catch (org.json.JSONException e) {
			// e.printStackTrace();
		}

		try {

			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");

		} catch (org.json.JSONException e) {
			// e.printStackTrace();
		}

		Clan clan = new Clan();
		Sadrzaj sadrzaj = new Sadrzaj();
		Stav stav = new Stav();

		if (!redniBroj.equals("")) {
			stav.setRedniBroj(Long.parseLong(redniBroj));
			stav.setTekst(tekstStava);
			sadrzaj.getStav().add(stav);
		}
		if (!tekstClana.equals("")) {
			sadrzaj.getTekst().add(tekstClana);
		}

		int brDelova = propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().size();
		int brGlava = propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1).getGlava()
				.size();

		if (brGlava != 0 && propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1)
				.getGlava() != null) {
			clan.setID(BigInteger.valueOf(getID()));
			clan.setNaziv(nazivClana);
			clan.setOpis(opisClana);
			clan.setSadrzaj(sadrzaj);
			propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1).getGlava()
					.get(brGlava - 1).getClan().add(clan);
		} else {
			clan.setID(BigInteger.valueOf(getID()));
			clan.setNaziv(nazivClana);
			clan.setOpis(opisClana);
			clan.setSadrzaj(sadrzaj);
			propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1).getClan().add(clan);
		}

		return propisi;
	}

	@Override
	public Propisi dodajStav(String requestData) throws JAXBException {

		// Unmarshalling generi�e objektni model na osnovu XML fajla
		Propisi propisi = (Propisi) unmarshall(new File("./data/xml/propisi.xml"));

		JSONObject json = new JSONObject(requestData);

		String redniBroj = "";
		String tekstStava = "";

		try {

			redniBroj = json.getString("redniBrojStava");
			tekstStava = json.getString("stavTekst");

		} catch (org.json.JSONException e) {
			// e.printStackTrace();
		}

		Stav stav = new Stav();
		try {
			Long rbrStava = Long.parseLong(redniBroj);
			if (!redniBroj.equals("")) {
				stav.setRedniBroj(rbrStava);
				stav.setTekst(tekstStava);
				int brGlava = 0;
				int brClan = 0;
				int brStav = 0;
				int brDelova = propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().size();

				/**
				 * Dodavanje stava na poslednje dodati clan koji u sebi sadrzi
				 * stavove
				 */
				if (brDelova != 0) { // provera da li ima delove
					brGlava = propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1)
							.getGlava().size();
					if (brGlava != 0) { // provera da li deo ima glave
						brClan = propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1)
								.getGlava().get(brGlava - 1).getClan().size();
						if (brClan != 0) { // provera da li glava ima clanove
							if (propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1)
									.getGlava().get(brGlava - 1).getClan().get(brClan - 1).getSadrzaj().getStav()
									.size() != 0
									&& propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo()
											.get(brDelova - 1).getGlava().get(brGlava - 1).getClan().get(brClan - 1)
											.getSadrzaj().getStav() != null) {
								// provera da li clan ima u sebi stavove, ako
								// ima na njega dodajemo
								propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1)
										.getGlava().get(brGlava - 1).getClan().get(brClan - 1).getSadrzaj().getStav()
										.add(stav);
							}
						}
					} else {
						brClan = propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1)
								.getClan().size();
						if (brClan != 0) { // provera da li deo ima clanove
							if (propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1)
									.getClan().get(brClan - 1).getSadrzaj().getStav().size() != 0
									&& propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo()
											.get(brDelova - 1).getClan().get(brClan - 1).getSadrzaj()
											.getStav() != null) {
								propisi.getPropisi().get(propisi.getPropisi().size() - 1).getDeo().get(brDelova - 1)
										.getClan().get(brClan - 1).getSadrzaj().getStav().add(stav);
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

		// Unmarshaller je objekat zadu�en za konverziju iz XML-a u objektni
		// model
		Unmarshaller unmarshaller = context.createUnmarshaller();

		// Unmarshalling generi�e objektni model na osnovu XML fajla
		return (Propisi) unmarshaller.unmarshal(f);
	}

	@Override
	public void marshall(Propisi t, File f) throws JAXBException {
		// TODO Auto-generated method stub
		JAXBContext context = JAXBContext.newInstance(Propisi.class);

		Marshaller marshaller = context.createMarshaller();

		// Pode�avanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		// Umesto System.out-a, mo�e se koristiti FileOutputStream
		marshaller.marshal(t, f);
	}

	@Override
	public void marshallPropis(Propis propis, File f) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Propis.class);

		Marshaller marshaller = context.createMarshaller();

		// Pode�avanje marshaller-a
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml-stylesheet type='text/xsl' href='propis.xsl' ?>");

		// Umesto System.out-a, mo�e se koristiti FileOutputStream
		marshaller.marshal(propis, f);
	}

	@Override
	public Propis unmarshallPropis(File f) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Propis.class);

		// Unmarshaller je objekat zadu�en za konverziju iz XML-a u objektni
		// model
		Unmarshaller unmarshaller = context.createUnmarshaller();

		// Unmarshalling generi�e objektni model na osnovu XML fajla
		return (Propis) unmarshaller.unmarshal(f);
	}

	@Override
	public Document findPropisById(String docId) throws JAXBException {
		// ideja ovde je da kada pronadjemo trazeni propis sa baze,
		// on se preuzme kao dokument, pa se posle prosledi u metodu za
		// generisanje xsl-a, da bi posle mogli prikazati njegove podatke
		// pomocu xsl-a.
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

		PrivateKey pk = readPrivateKey("data\\sertifikati\\qq.jks", "qq", "qq");
		doc = decryptXml(doc, pk);

		return doc;
	}

	@Override
	public String generateHtmlFromXsl(Document doc, File fileForXsl) throws ParserConfigurationException, SAXException,
			IOException, TransformerFactoryConfigurationError, TransformerException {

		// ovde se generise html kao string, koji se u stvari sastoji od
		// podataka iz xml-a spojenog sa xsl-om.
		StreamSource streamSource = new StreamSource(fileForXsl);
		Transformer transformer = TransformerFactory.newInstance().newTransformer(streamSource);
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));

		return writer.getBuffer().toString();
	}

	@Override
	public Document loadDocument(String file) {
		// TODO Auto-generated method stub
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
	public PrivateKey readPrivateKey(String file, String alias, String password) {
		// TODO Auto-generated method stub
		KeyStore ks;
		try {
			ks = KeyStore.getInstance("JKS", "SUN");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream("data\\sertifikati\\qq.jks"));
			ks.load(in, password.toCharArray());

			if (ks.isKeyEntry("qq")) {
				PrivateKey pk = (PrivateKey) ks.getKey("qq", "qq".toCharArray());
				return pk;
			} else {
				return null;
			}
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
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public Certificate readCertificate(String file, String certNaziv) {
		// TODO Auto-generated method stub

		try {
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(new File("data\\sertifikati\\qq.jks")));

			ks.load(in, "qq".toCharArray());
			if (ks.isKeyEntry("qq")) {
				Certificate cert = ks.getCertificate("qq");
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
	public Document signDocument(Document doc, PrivateKey privateKey, Certificate cert) throws XMLSecurityException {
		// TODO Auto-generated method stub
		Element rootEl = doc.getDocumentElement();
		com.sun.org.apache.xml.internal.security.Init.init();
		XMLSignature sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);

		Transforms transform = new Transforms(doc);

		transform.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);

		transform.addTransform(Transforms.TRANSFORM_C14N11_WITH_COMMENTS);

		sig.addDocument("", transform, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);

		sig.addKeyInfo(cert.getPublicKey());
		sig.addKeyInfo((X509Certificate) cert);

		rootEl.appendChild(sig.getElement());
		sig.sign(privateKey);
		return doc;

	}

	@Override
	public void signPropis(File propis, String jks, String allias, String password, String cer, String cerNaziv)
			throws IOException {
		// TODO Auto-generated method stub

		Document doc = loadDocument(propis.getCanonicalPath());

		PrivateKey pk = readPrivateKey(jks, allias, password);
		Certificate cert = readCertificate(cer, cerNaziv);
		try {
			doc = signDocument(doc, pk, cert);
		} catch (XMLSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			FileOutputStream f = new FileOutputStream(propis);

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
	public SecretKey generateDataEncryptionKey() {
		// TODO Auto-generated method stub

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
			NodeList odseci = doc.getElementsByTagName("Deo");
			Element odsek = (Element) odseci.item(0);

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
	public void encryptXml(File xmlDocument, File jks, String nazivCert) throws IOException {
		// TODO Auto-generated method stub
		// inicijalizacija
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();

		Document doc = loadDocument(xmlDocument.getCanonicalPath());
		// generise tajni kljuc

		SecretKey secretKey = generateDataEncryptionKey();
		// ucitava sertifikat za kriptovanje tajnog kljuca
		Certificate cert = readCertificate(jks.getCanonicalPath(), nazivCert);
		// kriptuje se dokument

		doc = encrypt(doc, secretKey, cert);
		// snima se tajni kljuc
		// snima se dokument
		saveDocument(doc, xmlDocument.getCanonicalPath());

	}

	@Override
	public Document decryptXml(Document doc, PrivateKey privateKey) {
		try {
			// cipher za dekritpovanje XML-a
			XMLCipher xmlCipher = XMLCipher.getInstance();
			// inicijalizacija za dekriptovanje
			xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
			// postavlja se kljuc za dekriptovanje tajnog kljuca
			xmlCipher.setKEK(privateKey);

			// trazi se prvi EncryptedData element
			NodeList encDataList = doc.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
			Element encData = (Element) encDataList.item(0);

			// dekriptuje se
			// pri cemu se prvo dekriptuje tajni kljuc, pa onda njime podaci
			xmlCipher.doFinal(doc, encData);

			return doc;
		} catch (XMLEncryptionException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
