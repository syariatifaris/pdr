package com.seoultechappsoftlab.wireloc.activity;

import java.io.File;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.map.reader.header.FileOpenResult;

import com.seoultechappsoftlab.wireloc.activity.canvases.ArrowOverlayCanvas;
import com.seoultechappsoftlab.wireloc.activity.canvases.BeaconOverlayCanvas;
import com.seoultechappsoftlab.wireloc.activity.canvases.ParticleFilterMapCanvas;
import com.seoultechappsoftlab.wireloc.activity.canvases.ParticleFilterOverlayCanvas;
import com.seoultechappsoftlab.wireloc.activity.dialogs.FingerprintDialog;
import com.seoultechappsoftlab.wireloc.activity.dialogs.FingerprintDialog.ovdListener;
import com.seoultechappsoftlab.wireloc.controllers.ParticleFilterMapController;
import com.seoultechappsoftlab.wireloc.helpers.GeoPositionHelper;
import com.seoultechappsoftlab.wireloc.utilities.MapsUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Particle Filter With Map View
 *
 * @author SeoulTech Application Software Lab
 */
public class ParticleFilterMapActivity extends MapActivity implements OnTouchListener, SensorEventListener, OnClickListener {

    // Region Private Variable

    private ParticleFilterMapCanvas mapView;
    private ParticleFilterMapController controller;

    private ParticleFilterOverlayCanvas particleOverlay;
    private BeaconOverlayCanvas beaconOverlay;
    private ArrowOverlayCanvas arrowOverlay;

    private final Handler rssiHandler = new Handler();
    private static final int POST_DELAY = 100;
    private static final int BOUNCE_DELAY = 2000;
    private static final int ROTATE_DELAY = 400;

    private boolean onCreateDone = false;
    private boolean fingerprintMode;
    private boolean radarMode;
    private boolean stepMode;
    private boolean editParticleMode = false;

    private long lastPressedTime;
    private long lastArrowRotatingTime;

    private boolean isRecording = false;

    // Region Sensor's Variables

    private SensorManager sensorManager;
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;

    private Sensor onSensorAccelLinear;
    private Sensor onSensorMagnetic;
    private Sensor onSensorAccel;

    float[] accelLinearData;
    float[] accelData;
    float[] magneticData;

    float[] rotationData = new float[9];
    float[] resultData = new float[3];

    int stepTotal = 0;

    // End Region Sensor's Variables

    //Region Edit Total Particle

    private EditText editTextTotalParticle;
    private Button buttonSaveTotalParticle;

    //End Region Edit Total Particle

    // End Region Private Variables

    // Region Constructors

    public ParticleFilterMapActivity() {
        super();
        this.onCreateDone = false;
        this.fingerprintMode = false;
        this.radarMode = false;
        this.stepMode = false;
        this.lastPressedTime = System.currentTimeMillis();
        this.lastArrowRotatingTime = System.currentTimeMillis();
    }

    // End Region Constructors

    // End Region Private Variable

