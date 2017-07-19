package com.seoultechappsoftlab.wireloc.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.seoultechappsoftlab.wireloc.services.BeaconService;

import android.content.Context;

/**
 * Beacon Helper
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class BeaconHelper {
	public static final double ALPHA = 0.9;
	/**
	 * Get Low Pass Filter
	 * 
	 * @param newOne
	 * @param lastOne
	 * @return
	 */
	public static double lowPassFilter(double newOne, double lastOne) {
		double resultOne = (ALPHA * lastOne) + (1 - ALPHA) * newOne;
		return resultOne;
	}

	/**
	 * Determine Current Stage By Active Beacons
	 * 
	 * @param nearbyBeacons
	 * @return
	 */
	public static int determineCurrentStageByNearbyBeacons(List<com.wisewells.iamzone.blelibrary.Beacon> nearbyBeacons, Context context) {
		BeaconService service = new BeaconService(context);
		
		// 1. eliminate which beacon is not registered as active
		List<com.wisewells.iamzone.blelibrary.Beacon> activedNearbyBeacons = new ArrayList<com.wisewells.iamzone.blelibrary.Beacon>();
		for(com.wisewells.iamzone.blelibrary.Beacon aNearbyBeacon : nearbyBeacons){
			boolean isCurrentBeaconActive = service.isBeaconActive(aNearbyBeacon.getMacAddress());
			if(isCurrentBeaconActive){
				activedNearbyBeacons.add(aNearbyBeacon);
			}
		}
		
		//2. Using Beacon Statistics, Get Beacons which the value is not eliminated.
		BeaconStatisticsHelper beaconStatisticsHelper = new BeaconStatisticsHelper(activedNearbyBeacons);
		List<com.wisewells.iamzone.blelibrary.Beacon> persistedNearbyBeacons = beaconStatisticsHelper.getHighStandardCollection(); 
		
		//3. Map the Nearby Beacon (Wisewell's) with List of Integer
		// Get by MAC address
		List<Integer> stageIdCollection = new ArrayList<Integer>();
		for(com.wisewells.iamzone.blelibrary.Beacon persistNearbyBeacon : persistedNearbyBeacons){
			stageIdCollection.add(Integer.valueOf(service.getBeaconStageId(persistNearbyBeacon.getMacAddress())));
		}
		
		//
		return getPopular(stageIdCollection);
	}
	
	/**
	 * Get Most Frequent Data From list of Integer 
	 * @param a
	 * @return
	 */
	private static int getPopular(List<Integer> a){
		if (a == null || a.size() == 0)
	        return 0;

	    Collections.sort(a);

	    int previous = a.get(0);
	    int popular = a.get(0);
	    int count = 1;
	    int maxCount = 1;

	    for (int i = 1; i < a.size(); i++) {
	        if (a.get(i) == previous)
	            count++;
	        else {
	            if (count > maxCount) {
	                popular = a.get(i - 1);
	                maxCount = count;
	            }
	            previous = a.get(i);
	            count = 1;
	        }
	    }
	    return count > maxCount ? a.get(a.size() - 1) : popular;
	}
}
