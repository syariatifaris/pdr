package com.seoultechappsoftlab.wireloc.entities;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Entity / View Object - Obstacle
 * @author SeoulTechAppSoftLab
 *
 */
public class RoomObject {

	private Bitmap object;
	private Point objectPosition;
	
	/**
	 * Constructor
	 * @param object
	 * @param resourceId
	 * @param objectPosition
	 */
	public RoomObject(Bitmap object, Point objectPosition){
		this.object = object;
		this.objectPosition = objectPosition;
	}
	
	/**
	 * Get the object (obstacle)
	 * @return
	 */
	public Bitmap getObject() {
		return this.object;
	}
	
	/**
	 * Set the object
	 * @param object
	 */
	public void setObject(Bitmap object) {
		this.object = object;
	}

	/**
	 * Get bounded rectangle of the object
	 * @return
	 */
	public Rect getObjectRectangle() {
		int height = this.object.getHeight();
		int width = this.object.getWidth();
		int right = this.objectPosition.x + width;
		int bottom = this.objectPosition.y + height;
		return new Rect((int)this.objectPosition.x, (int)this.objectPosition.y, right, bottom);
	}
	
	/**
	 * Get object left, top position
	 * @return
	 */
	public Point getObjectPosition() {
		return this.objectPosition;
	}
	
	/**
	 * Set the object position
	 * @param objectPosition
	 */
	public void setObjectPosition(Point objectPosition) {
		this.objectPosition = objectPosition;
	}

}
