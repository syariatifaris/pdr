package com.seoultechappsoftlab.wireloc.activity.canvases;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.seoultechappsoftlab.wireloc.entities.Track;

/**
 * PDR Canvas for PDR Activity
 * @author SeoulTech Application Software Lab
 *
 */
@SuppressLint("DrawAllocation")
public class PDRCanvas extends View {
	
	public int CANVAS_WIDTH = 1080;
	public int CANVAS_HEIGHT = 1577;
	
	List<Track> trackList;
	private Bitmap arrowBitmap;	
	
	/**
	 * Get Track List
	 * @return
	 */
	public List<Track> getTrackList() {
		return trackList;
	}

	/**
	 * Set Track List and Redraw Canvas
	 * @param trackList
	 */
	public void setTrackList(List<Track> trackList) {
		this.trackList = trackList;
		this.invalidate();
	}

	/**
	 * Get Arrow Bitmap
	 * @return
	 */
	public Bitmap getArrowBitmap() {
		return arrowBitmap;
	}

	/**
	 * Set Arrow Bitmap and Redraw Canvas
	 * @param arrowBitmap
	 */
	public void setArrowBitmap(Bitmap arrowBitmap) {
		this.arrowBitmap = arrowBitmap;
		this.invalidate();
	}

	/**
	 * Constructor
	 * @param context
	 */
	public PDRCanvas(Context context) {
		super(context);
	}
	
	/**
	 * Constructor
	 * @param context
	 * @param attrs
	 */
	public PDRCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas canvas) {
		
		Paint pnt = new Paint();
		pnt.setAntiAlias(true);
		pnt.setStrokeWidth(10);
		pnt.setColor(Color.BLUE);
		if(trackList != null){
			int getSize = trackList.size();
			if (getSize > 0 && arrowBitmap != null) {
				for(int i = 0 ; i < getSize; i++){
					if(i == getSize-1){
						canvas.drawBitmap(arrowBitmap, trackList.get(i).getX() - 22, trackList.get(i).getY() - 35, new Paint());
					} else{
						canvas.drawLine(trackList.get(i).getX(), trackList.get(i).getY(), trackList.get(i+1).getX(), trackList.get(i+1).getY(), pnt);
					}
				}
			}
		}
	}

}
