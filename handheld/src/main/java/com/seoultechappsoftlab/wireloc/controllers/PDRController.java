package com.seoultechappsoftlab.wireloc.controllers;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.SensorManager;

import com.seoultechappsoftlab.wireloc.activity.R;
import com.seoultechappsoftlab.wireloc.activity.canvases.PDRCanvas;
import com.seoultechappsoftlab.wireloc.entities.Track;
import com.seoultechappsoftlab.wireloc.models.PDRModel;
import com.seoultechappsoftlab.wireloc.utilities.PointUtils;

/**
 * PDR Controller
 * @author SeoulTech Application Software Lab
 *
 */
public class PDRController extends Application {
	
	// Region Public Field
	private PDRCanvas canvas;
	private List<Track> trackList;
	private PDRModel pdrModel;
	// End Region Public Field
	
	private Context context;

	// Bitmap
	private Bitmap arrow;
	private Bitmap arrowBitmap;
	// End of Bitmap

	// Region Sensor Purpose
	int azimuth = 0;
	double lastAccelZValue = -9999;
	long lastCheckTime = 0;
	boolean highLineState = true;
	boolean lowLineState = true;
	boolean passageState = false;
	double highLine = 1;
	double highBoundaryLine = 0;
	double highBoundaryLineAlpha = 1.0;
	double highLineMin = 0.50;
	double highLineMax = 1.5;
	double highLineAlpha = 0.0005;
	double lowLine = -1;
	double lowBoundaryLine = 0;
	double lowBoundaryLineAlpha = -1.0;
	double lowLineMax = -0.50;
	double lowLineMin = -1.5;
	double lowLineAlpha = 0.0005;
	double lowPassFilterAlpha = 0.9;

	float[] rotationData = new float[9];
	float[] resultData = new float[3];
	// End Region Sensor
	
	/**
	 * Constructor
	 */
	public PDRController(Context context){
		this.context = context;
		this.arrow = this.decodeBitmap(R.drawable.arrow);
		this.arrowBitmap = this.createBitmapAsScreenItem(arrow, 0, 0, arrow.getWidth(), arrow.getHeight());
		this.pdrModel = new PDRModel(context);
		this.initializeTrack();
	}
	
	/**
	 * Initialize TrackList by Adding Initial Point
	 */
	public void initializeTrack(){
		this.trackList = new ArrayList<Track>();
		this.trackList.add(new Track(PointUtils.WIDTH/2,PointUtils.HEIGHT/2,0,arrowBitmap));
	}
	
	
	// Region Getters and Setters
	
	/**
	 * get Track List
	 * @return
	 */
	public List<Track> getTrackList() {
		return trackList;
	}

	/**
	 * Set Track List
	 * @param trackList
	 */
	public void setTrackList(List<Track> trackList) {
		this.trackList = trackList;
	}
	
	/**
	 * Get Arrow Bitmap
	 * @return
	 */
	public Bitmap getArrowBitmap() {
		return arrowBitmap;
	}

	/**
	 * Set Arrow Bitmap
	 * @param arrowBitmap
	 */
	public void setArrowBitmap(Bitmap arrowBitmap) {
		this.arrowBitmap = arrowBitmap;
	}
	
	/**
	 * Get The Canvas
	 * 
	 * @return
	 */
	public PDRCanvas getCanvas() {
		return canvas;
	}

	/**
	 * Set The Canvas
	 * 
	 * @param canvas
	 */
	public void setCanvas(PDRCanvas canvas) {
		this.canvas = canvas;
	}
	
	/**
	 * Get PDR Model
	 * @return
	 */
	public PDRModel getPdrModel() {
		return pdrModel;
	}

	/**
	 * Set PDR Model
	 * @param pdrModel
	 */
	public void setPdrModel(PDRModel pdrModel) {
		this.pdrModel = pdrModel;
	}

	// End Region Getters and Setters
	
	// Region Sensor Action
	
	/**
	 * Action Read Step Detection
	 * @param accelLinearData
	 * @param accelData
	 * @param magneticData
	 */
	public void actionReadStepDetection(float[] accelLinearData, float[] accelData, float[] magneticData) {
		this.readStepDetection(accelLinearData, accelData, magneticData);
	}
	
