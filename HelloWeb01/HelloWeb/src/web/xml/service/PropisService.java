package web.xml.service;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

import jaxb.from.xsd.Propis;
import web.xml.model.Propisi;

public interface PropisService extends CrudService<Propisi>
{
	public Document findPropisById(String docId) throws JAXBException;
	
	public Propis dodajPropis(String requestData);
	
	public Propisi dodajDeo(String requestData) throws JAXBException;
	
	public Propisi dodajGlavu(String requestData) throws JAXBException;
	
	public Propisi dodajClan(String requestData) throws JAXBException;
	
	public Propisi dodajStav(String requestData) throws JAXBException;
	
	public String generateHtmlFromXsl(Document doc, File fileForXsl) throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException;
	
	public Document loadDocument(String file);
	
	public PrivateKey readPrivateKey(String file, String alias, String password);
	
	public Certificate readCertificate(String file, String certNaziv);
	
	public Document signDocument(Document doc, PrivateKey privateKey, Certificate cert) throws XMLSecurityException;
	
	public void marshallPropis(Propis propis, File f) throws JAXBException;
	
	public Propis unmarshallPropis(File f) throws JAXBException;
	
	public void signPropis(File propis, String jks, String allias, String password, String cer, String cerNaziv) throws IOException;
}
