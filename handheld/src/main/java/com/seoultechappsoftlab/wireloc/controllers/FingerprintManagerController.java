package com.seoultechappsoftlab.wireloc.controllers;

import java.util.List;

import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.models.FingerprintManagerModel;

import android.content.Context;

/**
 * Fingerprint Manager Controller
 * @author farissyariati
 *
 */
public class FingerprintManagerController {
	
	//Region Private Variable
	
	private FingerprintManagerModel fingerprintModel;
	
	//Region Private Variable
	
	//Region Constructors
	
	/**
	 * The Constructor
	 * @param context
	 */
	public FingerprintManagerController(Context context){
		this.fingerprintModel = new FingerprintManagerModel(context);
	}
	
	//End Region Constructors
	
	//Region Model Data
	
	/**
	 * Get Registered Fingerprint for Kriging Purpose
	 * @return
	 */
	public List<Fingerprint> getRegisteredFingeprint(){
		return this.fingerprintModel.getSavedFingerprints();
	}

	/**
	 * Refresh Fingerprint Data
	 */
	public void refreshFingerprintData(){
		this.fingerprintModel.refreshFingerprintData();
	}
	
	//End Region Model Data

	//Region Action Method

	/**
	 * Action Enable Fingerprints
	 * @param records
	 * @return
	 */
	public boolean actionEnableFingerprints(List<Fingerprint> records){
		return this.fingerprintModel.enableFingerprints(records);
	}

	/**
	 * Action Disable Fingerprints
	 * @param records
	 * @return
	 */
	public boolean actionDisableFingerprints(List<Fingerprint> records){
		return this.fingerprintModel.disableFingerprints(records);
	}

	//End Region Action Method
}
