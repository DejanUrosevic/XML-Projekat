package web.xml.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBException;



/**
 * CRUD (Create/Read/Update/Delete) service.
 * 
 * @param <T>
 */
public interface CrudService<T> {

	/**
	 * Find and return entity with passed id.
	 * 
	 * @param id
	 *            of the entity to return
	 * @return entity with passed id or null if not found
	 */
	T findOne(Long id);

	/**
	 * Return back all existing entities.
	 * 
	 * @return list of existing entities, empty list if there are no entities
	 * @throws JAXBException 
	 */
	T findAll() throws JAXBException;

	/**
	 * Save entity and return saved instance (with id set).
	 * 
	 * @param entity
	 *            to be saved
	 * @return saved instance
	 * @throws FileNotFoundException 
	 */
	void save(File f) throws FileNotFoundException;

	/**
	 * Remove entity with passed id.
	 * 
	 * @param id
	 *            of the entity to be removed
	 * @throws IllegalArgumentException
	 *             if there is no entity with passed id
	 */
	void remove(Long id) throws IllegalArgumentException;
	
	
	
	/**
	 * Java objekat prebacuje u XML fajl
	 * @param t objekat koji se marhsaluje
	 * @param f fajl gde ce se zapamtiti
	 * @throws JAXBException
	 */
	void marshall(T t, File f) throws JAXBException;
	
	
	/**
	 * Podatke iz XML fajla prebacuje u java objekat
	 * @param f lokacija gde se fajl koji se cita nalazi
	 * @return objekat te klase
	 * @throws JAXBException 
	 */
	T unmarshall(File f) throws JAXBException;
	
}
