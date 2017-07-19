package com.seoultechappsoftlab.wireloc.infrastructures;

import java.util.List;

/**
 * 
 * @author SeoulTech Application Software Lab
 *
 * @param <E> Entity
 */
public abstract class ServiceBase<E extends EntityBase> {
	
	//region abstract method
	private DALBase<E> dataAccess;
	
	/**
	 * Get Data Access
	 * @return
	 */
	public DALBase<E> getDataAccess() {
		return this.dataAccess;
	}
	
	/**
	 * Set Data Access
	 * @param dataAccess
	 */
	public void setDataAccess(DALBase<E> dataAccess) {
		this.dataAccess = dataAccess;
	}
	
	/**
	 * Insert data
	 * @param entity
	 * @return
	 */
	public abstract boolean insert(E entity);
	
	/**
	 * Update data
	 * @param entity
	 * @return
	 */
	public abstract boolean update(E entity);
	
	/**
	 * Delete data
	 * @param entity
	 * @return
	 */
	public abstract boolean delete(int id);
	
	/**
	 * Get By Id
	 * @param id
	 * @return
	 */
	public abstract E getById(int id);
	
	/**
	 * Get All
	 * @return
	 */
	public abstract List<E> getAll();
}
