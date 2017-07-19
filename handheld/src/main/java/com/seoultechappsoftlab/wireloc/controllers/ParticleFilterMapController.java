package com.seoultechappsoftlab.wireloc.controllers;

import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.seoultechappsoftlab.wireloc.activity.R;
import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.Particle;
import com.seoultechappsoftlab.wireloc.entities.ParticleStep;
import com.seoultechappsoftlab.wireloc.helpers.JsonExportHelper;
import com.seoultechappsoftlab.wireloc.models.ParticleFilterMapModel;
import com.seoultechappsoftlab.wireloc.utilities.BluetoothUtils;
import com.seoultechappsoftlab.wireloc.utilities.PointUtils;

/**
 * Controller Class For Particle Filter Map
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class ParticleFilterMapController extends Application {

	// Region Private Variables

	private Context context;
	private ParticleFilterMapModel particleFilterMapModel;
	private BluetoothUtils bluetoothUtility;

	private MatrixCalculationTask matrixCalculationTask;

	// End Region Private Variables

	// Region Constructor

	public ParticleFilterMapController(Context context) {
		this.context = context;
		this.particleFilterMapModel = new ParticleFilterMapModel(context);
		this.bluetoothUtility = new BluetoothUtils(context);
	}

	// End Region Constructor

	// Region Getters and Setters

	public Context getContext() {
		return this.context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	/**
	 * Get Bluetooth Utility
	 * @return
	 */
	public BluetoothUtils getBluetoothUtility() {
		return bluetoothUtility;
	}
	
	/**
	 * Set Bluetooth Utility
	 * @param bluetoothUtility
	 */
	public void setBluetoothUtility(BluetoothUtils bluetoothUtility) {
		this.bluetoothUtility = bluetoothUtility;
	}

	// End Region Getters and Setters

	// Region Model Accessors Method

	/**
	 * Get Stage Reference Beacon
	 * 
	 * @return
	 */
	public List<Beacon> getStageReferenceBeacon() {
		return this.particleFilterMapModel.getStageReferenceBeacon();
	}

	/**
	 * Get Current Stage Id
	 * 
	 * @return
	 */
	public int getCurrentStageId() {
		return this.particleFilterMapModel.getCurrentStageId();
	}

	/**
	 * Get The particle filter list
	 * 
	 * @return
	 */
	public List<Particle> getParticles() {
		return this.particleFilterMapModel.getParticleFilterList();
	}

	/**
	 * Get Person bitmap
	 * 
	 * @return
	 */
	public Bitmap getPersonBitmap() {
		return this.particleFilterMapModel.getPersonBitmap();
	}

	/**
	 * Get Beacon Bitmap
	 * 
	 * @return
	 */
	public Bitmap getBeaconBitmap() {
		return this.particleFilterMapModel.getBeaconBitmap();
	}

	/**
	 * Get Arrow Bitmap
	 * 
	 * @return
	 */
	public Bitmap getArrowBitmap() {
		return this.particleFilterMapModel.getArrowBitmap();
	}

	/**
	 * Get Particle Step Record
	 * @return
	 */
	public List<ParticleStep> getParticleStepRecord(){
		return this.particleFilterMapModel.getParticleStepRecord();
	}

	/**
	 * Check whether the step is recorded or not
	 * @return
	 */
	public boolean isStepRecorded(){
		return this.particleFilterMapModel.isStepRecorded();
	}
	
	/**
	 * Set step recorded stage
	 * @param state
	 */
	public void setStepRecorded(boolean state){
		this.particleFilterMapModel.setStepRecorded(state);
	}
	
	/**
	 * Get Current File Dumping Text
	 * @return
	 */
	public String getCurrentFileDumpingText(){
		return this.particleFilterMapModel.getCurrentFileDumpingText();
	}
	
	/**
	 * GEt Total Weight and Normal Distribution
	 * @return
	 */
	public double[] getTotalWeightAndNormalDistribution(){
		return this.particleFilterMapModel.getTotalWeightAndNormalDistribution();
	}
	
	/**
	 * Is After Reset
	 * @return
	 */
	public boolean isAfterReset(){
		return this.particleFilterMapModel.isAfterReset();
	}
	
	public void setAfterReset(boolean afterReset){
		this.particleFilterMapModel.setAfterReset(afterReset);
		if(!afterReset){
			this.particleFilterMapModel.setTrialAttempReset(0);
		}
	}

	// End Region Model Accessors Method

	// Region Action Method (Access-able from activity)

	/**
	 * Listen Nearby Beacons RSSI
	 */
	public void actionListenNearbyBeaconRSSIValue() {
		this.particleFilterMapModel.updateLatestReferenceBeaconRSSIValues(this.bluetoothUtility.getTracker().getAllNearbyBeacons());
	}

	/**
	 * Action Enable Bluetooth
	 * 
	 * @param activity
	 */
	public void actionEnableBluetooth(Activity activity) {
		if (!this.bluetoothUtility.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(enableBtIntent, 1000);
		}
	}

	/**
	 * Action activate bluetooth receiver on resume and stop
	 */
	public void actionActivateBluetoothReceiverIsNotActive() {
		if (!this.bluetoothUtility.getReceiver().isActive()) {
			this.bluetoothUtility.getReceiver().activate();
		}
	}

	/**
	 * Action Disperse Particle On Random Places
	 */
	public void actionDisperseParticleOnRandomPlaces(boolean setNullFirst) {
		this.particleFilterMapModel.disperseParticleOnRandomPlaces(setNullFirst);
	}
	
	/**
	 * Action Disperse Particle Grid Entire Stage
	 */
	public void actionDisperseParticleGridEntireStage(){
		this.particleFilterMapModel.disperseParticleGridEntireStage();
	}

	/**
	 * Set Arrow Angle
	 * 
	 * @param accelData
	 * @param magneticData
	 */
	public void actionSetArrowAngle(float[] accelData, float[] magneticData) {
		this.particleFilterMapModel.setArrowAngle(accelData, magneticData);
	}

	/**
	 * Action Stepping
	 * 
	 * @param accelLinearData
	 * @param accelData
	 * @param magneticData
	 * @return Step? True: False
	 */
	public boolean actionStepping(float[] accelLinearData, float[] accelData, float[] magneticData) {
		return this.particleFilterMapModel.isStepping(accelLinearData, accelData, magneticData);
	}
	
	/**
	 * Calculate Matrix RSSI
	 */
	public void actionCalculateMatrix() {
		this.matrixCalculationTask = new MatrixCalculationTask(this.context);
		this.matrixCalculationTask.execute();
	}
	
	/**
	 * Change Particle Radius
	 */
	public void actionChangeParticleRadius(){
		if(this.matrixCalculationTask != null && this.matrixCalculationTask.getStatus() == AsyncTask.Status.FINISHED){
			if(this.particleFilterMapModel.getCurrentStageId() != 0 && this.particleFilterMapModel.getStageReferenceBeacon().size() != 0){
				this.particleFilterMapModel.changeParticleRadius();
			}
		}
	}

	public boolean actionRecordStepDataToJson(){
		JsonExportHelper<ParticleStep> recordHelper = new JsonExportHelper<ParticleStep>();
		return recordHelper.saveToJson(this.particleFilterMapModel.getParticleStepRecord());
	}

	/**
	 * Action Set Total Particle
	 * @param totalParticle
	 */
	public void actionSetTotalParticle(int totalParticle){
		this.particleFilterMapModel.setTotalParticle(totalParticle);
	}

	public void actionDestroyReceiver(){
		this.bluetoothUtility.destroyReceiver();
	}

	// End Region Action Method
	
	// Region Asynchronous Task
	class MatrixCalculationTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog progressDialog;
		private Context context;

		public MatrixCalculationTask(Context context) {
			this.context = context;
			this.progressDialog = new ProgressDialog(this.context);
		}

		/**
		 * Prepare dialog
		 */
		@Override
		protected void onPreExecute() {
			this.progressDialog.setMessage(this.context.getString(R.string.kriging_loading));
			this.progressDialog.setCancelable(false);
			this.progressDialog.show();
		}

		/**
		 * Run after executing
		 */
		@Override
		protected void onPostExecute(final Boolean success) {
			if (this.progressDialog.isShowing()) {
				this.progressDialog.dismiss();
			}

			if (success) {
				Toast.makeText(this.context, R.string.matrix_reloaded, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this.context, R.string.matrix_failed, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			particleFilterMapModel.loadSavedFingerprintData(particleFilterMapModel.getStageReferenceBeacon().size(),
					particleFilterMapModel.getStageReferenceBeacon(), PointUtils.KRIGING_ALPHA, PointUtils.KRIGING_BETA, PointUtils.KRIGING_GAMMA);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return particleFilterMapModel.initializeKriging(particleFilterMapModel.getStageReferenceBeacon());
		}

	}
}