    /**
     * On Create Event
     *
     * @param savedInstanceStateBundle
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeSensors();

        // Initialize Particle Filter Map View and Controller

        this.mapView = new ParticleFilterMapCanvas(this);
        this.controller = new ParticleFilterMapController(this);

        // End of Initialize Particle Filter Map View and Controller
        this.controller.actionEnableBluetooth(this);
        this.controller.actionSetTotalParticle(this.getSavedTotalParticle());

        this.setContentView(R.layout.activity_map_particle_filter);

        Button resetParticleButton = (Button) this.findViewById(R.id.resetParticleButton);
        resetParticleButton.setOnClickListener(this);
        resetParticleButton.setVisibility(View.GONE);

        this.editTextTotalParticle = (EditText)this.findViewById(R.id.totalParticleEditText);
        this.buttonSaveTotalParticle = (Button)this.findViewById(R.id.saveTotalParticleButton);
        this.buttonSaveTotalParticle.setOnClickListener(this);
        this.showEditParticleField(this.editParticleMode);

        this.mapView = (ParticleFilterMapCanvas) this.findViewById(R.id.particleFilterMapView);
        this.mapView.setOnTouchListener(this);
        this.configureMapView();

        if (!this.isMapFileOpenSuccess()) {
            this.finish();
        }

        this.mapView.getController().setZoom(MapsUtils.PARTICLE_FILTER_MAP_ZOOM_LEVEL);
        this.mapView.getController().setCenter(MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_CENTER);
        this.addOverlayItem();
        this.onCreateDone = true;
    }

    /**
     * On Sensor Change Call Back Function
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (this.onCreateDone) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                this.accelLinearData = sensorEvent.values.clone();
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                this.accelData = sensorEvent.values.clone();
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                this.magneticData = sensorEvent.values.clone();
            }

            if (this.accelLinearData != null && this.accelData != null && this.magneticData != null) {
                // prevent too frequent rotation
                long currentArrowRotateTime = System.currentTimeMillis();
                this.controller.actionSetArrowAngle(accelData, magneticData);
                if (currentArrowRotateTime - this.lastArrowRotatingTime > ROTATE_DELAY) {
                    this.redrawArrowBitmap(false);
                    this.lastArrowRotatingTime = currentArrowRotateTime;
                }

                synchronized (this.controller.getStageReferenceBeacon()) {
                    if (!this.radarMode && !this.fingerprintMode && this.stepMode) {
                        if (this.controller.actionStepping(this.accelLinearData, this.accelData, this.magneticData)) {
                            this.particleOverlay.setTotalWeightAndNormalDistribution(controller.getTotalWeightAndNormalDistribution());
                            this.redrawParticleOverlay();
                        }
                    }
                }
            }
        }
    }

    /**
     * On Sensor Accuracy Call Back Function
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        this.controller.actionDestroyReceiver();
    }

    /**
     * On Touch Event Call Back
     *
     * @param vView
     * @param eventMotionEvent
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.stepMode) {
            if (this.controller.isAfterReset()) {
                this.controller.setAfterReset(false);
            }
        }

        if (this.fingerprintMode) {
            if (v.getId() == R.id.particleFilterMapView) {
                long pressedTime = System.currentTimeMillis();

                // Prevent Bounce
                if (pressedTime - this.lastPressedTime > BOUNCE_DELAY) {
                    GeoPoint currentLocation = this.mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                    Point positionInMeter = GeoPositionHelper.convertLatLonPositionToXYInMeter(currentLocation, MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_00);
                    int xInMeter = positionInMeter.x / MapsUtils.MAP_SCALE;
                    int yInmeter = positionInMeter.y / MapsUtils.MAP_SCALE;

                    ovdListener ovdlListener = new ovdListener() {
                        @Override
                        public void ovdResult() {
                            // Do kriging
                        }
                    };

                    FingerprintDialog psd = new FingerprintDialog(this, this.controller.getStageReferenceBeacon(), true, xInMeter, yInmeter,
                            this.controller.getCurrentStageId(), ovdlListener);

                    psd.setTitle(this.getString(R.string.dialog_fingerprint_title));
                    psd.show();
                    this.lastPressedTime = pressedTime;
                }
            }
        }
        return false;
    }

    /**
     * On Click Call Back Event
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetParticleButton:
                this.resetParticles();
                break;
            case R.id.saveTotalParticleButton:
                if(this.saveTotalParticle(this.editTextTotalParticle.getText().toString())){
                    this.editParticleMode = !this.editParticleMode;

                    this.showEditParticleField(this.editParticleMode);
                    this.controller.actionSetTotalParticle(this.getSavedTotalParticle());
                    this.resetParticles();
                    this.hideEditText();

                    Toast.makeText(this, this.getString(R.string.set_total_particle_success), Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }

    /**
     * On Resume On Application Resume Register Sensors and Update RSSI Reading
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.registerSensors();
        this.controller.actionActivateBluetoothReceiverIsNotActive();
        this.rssiHandler.post(this.listenNearbyBeacons);
    }

    /**
     * On Pause On Application Pause Stop the Update RSSI
     */
    @Override
    protected void onPause() {
        super.onPause();
        this.controller.actionActivateBluetoothReceiverIsNotActive();
        this.rssiHandler.removeCallbacks(this.listenNearbyBeacons);
    }

    /**
     * On Destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unRegisterSensor();
        this.clearActivity();
        this.controller.actionDestroyReceiver();
    }

    /**
     * On Back Pressed
     */
    @Override
    public void onBackPressed() {
        this.clearActivity();
    }

