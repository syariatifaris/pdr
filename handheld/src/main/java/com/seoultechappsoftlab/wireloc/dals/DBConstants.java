package com.seoultechappsoftlab.wireloc.dals;

/**
 * Table's Field Constant
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class DBConstants {
	// region database name
	public static final String SQLITE_DEFAULT_DATABASE = "pdrpractice.db";
	// end region database name
	
	// region table name

	/**
	 * Table Student
	 */
	public static final String TABLE_STUDENT = "Students";
	
	/**
	 * Table Beacon
	 */
	public static final String TABLE_BEACON = "beacon_tbl";
	
	/**
	 * Table Fingerprint
	 */
	public static final String TABLE_FINGERPRINT = "point_tbl";
	
	/**
	 * Table Stage
	 */
	public static final String TABLE_STAGE = "stage_tbl";
	
	// end region table name

	// Region Table Field
	
	/**
	 * The ID
	 */
	public static final String ID = "_id";
	
	/**
	 * The Student ID
	 */
	public static final String STUDENT_ID = "_id";

	/**
	 * The Student Name
	 */
	public static final String STUDENT_NAME = "StudentName";
	
	/**
	 * Beacon ID
	 */
	public static final String BEACON_ID = "_id";
	
	/**
	 * Beacon MAc Address
	 */
	public static final String BEACON_MACADDRESS = "macaddress";
	
	/**
	 * Label
	 */
	public static final String BEACON_LABEL = "label";
	
	/**
	 * Stage Id
	 */
	public static final String BEACON_STAGE_ID = "stage_id";
	
	/**
	 * Beacon's X Position
	 */
	public static final String BEACON_POINT_X = "point_x";
	
	/**
	 * Beacon's Y Position
	 */
	public static final String BEACON_POINT_Y = "point_y";
	
	/**
	 * Beacon Is Active
	 */
	public static final String BEACON_ISACTIVE = "is_active";
	
	/**
	 * Fingerprint's Beacon Reference No
	 */
	public static final String FINGERPRINT_BEACON_NO = "beaconno";
	
	/**
	 * Fingerprint's Beacon MAC Address
	 */
	public static final String FINGERPRINT_BEACON_MAC = "beaconmac";
	
	/**
	 * Fingerprint Point X
	 */
	public static final String FINGERPRINT_POINT_X = "pointx";
	
	/**
	 * Fingerprint Point Y
	 */
	public static final String FINGERPRINT_POINT_Y = "pointy";
	
	/**
	 * Fingerprint RSSI Value
	 */
	public static final String FINGERPRINT_RSSI_VALUE = "rssi";
	
	/**
	 * Fingerprint Stage ID;
	 */
	public static final String FINGERPRINT_STAGE_ID = "stage_id";

	/**
	 * Fingerprint Status
	 */
	public static final String FINGERPRINT_IS_ACTIVE = "is_active";
	
	/**
	 * Stage Name
	 */
	public static final String STAGE_NAME = "stage_name";
	
	/**
	 * Stage Description
	 */
	public static final String STAGE_DESCRIPTION = "stage_description";
	
	/**
	 * Stage Is Active
	 */
	public static final String STAGE_IS_ACTIVE = "is_active";
	
	/**
	 * Stage Resource Folder Name
	 */
	public static final String STAGE_RESOURCE_FOLDER_NAME = "resource_folder_name";
	
	// End Region Table Field

	// Region Database Query
	
	/**
	 * Create Table Students
	 */
	public static final String STUDENTS_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Students(_id INTEGER PRIMARY KEY, StudentName TEXT)";
	
	/**
	 * Drop Table Students
	 */
	public static final String STUDENTS_DROP_TABLE = "DROP TABLE IF EXISTS Students";
	// end region database query
	
	/**
	 * Get All Fingerprints Which Active
	 */
	//Obsolete
	//public static final String FINGERPRINT_GET_ALL = "SELECT a.* FROM point_tbl a LEFT JOIN beacon_tbl b ON a.beaconmac = b.macaddress WHERE b.is_active <> 0";
	public static final String FINGERPRINT_GET_ALL = "SELECT a.* FROM point_tbl a LEFT JOIN beacon_tbl b ON a.beaconmac = b.macaddress WHERE b.is_active <> 0 AND a.is_active <> 0	";

	public static final String FINGERPRINT_GET_ALL_ACTIVE = "SELECT a.* FROM point_tbl a LEFT JOIN beacon_tbl b ON a.beaconmac = b.macaddress WHERE b.is_active <> 0";

	/**
	 * Get All Fingerprint Unique
	 */
	public static final String FINGERPRINT_GET_ALL_DISTINCED = "SELECT pointx, pointy, stage_id, is_active FROM point_tbl GROUP BY pointx, pointy";

	/**
	 * Delete Fingerprint Data
	 */
	public static final String FINGERPRINT_DELETE = "DELETE FROM point_tbl";
}
