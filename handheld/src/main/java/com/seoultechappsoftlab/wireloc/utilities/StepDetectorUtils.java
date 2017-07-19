package com.seoultechappsoftlab.wireloc.utilities;

/**
 * Step Detector Utility
 * @author SeoulTech Application Software Lab
 *
 */
public class StepDetectorUtils {
	/**
	 * The Walk Distance in meter
	 */
	public static final double WALK_DISTANCE = 0.5; // in Meter
	
	/**
	 * The Walk Distance in meter
	 */
	public static final double MAP_WALK_DISTANCE = 0.5; // in Meter with scale
	
	/**
	 * Distance Variance
	 */
	public static final double DISTANCE_VARIANCE = 400;
	
	/**
	 * Map Distance Variance
	 */
	public static double MAP_DISTANCE_VARIANCE = 3;
	
	/**
	 * Azimuth Variance
	 */
	public static final double AZIMUTH_VARIANCE = 400;
	
	/**
	 * Map Azimuth Variance
	 */
	public static double MAP_AZIMUTH_VARIANCE = 3;
	
	/**
	 * RSSI Variance
	 */
	public static final double RSSI_VARIANCE = 100;
	
	/**
	 * Map RSSI Variance
	 */
	public static final double MAP_RSSI_VARIANCE = 100;
	
	/**
	 * Maximum Value of Step Recorded
	 */
	public static final double MAX_STEP_RECORD = 200;
	
	/**
	 * Minimum Distance Tolerance
	 */
	public static final double MINIMUM_DISTANCE_TOLERANCE = 0.15f;
	
	/**
	 * Maximum Trial Attemp Reset
	 */
	public static final int MAXIMUM_TRIAL_ATTEMP_RESET = 2;
	
	/**
	 * Disperse Radius in Circle Area
	 */
	public static final double DISPERSE_ROUND_AREA_RADIUS = 10.0;
	
	/**
	 * Directory of Particle Filter Save File
	 */
	public static final String PARTICLE_RECORD_DIR = "/pdrpractice-particle-dump/";
	
	/**
	 * Record Extension (File, Binary not Supported)
	 */
	public static final String PARTICLE_RECORD_FILE_NAME_AND_EXTENSION = "-step-record.txt";
}