    /**
     * On Create Options Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, R.string.step_mode);
        menu.add(0, 2, 0, R.string.fingerprint_mode);
        menu.add(0, 3, 0, R.string.radar_mode);
        menu.add(0, 4, 0, R.string.calculate_kriging);
        menu.add(0, 5, 0, R.string.hide_particle);
        menu.add(0, 6, 0, R.string.record_step);
        menu.add(0, 7, 0, R.string.trajectory);
        menu.add(0, 8, 0, R.string.reset_trajectory);
        menu.add(0, 9, 0, R.string.edit_particle_mode);
        return true;
    }

    /**
     * On Option Item Selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                this.stepMode = !this.stepMode;
                this.showOnOfMessage(ActivityMode.STEP);
                break;
            case 2:
                this.fingerprintMode = !this.fingerprintMode;
                this.showOnOfMessage(ActivityMode.FINGERPRINT);
                break;
            case 3:
                this.radarMode = !this.radarMode;
                this.showOnOfMessage(ActivityMode.RADAR);
                break;
            case 4:
                this.controller.actionCalculateMatrix();
                break;
            case 5:
                this.particleOverlay.setParticleHidden(!this.particleOverlay.isParticleHidden());
                this.showOnOfMessage(ActivityMode.HIDE_PARTICLE);
                break;
            case 6:
                this.controller.setStepRecorded(!this.controller.isStepRecorded());
                this.showOnOfMessage(ActivityMode.RECORD_STEP);
                if (this.controller.getCurrentFileDumpingText() != null) {
                    if (this.controller.getParticleStepRecord().size() != 0) {
                        if (this.controller.actionRecordStepDataToJson()) {
                            Toast.makeText(this, this.getString(R.string.step_recording_saved_successfully), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
            case 7:
                this.particleOverlay.setTrajectoryHidden(!this.particleOverlay.isTrajectoryHidden());
                this.showOnOfMessage(ActivityMode.TRAJECTORY);
                break;
            case 8:
                this.particleOverlay.resetTrajectoryParticle();
                Toast.makeText(this, this.getString(R.string.reset_trajectory), Toast.LENGTH_LONG).show();
                break;
            case 9:
                this.editParticleMode = !this.editParticleMode;
                this.showEditParticleField(this.editParticleMode);
                break;
            default:
                break;
        }
        return true;
    }

    // Region Private Methods & Functions

    /**
     * Check Whether Map File Access-able
     *
     * @return ? True : False
     */
    private boolean isMapFileOpenSuccess() {
        FileOpenResult openResult = this.mapView.setMapFile(this.getMapFile());
        if (openResult.isSuccess()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get Map File From Storage
     *
     * @return File
     */
    private File getMapFile() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        return new File(path + MapsUtils.MAP_ASSET_DIR + MapsUtils.PARTICLE_FILTER_MAP);
    }

    private void configureMapView() {
        this.mapView.setClickable(true);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.setFocusable(true);
    }

    private void clearActivity() {
        this.rssiHandler.removeCallbacks(this.listenNearbyBeacons);
        this.finish();
    }

    /**
     * Add Mock Overlay Items
     */
    private void addOverlayItem() {
        this.controller.actionDisperseParticleOnRandomPlaces(false);
        this.particleOverlay = new ParticleFilterOverlayCanvas(new ParticleFilterOverlayCanvas.OnParticleDraw() {
            @Override
            public void setMapCenter(GeoPoint averagePoint) {
                if (!controller.isAfterReset()) {
                    mapView.setCenter(averagePoint);
                }
            }

            @Override
            public void redrawArrow() {
                redrawArrowBitmap(true);
            }
        }, this);

        this.particleOverlay.setPersonBitmap(this.controller.getPersonBitmap());
        this.particleOverlay.setParticleFilterList(this.controller.getParticles());
        this.mapView.getOverlays().add(particleOverlay);

        this.beaconOverlay = new BeaconOverlayCanvas(this.controller.getBeaconBitmap());
        this.beaconOverlay.setStageReferenceBeacon(this.controller.getStageReferenceBeacon());
        this.mapView.getOverlays().add(beaconOverlay);

        this.arrowOverlay = new ArrowOverlayCanvas(this.controller.getArrowBitmap());
        this.mapView.getOverlays().add(this.arrowOverlay);
    }

    /**
     * This Method will Show The Message whenever The Mode is Change
     *
     * @param mode
     */
    private void showOnOfMessage(ActivityMode mode) {
        String modeMessage = this.getString(R.string.mode_message).toString();
        String state = null;
        switch (mode) {
            case STEP:
                state = modeMessage.replace("[mode]", this.getString(R.string.step_mode));
                state = state.replace("[state]", this.stepMode ? this.getString(R.string.on_state) : this.getString(R.string.off_state));
                break;
            case FINGERPRINT:
                state = modeMessage.replace("[mode]", this.getString(R.string.fingerprint_mode));
                state = state.replace("[state]", this.fingerprintMode ? this.getString(R.string.on_state) : this.getString(R.string.off_state));
                break;
            case RADAR:
                state = modeMessage.replace("[mode]", this.getString(R.string.radar_mode));
                state = state.replace("[state]", this.radarMode ? this.getString(R.string.on_state) : this.getString(R.string.off_state));
                break;
            case HIDE_PARTICLE:
                state = modeMessage.replace("[mode]", this.getString(R.string.hide_particle));
                state = state.replace("[state]", this.particleOverlay.isParticleHidden() ? this.getString(R.string.on_state) : this.getString(R.string.off_state));
                break;
            case RECORD_STEP:
                state = modeMessage.replace("[mode]", this.getString(R.string.record_step));
                state = state.replace("[state]", this.controller.isStepRecorded() ? this.getString(R.string.on_state) : this.getString(R.string.off_state));
                break;
            case TRAJECTORY:
                state = modeMessage.replace("[mode]", this.getString(R.string.trajectory));
                state = state.replace("[state]", this.particleOverlay.isTrajectoryHidden() ? this.getString(R.string.on_state) : this.getString(R.string.off_state));
                break;
            default:
                break;
        }

        Toast.makeText(this, state, Toast.LENGTH_SHORT).show();
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
        this.sensorManager.registerListener(this, onSensorAccelLinear, SENSOR_DELAY);
        this.sensorManager.registerListener(this, onSensorMagnetic, SENSOR_DELAY);
        this.sensorManager.registerListener(this, onSensorAccel, SENSOR_DELAY);
    }

    /**
     * Unregister Sensors
     */
    private void unRegisterSensor() {
        this.sensorManager.unregisterListener(this);
    }

    /**
     * Redraw particle overlay
     */
    private void redrawParticleOverlay() {
        this.particleOverlay.setParticleFilterList(controller.getParticles());
    }

    /**
     * Redraw Arrow
     */
    private void redrawArrowBitmap(boolean setPosition) {
        if (setPosition) {
            if (this.particleOverlay.getAveragePositionParticle() != null) {
                if (!this.controller.isAfterReset()) {
                    this.arrowOverlay.setArrowPosition(this.particleOverlay.getAveragePositionParticle().getDisplayPoint());
                }
            }
        }

        this.arrowOverlay.setArrowBitmap(this.controller.getArrowBitmap());
    }

    /**
     * Show or Hide Edit Particle Field
     * @param state
     */
    private void showEditParticleField(boolean state){
        if(state){
            this.editTextTotalParticle.setText(Integer.toString(this.getSavedTotalParticle()));
            this.editTextTotalParticle.setVisibility(View.VISIBLE);
            this.buttonSaveTotalParticle.setVisibility(View.VISIBLE);
        }else{
            this.editTextTotalParticle.setVisibility(View.GONE);
            this.buttonSaveTotalParticle.setVisibility(View.GONE);
        }
    }

    /**
     * Save Total Particle in Shared Preferences
     * @param inputText
     * @return
     */
    private boolean saveTotalParticle(String inputText){
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(this.getString(R.string.key_total_particle), Integer.parseInt(inputText));
        return editor.commit();
    }

    /**
     * Hide Edit Text
     */
    private void hideEditText(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.editTextTotalParticle.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * Reset Particles
     */
    private void resetParticles(){
        this.controller.actionDisperseParticleOnRandomPlaces(true);
        this.particleOverlay.setParticleFilterList(this.controller.getParticles());
    }

    /**
     * Get Saved Total Particle
     * @return
     */
    private int getSavedTotalParticle() {
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getInt(this.getString(R.string.key_total_particle), Integer.parseInt(this.getString(R.string.total_particle_default)));
    }

    // End Region Private Methods & Functions

    /**
     * Thread - Runnable
     */
    private Runnable listenNearbyBeacons = new Runnable() {
        @Override
        public void run() {
            if (onCreateDone) {
                controller.actionListenNearbyBeaconRSSIValue();
                if (radarMode) {
                    controller.actionChangeParticleRadius();
                    redrawParticleOverlay();
                }
                rssiHandler.postDelayed(this, POST_DELAY);
            }
        }
    };

    /**
     * Activity Mode Type
     *
     * @author SeoulTech Application Software Lab
     */
    public enum ActivityMode {
        STEP, FINGERPRINT, RADAR, CHANGE_STAGE, HIDE_PARTICLE, RECORD_STEP, TRAJECTORY
    }
}
