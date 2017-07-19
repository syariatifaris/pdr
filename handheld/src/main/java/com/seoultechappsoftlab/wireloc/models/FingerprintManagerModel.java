package com.seoultechappsoftlab.wireloc.models;

import java.util.List;

import android.content.Context;

import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.services.FingerprintService;

/**
 * Fingerprint Manager Model
 * @author farissyariati
 *
 */
public class FingerprintManagerModel {
	
	//Region Private Variable
	
	private Context context;

	private List<Fingerprint> savedFingerprints;
	private FingerprintService fingerprintService;
	
	//End Region Private Variable
	
	//Region Constructors
	
	public FingerprintManagerModel(Context context){
		this.context = context;
		this.fingerprintService = new FingerprintService(context);
		this.savedFingerprints = this.fingerprintService.getAllDistincted();
	}
	
	//End Region Constructors
	
	//Region Getters and Setters

	/**
	 * Get Saved Fingerprints
	 * @return
	 */
	public List<Fingerprint> getSavedFingerprints() {
		return this.savedFingerprints;
	}
	
	/**
	 * Set Saved Fingerprints
	 * @param savedFingerprints
	 */
	public void setSavedFingerprints(List<Fingerprint> savedFingerprints) {
		this.savedFingerprints = savedFingerprints;
	}

	/**
	 * Get The Context
	 * @return
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * Set The Context
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	
	//End Region Getters and Setters

	/**
	 * Enable Fingerprints
	 * @param data
	 * @return
	 */
	public boolean enableFingerprints(List<Fingerprint> data){
		try{
			for(Fingerprint fingerprint : data){
				if(fingerprint.isSelected()){
					this.fingerprintService.updateIsActive(fingerprint.getPointX(), fingerprint.getPointY(), 1);
				}
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Disable Fingerprints
	 * @param data
	 * @return
	 */
	public boolean disableFingerprints(List<Fingerprint> data){
		try{
			for(Fingerprint fingerprint : data){
				if(fingerprint.isSelected()){
					this.fingerprintService.updateIsActive(fingerprint.getPointX(), fingerprint.getPointY(), 0);
				}
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Refresh Fingerprint Data
	 */
	public void refreshFingerprintData(){
		this.savedFingerprints = this.fingerprintService.getAllDistincted();
	}
}
