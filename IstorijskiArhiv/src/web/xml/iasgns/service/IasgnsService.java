package web.xml.iasgns.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;
import javax.xml.bind.JAXBException;

import org.w3c.dom.Document;

import web.xml.iasgns.model.Propis;
import web.xml.iasgns.model.Propisi;

public interface IasgnsService 
{
	public Propisi findAll() throws JAXBException;
	
	public void sendToDatabase(String idPropis) throws JAXBException, FileNotFoundException, IOException;
	
	public Document findPropisById(String docId);
	
	public Document loadDocument(String file);
	
	public SecretKey generateDataEncryptionKey();
	
	public Certificate readCertificate(String file, String certNaziv);
	
	public Document encrypt(Document doc, SecretKey key, Certificate certificate);
	
	public void saveDocument(Document doc, String fileName);
	
	public void encryptXml(Document xmlDocument, File jks, String nazivCert) throws IOException;
	
	public void marshallpropis(Propis p, File f) throws JAXBException;
	
	public Propis unmarshallDocumentPropis(Document propis) throws JAXBException;
}
