package com.seoultechappsoftlab.wireloc.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.seoultechappsoftlab.wireloc.activity.canvases.PDRCanvas;
import com.seoultechappsoftlab.wireloc.controllers.PDRController;

/**
 * PDR Implementation Activity
 * @author SeoulTech Application Software Lab
 *
 */
public class PDRActivity extends Activity implements SensorEventListener{

	private PDRController controller;
	private PDRCanvas canvas;

	// Region Sensor
	private SensorManager sensorManager;
	private final int delay = SensorManager.SENSOR_DELAY_FASTEST;

	private Sensor onSensorAccelLinear;
	private Sensor onSensorMagnetic;
	private Sensor onSensorAccel;

	private float[] accelLinearData;
	private float[] accelData;
	private float[] magneticData;
	// End Region Sensor

	private boolean onCreateIsReady;
	
	/**
	 * Constructor
	 */
	public PDRActivity (){
		
	}
	
	/**
	 * On Create Event
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track);
		this.initializeController();
		
		// register sensors
		this.initializeSensors();
		
		// implements canvas
		this.canvas = new PDRCanvas(this);
		this.canvas = (PDRCanvas) this.findViewById(R.id.trackView);
		this.onCreateIsReady = true;
		
	}

	/**
	 * On Sensor Event
	 */
	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			this.accelLinearData = sensorEvent.values.clone();
		}
		if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			this.accelData = sensorEvent.values.clone();
		}
		if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			this.magneticData = sensorEvent.values.clone();
		}

		if (this.onCreateIsReady) {
			if (this.accelLinearData != null && this.accelData != null && this.magneticData != null) {
				this.controller.actionReadStepDetection(accelLinearData, accelData, magneticData);
			}
		}
		
		this.controller.actionSetArrowAngle(accelData, magneticData);
		this.canvas.setTrackList(this.controller.getTrackList());;
		this.canvas.setArrowBitmap(this.controller.getArrowBitmap());
	}
	

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * On Resume
	 * Register Sensor
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.registerSensors();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.clearActivity();
	}

	@Override
	public void onBackPressed() {
		this.clearActivity();
	}
	
	/**
	 * Initialize Sensor
	 */
	@SuppressLint("InlinedApi")
	private void initializeSensors() {
		this.sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
		this.onSensorAccelLinear = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		this.onSensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		this.onSensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
	
	/**
	 * Register Sensor
	 */
	private void registerSensors() {
		this.sensorManager.registerListener(this, onSensorAccelLinear, delay);
		this.sensorManager.registerListener(this, onSensorMagnetic, delay);
		this.sensorManager.registerListener(this, onSensorAccel, delay);
	}

	/**
	 * Unregister Sensor
	 */
	private void unRegisterSensor() {
		this.sensorManager.unregisterListener(this);
	}

	/**
	 * Clear Activity
	 */
	private void clearActivity() {
		this.unRegisterSensor();
		this.finish();
	}
	
	/**
	 * Initialize Particle Filter Controller
	 */
	private void initializeController() {
		this.controller = new PDRController(this);
	}
}
