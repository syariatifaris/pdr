package com.seoultechappsoftlab.wireloc.infrastructures;

import java.util.List;

import com.seoultechappsoftlab.wireloc.dals.DBConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public abstract class DALBase<E extends EntityBase> extends SQLiteOpenHelper {	
	/**
	 * Database Name
	 */
	private static final String DB_NAME = DBConstants.SQLITE_DEFAULT_DATABASE;
	
	private static int DB_VERSION = 1;
	
	/**
	 * The SQL Database Accessor
	 */
	private SQLiteDatabase sqlDatabase;
	
	/**
	 * The content Values;
	 */
	private ContentValues contentValues;
	
	/**
	 * Constructor
	 * @param context
	 * @param entity
	 */
	public DALBase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
	}
	
	//region abstract method
	
	/**
	 * Insert Data
	 * @param entity
	 * @return true: false
	 */
	public abstract boolean insert(E entity);
	
	/**
	 * Update Data
	 * @param enity
	 * @return true : false
	 */
	public abstract boolean update(E entity);
	
	/**
	 * Delete By Id
	 * @param id
	 * @return
	 */
	public abstract boolean delete(int id);
	
	/**
	 * Get Single Data
	 * @param entity filter
	 * @return entity
	 */
	public abstract E getById(int id); 
	
	/**
	 * Get All Data
	 * @return collection
	 */
	public abstract List<E> getAll();
	
	/**
	 * Map Object Data
	 * @param entity
	 */
	public abstract void mapObjectData(E entity);
	
	//end region abstract method
	
	/**
	 * Initialize Database Access
	 */
	protected SQLiteDatabase getDatabase(){
		this.sqlDatabase = this.getWritableDatabase();
		return this.sqlDatabase;
	}
	
	/**
	 * Set the content values
	 */
	protected void setContentValues(){
		this.contentValues = new ContentValues();
	}
	
	/**
	 * Get Content Values
	 * @return
	 */
	protected ContentValues getContentValues(){
		return this.contentValues;
	}
}
