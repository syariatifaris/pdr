package com.seoultechappsoftlab.wireloc.infrastructures;

import org.mapsforge.core.model.GeoPoint;

import android.graphics.Point;

/**
 * Class Entity Base
 * @author SeoulTech Application Software Lab
 *
 */
public class EntityBase {
	/**
	 * The ID
	 */
	private int id;
	
	/**
	 * The Position on Latitude Longitude
	 */
	private GeoPoint geoPoint;

	/**
	 * The Displayed Point in Px
	 */
	private Point displayPoint;
	
	/**
	 * Get The ID
	 * @return
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Set The ID
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	//Region Map Purpose Field
	
	/**
	 * The GeoPoint
	 * @return GeoPoint
	 */
	public GeoPoint getGeoPoint() {
		return this.geoPoint;
	}
	
	/**
	 * Set GeoPoint
	 * @param geoPoint
	 */
	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}
	
	/**
	 * Get Display Point
	 * @return
	 */
	public Point getDisplayPoint() {
		return this.displayPoint;
	}
	
	/**
	 * Set Display Point
	 * @param displayPoint
	 */
	public void setDisplayPoint(Point displayPoint) {
		this.displayPoint = displayPoint;
	}
	
	//End Region Map Purpose Field
}
