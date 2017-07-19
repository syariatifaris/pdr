package com.seoultechappsoftlab.wireloc.utilities;

import org.mapsforge.core.model.GeoPoint;

/**
 * Maps Utility
 * @author SeoulTech Application Software Lab
 *
 */
public class MapsUtils {
	//Region Particle Filter Map
	
	/**
	 * Particle Filter Map Name
	 */
	public static String PARTICLE_FILTER_MAP = "rotc_3rd_floor_100x.map";
	
	/**
	 * Particle Filter Map Geographic point Reference
	 * This geographic point is refer to (0,0) in meter
	 */
	public static final GeoPoint ROTC_3RD_FLOOR_GEOPOINT_00 = new GeoPoint(0.0682586, 0.0178733);
	
	/**
	 * Mock Center Point of Map (Initial)
	 */
	public static final GeoPoint ROTC_3RD_FLOOR_GEOPOINT_CENTER = new GeoPoint(0.086147, 0.0318482);
	
	/**
	 * Max Distance From Center
	 */
	public static final int ROTC_MAX_DISTANCE_FROM_CENTER = 2500;
	
	/**
	 * ROTC 3rd Floor Width in Meter
	 */
	public static final int ROTC_3RD_FLOOR_WIDTH = 30;
	
	/**
	 * ROTC 3rd Floor Height in Meter
	 */
	public static final int ROTC_3RD_FLOOR_HEIGHT = 39;
	
	/**
	 * Particle Filter Map Zoom Level
	 */
	public static final int PARTICLE_FILTER_MAP_ZOOM_LEVEL = 17;
	
	//End Region Particle Filter Map
	
	/**
	 * Map Assets Folder on External Storage
	 */
	public static final String MAP_ASSET_DIR = "/maps/";
	
	/**
	 * Map Scale from Meter
	 */
	public static final int MAP_SCALE = 100;
	
	/**
	 * Boundary of Map
	 * minlat='0.065591' minlon='0.0034861' maxlat='0.1051976' maxlon='0.0549383'
	 */
	public static final GeoPoint MIN_GEOPOINT = new GeoPoint(0.0682586, 0.0138413);
	
	/**
	 * Max GeoPoint
	 */
	public static final GeoPoint MAX_GEOPOINT = new GeoPoint(0.1051976, 0.043882);
}
