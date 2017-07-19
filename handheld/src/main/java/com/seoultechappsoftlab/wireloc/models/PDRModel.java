package com.seoultechappsoftlab.wireloc.models;

import java.util.List;

import com.seoultechappsoftlab.wireloc.entities.Track;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * PDR Class Model
 * @author SeoulTech Application Software Lab
 *
 */
public class PDRModel {
	
	/**
	 * Constructor
	 */
	public PDRModel(Context context){
		//add service later to connect the database
	}
	
	/**
	 * Add Track to List
	 * @param trackList
	 * @param azimuth
	 * @param bitmap
	 * @return
	 */
	public List<Track> addTrackOnStep(List<Track> trackList, int azimuth, Bitmap bitmap){
		
		trackList.add(new Track(0, 0, azimuth, bitmap));
		int newSize = trackList.size();
		trackList.get(newSize - 1).setX((int) (trackList.get(newSize - 2).getX() + (Math.cos(Math.toRadians(azimuth - 90)) * 50)));
		trackList.get(newSize - 1).setY((int) (trackList.get(newSize - 2).getY() + (Math.sin(Math.toRadians(azimuth - 90)) * 50)));
		
		return trackList;
	}

}
