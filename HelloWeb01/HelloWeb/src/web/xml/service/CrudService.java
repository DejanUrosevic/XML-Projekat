package web.xml.service;

import java.util.List;


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
	 */
	List<T> findAll();

	/**
	 * Save entity and return saved instance (with id set).
	 * 
	 * @param entity
	 *            to be saved
	 * @return saved instance
	 */
	T save(T t);

	/**
	 * Remove entity with passed id.
	 * 
	 * @param id
	 *            of the entity to be removed
	 * @throws IllegalArgumentException
	 *             if there is no entity with passed id
	 */
	void remove(Long id) throws IllegalArgumentException;
}
