package web.xml.service;

import javax.xml.bind.JAXBException;

import org.json.JSONObject;

import jaxb.from.xsd.Propis;

public interface PropisService extends CrudService<Propis>
{

	public Propis dodajPropis(String requestData);
	
	public Propis dodajDeo(String requestData) throws JAXBException;
	
	public Propis dodajGlavu(String requestData) throws JAXBException;
	
	public Propis dodajClan(String requestData) throws JAXBException;
	
	public Propis dodajStav(String requestData) throws JAXBException;
	
}
