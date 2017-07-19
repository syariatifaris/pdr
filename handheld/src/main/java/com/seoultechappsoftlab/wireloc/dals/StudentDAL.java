package com.seoultechappsoftlab.wireloc.dals;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;

import com.seoultechappsoftlab.wireloc.entities.Student;
import com.seoultechappsoftlab.wireloc.infrastructures.DALBase;

public class StudentDAL extends DALBase<Student> {
	/**
	 * The Constructor
	 * 
	 * @param context
	 */
	public StudentDAL(Context context) {
		super(context);
	}
	
	/**
	 * DAL - Insert Data
	 */
	@Override
	public boolean insert(Student entity) {
		boolean result = false;
		try {
			this.mapObjectData(entity);
			this.getDatabase().insert(DBConstants.TABLE_STUDENT, null, this.getContentValues());
			this.getDatabase().close();
			result = true;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * DAL - Update Data
	 */
	@Override
	public boolean update(Student enity) {
		boolean result = true;
		try{
			this.mapObjectData(enity);
			this.getDatabase().update(DBConstants.TABLE_STUDENT, this.getContentValues(), DBConstants.STUDENT_ID +"="+enity.getId(), null);
			result = true;
		}catch(SQLiteException ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * DAL - Delete Data by Id
	 */
	@Override
	public boolean delete(int id) {
		boolean result = false;
		try {
			this.getDatabase().delete(DBConstants.TABLE_STUDENT, DBConstants.STUDENT_ID + "=" + id, null);
			this.getDatabase().close();
			result = true;
		} catch (SQLiteException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * DAL - delete data(s)
	 * @param ids
	 * @return
	 */
	public boolean delete(int[] ids) {
		boolean result = false;
		try {
			for (int i = 0; i < ids.length; i++) {
				this.getDatabase().delete(DBConstants.TABLE_STUDENT, DBConstants.STUDENT_ID + "=" + ids[i], null);
			}
			this.getDatabase().close();
			result = true;
		} catch (SQLiteException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * DAL - Get by ID
	 */
	@Override
	public Student getById(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * DAL - Get All Data
	 */
	@Override
	public List<Student> getAll() {
		List<Student> students = new ArrayList<Student>();
		Cursor cursor = this.getDatabase().rawQuery("Select * FROM Students", null);
		if (cursor.moveToFirst()) {
			do {
				Student student = new Student();
				student.setId(cursor.getInt(0));
				student.setName(cursor.getString(1));
				students.add(student);
			} while (cursor.moveToNext());
		}
		return students;
	}
	
	/**
	 * Example calling a method using downcast on service
	 * @return
	 */
	public int getFunctionCalledByDowncasting(){
		return -1;
	}
	
	/**
	 * Map the Content Values
	 */
	@Override
	public void mapObjectData(Student student) {
		this.setContentValues();
		this.getContentValues().put(DBConstants.STUDENT_NAME, student.getName());
	}
}
