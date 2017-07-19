package com.seoultechappsoftlab.wireloc.activity.canvases;

import java.util.List;

import org.mapsforge.android.maps.Projection;
import org.mapsforge.core.model.GeoPoint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.helpers.GeoPositionHelper;
import com.seoultechappsoftlab.wireloc.infrastructures.OverlayCanvasBase;
import com.seoultechappsoftlab.wireloc.utilities.MapsUtils;

public class BeaconOverlayCanvas extends OverlayCanvasBase {

	// Region mock objects

	private List<Beacon> stageReferenceBeacon;
	private Bitmap beaconBitmap;

	// End Region mock objects

	// Region Constructors

	/**
	 * Constructor
	 */
	public BeaconOverlayCanvas(Bitmap beaconBitmap) {
		super();
		this.beaconBitmap = beaconBitmap;
	}

	/**
	 * Constructor
	 * 
	 * @param bitmap
	 *            Bitmap
	 * @param geoPoint
	 *            GeoPoint
	 */
	public BeaconOverlayCanvas(Bitmap bitmap, GeoPoint geoPoint) {
		super(bitmap, geoPoint);
	}

	/**
	 * Constructor
	 * 
	 * @param bitmap
	 *            Bitmap
	 * @param position
	 *            Position
	 * @param geoPoint
	 *            GeoPoint
	 */
	public BeaconOverlayCanvas(Bitmap bitmap, PointF position, GeoPoint geoPoint) {
		super(bitmap, position, geoPoint);
	}

	// End Region Constructors

	// Setters and Getters

	public List<Beacon> getStageReferenceBeacon() {
		return stageReferenceBeacon;
	}

	public void setStageReferenceBeacon(List<Beacon> stageReferenceBeacon) {
		this.stageReferenceBeacon = stageReferenceBeacon;
		this.invalidate();
	}

	// End Setters and Getters

	/**
	 * Draw Overlay
	 * 
	 * @param canvas
	 *            Canvas
	 * @param point
	 *            Point
	 * @param projection
	 *            Projection
	 * @param zoomLevel
	 *            byte
	 */
	@Override
	protected void drawOverlayBitmap(Canvas canvas, Point point, Projection projection, byte zoomLevel) {
		super.drawOverlayBitmap(canvas, point, projection, zoomLevel);

		if (this.stageReferenceBeacon != null && this.stageReferenceBeacon.size() > 0) {
			this.setBeaconGeoPoint();
			for (Beacon beacon : stageReferenceBeacon) {
				beacon.setDisplayPoint(beacon.getDisplayPoint() == null ? new Point() : beacon.getDisplayPoint());
				projection.toPixels(beacon.getGeoPoint(), beacon.getDisplayPoint());
				canvas.drawBitmap(this.beaconBitmap, beacon.getDisplayPoint().x - (this.beaconBitmap.getWidth() + 50), beacon.getDisplayPoint().y
						- this.beaconBitmap.getHeight(), new Paint());
			}
		}
	}

	public boolean setBeaconGeoPoint() {
		if (this.stageReferenceBeacon != null) {
			for (Beacon beacon : stageReferenceBeacon) {
				// multiply by scale

				PointF pointF = new PointF(beacon.getPointX() * 100, beacon.getPointY() * 100);
				pointF = GeoPositionHelper.convertMeterToMiles(pointF);
				beacon.setGeoPoint(GeoPositionHelper.convertXYMilesToLatLon(pointF, MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_00));
			}
			return true;
		} else {
			return false;
		}
	}
}
