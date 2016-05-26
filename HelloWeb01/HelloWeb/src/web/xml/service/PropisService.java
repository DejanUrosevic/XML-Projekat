package web.xml.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import jaxb.from.xsd.Propis;
import web.xml.model.Propisi;

public interface PropisService extends CrudService<Propisi>
{
	public Propis findPropisById(Long id) throws JAXBException;
	
	public Propis dodajPropis(String requestData);
	
	public Propisi dodajDeo(String requestData) throws JAXBException;
	
	public Propisi dodajGlavu(String requestData) throws JAXBException;
	
	public Propisi dodajClan(String requestData) throws JAXBException;
	
	public Propisi dodajStav(String requestData) throws JAXBException;
	
	public String generateHtmlFromXsl(File fileForXml, File fileForXsl) throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException;
	
}
