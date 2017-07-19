package com.seoultechappsoftlab.wireloc.services;

import java.util.List;

import android.content.Context;

import com.seoultechappsoftlab.wireloc.dals.FingerprintDAL;
import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.infrastructures.ServiceBase;

/**
 * Fingerprint Service
 * @author SeoulTech Application Software Lab
 *
 */
public class FingerprintService extends ServiceBase<Fingerprint>{
	/**
	 * Constructor
	 * @param context
	 */
	public FingerprintService(Context context) {
		this.setDataAccess(new FingerprintDAL(context));
	}
	
	/**
	 * Insert the Fingerprint
	 */
	@Override
	public boolean insert(Fingerprint fingerprint) {
		return this.getDataAccess().insert(fingerprint);
	}
	
	/**
	 * Update the Fingerprint
	 */
	@Override
	public boolean update(Fingerprint fingerprint) {
		return this.getDataAccess().update(fingerprint);
	}

	public boolean updateIsActive(int x, int y, int isActive) {
		return ((FingerprintDAL)this.getDataAccess()).updateIsActive(x, y, isActive);
	}
	
	/**
	 * Delete the Fingerprint
	 */
	@Override
	public boolean delete(int id) {
		return this.getDataAccess().delete(id);
	}
	
	/**
	 * Delete the Fingerprint by X and Y
	 */
	public boolean deleteByXAndY(int x, int y) {
		return ((FingerprintDAL)this.getDataAccess()).deleteByXAndY(x, y);
	}
	
	/**
	 * Get By Id
	 */
	@Override
	public Fingerprint getById(int id) {
		return this.getDataAccess().getById(id);
	}
	
	/**
	 * Get All Fingerprint
	 */
	@Override
	public List<Fingerprint> getAll() {
		return this.getDataAccess().getAll();
	}
	
	/**
	 * Get All By Beacon No
	 * @param beaconNo
	 * @return
	 */
	public List<Fingerprint> getAll(int beaconNo){
		return ((FingerprintDAL)this.getDataAccess()).getAll(beaconNo);
	}
	
	/**
	 * Get All By Stage Id
	 * @param stageId
	 * @return
	 */
	public List<Fingerprint> getAllByStageId(int stageId){
		return ((FingerprintDAL)this.getDataAccess()).getAllByStageId(stageId);
	}
	
	/**
	 * Get All Fingerprint. Avoid Same Position
	 * @return Fingerprints
	 */
	public List<Fingerprint> getAllDistincted(){
		return ((FingerprintDAL)this.getDataAccess()).getAllDistincted();
	}
	
	/**
	 * Get Fingerprint by X and Y
	 * @return Fingerprints
	 */
	public List<Fingerprint> getByXAndY(int x, int y){
		return ((FingerprintDAL)this.getDataAccess()).getByXAndY(x, y);
	}
	
	/**
	 * Insert Fingerprint
	 * @param mPointSaveList
	 * @param pointX
	 * @param pointY
	 * @param stageId
	 * @return
	 */
	public long insert(List<Beacon> mPointSaveList, int pointX, int pointY, int stageId){
		return ((FingerprintDAL)this.getDataAccess()).insert(mPointSaveList, pointX, pointY, stageId);
	}	
}
