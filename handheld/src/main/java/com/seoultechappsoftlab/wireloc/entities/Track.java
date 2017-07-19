package com.seoultechappsoftlab.wireloc.entities;

import android.graphics.Bitmap;

/**
 * Track Class
 * @author SeoulTech Application Software Lab
 *
 */
public class Track {
	private int x;
	private int y;
	private int azimuth = 0;
	private Bitmap bitmap = null;
	
	/**
	 * Constructor
	 */
	public Track(){
		
	}
	
	/**
	 * Constructor
	 * @param x
	 * @param y
	 * @param azimuth
	 */
	public Track(int x, int y, int azimuth) {
		super();
		this.x = x;
		this.y = y;
		this.azimuth = azimuth;
	}

	/**
	 * Constructor
	 * @param x
	 * @param y
	 */
	public Track(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructor
	 * @param x
	 * @param y
	 * @param azimuth
	 * @param bitmap
	 */
	public Track(int x, int y, int azimuth, Bitmap bitmap) {
		super();
		this.x = x;
		this.y = y;
		this.azimuth = azimuth;
		this.bitmap = bitmap;
	}

	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getAzimuth() {
		return azimuth;
	}
	
	public void setAzimuth(int azimuth) {
		this.azimuth = azimuth;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	
}
