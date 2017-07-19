package com.seoultechappsoftlab.wireloc.dals;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.infrastructures.DALBase;

public class BeaconDAL extends DALBase<Beacon> {
	
	public BeaconDAL(Context  context) {
		super(context);
	}
	
	@Override
	public boolean insert(Beacon entity) {
		boolean result = false;
		try {
			this.mapObjectData(entity);
			this.getDatabase().insert(DBConstants.TABLE_BEACON, null, this.getContentValues());
			this.getDatabase().close();
			result = true;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean update(Beacon enity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Beacon getById(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Get All
	 */
	@Override
	public List<Beacon> getAll() {
		List<Beacon> beacons = new ArrayList<Beacon>();
		Cursor cursor = this.getDatabase().rawQuery("Select * FROM "+DBConstants.TABLE_BEACON+" WHERE is_active = 1", null);
		if (cursor.moveToFirst()) {
			do {
				Beacon beacon = new Beacon();
				beacon.setId(cursor.getInt(0));
				beacon.setMacAddress(cursor.getString(1));
				beacon.setLabel(cursor.getString(2));
				beacon.setPointX(cursor.getInt(3));
				beacon.setPointY(cursor.getInt(4));
				beacon.setStageId(cursor.getInt(5));
				beacon.setActive(cursor.getInt(6));
				//this is initial value of beacon's RSSI.
				//Please remove if it's necessary
				beacon.setRssi(-77.00);
				beacons.add(beacon);
			} while (cursor.moveToNext());
		}
		return beacons;
	}
	
	/**
	 * Get All by Stage Id
	 * @param stageId
	 * @return
	 */
	public List<Beacon> getAll(int stageId){
		List<Beacon> filteredBeacons = new ArrayList<Beacon>();
		for(Beacon beacon : this.getAll()){
			if(beacon.getStageId() == stageId){
				filteredBeacons.add(beacon);
			}
		}
		return filteredBeacons;
	}
	
	public boolean isBeaconActive(String macAddress){
		boolean result = false;
		for(Beacon beacon : this.getAll()){
			if(beacon.getMacAddress().equals(macAddress)){
				result = (beacon.isActive() == 1);
				break;
			}
		}
		return result;
	}
	
	public int getBeaconStageId(String macAddress){
		int result = 0;
		for(Beacon beacon : this.getAll()){
			if(beacon.getMacAddress().equals(macAddress)){
				result = beacon.getStageId();
				break;
			}
		}
		return result;
	}

	@Override
	public void mapObjectData(Beacon beacon) {
		this.setContentValues();
		this.getContentValues().put(DBConstants.BEACON_MACADDRESS, beacon.getMacAddress());
		this.getContentValues().put(DBConstants.BEACON_LABEL, beacon.getLabel());
		this.getContentValues().put(DBConstants.BEACON_POINT_X, beacon.getPointX());
		this.getContentValues().put(DBConstants.BEACON_POINT_Y, beacon.getPointY());
		this.getContentValues().put(DBConstants.BEACON_STAGE_ID, beacon.getStageId());
		this.getContentValues().put(DBConstants.BEACON_ISACTIVE, beacon.isActive());
	}

}
