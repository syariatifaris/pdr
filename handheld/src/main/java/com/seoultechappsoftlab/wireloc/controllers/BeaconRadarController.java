package com.seoultechappsoftlab.wireloc.controllers;

import java.util.ArrayList;
import java.util.List;

import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.PointBeacon;
import com.seoultechappsoftlab.wireloc.models.BeaconRadarModel;
import com.seoultechappsoftlab.wireloc.utilities.BluetoothUtils;

import android.app.Application;
import android.content.Context;

/**
 * Controller for Beacon Radar Activity
 * @author SeoulTech Application Software Lab
 *
 */
public class BeaconRadarController extends Application {
	//Region Private Variable
	
	private BeaconRadarModel model;
	private BluetoothUtils bluetoothUtils;
	private List<com.wisewells.iamzone.blelibrary.Beacon> nearbyBeacons;
	
	//End Region Private Variable
	
	/**
	 * Constructor
	 * @param context
	 */
	public BeaconRadarController(Context context){
		this.model = new BeaconRadarModel(context);
		this.bluetoothUtils = new BluetoothUtils(context);
		this.nearbyBeacons = new ArrayList<com.wisewells.iamzone.blelibrary.Beacon>();
	}
	
	//Region Action Method
	
	public void actionListenNearbyBeacons(){
		this.nearbyBeacons = this.bluetoothUtils.getTracker().getAllNearbyBeacons();
	}
	
	/**
	 * Load Saved Fingerprint Data
	 */
	public void actionLoadSavedFingerprints(){
		this.model.loadSavedFingerprintData();
	}
	
	/**
	 * Action Update Beacon Radar
	 */
	public void actionUpdateBeaconRadar(){
		this.model.setNearbyBeacons(this.nearbyBeacons);
		this.model.initializeKriging();
		this.model.updateBeaconRadarInDelay();
	}
	
	/**
	 * Action Set Latest Particle Filter
	 * @param particles
	 */
	public void actionSetParticles(List<PointBeacon> particles){
		this.model.setParticles(particles);
	}
	
	/**
	 * Action activate bluetooth receiver on resume and stop
	 */
	public void actionActivateBluetoothReceiverIfNotActive() {
		if (!this.bluetoothUtils.getReceiver().isActive()) {
			this.bluetoothUtils.getReceiver().activate();
		}
	}
	
	//End Region Action Method
	
	//Region Public Method
	
	/**
	 * Get latest Particles From Model
	 * @return
	 */
	public ArrayList<PointBeacon> getLatestParticles(){
		return (ArrayList<PointBeacon>)this.model.getParticles();
	}
	
	/**
	 * Get RSSI Latest Information
	 * @return
	 */
	public String getLatestRSSIInformation(){
		return this.model.getRssiInformation();
	}
	
	/**
	 * Get Model's Active Beacon
	 * @return
	 */
	public List<Beacon> getActiveBeacons(){
		return this.model.getActiveBeacons();
	}
	
	/**
	 * Get Model's Stage Id
	 * @return Integer
	 */
	public int getCurrentStageId(){
		return this.model.getCurrentStageId();
	}
	
	//End Region Public Method
}
