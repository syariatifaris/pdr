package com.seoultechappsoftlab.wireloc.activity.canvases;

import org.mapsforge.android.maps.Projection;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.seoultechappsoftlab.wireloc.infrastructures.OverlayCanvasBase;

/**
 * Arrow Overlay
 * @author SeoulTech Application Software Lab
 *
 */
public class ArrowOverlayCanvas extends OverlayCanvasBase {
	
	//Region Private Variables
	
	private Bitmap arrowBitmap;
	private Point arrowPosition;
	
	//End Region Private Variables

	//Region Constructors

	public ArrowOverlayCanvas(Bitmap bitmap){
		super();
		this.arrowBitmap = bitmap;
	}
	
	//End Region Constructors
	
	/**
	 * Draw Overlay Bitmap
	 */
	@Override
	protected void drawOverlayBitmap(Canvas canvas, Point point, Projection projection, byte zoomLevel) {
		super.drawOverlayBitmap(canvas, point, projection, zoomLevel);
		if(this.arrowBitmap != null){
			if(this.arrowPosition == null){
				canvas.drawBitmap(this.arrowBitmap, 50, 50, new Paint());
			}else{
				canvas.drawBitmap(this.arrowBitmap, this.arrowPosition.x, this.arrowPosition.y, new Paint());
			}
		}
	}
	
	//Region Getters and Setters
	
	public Bitmap getArrowBitmap() {
		return arrowBitmap;
	}

	public void setArrowBitmap(Bitmap arrowBitmap) {
		this.arrowBitmap = arrowBitmap;
		this.invalidate();
	}
	
	public Point getArrowPosition() {
		return arrowPosition;
	}

	public void setArrowPosition(Point arrowPosition) {
		this.arrowPosition = arrowPosition;
	}
	
	//End Region Getters and Setters
	
}
