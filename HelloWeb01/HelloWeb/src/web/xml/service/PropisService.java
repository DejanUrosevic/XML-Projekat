package web.xml.service;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.json.JSONObject;

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
	
}
