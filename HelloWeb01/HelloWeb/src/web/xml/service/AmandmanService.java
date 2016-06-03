package web.xml.service;

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.w3c.dom.Document;

import jaxb.from.xsd.Amandman;
import web.xml.model.User;

public interface AmandmanService extends CrudService<Amandman>{
	
	public void dodajAmandman(String postPayLoad, User korisnik) throws DatatypeConfigurationException, JAXBException;
	public Document encrypt(Document doc, SecretKey key, Certificate certificate);
	public void encryptXml(File xmlDocument, File jks, String nazivCert) throws IOException;
}
