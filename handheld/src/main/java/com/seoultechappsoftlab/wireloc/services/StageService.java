package com.seoultechappsoftlab.wireloc.services;

import java.util.List;

import android.content.Context;

import com.seoultechappsoftlab.wireloc.dals.StageDAL;
import com.seoultechappsoftlab.wireloc.entities.Stage;
import com.seoultechappsoftlab.wireloc.infrastructures.ServiceBase;

public class StageService extends ServiceBase<Stage> {
	
	/**
	 * The Constructor
	 * @param context
	 */
	public StageService(Context context) {
		this.setDataAccess(new StageDAL(context));
	}
	
	@Override
	public boolean insert(Stage stage) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Stage stage) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Stage getById(int id) {
		return this.getDataAccess().getById(id);
	}

	/**
	 * Get All Stage
	 */
	@Override
	public List<Stage> getAll() {
		return this.getDataAccess().getAll();
	}
	
	/**
	 * Get Total Stage
	 * @return
	 */
	public int getTotal(){
		return ((StageDAL)this.getDataAccess()).getTotal();
	}

}
