package com.seoultechappsoftlab.wireloc.helpers;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Detect Collision
 * 
 * @author SeoulTechAppSoftLab
 *
 */
public class CollisionDetectionHelper {

	// region private variable

	private Rect rectangle;
	private Point point;
	private double circleX;
	private double circleY;
	private double circleRadius;

	// end region private variable

	// region constructor

	/**
	 * empty constructor
	 */
	public CollisionDetectionHelper() {
	}

	/**
	 * constructor for rectangle - point comparison
	 * 
	 * @param rectangle
	 * @param point
	 */
	public CollisionDetectionHelper(Rect rectangle, Point point) {
		this.setRectangle(rectangle);
		this.setPoint(point);
	}

	/**
	 * constructor for circle - point comparison
	 * 
	 * @param circleX
	 * @param circleY
	 * @param circleRadius
	 * @param point
	 */
	public CollisionDetectionHelper(double circleX, double circleY,
			double circleRadius, Point point) {
		this.setCircleX(circleX);
		this.setCircleY(circleY);
		this.setCircleRadius(circleRadius);
		this.setPoint(point);
	}

	// region getter and setter

	/**
	 * Get the Rectangle
	 * 
	 * @return
	 */
	public Rect getRectangle() {
		return this.rectangle;
	}

	/**
	 * Set the Rectangle
	 * 
	 * @param rectangle
	 */
	public void setRectangle(Rect rectangle) {
		this.rectangle = rectangle;
	}

	/**
	 * Get the point
	 * 
	 * @return
	 */
	public Point getPoint() {
		return this.point;
	}

	/**
	 * Set the point
	 * 
	 * @param point
	 */
	public void setPoint(Point point) {
		this.point = point;
	}

	/**
	 * Get the central of circle's X coordinate
	 * 
	 * @return
	 */
	public double getCircleX() {
		return this.circleX;
	}

	/**
	 * Set the central of circle's X coordinate
	 * 
	 * @param circleX
	 */
	public void setCircleX(double circleX) {
		this.circleX = circleX;
	}

	/**
	 * Get the central of circle's Y coordinate
	 * 
	 * @return
	 */
	public double getCircleY() {
		return circleY;
	}

	/**
	 * Set the central of circle's Y coordinate
	 * 
	 * @param circleY
	 */
	public void setCircleY(double circleY) {
		this.circleY = circleY;
	}

	/**
	 * Get the central of circle's radius size
	 * 
	 * @return
	 */
	public double getCircleRadius() {
		return this.circleRadius;
	}

	/**
	 * Set the circle radius
	 * 
	 * @param circleRadius
	 */
	public void setCircleRadius(double circleRadius) {
		this.circleRadius = circleRadius;
	}

	// end region - getter and setter

	// region check collision function

	/**
	 * Check if the point is in rectangle
	 * 
	 * @return
	 */
	public boolean isInsideRectangle() {
		return this.rectangle.contains(this.point.x, this.point.y);
	}

	/**
	 * Check if the point is inside a circle
	 * 
	 * @return
	 */
	public boolean isInsideCircle() {
		return Math.pow(this.point.x - this.circleX, 2)
				+ Math.pow(this.point.y - this.circleY, 2) <= Math.pow(
				this.circleRadius, 2);
	}

	/**
	 * Get nearest point outside perimiter
	 * 
	 * @param rect
	 * @param point
	 * @return
	 */
	public Point getNearestPointOutPerimiter(Rect rect, Point point) {
		return new Point((int) this.nearest(point.x, rect.left, rect.left
				+ rect.width()), (int) this.nearest(point.y, rect.top, rect.top
				+ rect.height()));
	}

	// region check collision function
	
	/**
	 * get nearest point
	 * @param x
	 * @param a
	 * @param b
	 * @return
	 */
	private double nearest(double x, double a, double b) {
		if (a <= x && x <= b) {
			return x;
		} else if (Math.abs(a - x) < Math.abs(b - x)) {
			return a;
		} else {
			return b;
		}
	}
	
	/**
	 * The Clamp Position
	 * @param x
	 * @param lower
	 * @param upper
	 * @return
	 */
	private int clamp(int x, int lower, int upper){
		return Math.max(lower, Math.min(upper, x));
	}
	
	public Point getNearestPointInPerimiter(Rect rect, Point currPoint){
		int r = rect.left + rect.right;
		int b = rect.top + rect.bottom;
		
		int x = this.clamp(currPoint.x, rect.left, r);
		int y = this.clamp(currPoint.y, rect.top, b);
		
		int dl, dr, dt, db;
		dl = Math.abs(x - rect.left);
		dr = Math.abs(x - r);
		dt = Math.abs(y - rect.top);
		db = Math.abs(y - b);
		
		int m = Math.min(Math.min(dl, dr), Math.min(dt, db));
		
		if(m == dt){
			return new Point(x, rect.top);
		}
		
		if(m == db){
			return new Point(x, b);
		}
		
		if(m == dl){
			return new Point(rect.left, y);
		}
		
		//else
		return new Point(r, y);
	}
}
