package com.seoultechappsoftlab.wireloc.services;

import java.util.List;

import android.content.Context;

import com.seoultechappsoftlab.wireloc.dals.StudentDAL;
import com.seoultechappsoftlab.wireloc.entities.Student;
import com.seoultechappsoftlab.wireloc.infrastructures.ServiceBase;

/**
 * Student Service
 * @author SeoulTech Application Software Lab
 *
 */
public class StudentService extends ServiceBase<Student> {
	/**
	 * The Constructor
	 * @param context
	 */
	public StudentService(Context context) {
		this.setDataAccess(new StudentDAL(context));
	}
	
	/**
	 * Service - Insert Data
	 * @param entity
	 * @return
	 */
	@Override
	public boolean insert(Student entity) {
		return this.getDataAccess().insert(entity);
	}
	
	/**
	 * Service - Update Data
	 * @param entity
	 * @return
	 */
	@Override
	public boolean update(Student entity) {
		return this.getDataAccess().update(entity);
	}
	
	/**
	 * Service - Delete Data
	 * @param entity
	 * @return
	 */
	@Override
	public boolean delete(int id) {
		return this.getDataAccess().delete(id);
	}
	
	/**
	 * Service - Get By Id
	 * @param id
	 * @return
	 */
	@Override
	public Student getById(int id) {
		return this.getDataAccess().getById(id);
	}
	
	/**
	 * Service - Get All Data
	 * @return
	 */
	@Override
	public List<Student> getAll() {
		return this.getDataAccess().getAll();
	}
	
	//region downcasting method
	
	/**
	 * Overload method - delete
	 * This one need to be downcasted
	 * @param ids
	 * @return
	 */
	public boolean delete(int[] ids){
		//cast to StudentDAL
		return ((StudentDAL)this.getDataAccess()).delete(ids);
	}
	
	/**
	 * Downcasted method
	 * @return
	 */
	public int getFunctionCalledByDowncasting(){
		//cast to StudentDAL
		return ((StudentDAL)this.getDataAccess()).getFunctionCalledByDowncasting();
	}
	
	//end region downcasting method
}
