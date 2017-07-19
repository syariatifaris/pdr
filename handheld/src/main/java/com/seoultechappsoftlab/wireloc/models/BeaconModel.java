package com.seoultechappsoftlab.wireloc.models;

import java.util.List;

import android.content.Context;

import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.services.BeaconService;

/**
 * Class Beacon Model
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class BeaconModel {
	private int currentStage;
	private List<Beacon> activeBeacons;
	private BeaconService beaconService;

	/**
	 * Constructor
	 */
	public BeaconModel(Context context) {
		this.beaconService = new BeaconService(context);
	}

	/**
	 * Get CurrentStage
	 * 
	 * @return
	 */
	public int getCurrentStage() {
		return this.currentStage;
	}

	/**
	 * Set Current Stage
	 * 
	 * @param currentStage
	 */
	public void setCurrentStage(int currentStage) {
		this.currentStage = currentStage;
	}

	/**
	 * getActiveBeacons
	 * 
	 * @return
	 */
	public List<Beacon> getActiveBeacons() {
		this.activeBeacons = this.beaconService.getAll(this.currentStage);
		return this.activeBeacons;
	}

	/**
	 * Set Active Beacons
	 * 
	 * @param activeBeacons
	 */
	public void setActiveBeacons(List<Beacon> activeBeacons) {
		this.activeBeacons = activeBeacons;
	}

	public List<Beacon> getAllBeacons() {
		return this.beaconService.getAll();
	}
}
