package com.seoultechappsoftlab.wireloc.activity;

import java.util.ArrayList;

import com.seoultechappsoftlab.wireloc.activity.canvases.ParticleFilterCanvas;
import com.seoultechappsoftlab.wireloc.activity.dialogs.FingerprintDialog;
import com.seoultechappsoftlab.wireloc.activity.dialogs.FingerprintDialog.ovdListener;
import com.seoultechappsoftlab.wireloc.controllers.ParticleFilterController;
import com.seoultechappsoftlab.wireloc.entities.Particle;
import com.seoultechappsoftlab.wireloc.utilities.BluetoothUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

/**
 * Particle Filter Implementation Activity
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class ParticleFilterActivity extends Activity implements OnTouchListener, SensorEventListener {

	private static final int POST_DELAY = 100;

	private ParticleFilterController controller;
	private BluetoothUtils bluetoothUtils;
	private ParticleFilterCanvas canvas;
	private boolean firstViewSize = true;
	private final Handler rssiHandler = new Handler();

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

	private boolean fingerprintMode;
	private boolean radarMode;
	private boolean labSettingActive;
	private boolean canvasIsReady;
	private boolean onCreateIsReady;

	/**
	 * Constructor
	 */
	public ParticleFilterActivity() {
		this.firstViewSize = true;
		this.fingerprintMode = false;
		this.radarMode = false;
		this.labSettingActive = false;
		this.canvasIsReady = false;
		this.onCreateIsReady = false;
	}

	/**
	 * On Create Event
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_particle_filter);
		this.initializeController();

		// activate bluetooth
		this.checkBluetoothCondition();

		// register sensors
		this.initializeSensors();

		// implements canvas
		this.canvas = new ParticleFilterCanvas(this);
		this.canvas = (ParticleFilterCanvas) this.findViewById(R.id.particleView);
		this.canvas.setOnTouchListener(this);
		
		this.onCreateIsReady = true;
	}

	/**
	 * On Sensor Change
	 * Handle the Step Detection by Calling the Controller
	 * And Set the Canvas property when Changes is Done
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

		if (this.canvasIsReady && this.onCreateIsReady) {
			if (this.fingerprintMode == false && this.radarMode == false && this.labSettingActive) {
				if (this.accelLinearData != null && this.accelData != null && this.magneticData != null) {
					synchronized (this.controller.getActiveBeacons()) {
						this.controller.actionReadStepDetection(accelLinearData, accelData, magneticData);
					}
				}
			}

			this.controller.actionSetArrowAngle(accelData, magneticData);
			this.canvas.setParticleFilterList(this.controller.getParticleFilterList());
			this.canvas.setArrowBitmap(this.controller.getArrowBitmap());
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * On Window Focus Changed
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (this.firstViewSize) {
			this.firstViewSize = false;
		}
	}

	/**
	 * On Touch Event
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.particleView) {
			int pointX = (int) (event.getX() / com.seoultechappsoftlab.wireloc.utilities.PointUtils.SCALE_X);
			int pointY = (int) (event.getY() / com.seoultechappsoftlab.wireloc.utilities.PointUtils.SCALE_Y);
			if (fingerprintMode) {
				ovdListener ovdlListener = new ovdListener() {
					@Override
					public void ovdResult() {
					}
				};
				FingerprintDialog psd = new FingerprintDialog(this, this.controller.getActiveBeacons(), true, pointX, pointY,
						this.controller.getCurrentStage(), ovdlListener);
				psd.setTitle(this.getString(R.string.dialog_fingerprint_title));
				psd.show();
			}
		}
		return false;
	}

	/**
	 * On Resume
	 * On Application Resume Register Sensors and Update RSSI Reading
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.registerSensors();
		this.controller.actionActivateBluetoothReceiverIsNotActive();
		this.rssiHandler.post(this.updateNearbyBeaconRSSI);
	}

	/**
	 * On Pause
	 * On Application Pause Stop the Update RSSI
	 */
	@Override
	protected void onPause() {
		super.onPause();
		this.controller.actionActivateBluetoothReceiverIsNotActive();
		this.rssiHandler.removeCallbacks(this.updateNearbyBeaconRSSI);
	}

	/**
	 * On Destroy
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.clearActivity();
	}

	/**
	 * On Back Pressed
	 */
	@Override
	public void onBackPressed() {
		this.clearActivity();
	}

	/**
	 * Menu Item Property
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, R.string.fingerprint_mode);
		menu.add(0, 2, 0, R.string.radar_mode);
		menu.add(0, 3, 0, R.string.calculate_kriging);
		menu.add(0, 4, 0, R.string.show_room_id);
		menu.add(0, 5, 0, R.string.change_stage);
		return true;
	}

	/**
	 * Menu Handling Method
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case 1:
			this.switchActivityMode(ActivityMode.FINGERPRINT);
			return true;
		case 2:
			this.switchActivityMode(ActivityMode.RADAR);
			return true;
		case 3:
			this.controller.actionCalculateMatrix();
			return true;
		case 4:
			Toast.makeText(this, "Location: " + this.controller.getCurrentStage(), Toast.LENGTH_SHORT).show();
			return true;
		case 5:
			this.switchActivityMode(ActivityMode.CHANGE_STAGE);
			return true;
		default:
			return false;
		}
	}

	/**
	 * Check the bluetooth condition in the first trial
	 */
	private void checkBluetoothCondition() {
		this.bluetoothUtils = new BluetoothUtils(this);
		if (!this.bluetoothUtils.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1000);
		}
	}

	/**
	 * Initialize Particle Filter Controller
	 */
	private void initializeController() {
		this.controller = new ParticleFilterController(this);
	}

	@SuppressLint("InlinedApi")
	private void initializeSensors() {
		this.sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
		this.onSensorAccelLinear = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		this.onSensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		this.onSensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	/**
	 * Register Sensors
	 */
	private void registerSensors() {
		this.sensorManager.registerListener(this, onSensorAccelLinear, delay);
		this.sensorManager.registerListener(this, onSensorMagnetic, delay);
		this.sensorManager.registerListener(this, onSensorAccel, delay);
	}

	/**
	 * Unregister Sensors
	 */
	private void unRegisterSensor() {
		this.sensorManager.unregisterListener(this);
	}

	/**
	 * Clear Activity
	 */
	private void clearActivity() {
		this.rssiHandler.removeCallbacks(this.updateNearbyBeaconRSSI);
		this.unRegisterSensor();
		this.finish();
	}

	/**
	 * Switch Activity Mode
	 * 
	 * @param mode
	 */
	private void switchActivityMode(ActivityMode mode) {
		switch (mode) {
		case FINGERPRINT:
			this.fingerprintMode = !this.fingerprintMode;
			if (this.fingerprintMode) {
				this.radarMode = false;
			}
			this.showOnOfMessage(ActivityMode.FINGERPRINT);
			break;
		case RADAR:
			this.radarMode = !this.radarMode;
			if (this.radarMode) {
				this.fingerprintMode = false;
			}
			this.showOnOfMessage(ActivityMode.RADAR);
			this.controller.actionReInitializeParticleFilter(this.canvas.getWidth(), this.canvas.getHeight());
			this.canvas.setParticleFilterList(this.controller.getParticleFilterList());
			this.canvas.setRadarMode(this.radarMode);
			break;
		case CHANGE_STAGE:
			this.controller.setCurrentStage(2);
			this.canvas.setRoomObjects(this.controller.actionPutStageRoomObjects(this.controller.getObstaclesFromXML()));
			this.canvas.setPersonBitmap(this.controller.getPersonBitmap());
			this.labSettingActive = this.controller.actionSetLabSetting();
			this.canvas.setPreloadBeacons(this.controller.getActiveBeacons());
			break;
		default:
			break;
		}

		if (this.fingerprintMode || this.radarMode) {
			this.canvas.setPauseState(true);
		} else {
			this.canvas.setPauseState(false);
		}
	}
	
	/**
	 * This Method will Show The Message whenever The Mode is Change
	 * @param mode
	 */
	private void showOnOfMessage(ActivityMode mode) {
		String modeMessage = this.getString(R.string.mode_message).toString();
		String state = null;
		switch (mode) {
		case FINGERPRINT:
			state = modeMessage.replace("[mode]", this.getString(R.string.fingerprint_mode));
			state = state.replace("[state]", this.fingerprintMode ? this.getString(R.string.on_state) : this.getString(R.string.off_state));
			break;
		case RADAR:
			state = modeMessage.replace("[mode]", this.getString(R.string.radar_mode));
			state = state.replace("[state]", this.radarMode ? this.getString(R.string.on_state) : this.getString(R.string.off_state));
			break;
		default:
			break;
		}
		Toast.makeText(this, state, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Update Nearby Beacon RSSI
	 * This Thread to Update the Beacon Reading and Update The View by Redraw
	 */
	private Runnable updateNearbyBeaconRSSI = new Runnable() {
		@Override
		public void run() {
			if (onCreateIsReady) {
				// first lab setting will not active because device has not
				// detect the stage
				if (labSettingActive) {
					controller.actionListenNearbyBeaconRSSIValue(radarMode ? ActivityMode.RADAR : null);
					canvas.setParticleFilterList(controller.getParticleFilterList());
					// if the room is changing
					if (controller.isRoomChanging()) {
						drawAllWhenRoomChanges();
					}
				} else {
					if (controller.getCurrentStage() == 0) {
						labSettingActive = controller.actionSetLabSettingIfStageIsNotDetermined();
						drawFirst();
					}
				}

				rssiHandler.postDelayed(this, POST_DELAY);
			}
		}
	};

	/**
	 * Draw All Wen Room Changes
	 * This Method Redraw The View when The Room is Changed
	 */
	private void drawAllWhenRoomChanges() {
		this.drawParticleFilterCanvas();
		this.controller.setRoomChanging(false);
		this.canvasIsReady = true;
	}

	/**
	 * Draw First
	 * This Method Draw the View for the First Time
	 */
	private void drawFirst() {
		this.drawParticleFilterCanvas();
		this.canvasIsReady = true;
	}

	/**
	 * Draw Particle Filter Canvas
	 * This Method Redraw The View and Set the Canvas particle, Obstacle, Detail
	 * to Their Respective Property
	 */
	private void drawParticleFilterCanvas() {
		// this.controller.initializeParticleFilter();
		this.canvas.setPreloadBeacons(this.controller.getActiveBeacons());

		if (this.controller.getTemporarySavedParticleFilterList(this.controller.getCurrentStage()) != null) {
			this.canvas.setParticleList((ArrayList<Particle>) this.controller.getTemporarySavedParticleFilterList(this.controller.getCurrentStage()));
		} else {
			this.controller.initializeParticleFilter();
			this.controller.actionDisperseParticleFirst(this.canvas.CANVAS_WIDTH, this.canvas.CANVAS_HEIGHT);
			this.canvas.setParticleFilterList(this.controller.getParticleFilterList());
		}

		this.canvas.setStageDetail(this.controller.getStage());
		this.canvas.setRoomObjects(this.controller.actionPutStageRoomObjects(this.controller.getObstaclesFromXML()));
		this.canvas.setPersonBitmap(this.controller.getPersonBitmap());
	}

	/**
	 * Activity Mode Type
	 * 
	 * @author SeoulTech Application Software Lab
	 *
	 */
	public enum ActivityMode {
		FINGERPRINT, RADAR, CHANGE_STAGE
	}
}
