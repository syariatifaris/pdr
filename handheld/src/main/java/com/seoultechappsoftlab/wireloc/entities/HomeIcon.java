package com.seoultechappsoftlab.wireloc.entities;

import android.graphics.Bitmap;

import com.seoultechappsoftlab.wireloc.infrastructures.EntityBase;

/**
 * Home Icon
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class HomeIcon extends EntityBase {
	// Region Private Fields
	private String name;
	private int imageResourceId;
	private Bitmap iconBitmap;
	private Class<?> actionClass;
	
	// End Region Private Fields

	// Region Constructor
	/**
	 * Constructor
	 */
	public HomeIcon() {
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param imageResourceId
	 */
	public HomeIcon(String name, int imageResourceId, Class<?> actionClass) {
		this.name = name;
		this.imageResourceId = imageResourceId;
		this.actionClass = actionClass;
	}

	// End Region Constructor

	// Region Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getImageResourceId() {
		return imageResourceId;
	}

	public void setImageResourceId(int imageResourceId) {
		this.imageResourceId = imageResourceId;
	}
	
	public Bitmap getIconBitmap() {
		return iconBitmap;
	}

	public void setIconBitmap(Bitmap iconBitmap) {
		this.iconBitmap = iconBitmap;
	}
	
	public Class<?> getActionClass() {
		return actionClass;
	}

	public void setActionClass(Class<?> actionClass) {
		this.actionClass = actionClass;
	}
	// End Region Getters and Setters
}
