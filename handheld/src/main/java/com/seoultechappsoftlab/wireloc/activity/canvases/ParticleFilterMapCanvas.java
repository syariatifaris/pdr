package com.seoultechappsoftlab.wireloc.activity.canvases;

import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.mapgenerator.MapGenerator;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class ParticleFilterMapCanvas extends MapView{
	
	//Region Constructors
	
	/**
	 * Constructor
	 * @param context Context
	 */
	public ParticleFilterMapCanvas(Context context) {
		super(context);
	}
	
	/**
	 * Constructor
	 * @param context Constructors
	 * @param mapGenerator MapGenerator
	 */
	public ParticleFilterMapCanvas(Context context, MapGenerator mapGenerator){
		super(context, mapGenerator);
	}
	
	/**
	 * Constructors
	 * @param context Context
	 * @param attributeSet AttributeSet
	 */
	public ParticleFilterMapCanvas(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
	}
	
	//End Region Constructors
	
	/**
	 * On Draw
	 * @param canvas Canvas
	 */
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
	}
	
	/**
	 * Draw Child
	 */
	@Override
	public void dispatchDraw(Canvas pCanvas){
		super.dispatchDraw(pCanvas);       
	}
}
