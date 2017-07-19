package com.seoultechappsoftlab.wireloc.controllers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.seoultechappsoftlab.wireloc.activity.ParticleFilterActivity;
import com.seoultechappsoftlab.wireloc.activity.R;
import com.seoultechappsoftlab.wireloc.activity.canvases.ParticleFilterCanvas;
import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.Obstacle;
import com.seoultechappsoftlab.wireloc.entities.Particle;
import com.seoultechappsoftlab.wireloc.entities.RoomObject;
import com.seoultechappsoftlab.wireloc.entities.Stage;
import com.seoultechappsoftlab.wireloc.helpers.BeaconHelper;
import com.seoultechappsoftlab.wireloc.helpers.ObstacleXMLHelper;
import com.seoultechappsoftlab.wireloc.models.BeaconModel;
import com.seoultechappsoftlab.wireloc.models.ParticleFilterModel;
import com.seoultechappsoftlab.wireloc.models.StageModel;
import com.seoultechappsoftlab.wireloc.services.StageService;
import com.seoultechappsoftlab.wireloc.utilities.BluetoothUtils;
import com.seoultechappsoftlab.wireloc.utilities.PointUtils;
import com.seoultechappsoftlab.wireloc.utilities.StepDetectorUtils;

/**
 * Controller for Particle Filter Activity
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class ParticleFilterController extends Application {

	// Region public field
	private List<Particle> particleFilterList;

	private ParticleFilterCanvas canvas;
	private ParticleFilterModel particleFilterModel;
	private BeaconModel beaconModel;
	private StageModel stageModel;

	private Context context;
	// Beacon com.seoultechappsoftlab.wireloc.entities
	private List<Beacon> preloadBeacons;
	// Beacon com.seoultechappsoftlab.wireloc.entities
	private List<com.wisewells.iamzone.blelibrary.Beacon> nearbyBeacons;
	private List<Beacon> activeBeacons;
	private int currentStage;
	private Stage stage;
	private HashMap<Integer, String> stageHashMap;
	private HashMap<Integer, List<Particle>> lastStageParticles;

	// Bitmap
	private Bitmap arrow;
	private Bitmap person;
	private Bitmap arrowBitmap;
	private Bitmap personBitmap;
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
	double highBoundaryLineAlpha = 0.3;
	double highLineMin = 0.15;
	double highLineMax = 1.3;
	double highLineAlpha = 0.05;
	double lowLine = -1;
	double lowBoundaryLine = 0;
	double lowBoundaryLineAlpha = 0.3;
	double lowLineMax = -0.15;
	double lowLineMin = -1.3;
	double lowLineAlpha = 0.05;
	double lowPassFilterAlpha = 0.9;

	float[] rotationData = new float[9];
	float[] resultData = new float[3];
	// End Region Sensor
	// End Region public field

	// Utility
	private BluetoothUtils bluetoothUtility;

	private MatrixCalculationTask matrixCalculationTask = null;
	private int oldLocationStage;
	public boolean isRoomChanging;

	/**
	 * The Constructor
	 * 
	 * @param context
	 */
	public ParticleFilterController(Context context) {
		this.oldLocationStage = 0;
		this.context = context;
		this.arrow = this.decodeBitmap(R.drawable.arrow);
		this.person = this.decodeBitmap(R.drawable.person);
		this.arrowBitmap = this.createBitmapAsScreenItem(arrow, 0, 0, arrow.getWidth(), arrow.getHeight());
		this.personBitmap = this.createBitmapAsScreenItem(person, 0, 0, person.getWidth(), person.getHeight());
		this.particleFilterModel = new ParticleFilterModel(context);
		this.beaconModel = new BeaconModel(context);
		this.bluetoothUtility = new BluetoothUtils(context);
		this.stageModel = new StageModel(context);
		this.initializeStageHashMap();
		this.initializeLastStateStage();
	}

	/**
	 * Get Particle Filter Model
	 * 
	 * @return
	 */
	public ParticleFilterModel getParticleFilterModel() {
		return this.particleFilterModel;
	}

	/**
	 * Set Particle Filter Model
	 * 
	 * @param particleFilterModel
	 */
	public void setParticleFilterModel(ParticleFilterModel particleFilterModel) {
		this.particleFilterModel = particleFilterModel;
	}

	/**
	 * Get Beacon Model
	 * 
	 * @return
	 */
	public BeaconModel getBeaconModel() {
		return beaconModel;
	}

	/**
	 * Set Beacon Model
	 * 
	 * @param beaconModel
	 */
	public void setBeaconModel(BeaconModel beaconModel) {
		this.beaconModel = beaconModel;
	}

	/**
	 * Get Stage Model
	 * @return
	 */
	public StageModel getStageModel() {
		return stageModel;
	}

	/**
	 * Set Stage Model
	 * @param stageModel
	 */
	public void setStageModel(StageModel stageModel) {
		this.stageModel = stageModel;
	}

	/**
	 * Initialize Particle Filter
	 */
	public void initializeParticleFilter() {
		this.particleFilterList = new ArrayList<Particle>();
	}

	/**
	 * Get particle Filter List
	 * 
	 * @return
	 */
	public List<Particle> getParticleFilterList() {
		return particleFilterList;
	}

	/**
	 * Set Particle Filter List
	 * 
	 * @param particleFilterList
	 */
	public void setParticleFilterList(List<Particle> particleFilterList) {
		this.particleFilterList = particleFilterList;
	}

	/**
	 * Get Last Stage Particles (Hash Map)
	 * 
	 * @return
	 */
	public HashMap<Integer, List<Particle>> getLastStageParticles() {
		return lastStageParticles;
	}

	/**
	 * Set Last Stage Particles (Hash Map)
	 * 
	 * @param lastStageParticles
	 */
	public void setLastStageParticles(HashMap<Integer, List<Particle>> lastStageParticles) {
		this.lastStageParticles = lastStageParticles;
	}

	/**
	 * Get the context
	 * 
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Set the context
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * Initialize Canvas
	 */
	public void initializeCanvas() {
		this.canvas = new ParticleFilterCanvas(this.context);
	}

	/**
	 * Get The Canvas
	 * 
	 * @return
	 */
	public ParticleFilterCanvas getCanvas() {
		return canvas;
	}

	/**
	 * Set The Canvas
	 * 
	 * @param canvas
	 */
	public void setCanvas(ParticleFilterCanvas canvas) {
		this.canvas = canvas;
	}

	/**
	 * Get PreloadBeacons
	 * 
	 * @return
	 */
	public List<Beacon> getPreloadBeacons() {
		return preloadBeacons;
	}

	/**
	 * Set The PreloadBeacons
	 * 
	 * @param preloadBeacons
	 */
	public void setPreloadBeacons(List<Beacon> preloadBeacons) {
		this.preloadBeacons = preloadBeacons;
	}

	/**
	 * Get Arrow Bitmap
	 * 
	 * @return
	 */
	public Bitmap getArrowBitmap() {
		return arrowBitmap;
	}

	/**
	 * Set Arrow Bitmap
	 * 
	 * @param arrowBitmap
	 */
	public void setArrowBitmap(Bitmap arrowBitmap) {
		this.arrowBitmap = arrowBitmap;
	}

	/**
	 * Get Person Bitmap
	 * 
	 * @return
	 */
	public Bitmap getPersonBitmap() {
		return personBitmap;
	}

	/**
	 * Set Person Bitmap
	 * 
	 * @param personBitmap
	 */
	public void setPersonBitmap(Bitmap personBitmap) {
		this.personBitmap = personBitmap;
	}

	/**
	 * Get Nearby Beacons
	 * 
	 * @return
	 */
	public List<com.wisewells.iamzone.blelibrary.Beacon> getNearbyBeacons() {
		return nearbyBeacons;
	}

	/**
	 * Set Nearby Beacons
	 * 
	 * @param nearbyBeacons
	 */
	public void setNearbyBeacons(List<com.wisewells.iamzone.blelibrary.Beacon> nearbyBeacons) {
		this.nearbyBeacons = nearbyBeacons;
	}

	/**
	 * Get Active Beacons
	 * 
	 * @return
	 */
	public List<Beacon> getActiveBeacons() {
		return activeBeacons;
	}

	/**
	 * Set The Active Beacons
	 * 
	 * @param activeBeacons
	 */
	public void setActiveBeacons(List<Beacon> activeBeacons) {
		this.activeBeacons = activeBeacons;
	}

	/**
	 * Get Current Stage
	 * 
	 * @return
	 */
	public int getCurrentStage() {
		return currentStage;
	}

	/**
	 * Set Current Stage
	 * 
	 * @param currentStage
	 */
	public void setCurrentStage(int currentStage) {
		this.currentStage = currentStage;
	}

	/**
	 * Get Stage
	 * @return
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * Set Stage
	 * @param stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public boolean isRoomChanging() {
		return isRoomChanging;
	}

	public void setRoomChanging(boolean isRoomChanging) {
		this.isRoomChanging = isRoomChanging;
	}
	

	/**
	 * Initialize Stage Hash Map
	 */
	@SuppressLint("UseSparseArrays")
	public void initializeStageHashMap() {
		this.stageHashMap = new HashMap<Integer, String>();
		StageService service = new StageService(this.context);
		for (Stage stage : service.getAll()) {
			this.stageHashMap.put(stage.getId(), "stages/" + stage.getResourceFolderName() + "/");
		}
	}

	@SuppressLint("UseSparseArrays")
	public void initializeLastStateStage() {
		this.lastStageParticles = new HashMap<Integer, List<Particle>>();
		this.lastStageParticles.put(1, null);
		this.lastStageParticles.put(2, null);
	}

	public List<Particle> getTemporarySavedParticleFilterList(int stageId) {
		return this.lastStageParticles.get(stageId);
	}

	// Region Particle Filter Action Method
	/**
	 * Action Disperse Particle at the First Time
	 * 
	 * @param canvasWidth
	 * @param canvasHeight
	 */
	public void actionDisperseParticleFirst(int canvasWidth, int canvasHeight) {
		this.particleFilterModel.initializeParticles(this.getParticleFilterList(), canvasWidth, canvasHeight, PointUtils.TOTAL_PARTICLE);
	}

	/**
	 * Action Reinitialize Particle Filter
	 * 
	 * @param canvasWidth
	 * @param canvasHeight
	 */
	public void actionReInitializeParticleFilter(int canvasWidth, int canvasHeight) {
		this.particleFilterList = new ArrayList<Particle>();
		this.particleFilterModel.initializeParticles(this.getParticleFilterList(), canvasWidth, canvasHeight, PointUtils.TOTAL_PARTICLE);
	}

	/**
	 * Action set Beacon Current Stage Update Beacon Condition
	 */
	public void actionSetBeaconCurrentStage() {
		this.beaconModel.setCurrentStage(this.currentStage);
		this.activeBeacons = this.beaconModel.getActiveBeacons();
	}

	/**
	 * Action Listen Nearby Beacons RSSI Value
	 */
	public void actionListenNearbyBeaconRSSIValue(ParticleFilterActivity.ActivityMode mode) {
		this.nearbyBeacons = this.bluetoothUtility.getTracker().getAllNearbyBeacons();
		// test function - get current stage
		this.setCurrentStage(BeaconHelper.determineCurrentStageByNearbyBeacons(nearbyBeacons, this.context));
		if ((this.oldLocationStage != this.currentStage)) {
			if (this.currentStage != 0) {
				this.lastStageParticles.put(this.oldLocationStage, this.getParticleFilterList());
				this.oldLocationStage = this.currentStage;
				this.particleFilterList = this.lastStageParticles.get(currentStage);
				this.stageModel.setCurrentStage(currentStage);
				this.stage = this.stageModel.getStage();
				this.actionSetBeaconCurrentStage();
				this.isRoomChanging = true;
			}
		}

		if (this.getActiveBeacons() != null && !this.getActiveBeacons().isEmpty()) {
			for (Beacon activeBeacon : this.activeBeacons) {
				for (com.wisewells.iamzone.blelibrary.Beacon nearbyBeacon : this.nearbyBeacons) {
					if (activeBeacon.getMacAddress().equals(nearbyBeacon.getMacAddress())) {
						activeBeacon.setRssi(BeaconHelper.lowPassFilter(nearbyBeacon.getRssi(), activeBeacon.getRssi()));
					}
				}
			}

			if (mode != null) {
				switch (mode) {
				case RADAR:
					if (this.matrixCalculationTask != null && this.matrixCalculationTask.getStatus() == AsyncTask.Status.FINISHED) {
						if(this.currentStage != 0){
							this.setParticleFilterList(this.particleFilterModel.changeRadiusOnStep(this.particleFilterList, this.activeBeacons, 400, 400,
									100, this.currentStage));
						}
					}
					break;
				default:
					break;
				}
			}
		}
	}

	public boolean actionSetLabSettingIfStageIsNotDetermined() {
		this.nearbyBeacons = this.bluetoothUtility.getTracker().getAllNearbyBeacons();
		this.setCurrentStage(BeaconHelper.determineCurrentStageByNearbyBeacons(nearbyBeacons, this.context));
		if (this.currentStage != 0) {
			this.actionSetBeaconCurrentStage();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Action Set Lab Setting
	 * 
	 * @return
	 */
	public boolean actionSetLabSetting() {
		if (this.currentStage == 0) {
			this.beaconModel.setCurrentStage(1);
		} else {
			this.beaconModel.setCurrentStage(this.currentStage);
		}

		this.activeBeacons = this.beaconModel.getActiveBeacons();
		try {
			this.particleFilterModel.loadSavedFingerprintData(this.activeBeacons.size(), activeBeacons, PointUtils.KRIGING_ALPHA,
					PointUtils.KRIGING_BETA, PointUtils.KRIGING_GAMMA, this.currentStage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Action activate bluetooth receiver on resume and stop
	 */
	public void actionActivateBluetoothReceiverIsNotActive() {
		if (!this.bluetoothUtility.getReceiver().isActive()) {
			this.bluetoothUtility.getReceiver().activate();
		}
	}

	/**
	 * Put Stage's Room Object
	 * 
	 * @param obstacles
	 * @return
	 */
	public List<RoomObject> actionPutStageRoomObjects(List<Obstacle> obstacles) {
		List<RoomObject> roomObjects = new ArrayList<RoomObject>();
		if (this.currentStage != 0) {
			for (Obstacle obstacle : obstacles) {
				try {
					AssetManager assetManager = this.context.getAssets();
					InputStream inputStream = assetManager.open(stageHashMap.get(currentStage) + obstacle.getResource());
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, obstacle.getWidth(), obstacle.getHeight(), true);
					roomObjects.add(new RoomObject(scaledBitmap, obstacle.getInitPoint()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return roomObjects;
	}

	// Region Sensor Action
	public void actionReadStepDetection(float[] accelLinearData, float[] accelData, float[] magneticData) {
		this.readStepDetection(accelLinearData, accelData, magneticData, preloadBeacons);
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

	public void readStepDetection(float[] accelLinearData, float[] accelData, float[] magneticData, List<Beacon> preloadBeacons) {
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
					if (this.getActiveBeacons() != null) {
						if (this.matrixCalculationTask != null && this.matrixCalculationTask.getStatus() == AsyncTask.Status.FINISHED) {
							if(currentStage != 0){
								this.setParticleFilterList(this.particleFilterModel.changeParticleOnStep(this.getParticleFilterList(),
										this.getActiveBeacons(), StepDetectorUtils.DISTANCE_VARIANCE, StepDetectorUtils.AZIMUTH_VARIANCE,
										StepDetectorUtils.RSSI_VARIANCE, StepDetectorUtils.WALK_DISTANCE, azimuth, this.currentStage));
							}
						}
					}

					lastCheckTime = currentTime;
				}
			}
		}

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

	// End Region Sensor Action

	/**
	 * Action Calculate Matrix
	 */
	public void actionCalculateMatrix() {
		matrixCalculationTask = new MatrixCalculationTask(this.context);
		matrixCalculationTask.execute();
	}

	/**
	 * Action Set Manually Added Active Beacon
	 */
	public void actionSetManuallyAddedActiveBeacon() {
		this.activeBeacons = this.getManuallyAddedActiveBeacons();
	}

	// End Region Particle Filter Action Method

	// Region Public Method
	/**
	 * Get obstacle from XML
	 * 
	 * @return
	 */
	public List<Obstacle> getObstaclesFromXML() {
		AssetManager assetManager = this.context.getAssets();
		ArrayList<Obstacle> obstacles = null;
		if (this.currentStage != 0) {
			try {
				String roomResourcesXML = this.stageHashMap.get(this.currentStage) + "obstacle.xml";
				InputStream is = assetManager.open(roomResourcesXML);
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();

				ObstacleXMLHelper myXMLHandler = new ObstacleXMLHelper();
				xr.setContentHandler(myXMLHandler);
				InputSource inStream = new InputSource(is);
				xr.parse(inStream);
				obstacles = myXMLHandler.getObstacles();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return obstacles;
	}

	// End Region Public Method

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
	
	/**
	 * Get Manually Added Active Beacons This Method is not save
	 * 
	 * @return
	 */
	private List<Beacon> getManuallyAddedActiveBeacons() {
		List<Beacon> activeBeacons = new ArrayList<Beacon>();
		activeBeacons.add(new Beacon("20:CD:39:9F:08:F1", "RedBear A", 0, 0, -77, 1));
		activeBeacons.add(new Beacon("20:CD:39:9F:0A:B2", "RedBear B", 15, 1, -77, 1));
		activeBeacons.add(new Beacon("20:CD:39:9F:12:03", "RedBear B", 15, 30, -77, 1));
		activeBeacons.add(new Beacon("20:CD:39:9F:10:0A", "RedBear B", 0, 30, -77, 1));
		return activeBeacons;
	}

	// End Region Private Variable

	// Region Asynchronous Task
	class MatrixCalculationTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog progressDialog;
		private Context context;

		public MatrixCalculationTask(Context context) {
			this.context = context;
			this.progressDialog = new ProgressDialog(this.context);
		}

		/**
		 * Prepare dialog
		 */
		@Override
		protected void onPreExecute() {
			this.progressDialog.setMessage("Preparing Matrix Data");
			this.progressDialog.show();
		}

		/**
		 * Run after executing
		 */
		@Override
		protected void onPostExecute(final Boolean success) {
			if (this.progressDialog.isShowing()) {
				this.progressDialog.dismiss();
			}

			if (success) {
				Toast.makeText(this.context, R.string.matrix_reloaded, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this.context, R.string.matrix_failed, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			particleFilterModel.loadAllStagesSavedFingerpringData(PointUtils.KRIGING_ALPHA, PointUtils.KRIGING_BETA, PointUtils.KRIGING_GAMMA);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return particleFilterModel.initializeAllStageKriging();
		}

	}
}
