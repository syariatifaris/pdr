package com.seoultechappsoftlab.wireloc.dals;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import com.seoultechappsoftlab.wireloc.entities.Stage;
import com.seoultechappsoftlab.wireloc.infrastructures.DALBase;

public class StageDAL extends DALBase<Stage> {
	
	//Region Contructor
	public StageDAL(Context context) {
		super(context);
	}
	
	@Override
	public boolean insert(Stage stage) {
		return false;
	}

	@Override
	public boolean update(Stage stage) {
		return false;
	}

	@Override
	public boolean delete(int id) {
		return false;
	}

	@Override
	public Stage getById(int id) {
		
		Cursor cursor = this.getDatabase().rawQuery("Select * FROM " + DBConstants.TABLE_STAGE + " where "+DBConstants.ID+ "=?", new String[]{Integer.toString(id)});
		if (cursor != null)
			cursor.moveToFirst();
		Stage stage = new Stage();
		stage.setId(cursor.getInt(0));
		stage.setStageName(cursor.getString(1));
		stage.setStageDescription(cursor.getString(2));
		stage.setIsActive(cursor.getInt(3));
		stage.setResourceFolderName(cursor.getString(4));
		
		return stage;
	}
	
	/**
	 * Get All Stage
	 */
	@Override
	public List<Stage> getAll() {
		List<Stage> stages = new ArrayList<Stage>();
		Cursor cursor = this.getDatabase().rawQuery("Select * FROM " + DBConstants.TABLE_STAGE + " where is_active = 1", null);
		try {
			if (cursor.moveToFirst()) {
				do {
					Stage stage = new Stage();
					stage.setId(cursor.getInt(0));
					stage.setStageName(cursor.getString(1));
					stage.setStageDescription(cursor.getString(2));
					stage.setIsActive(cursor.getInt(3));
					stage.setResourceFolderName(cursor.getString(4));
					stages.add(stage);
				} while (cursor.moveToNext());
			}
		} catch (SQLiteException exception) {
			exception.printStackTrace();
		}
		return stages;
	}
	
	/**
	 * Get Total Stage
	 * @return
	 */
	public int getTotal(){
		return this.getAll().size();
	}

	@Override
	public void mapObjectData(Stage stage) {
		this.setContentValues();
		this.getContentValues().put(DBConstants.STAGE_NAME, stage.getStageName());
		this.getContentValues().put(DBConstants.STAGE_DESCRIPTION, stage.getStageDescription());
		this.getContentValues().put(DBConstants.STAGE_IS_ACTIVE, stage.getIsActive());
		this.getContentValues().put(DBConstants.STAGE_RESOURCE_FOLDER_NAME, stage.getResourceFolderName());
	}

}
