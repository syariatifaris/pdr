package com.seoultechappsoftlab.wireloc.models;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class CompassModel {
	private Context context;
	private RotateAnimation rotateAnimation;
	private float headingDegree;
	private float currentDegree = 0f;
	
	private static final int DURATION = 210;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public CompassModel(Context context) {
		this.context = context;
	}

	/**
	 * Get the Context
	 * 
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Set the Context
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * Get Rotate Animation
	 * 
	 * @return
	 */
	public RotateAnimation getRotateAnimation() {
		return rotateAnimation;
	}

	/**
	 * Set Rotate Animation
	 * 
	 * @param rotateAnimation
	 */
	public void setRotateAnimation(RotateAnimation rotateAnimation) {
		this.rotateAnimation = rotateAnimation;
	}

	/**
	 * Get Heading Degree
	 * 
	 * @return
	 */
	public float getHeadingDegree() {
		return headingDegree;
	}

	/**
	 * Set Heading Degree
	 * 
	 * @param headingDegree
	 */
	public void setHeadingDegree(float headingDegree) {
		this.headingDegree = headingDegree;
	}

	/**
	 * Get Current Degree
	 * 
	 * @return
	 */
	public float getCurrentDegree() {
		return currentDegree;
	}

	/**
	 * Set Current Degree
	 * 
	 * @param currentDegree
	 */
	public void setCurrentDegree(float currentDegree) {
		this.currentDegree = currentDegree;
	}

	/**
	 * Rotate The Compass
	 */
	public void rotateCompass() {
		this.rotateAnimation = new RotateAnimation(this.currentDegree, -this.headingDegree, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		this.rotateAnimation.setDuration(DURATION);
		this.rotateAnimation.setFillAfter(true);
	}
	
	/**
	 * Rotate The Compass
	 * @param gravityValues
	 * @param geoMagneticValues
	 */
	public void rotateCompass(float [] gravityValues, float[] geoMagneticValues){
		float R[] = new float[9];
		float I[] = new float[9];
		
		boolean success = SensorManager.getRotationMatrix(R, I, gravityValues, geoMagneticValues);
		if(success){
			float orientation[] = new float[3];
			SensorManager.getOrientation(R, orientation);
			//this.headingDegree = orientation[0];
			this.headingDegree = (float)Math.toDegrees(orientation[0]);
		}
	}
}
