package com.seoultechappsoftlab.wireloc.entities;

import com.seoultechappsoftlab.wireloc.infrastructures.EntityBase;

/**
 * Entity : Stage
 * @author SeoulTech Application Software Lab
 *
 */
public class Stage extends EntityBase {
	
	//Region Private Variable
	private String stageName;
	private String stageDescription;
	private int isActive;
	private String resourceFolderName;
	//End Region Private Variable
	
	//Region Getters and Setters
	public String getStageName() {
		return stageName;
	}
	
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	
	public String getStageDescription() {
		return stageDescription;
	}
	
	public void setStageDescription(String stageDescription) {
		this.stageDescription = stageDescription;
	}
	
	public int getIsActive() {
		return isActive;
	}
	
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	public String getResourceFolderName() {
		return resourceFolderName;
	}
	
	public void setResourceFolderName(String resourceFolderName) {
		this.resourceFolderName = resourceFolderName;
	}
	//End Region Getters and Setters
	
}
