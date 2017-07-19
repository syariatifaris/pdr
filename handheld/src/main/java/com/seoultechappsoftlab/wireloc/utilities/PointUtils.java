package com.seoultechappsoftlab.wireloc.utilities;

/**
 * Contains contants for Points Calculation
 * @author SeoulTech Application Software Lab
 *
 */
public class PointUtils {
	/**
	 * ALPHA for Kriging
	 */
	public static final double KRIGING_ALPHA = 0.01;
	
	/**
	 * BETA for Kriging
	 */
	public static final double KRIGING_BETA = 1.00;
	
	/**
	 * GAMMA for Kriging
	 */
	public static final double KRIGING_GAMMA = 1.00;

	/**
	 * Distance Scale
	 */
	public static final double DISTANCE_SCALE = 3;
	
	/**
	 * Distance Scale For Map
	 */
	public static final double MAP_DISTANCE_SCALE = 0.05;
	
	/**
	 * Total Particle
	 */
	public static final int TOTAL_PARTICLE = 100;
	
	/**
	 * Map Total Particle
	 */
	public static final int MAP_TOTAL_PARTICLE = 100;
	
	/**
	 * Width
	 */
	public static final int WIDTH = 1080;
	
	/**
	 * Height
	 */
	public static final int HEIGHT = 1533;
	
	/**
	 * Balancing the Direction
	 */
	public static final int BALANCER = -73;
	
	/**
	 * Scale X
	 */
	public static final int SCALE_X = 70;
	
	/**
	 * Scale Y
	 */
	public static final int SCALE_Y = 50;
	
	/**
	 * The Distance Variance
	 */
	public static final double DISTANCE_VARIANCE = 400.00;
	
	/**
	 * The Azimuth Variance
	 */
	public static final double AZIMUTH_VARIANCE = 400.00;
	
	/**
	 * The RSSI Variance
	 */
	public static final double RSSI_VARIANCE = 100.00;

	/**
	 * Common variance
	 */
	public static final double VARIANCE = 8400;

	/**
	 * Map Distance Variance
	 */
	public static final double MAP_DISTANCE_VARIANCE = 1000.00;
	
	/**
	 * Map Azimuth Variance
	 */
	public static final double MAP_AZIMUTH_VARIANCE = 1000.00;
	
	/**
	 * Minimum Particle Weight Multiplier
	 */
	public static final double MINIMUM_PARTICLE_WEIGHT_MULTIPLIER = 0.01;
	
	/**
	 * Maximum Benchmark Value
	 */
	public static final int MAXIMUM_BENCHMARK_VALUE = 9999;
	
	/**
	 * Minimum Benchmark Value
	 */
	public static final int MINIMUM_BENCHMARK_VALUE = -9999;
	
	/**
	 * Scaled original X position to room metric
	 * @param originalX
	 * @return
	 */
	public static final int getScaledXPosition(int originalX){
		return originalX / SCALE_X;
	}
	
	/**
	 * return X position to actual screen after scaled
	 * @param scaledX
	 * @return
	 */
	public static final int getOriginalXPosition(int scaledX){
		return scaledX * SCALE_X;
	}
	
	/**
	 * Scale original Y position to room metric
	 * @param originalY
	 * @return
	 */
	public static final int getScaledYPosition(int originalY){
		return originalY / SCALE_Y;
	}
	
	/**
	 * return Y position to actual screen after scaled 
	 * @param scaledY
	 * @return
	 */
	public static final int getOriginalYPosition(int scaledY){
		return scaledY * SCALE_Y;
	}
}
