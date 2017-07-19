package com.seoultechappsoftlab.wireloc.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import com.seoultechappsoftlab.wireloc.dals.DBConstants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author SQLite Import Helper
 *
 */
public class SQLiteImportHelper extends SQLiteOpenHelper {
	/**
	 * Database Default Name
	 */
	private static final String DB_NAME_DEFAULT = DBConstants.SQLITE_DEFAULT_DATABASE;
	
	/**
	 * Database Name
	 */
	@SuppressLint("SdCardPath")
	private static final String DB_NAME = DBConstants.SQLITE_DEFAULT_DATABASE;
	
	/**
	 * Database Path
	 */
	@SuppressLint("SdCardPath")
	private static String DB_PATH = "/data/data/";

	/**
	 * The Constructor
	 * @param context
	 */
	public SQLiteImportHelper(Context context) {
		super(context, DB_NAME_DEFAULT, null, 1);
		DB_PATH += context.getPackageName() + "/databases/";
		//this.removeDatabase(context);
	}
	
	/**
	 * On Create Event
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
	}
	
	/**
	 * On Upgrade Event
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 * Import Database From Assets
	 * 
	 * @param mContext
	 * @return
	 */
	public boolean importDatabaseFromAssets(Context mContext) {
		boolean result = false;
		try {
			File databaseFolder = new File(DB_PATH);
			if(!databaseFolder.exists()){
				databaseFolder.mkdir();
			}
			
			InputStream mInput = mContext.getAssets().open(DB_NAME);
			String outFileName = DB_PATH + DB_NAME;
			OutputStream mOutput = new FileOutputStream(outFileName);
			byte[] mBuffer = new byte[1024];
			int mLength;
			while ((mLength = mInput.read(mBuffer)) > 0) {
				mOutput.write(mBuffer, 0, mLength);
			}
			mOutput.flush();
			mOutput.close();
			mInput.close();
			result = true;
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}
		return result;
	}

	// region private variable

	/**
	 * Remove the Database
	 * 
	 * @param context
	 */
	public boolean removeDatabase(Context context) {
		return context.deleteDatabase(DB_NAME_DEFAULT);
	}

	// end region private variable

	/**
	 * Internal Class File Utility
	 * 
	 * @author SeoulTech Application Software Lab
	 *
	 */
	class FileUtils {
		/**
		 * Copy The File
		 * 
		 * @param fromFile
		 * @param toFile
		 * @throws IOException
		 */
		public void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
			FileChannel fromChannel = null;
			FileChannel toChannel = null;
			try {
				fromChannel = fromFile.getChannel();
				toChannel = toFile.getChannel();
				fromChannel.transferTo(0, fromChannel.size(), toChannel);
			} finally {
				try {
					if (fromChannel != null) {
						fromChannel.close();
					}
				} finally {
					if (toChannel != null) {
						toChannel.close();
					}
				}
			}
		}
	}
}
