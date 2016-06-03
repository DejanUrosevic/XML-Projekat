package web.xml.service;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import jaxb.from.xsd.Amandman;
import web.xml.model.User;

public interface AmandmanService extends CrudService<Amandman>{
	
	public void dodajAmandman(String postPayLoad, User korisnik) throws DatatypeConfigurationException, JAXBException;
	
}
