package com.seoultechappsoftlab.wireloc.services;

import java.util.List;

import android.content.Context;

import com.seoultechappsoftlab.wireloc.dals.BeaconDAL;
import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.infrastructures.ServiceBase;

public class BeaconService extends ServiceBase<Beacon>{
	
	public BeaconService(Context context) {
		this.setDataAccess(new BeaconDAL(context));
	}
	
	@Override
	public boolean insert(Beacon entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Beacon entity) {
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

	@Override
	public List<Beacon> getAll() {
		return this.getDataAccess().getAll();
	}
	
	public List<Beacon> getAll(int stageId){
		return ((BeaconDAL)this.getDataAccess()).getAll(stageId);
	}
	
	public int getBeaconStageId(String macAddress){
		return ((BeaconDAL)this.getDataAccess()).getBeaconStageId(macAddress);
	}
	
	public boolean isBeaconActive(String macAddress){
		return((BeaconDAL)this.getDataAccess()).isBeaconActive(macAddress);
	}
}
