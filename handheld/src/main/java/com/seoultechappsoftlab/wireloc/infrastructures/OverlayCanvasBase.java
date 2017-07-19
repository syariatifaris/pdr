package com.seoultechappsoftlab.wireloc.infrastructures;

import org.mapsforge.android.maps.Projection;
import org.mapsforge.android.maps.overlay.Overlay;
import org.mapsforge.core.model.GeoPoint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Overlay Canvas Base
 * Base Class to Handle Overlay Canvas Objects
 * @author SeoulTech Application Software Lab
 *
 */
public class OverlayCanvasBase extends Overlay {
	
	//Region Private & Protected Variables
	
	private Bitmap bitmap;
	private PointF position;
	private GeoPoint geoPoint;
	private Projection projection;

	protected Canvas canvas;
	
	//End Region Private Variables
	
	//Region Constructors
	
	/**
	 * Constructor
	 */
	public OverlayCanvasBase(){
	}
	
	/**
	 * Constructors
	 * @param bitmap Bitmap
	 * @param position Position (Double)
	 * @param geoPoint GeoPoint
	 */
	public OverlayCanvasBase(Bitmap bitmap, PointF position, GeoPoint geoPoint){
		super();
		this.bitmap = bitmap;
		this.position = position;
		this.geoPoint = geoPoint;
	}
	
	/**
	 * Constructors
	 * @param bitmap Bitmap
	 * @param geoPoint GeoPoint
	 */
	public OverlayCanvasBase(Bitmap bitmap, GeoPoint geoPoint){
		super();
		this.bitmap = bitmap;
		this.geoPoint = geoPoint;
	}
	
	//End Region Constructors
	
	//Region Getters and Setters
	
	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public PointF getPosition() {
		return position;
	}

	public void setPosition(PointF position) {
		this.position = position;
	}

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
		//this.requestRedraw();
	}
	
	
	public Projection getProjection() {
		return projection;
	}

	public void setProjection(Projection projection) {
		this.projection = projection;
	}
	
	public void invalidate(){
		if(this.canvas != null){
			this.requestRedraw();
		}
	}
	
	//End Region Getters and Setters
	
	@Override
	protected void drawOverlayBitmap(Canvas canvas, Point point, Projection projection, byte zoomLevel) {
		this.canvas = canvas;
		this.projection = projection;
	}
}
