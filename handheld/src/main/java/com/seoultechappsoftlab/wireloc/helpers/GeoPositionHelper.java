package com.seoultechappsoftlab.wireloc.helpers;

import org.mapsforge.core.model.GeoPoint;

import com.seoultechappsoftlab.wireloc.entities.Particle;
import com.seoultechappsoftlab.wireloc.utilities.MapsUtils;

import android.graphics.Point;
import android.graphics.PointF;
import android.location.Location;

/**
 * Geographic Position Helper Class. Latitude / Longitude to Miles, Meter
 * Conversion
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class GeoPositionHelper {
	/**
	 * Convert Latitude Longitude to XY in Meter
	 * 
	 * @param target
	 * @param reference
	 *            GeoPoint. Indicate Latitude and Longitude toward (0, 0) in XY
	 *            Meter
	 * @return
	 */
	public static Point convertLatLonPositionToXYInMeter(GeoPoint target, GeoPoint reference) {
		double distanceBetween = getDistanceFromGeopointInMeter(target, reference);
		double earthRadius = 6371000; // in meter
		double distanceToNorth = earthRadius * Math.PI * (target.getLatitude() - reference.getLatitude()) / 180; // in
																													// meter
		double distanceToEast = Math.pow(Math.pow(distanceBetween, 2) - Math.pow(distanceToNorth, 2), 0.5);
		return new Point((int) distanceToEast, (int) distanceToNorth);
	}

	/**
	 * Convert X,Y Position In Miles to Latitude Longitude
	 * 
	 * @param xyInMiles
	 * @param reference
	 * @return GeoPoint
	 */
	public static GeoPoint convertXYMilesToLatLon(PointF xyInMiles, GeoPoint reference) {
		double earthRadiusInMiles = 3956;
		double latDestination = reference.getLatitude() + xyInMiles.y * 180 / (Math.PI * earthRadiusInMiles);
		double lonDestination = reference.getLongitude() + xyInMiles.x * 180 / (Math.PI * earthRadiusInMiles * Math.cos(reference.getLatitude()));
		return new GeoPoint(latDestination, lonDestination);
	}

	/**
	 * Convert Meter to Miles
	 * 
	 * @param oldPoint
	 * @return PointF
	 */
	public static PointF convertMeterToMiles(PointF oldPoint) {
		double devider = 1609.344;
		return new PointF((float) (oldPoint.x / devider), (float) (oldPoint.y / devider));
	}

	/**
	 * Magnified Position
	 * 
	 * @param notScaledPoint
	 * @param scale
	 * @return
	 */
	public static PointF magnifiedPosition(PointF notScaledPoint, double scale) {
		return new PointF((float) (notScaledPoint.x * scale), (float) (notScaledPoint.y * scale));
	}

	/**
	 * Get Distance Between 2 Position (Latitude, Longitude) in KM
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public static double getDistanceFromGeopointInKm(double lat1, double lon1, double lat2, double lon2) {
		double earthRadius = 6371.00;
		double dLat = degreeToRad(lat2 - lat1);
		double dLon = degreeToRad(lon2 - lon1);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(degreeToRad(lat1)) * Math.cos(degreeToRad(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = earthRadius * c; // Distance in km
		return d;
	}

	/**
	 * Get Distance Between 2 Position (Latitude, Longitude) in Meter
	 * 
	 * @param target
	 * @param reference
	 * @return Double distance
	 */
	public static double getDistanceFromGeopointInMeter(GeoPoint target, GeoPoint reference) {
		return getDistanceFromGeopointInKm(reference.getLatitude(), reference.getLongitude(), target.getLatitude(), target.getLongitude()) * 1000;
	}

	/**
	 * Get Distance Between 2 Position (Latitude, Longitude) in KM
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return Double Distance
	 */
	public static double getDistanceFromGeopointInMeter(double lat1, double lon1, double lat2, double lon2) {
		return getDistanceFromGeopointInKm(lat1, lon1, lat2, lon2) * 1000;
	}

	/**
	 * Move Location toward defined distance and angle
	 * 
	 * @param distance
	 * @param origpoint
	 * @param angle
	 * @return New Location
	 */
	public static GeoPoint translateCoordinates(final double distance, final GeoPoint origpoint, final double angle) {
		final double distanceNorth = Math.sin(angle) * distance;
		final double distanceEast = Math.cos(angle) * distance;

		final double earthRadius = 6371000;

		final double newLat = origpoint.getLatitude() + (distanceNorth / earthRadius) * 180 / Math.PI;
		final double newLon = origpoint.getLongitude() + (distanceEast / (earthRadius * Math.cos(newLat * 180 / Math.PI))) * 180 / Math.PI;

		return new GeoPoint(newLat, newLon);
	}
	
	/**
	 * Check Whether a single location is out of the range
	 * @param centerMap
	 * @param testedArea
	 * @param rangeInMeter
	 * @return
	 */
	public static boolean isOutsideRange(GeoPoint centerMap, GeoPoint testedArea, double rangeInMeter){
		float[] results = new float[1];
		Location.distanceBetween(centerMap.getLatitude(), centerMap.getLatitude(), testedArea.getLatitude(), testedArea.getLongitude(), results);
		float distanceInMeters = results[0];
		return distanceInMeters > rangeInMeter;
	}
	
	/**
	 * Get Particle Distance From Center Map
	 * @param particle
	 * @return
	 */
	public static int getParticleDistanceFromCenterMap(Particle particle){
		float[] results = new float[1];
		Location.distanceBetween(MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_CENTER.getLatitude(), MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_CENTER.getLongitude(), particle.getGeoPoint().getLatitude(), particle.getGeoPoint().getLongitude(), results);
		int distanceInMeters = (int)results[0];
		return distanceInMeters;
	}

	/**
	 * Convert Degree to Radian
	 * 
	 * @param degree
	 * @return Double to Radian
	 */
	private static double degreeToRad(double degree) {
		return degree * (Math.PI / 180);
	}
}
