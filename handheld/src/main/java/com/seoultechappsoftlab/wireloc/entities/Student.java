package com.seoultechappsoftlab.wireloc.entities;

import com.seoultechappsoftlab.wireloc.infrastructures.EntityBase;

/**
 * Example Entity Class : Student
 * @author SeoulTech Application Software Lab
 *
 */
public class Student extends EntityBase{	
	/**
	 * Student's Name
	 */
	private String name;
	
	/**
	 * Empty Constructor
	 */
	public Student(){
	}
	
	/**
	 * The Constructor
	 * @param name
	 */
	public Student(String name){
		this.name = name;
	}
	
	/**
	 * Get The Name
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Set The Name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