	/**
	 * Action Set Arrow Angle
	 * 
	 * @param accelData
	 * @param magneticData
	 */
	public void actionSetArrowAngle(float[] accelData, float[] magneticData) {
		int azimuth = this.getAzimuthRotation(accelData, magneticData);
		arrowBitmap = createBitmapAsScreenItem(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), azimuth);
		this.setArrowBitmap(arrowBitmap);
	}
	// End Region Sensor Action

	// Region Step Detection Method
	
	/**
	 * Read Step Detection
	 * @param accelLinearData
	 * @param accelData
	 * @param magneticData
	 */
	private void readStepDetection(float[] accelLinearData, float[] accelData,float[] magneticData) {
		int azimuth = this.getAzimuthRotation(accelData, magneticData);

		long currentTime = System.currentTimeMillis();
		long gapTime1 = (currentTime - lastCheckTime);

		if (lastAccelZValue == -9999)
			lastAccelZValue = accelLinearData[2];

		if (highLineState && highLine > highLineMin) {
			highLine = highLine - highLineAlpha;
			highBoundaryLine = highLine * highBoundaryLineAlpha;
		}

		if (lowLineState && lowLine < lowLineMax) {
			lowLine = lowLine + lowLineAlpha;
			lowBoundaryLine = lowLine * lowBoundaryLineAlpha;
		}

		double zValue = (lowPassFilterAlpha * lastAccelZValue) + (1 - lowPassFilterAlpha) * accelLinearData[2];

		if (highLineState && gapTime1 > 100 && zValue > highBoundaryLine) {
			highLineState = false;
		}

		if (lowLineState && zValue < lowBoundaryLine && passageState) {
			lowLineState = false;
		}

		if (!highLineState) {
			if (zValue > highLine) {
				highLine = zValue;
				highBoundaryLine = highLine * highBoundaryLineAlpha;
				if (highLine > highLineMax) {
					highLine = highLineMax;
					highBoundaryLine = highLine * highBoundaryLineAlpha;
				}
			} else {

				if (highBoundaryLine > zValue) {
					highLineState = true;
					passageState = true;
				}
			}
		}

		if (!lowLineState && passageState) {
			if (zValue < lowLine) {
				lowLine = zValue;
				lowBoundaryLine = lowLine * lowBoundaryLineAlpha;
				if (lowLine < lowLineMin) {
					lowLine = lowLineMin;
					lowBoundaryLine = lowLine * lowBoundaryLineAlpha;
				}
			} else {
				if (lowBoundaryLine < zValue) {
					lowLineState = true;
					passageState = false;

					//do something there
					this.setTrackList(this.pdrModel.addTrackOnStep(this.getTrackList(), azimuth, arrowBitmap));
					
					//trackList.add(new Track(0, 0, azimuth, arrowBitmap));
					//int newSize = trackList.size();
					//trackList.get(newSize - 1).setX((int) (trackList.get(newSize - 2).getX() + (Math.cos(Math.toRadians(azimuth - 90)) * 50)));
					//trackList.get(newSize - 1).setY((int) (trackList.get(newSize - 2).getY() + (Math.sin(Math.toRadians(azimuth - 90)) * 50)));
					//end of registering data
					lastCheckTime = currentTime;
				}
			}
		}		
		lastAccelZValue = zValue;
	}
	
	/**
	 * Get Azimuth by Converting Acceleration Data and Magnetic Data
	 * 
	 * @param accelData
	 * @param magneticData
	 * @return
	 */
	public int getAzimuthRotation(float[] accelData, float[] magneticData) {
		int azimuth = 0;

		if (accelData != null && magneticData != null) {

			SensorManager.getRotationMatrix(rotationData, null, accelData, magneticData);
			SensorManager.getOrientation(rotationData, resultData);

			azimuth = (int) Math.toDegrees(resultData[0]);
			azimuth -= PointUtils.BALANCER;

			if (azimuth < 0) {
				azimuth += 360;
			}
		}

		return azimuth;
	}
	
	// End Region Step Detection Method
	
	// Region Private Variable
	
	/**
	 * Decode Bitmap
	 * 
	 * @param resourceId
	 * @return
	 */
	private Bitmap decodeBitmap(int resourceId) {
		return BitmapFactory.decodeResource(this.context.getResources(), resourceId);
	}
	
	/**
	 * Create Bitmap As Screen Item
	 * 
	 * @param bitmap
	 * @param pointX
	 * @param pointY
	 * @param width
	 * @param height
	 * @param rotateMatrix
	 * @return
	 */
	private Bitmap createBitmapAsScreenItem(Bitmap bitmap, int pointX, int pointY, int width, int height) {
		Matrix matrix = new Matrix();
		matrix.postRotate(0);
		matrix.postScale(0.3f, 0.3f);
		return Bitmap.createBitmap(bitmap, pointX, pointY, width, height, matrix, true);
	}

	/**
	 * Create Bitmap As Item with Azimuth Rotation
	 * 
	 * @param bitmap
	 * @param pointX
	 * @param pointY
	 * @param width
	 * @param height
	 * @param azimuth
	 * @return
	 */
	private Bitmap createBitmapAsScreenItem(Bitmap bitmap, int pointX, int pointY, int width, int height, int azimuth) {
		Matrix matrix = new Matrix();
		matrix.postRotate(azimuth);
		matrix.postScale(0.3f, 0.3f);
		return Bitmap.createBitmap(bitmap, pointX, pointY, width, height, matrix, true);
	}
	
	// End Region Private Variable
	
}
