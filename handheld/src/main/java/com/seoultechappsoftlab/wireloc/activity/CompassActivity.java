package com.seoultechappsoftlab.wireloc.activity;

import com.seoultechappsoftlab.wireloc.controllers.CompassController;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Compass Activity
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class CompassActivity extends Activity implements SensorEventListener {

	// Region Private Variables

	private CompassController controller;

	private SensorManager sensorManager;
	private ImageView imageView;
	private TextView tvHeadingDegree;

	private Sensor accelerometer;
	private Sensor magnetometer;

	private float[] gravityValues;
	private float[] geoMagneticValues;

	// End Region Private Variables

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_compass);
		this.initializeSensors();

		this.imageView = (ImageView) this.findViewById(R.id.iVCompassImage);
		this.tvHeadingDegree = (TextView) this.findViewById(R.id.tvHeadingDegree);

		this.initializeController();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.registerSensors();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		this.unRegisterSensors();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			this.gravityValues = event.values.clone();
		}

		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			this.geoMagneticValues = event.values.clone();
		}

		if (this.gravityValues != null && this.geoMagneticValues != null) {
			this.controller.actionRotateCompass(this.gravityValues, this.geoMagneticValues);

			this.tvHeadingDegree.setText("Heading: " + Float.toString(this.controller.getHeadingDegree()) + " degree");

			this.controller.actionRotateCompassImage();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	// Region Private Method

	/**
	 * Register Sensors
	 */
	private void registerSensors() {
		this.sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		this.sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
	}
	
	private void unRegisterSensors(){
		this.sensorManager.unregisterListener(this);
	}

	/**
	 * Initialize Sensors
	 */
	private void initializeSensors() {
		this.sensorManager = (SensorManager) this.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
		this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		this.magnetometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	/**
	 * Initialize Controller
	 */
	private void initializeController() {
		this.controller = new CompassController(this, new CompassController.AfterCompassRotating() {
			@Override
			public void setImageAnimation() {
				imageView.setAnimation(controller.getRotateAnimation());
			}
		});
	}

	// End Region Private Method
}
