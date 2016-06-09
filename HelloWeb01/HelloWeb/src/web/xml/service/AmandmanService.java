package web.xml.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import jaxb.from.xsd.Amandman;
import web.xml.model.User;

public interface AmandmanService extends CrudService<Amandman>{
	
	public void dodajAmandman(String postPayLoad, User korisnik) throws DatatypeConfigurationException, JAXBException, FileNotFoundException, IOException;
	public Document encrypt(Document doc, SecretKey key, Certificate certificate);
	public void encryptXml(File xmlDocument, File jks, String nazivCert) throws IOException;
	
	public void saveAmandman(File f, long amandmanId) throws FileNotFoundException;
	
	public Document findAmandmanById(String docId);
	
	public void primeniAmandman(String data) throws JAXBException, FileNotFoundException;
	
	public Amandman unmarshallDocument(Document document) throws JAXBException;
	
	public void toPdf(File amandman, File amandmanXsl) throws FileNotFoundException, SAXException, IOException, TransformerConfigurationException, TransformerException;
	
	public void marshallAmandman(Amandman amandman, File f) throws JAXBException;
	
}
