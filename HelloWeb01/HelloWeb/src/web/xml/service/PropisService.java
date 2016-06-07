package web.xml.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

import jaxb.from.xsd.Propis;
import web.xml.model.Propisi;

public interface PropisService extends CrudService<Propisi> {
	/**
	 * Pronalazenje xml fajla na osnovu njegovog ID-a
	 * 
	 * @param docId
	 *            id xml fajla
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException 
	 */
	public Document findPropisById(String docId) throws JAXBException, FileNotFoundException;
	
	/**
	 * Pronalaženje propisa prema odgovarajućem upitu
	 * 
	 * @param reqBody
	 * @return
	 * @throws JAXBException
	 */
	public Propisi pretrazi(String reqBody) throws JAXBException;

	/**
	 * Podaci sa klijenta kojima se puni objekat Propis
	 * 
	 * @param requestData
	 *            podaci sa klijenta
	 * @return
	 * @throws DatatypeConfigurationException 
	 */
	public Propis dodajPropis(String requestData) throws DatatypeConfigurationException;

	/**
	 * Dodavanje novog dela na postojeci propi
	 * 
	 * @param requestData
	 *            podaci sa klijenta
	 * @return
	 * @throws JAXBException
	 */
	public Propisi dodajDeo(String requestData) throws JAXBException;

	/**
	 * Dodavanje glave na posledji deo
	 * 
	 * @param requestData
	 *            podaci sa klijenta
	 * @return
	 * @throws JAXBException
	 */
	public Propisi dodajGlavu(String requestData) throws JAXBException;

	/**
	 * Dodavanje clana na poslednju glavu
	 * 
	 * @param requestData
	 *            podaci sa klijenta
	 * @return
	 * @throws JAXBException
	 */
	public Propisi dodajClan(String requestData) throws JAXBException;

	/**
	 * Dodavanje stava na poslednju glavu
	 * 
	 * @param requestData
	 *            podaci sa klijenta
	 * @return
	 * @throws JAXBException
	 */
	public Propisi dodajStav(String requestData) throws JAXBException;

	/**
	 * Genersisanje stringa, koji predstavlja spojen xml i xsl kao html.
	 * 
	 * @param doc
	 *            dokument xml cije podatke hocemo da prikazemo
	 * @param fileForXsl
	 *            xsl koji smo specijalno napravili za prikaz propisa
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public String generateHtmlFromXsl(Document doc, File fileForXsl) throws ParserConfigurationException, SAXException,
			IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException;

	/**
	 * Ucitavanje dokumenta na osnovu .xml fajla
	 * 
	 * @param file
	 *            lokacija .xml fajla
	 * @return
	 */
	public Document loadDocument(String file);

	/**
	 * Uzimanje privatnog kljuca iz keystore-a.
	 * 
	 * @param file
	 *            lokacija keystore-a
	 * @param alias
	 * @param password
	 * @return
	 */
	public PrivateKey readPrivateKey(String file, String alias, String password);

	/**
	 * Citanje odgovoarajuceg sertifikata
	 * 
	 * @param file
	 *            lokacija gde se nalazi certifikat
	 * @param certNaziv
	 *            naziv sertifikata
	 * @return
	 */
	public Certificate readCertificate(String file, String certNaziv);

	/**
	 * Potpisivanje dokumenta na osnovu privatnog kljuca i sertifikata
	 * 
	 * @param doc
	 *            dokument koji se potpisuje
	 * @param privateKey
	 * @param cert
	 * @return
	 * @throws XMLSecurityException
	 */
	public Document signDocument(Document doc, PrivateKey privateKey, Certificate cert) throws XMLSecurityException;

	/**
	 * Pomocna metoda za marshalovanje propisa u neki fajl
	 * 
	 * @param propis
	 * @param f
	 * @throws JAXBException
	 */
	public void marshallPropis(Propis propis, File f) throws JAXBException;

	/**
	 * Pomocna metoda za unmarshal propisa
	 * 
	 * @param f
	 * @return
	 * @throws JAXBException
	 */
	public Propis unmarshallPropis(File f) throws JAXBException;

	/**
	 * Potpisivanje propisa na osnovu potrebnih parametara.
	 * 
	 * @param propis
	 * @param jks
	 * @param allias
	 * @param password
	 * @param cer
	 * @param cerNaziv
	 * @throws IOException
	 */
	public void signPropis(File propis, String jks, String allias, String password, String cer, String cerNaziv)
			throws IOException;

	/**
	 * Generise tajni kljuc sa kojim radimo enkripciju
	 * 
	 * @return tajni kljuc
	 */
	public SecretKey generateDataEncryptionKey();

	/**
	 * Snima dokument u .xml fajl.
	 * 
	 * @param doc
	 * @param fileName
	 */
	public void saveDocument(Document doc, String fileName);

	/**
	 * Vrsi se enkripcija dokumenta simetricnim algoritmom, kao i sifrovanje
	 * tajnog kljuca simetricnim algoritmom
	 * 
	 * @param doc
	 * @param key
	 * @param certificate
	 * @return enkriptovan dokument
	 */
	public Document encrypt(Document doc, SecretKey key, Certificate certificate);

	/**
	 * Konacna enkripcija dokumenta
	 * 
	 * @param xmlDocument
	 *            koji zelimo da enkriptujemo
	 * @param jks
	 *            javakeystore koji koristimo
	 * @throws IOException
	 */
	public void encryptXml(File xmlDocument, File jks, String nazivCert) throws IOException;

	/**
	 * Metoda za dekriptovanje xml-a dobijenog od strane IAGNS
	 * 
	 * @param doc
	 *            dokument koji je enkriptovan
	 * @param privateKey
	 *            kojim je kriptovan
	 * @return obican, citljiv .xml dokument
	 */
	public Document decryptXml(Document doc, PrivateKey privateKey);
	
	/**
	 * Brisanje iz baze propisa koji je odbijen prilikom sednice
	 * @param docId naziv propisa koji se brise
	 */
	public void removePropis(String docId);
	
	
	/**
	 * Pamtimo pure xml, bez enkripcije i potpisa, radi pretrage sadrzaja i metapodataka
	 * @param f xml fajl koji se pamti
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	public void saveWithoutEncrypt(File f) throws JAXBException, FileNotFoundException;
	
	public void savePropisiXML() throws FileNotFoundException;
	
	public Propis unmarshallDocumentPropis(Document propis) throws JAXBException;
	
	public void marshallPureXml(String textXml) throws JAXBException;
	
	public void savePureXml(File f) throws JAXBException, FileNotFoundException;
	
	public boolean verifySignature(Document doc) throws org.apache.xml.security.exceptions.XMLSecurityException;
}
