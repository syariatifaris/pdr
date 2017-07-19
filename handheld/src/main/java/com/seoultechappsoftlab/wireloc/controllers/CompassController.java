package com.seoultechappsoftlab.wireloc.controllers;

import com.seoultechappsoftlab.wireloc.models.CompassModel;

import android.app.Application;
import android.content.Context;
import android.view.animation.RotateAnimation;

/**
 * Compass Activity's Controller
 * @author SeoulTech Application Software Lab
 *
 */
public class CompassController extends Application{

	private CompassModel compassModel;
	private AfterCompassRotating afterCompassRotatingCallback;
	
	/**
	 * Constructor
	 * @param context
	 */
	public CompassController(Context context, AfterCompassRotating afterCompassRotatingCallback){
		this.compassModel = new CompassModel(context);
		this.afterCompassRotatingCallback = afterCompassRotatingCallback;
	}
	
	//Region Action Method
	
	public void actionRotateCompassImage(){
		this.compassModel.rotateCompass();
		this.afterCompassRotatingCallback.setImageAnimation();
		this.compassModel.setCurrentDegree(- this.compassModel.getHeadingDegree());
	}
	
	public void actionRotateCompass(float[] gravityValues, float[] geoMagneticValues){
		this.compassModel.rotateCompass(gravityValues, geoMagneticValues);
	}
	
	//End Region Action Method
	
	//Region Model Accessors
	
	public RotateAnimation getRotateAnimation(){
		return this.compassModel.getRotateAnimation();
	}
	
	public float getHeadingDegree(){
		return this.compassModel.getHeadingDegree();
	}
	
	//End Region Model Accessors
	
	public interface AfterCompassRotating{
		void setImageAnimation();
	}
}
