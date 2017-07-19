package com.seoultechappsoftlab.wireloc.dals;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.graphics.Point;

import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.infrastructures.DALBase;

/**
 * Fingerprint Data Access Layer
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class FingerprintDAL extends DALBase<Fingerprint> {

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public FingerprintDAL(Context context) {
		super(context);
	}

	/**
	 * Insert the fingerprint
	 */
	@Override
	public boolean insert(Fingerprint fingerprint) {
		boolean result = false;
		try {
			this.mapObjectData(fingerprint);
			this.getDatabase().insert(DBConstants.TABLE_FINGERPRINT, null, this.getContentValues());
			this.getDatabase().close();
			result = true;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Insert Multiple
	 * @param mPointSaveList
	 * @param pointX
	 * @param pointY
	 * @return
	 */
	public long insert(List<Beacon> mPointSaveList, int pointX, int pointY, int stageId){
		if(!isDataExists(new Point(pointX, pointY))){
			int getSize = mPointSaveList.size();
			for (Beacon vo : mPointSaveList) {
				Fingerprint fingerprint = new Fingerprint();
				fingerprint.setBeaconNo(getSize);
				fingerprint.setMacAddress(vo.getMacAddress());
				fingerprint.setPointX(pointX);
				fingerprint.setPointY(pointY);
				fingerprint.setRssi(vo.getRssi());
				fingerprint.setStageId(stageId);
				boolean result = insert(fingerprint);
				if (!result) {
					return -1;
				}
			}
			return 1;
		}else{
			return -2;
		}
	}
	
	/**
	 * Check Whether the data is exists
	 * @param allData
	 * @param currentCheckedPoint
	 * @return
	 */
	public boolean isDataExists(Point currentCheckedPoint){
		List<Fingerprint> allData = getAll();
		for(Fingerprint data: allData){
			if(currentCheckedPoint.x == data.getPointX() && currentCheckedPoint.y == data.getPointY()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Update the fingerprint
	 */
	@Override
	public boolean update(Fingerprint fingerprint) {
		return false;
	}

	public boolean updateIsActive(int x, int y, int isActive) {
		boolean result = false;
		try {
			this.getDatabase().execSQL("UPDATE "+ DBConstants.TABLE_FINGERPRINT + " SET " + "is_active ="+ isActive + " WHERE " + "pointx =" + x + " AND pointy =" + y);
			this.getDatabase().close();
			result = true;
		} catch (SQLiteException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * Delete the fingerprint
	 */
	@Override
	public boolean delete(int id) {
		return false;
	}
	
	/**
	 * Delete the fingerprint by X and Y
	 */
	public boolean deleteByXAndY(int x, int y) {
		boolean result = false;
		try {
			this.getDatabase().execSQL(DBConstants.FINGERPRINT_DELETE + " WHERE " + "pointx =" + x + " AND pointy =" + y);
			this.getDatabase().close();
			result = true;
		} catch (SQLiteException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * Get fingerprint by Id
	 */
	@Override
	public Fingerprint getById(int id) {
		return null;
	}

	/**
	 * Get All Fingerprint Data
	 */
	@Override
	public List<Fingerprint> getAll() {
		List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
		Cursor cursor = this.getDatabase().rawQuery(DBConstants.FINGERPRINT_GET_ALL, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					Fingerprint fingerprint = new Fingerprint();
					fingerprint.setId(cursor.getInt(0));
					fingerprint.setBeaconNo(cursor.getInt(1));
					fingerprint.setMacAddress(cursor.getString(2));
					fingerprint.setPointX(cursor.getInt(3));
					fingerprint.setPointY(cursor.getInt(4));
					fingerprint.setRssi(cursor.getDouble(5));
					fingerprint.setStageId(cursor.getInt(6));
					fingerprint.setIsActive(cursor.getInt(7) == 0 ? false: true);
					fingerprints.add(fingerprint);
				} while (cursor.moveToNext());
			}
		} catch (SQLiteException exception) {
			exception.printStackTrace();
		}
		return fingerprints;
	}
	
	/**
	 * Get Fingerprint Data By X and Y
	 */
	public List<Fingerprint> getByXAndY(int x, int y) {
		List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
		Cursor cursor = this.getDatabase().rawQuery(DBConstants.FINGERPRINT_GET_ALL_ACTIVE + " AND a.pointx =" + x +" AND a.pointy =" + y, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					Fingerprint fingerprint = new Fingerprint();
					fingerprint.setId(cursor.getInt(0));
					fingerprint.setBeaconNo(cursor.getInt(1));
					fingerprint.setMacAddress(cursor.getString(2));
					fingerprint.setPointX(cursor.getInt(3));
					fingerprint.setPointY(cursor.getInt(4));
					fingerprint.setRssi(cursor.getDouble(5));
					fingerprint.setStageId(cursor.getInt(6));
					fingerprint.setIsActive(cursor.getInt(7) == 0 ? false : true);
					fingerprints.add(fingerprint);
				} while (cursor.moveToNext());
			}
		} catch (SQLiteException exception) {
			exception.printStackTrace();
		}
		return fingerprints;
	}
	
	/**
	 * Get All Fingerprint Data By Beacon No
	 */
	public List<Fingerprint> getAll(int beaconNo) {
		List<Fingerprint> allFingerprints = this.getAll();
		List<Fingerprint> filteredFingerprints = new ArrayList<Fingerprint>();
		//region filter
		for(Fingerprint fingerprint : allFingerprints){
			if(fingerprint.getBeaconNo() == beaconNo){
				filteredFingerprints.add(fingerprint);
			}
		}
		//end of filter
		return filteredFingerprints;
	}
	
	/**
	 * Get All By Stage Id
	 * @param stageId
	 * @return
	 */
	public List<Fingerprint> getAllByStageId(int stageId){
		List<Fingerprint> filteredFingerprints = new ArrayList<Fingerprint>();
		//region filter
		for(Fingerprint fingerprint : this.getAll()){
			if(fingerprint.getStageId() == stageId){
				filteredFingerprints.add(fingerprint);
			}
		}
		//end of filter
		return filteredFingerprints;
	}
	
	/**
	 * Get All Fingerprint, Avoid all occurence
	 * @return Fingerprints
	 */
	public List<Fingerprint> getAllDistincted(){
		List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
		Cursor cursor = this.getDatabase().rawQuery(DBConstants.FINGERPRINT_GET_ALL_DISTINCED, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					Fingerprint fingerprint = new Fingerprint();
					fingerprint.setPointX(cursor.getInt(0));
					fingerprint.setPointY(cursor.getInt(1));
					fingerprint.setStageId(cursor.getInt(2));
					fingerprint.setIsActive(cursor.getInt(3) == 0 ? false : true);
					fingerprints.add(fingerprint);
				} while (cursor.moveToNext());
			}
		} catch (SQLiteException exception) {
			exception.printStackTrace();
		}
		return fingerprints;
	}

	/**
	 * Map object data
	 */
	@Override
	public void mapObjectData(Fingerprint fingerprint) {
		this.setContentValues();
		this.getContentValues().put(DBConstants.FINGERPRINT_BEACON_NO, fingerprint.getBeaconNo());
		this.getContentValues().put(DBConstants.FINGERPRINT_BEACON_MAC, fingerprint.getMacAddress());
		this.getContentValues().put(DBConstants.FINGERPRINT_POINT_X, fingerprint.getPointX());
		this.getContentValues().put(DBConstants.FINGERPRINT_POINT_Y, fingerprint.getPointY());
		this.getContentValues().put(DBConstants.FINGERPRINT_RSSI_VALUE, fingerprint.getRssi());
		this.getContentValues().put(DBConstants.FINGERPRINT_STAGE_ID, fingerprint.getStageId());
		this.getContentValues().put(DBConstants.FINGERPRINT_IS_ACTIVE, fingerprint.isActive());
	}

}
