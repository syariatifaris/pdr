package com.seoultechappsoftlab.wireloc.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.mapsforge.core.model.GeoPoint;

import com.seoultechappsoftlab.wireloc.activity.R;
import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.entities.MatrixRSSI;
import com.seoultechappsoftlab.wireloc.entities.Particle;
import com.seoultechappsoftlab.wireloc.entities.ParticleStep;
import com.seoultechappsoftlab.wireloc.entities.Stage;
import com.seoultechappsoftlab.wireloc.helpers.BeaconHelper;
import com.seoultechappsoftlab.wireloc.helpers.GeoPositionHelper;
import com.seoultechappsoftlab.wireloc.helpers.ParticleFilterHelper;
import com.seoultechappsoftlab.wireloc.helpers.ParticleFilterStatisticsHelper;
import com.seoultechappsoftlab.wireloc.services.BeaconService;
import com.seoultechappsoftlab.wireloc.services.FingerprintService;
import com.seoultechappsoftlab.wireloc.services.StageService;
import com.seoultechappsoftlab.wireloc.utilities.MapsUtils;
import com.seoultechappsoftlab.wireloc.utilities.PointUtils;
import com.seoultechappsoftlab.wireloc.utilities.StepDetectorUtils;

import Jama.Matrix;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.hardware.SensorManager;

/**
 * Particle Filter Map Model
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class ParticleFilterMapModel {

	// Region Private Variables

	private List<Fingerprint> savedFingerprints;
	private List<Particle> particleGrid;
	private List<Beacon> stageReferenceBeacon;
	private List<com.wisewells.iamzone.blelibrary.Beacon> nearbyBeacons;
	private List<Particle> particleFilterList;

	private List<ParticleStep> particleStepRecord = new ArrayList<ParticleStep>();

	private HashMap<Integer, String> stageHashMap;

	private int currentStageId;
	private int currentStep;
	private double[] totalWeightAndNormalDistribution;

	private double rssiVarience;
	private Matrix matrixInverse;
	private ParticleFilterStatisticsHelper statisticsHelper;

	private FingerprintService fingerPrintService;
	private BeaconService beaconService;

	private String currentFileDumpingText;

	private Context context;
	private Bitmap rawArrowBitmap;
	private Bitmap arrowBitmap;

	private boolean isFingerprintNotEmpty;
	private boolean isStepRecorded;

	private boolean afterReset;
	private int trialAttempReset;

	private double alpha;
	private double beta;
	private double gamma;

	private int totalParticle;

	// Region Sensor Reading Purpose

	private float[] rotationData = new float[9];
	private float[] resultData = new float[3];

	int azimuth = 0;
	double lastAccelZValue = -9999;
	long lastCheckTime = 0;
	boolean highLineState = true;
	boolean lowLineState = true;
	boolean passageState = false;
	double highLine = 1;
	double highBoundaryLine = 0;
	double highBoundaryLineAlpha = 0.2;
	double highLineMin = 0.10;
	double highLineMax = 1.3;
	double highLineAlpha = 0.05;
	double lowLine = -1;
	double lowBoundaryLine = 0;
	double lowBoundaryLineAlpha = 0.2;
	double lowLineMax = -0.175;
	double lowLineMin = -1.3;
	double lowLineAlpha = 0.05;
	double lowPassFilterAlpha = 0.9;

	// End Region Sensor Reading Purpose

	// End Region Private Variables

	// Region Constants
	private static final double DISTANCE_SCALE = 1;

	// Region Constructors

	public ParticleFilterMapModel(Context context) {
		this.context = context;
		this.beaconService = new BeaconService(context);
		this.fingerPrintService = new FingerprintService(context);

		// mock data. please delete if necessary
		this.currentStageId = this.currentStageId == 0 ? 1 /* mock stage id */: this.currentStageId;
		this.stageReferenceBeacon = this.stageReferenceBeacon == null ? this.beaconService.getAll(this.currentStageId) : this.stageReferenceBeacon;

		this.isStepRecorded = false;
		this.currentStep = 0;

		this.rawArrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow);
		this.arrowBitmap = this.createBitmapAsScreenItem(this.rawArrowBitmap, 0, 0, this.rawArrowBitmap.getWidth(), this.rawArrowBitmap.getHeight());
		this.initializeStageHashMap();

		this.afterReset = false;
		this.trialAttempReset = 0;
	}

	// End Region Constructors

	// Region Getters and Setters

	/**
	 * Get Context
	 * 
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Set Context
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * Get Particle Grid
	 * 
	 * @return
	 */
	public List<Particle> getParticleGrid() {
		return particleGrid;
	}

	/**
	 * Set Particle Grid
	 * 
	 * @param particlesGrid
	 */
	public void setParticleGrid(List<Particle> particlesGrid) {
		this.particleGrid = particlesGrid;
	}

	/**
	 * Get The Matrix Inverse
	 * 
	 * @return
	 */
	public Matrix getMatrixInverse() {
		return this.matrixInverse;
	}

	/**
	 * Set The Matrix Inverse
	 * 
	 * @param matrixInverse
	 */
	public void setMatrixInverse(Matrix matrixInverse) {
		this.matrixInverse = matrixInverse;
	}

	/**
	 * Get RSSI Variance
	 * 
	 * @return
	 */
	public double getRssiVarience() {
		return rssiVarience;
	}

	/**
	 * Set RSSI Variance
	 * 
	 * @param rssiVarience
	 */
	public void setRssiVarience(double rssiVarience) {
		this.rssiVarience = rssiVarience;
	}

	/**
	 * Get Alpha
	 * 
	 * @return
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * Set Alpha
	 * 
	 * @param alpha
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * Get Beta
	 * 
	 * @return
	 */
	public double getBeta() {
		return beta;
	}

	/**
	 * Set Beta
	 * 
	 * @param beta
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}

	/**
	 * Get Gamma
	 * 
	 * @return
	 */
	public double getGamma() {
		return gamma;
	}

	/**
	 * Set Gamma
	 * 
	 * @param gamma
	 */
	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	/**
	 * Get Saved Fingerprint Data
	 * 
	 * @return
	 */
	public List<Fingerprint> getSavedFingerprints() {
		return this.savedFingerprints;
	}

	/**
	 * Set Saved Fingerprint
	 * 
	 * @param savedFingerprints
	 */
	public void setSavedFingerprints(List<Fingerprint> savedFingerprints) {
		this.savedFingerprints = savedFingerprints;
	}

	/**
	 * Get Stage Reference Beacon
	 * 
	 * @return
	 */
	public List<Beacon> getStageReferenceBeacon() {
		return stageReferenceBeacon;
	}

	/**
	 * Set Stage Reference Beacon
	 * 
	 * @param stageReferenceBeacon
	 */
	public void setStageReferenceBeacon(List<Beacon> stageReferenceBeacon) {
		this.stageReferenceBeacon = stageReferenceBeacon;
	}

	/**
	 * Get Nearby Beacons
	 * 
	 * @return List<com.wisewells.iamzone.blelibrary.Beacon>
	 */
	public List<com.wisewells.iamzone.blelibrary.Beacon> getNearbyBeacons() {
		return this.nearbyBeacons;
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
	 * Get particle Filter List
	 * 
	 * @return
	 */
	public List<Particle> getParticleFilterList() {
		return particleFilterList;
	}

	/**
	 * Set Particle Filter list
	 * 
	 * @param particleFilterList
	 */
	public void setParticleFilterList(List<Particle> particleFilterList) {
		this.particleFilterList = particleFilterList;
	}

	/**
	 * Whether the fingerprint(s) are not empty
	 * 
	 * @return
	 */
	public boolean isFingerprintNotEmpty() {
		return isFingerprintNotEmpty;
	}

	/**
	 * Set The fingerprint(s) status
	 * 
	 * @param isFingerprintNotEmpty
	 */
	public void setFingerprintNotEmpty(boolean isFingerprintNotEmpty) {
		this.isFingerprintNotEmpty = isFingerprintNotEmpty;
	}

	/**
	 * Get Current Stage Id
	 * 
	 * @return
	 */
	public int getCurrentStageId() {
		return currentStageId;
	}

	/**
	 * Set Current Stage Id
	 * 
	 * @param currentStageId
	 */
	public void setCurrentStageId(int currentStageId) {
		this.currentStageId = currentStageId;
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
	 * Get particle Step Record
	 * @return
	 */
	public List<ParticleStep> getParticleStepRecord() {
		return particleStepRecord;
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
	 * Is Step Recorded?
	 * 
	 * @return True : False
	 */
	public boolean isStepRecorded() {
		return isStepRecorded;
	}

	/**
	 * Set Step Recorded
	 * 
	 * @param isStepRecorded
	 */
	public void setStepRecorded(boolean isStepRecorded) {
		this.isStepRecorded = isStepRecorded;
	}

	/**
	 * Get Current Step
	 * 
	 * @return
	 */
	public int getCurrentStep() {
		return currentStep;
	}

	/**
	 * Set Current Step
	 * 
	 * @param currentStep
	 */
	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	/**
	 * Get Current File Dumping Text
	 * 
	 * @return
	 */
	public String getCurrentFileDumpingText() {
		return currentFileDumpingText;
	}

	/**
	 * Set Current File Dumping Text
	 * 
	 * @param currentFileDumpingText
	 */
	public void setCurrentFileDumpingText(String currentFileDumpingText) {
		this.currentFileDumpingText = currentFileDumpingText;
	}

	/**
	 * Get Total Weight and Normal Distribution
	 * 
	 * @return
	 */
	public double[] getTotalWeightAndNormalDistribution() {
		return totalWeightAndNormalDistribution;
	}

	/**
	 * Set Total Weight and Normal Distribution
	 * 
	 * @param totalWeightAndNormalDistribution
	 */
	public void setTotalWeightAndNormalDistribution(double[] totalWeightAndNormalDistribution) {
		this.totalWeightAndNormalDistribution = totalWeightAndNormalDistribution;
	}

	/**
	 * Is After Reset
	 * 
	 * @return
	 */
	public boolean isAfterReset() {
		return afterReset;
	}

	/**
	 * Set After Reset
	 * 
	 * @param afterReset
	 */
	public void setAfterReset(boolean afterReset) {
		this.afterReset = afterReset;
	}

	/**
	 * Get Trial Attemp Reset
	 * @return
	 */
	public int getTrialAttempReset() {
		return trialAttempReset;
	}
	
	/**
	 * Set Trial Attemp Reset
	 * @param trialAttempReset
	 */
	public void setTrialAttempReset(int trialAttempReset) {
		this.trialAttempReset = trialAttempReset;
	}

	/**
	 * Get Total Particle
	 * @return
	 */
	public int getTotalParticle() {
		return totalParticle;
	}

	/**
	 * Set Total Particle
	 * @param totalParticle
	 */
	public void setTotalParticle(int totalParticle) {
		this.totalParticle = totalParticle;
	}

	// End Region Getters and Setters

	// Public Internal Method
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

	/**
	 * Update Latest Reference Beacon RSSI Value Periodically
	 * 
	 * @param nearbyBeaconscom
	 *            .wisewells.iamzone.blelibrary.Beacon
	 */
	public void updateLatestReferenceBeaconRSSIValue(List<com.wisewells.iamzone.blelibrary.Beacon> nearbyBeacons) {
		this.nearbyBeacons = nearbyBeacons;
		if (this.stageReferenceBeacon != null && !this.stageReferenceBeacon.isEmpty()) {
			for (Beacon referenceBeacon : this.stageReferenceBeacon) {
				for (com.wisewells.iamzone.blelibrary.Beacon nearbyBeacon : this.nearbyBeacons) {
					if (referenceBeacon.getMacAddress().equals(nearbyBeacon.getMacAddress())) {
						referenceBeacon.setRssi(BeaconHelper.lowPassFilter(nearbyBeacon.getRssi(), referenceBeacon.getRssi()));
					}
				}
			}
		}
	}

	/**
	 * Update Latest Reference Beacon RSSI Value Periodically
	 * 
	 * @param nearbyBeacons
	 */
	public void updateLatestReferenceBeaconRSSIValues(List<com.wisewells.iamzone.blelibrary.Beacon> nearbyBeacons) {
		this.nearbyBeacons = nearbyBeacons;
		if (this.stageReferenceBeacon != null && !this.stageReferenceBeacon.isEmpty()) {
			for (Beacon referenceBeacon : this.stageReferenceBeacon) {
				double rssi = this.fetchNewestRSSI(nearbyBeacons, referenceBeacon);
				if ((int) rssi != -200) {
					referenceBeacon.setRssi(BeaconHelper.lowPassFilter(rssi, referenceBeacon.getRssi()));
				} else {
					referenceBeacon.setRssi(-200);
				}
			}
		}
	}

	/**
	 * Disperse particle on Random Places on the MapView
	 */
	public void disperseParticleOnRandomPlaces(boolean setNullFirst) {
		if (setNullFirst) {
			this.particleFilterList = new ArrayList<Particle>();
		}

		this.particleFilterList = this.particleFilterList == null || this.particleFilterList.isEmpty() ? this.particleFilterList = new ArrayList<Particle>() : this.particleFilterList;
		Random randomXPosition = new Random();
		Random randomYPosition = new Random();

		// coordinate start from 0
		int low = 0;
		int highX = MapsUtils.ROTC_3RD_FLOOR_WIDTH;
		int highY = MapsUtils.ROTC_3RD_FLOOR_HEIGHT;

		for (int i = 0; i < this.totalParticle; i++) {
			int newXPosition = randomXPosition.nextInt(highX - low) + low;
			int newYPosition = randomYPosition.nextInt(highY - low) + low;
			this.particleFilterList.add(new Particle(newXPosition, newYPosition, 1, 0, 10));
		}

		this.updateGeoPointFromMeterPosition();
	}
	
	/**
	 * Disperse particle on Random Places on the MapView
	 */
	public void disperseParticleOnRandomPlacesWithRadius(boolean setNullFirst) {
		
		this.statisticsHelper = new ParticleFilterStatisticsHelper(this.particleFilterList);
		Particle averagePoint = new Particle();
		Particle tmpParticle = statisticsHelper.getAverageLocationParticle();
		
		averagePoint.setParticle_x(tmpParticle.getParticle_x());
		averagePoint.setParticle_y(tmpParticle.getParticle_y());
		
		if (setNullFirst) {
			this.particleFilterList = new ArrayList<Particle>();
		}

		for (int i = 0; i < this.totalParticle; i++) {
			double angle = Math.random() * 2 * Math.PI;
			double newXPosition = (averagePoint.getParticle_x() + Math.cos(angle) * StepDetectorUtils.DISPERSE_ROUND_AREA_RADIUS * Math.random());
			double newYPosition = (averagePoint.getParticle_y() + Math.sin(angle) * StepDetectorUtils.DISPERSE_ROUND_AREA_RADIUS * Math.random());			
			this.particleFilterList.add(new Particle(newXPosition, newYPosition, 1, 0, 10));
		}

		this.updateGeoPointFromMeterPosition();
	}

	/**
	 * Particle for Radar Purpose
	 */
	public void disperseParticleGridEntireStage() {
		this.particleFilterList = new ArrayList<Particle>();
		for (int y = 0; y < MapsUtils.ROTC_3RD_FLOOR_HEIGHT; y++) {
			for (int x = 0; x < MapsUtils.ROTC_3RD_FLOOR_WIDTH; x++) {
				this.particleFilterList.add(new Particle(x, y, 1, 0, 10));
			}
		}
		this.updateGeoPointFromMeterPosition();
	}

	/**
	 * Get Person Bitmap
	 * 
	 * @return Bitmap
	 */
	public Bitmap getPersonBitmap() {
		Bitmap rawPersonBitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.person);
		return Bitmap.createScaledBitmap((Bitmap.createBitmap(rawPersonBitmap, 0, 0, rawPersonBitmap.getWidth(), rawPersonBitmap.getHeight())), rawPersonBitmap.getWidth() / 5, rawPersonBitmap.getHeight() / 5, true);
	}

	/**
	 * Get Beacon Bitmap
	 * 
	 * @return Bitmap
	 */
	public Bitmap getBeaconBitmap() {
		Bitmap rawBeaconBitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.beacon);
		return Bitmap.createScaledBitmap((Bitmap.createBitmap(rawBeaconBitmap, 0, 0, rawBeaconBitmap.getWidth(), rawBeaconBitmap.getHeight())), rawBeaconBitmap.getWidth() / 5, rawBeaconBitmap.getHeight() / 5, true);
	}

	/**
	 * Set Arrow Bitmap With Rotation Angle
	 * 
	 * @param accelData
	 * @param magneticData
	 */
	public void setArrowAngle(float[] accelData, float[] magneticData) {
		this.azimuth = this.getAzimuthDirection(accelData, magneticData);
		this.arrowBitmap = this.createBitmapAsScreenItem(this.rawArrowBitmap, 0, 0, this.rawArrowBitmap.getWidth(), this.rawArrowBitmap.getHeight(), this.azimuth);
	}

	/**
	 * Initialize Kriging Calculation
	 * 
	 * @param activeBeacons
	 * @return
	 */
	public boolean initializeKriging(List<Beacon> activeBeacons) {
		try {
			initializeGridParticleVO();
			int savedFingerprintsSize = savedFingerprints.size() / activeBeacons.size();
			for (Particle particle : getParticleGrid()) {
				ArrayList<MatrixRSSI> matrixRssiList = new ArrayList<MatrixRSSI>();
				for (int i = 0; i < activeBeacons.size(); i++) {
					// region - changed by faris - redeclare array and its count
					double[][] tempMatrix = new double[savedFingerprintsSize + 1][1];
					double[][] tempMatrixRssi = new double[savedFingerprintsSize + 1][1];
					int countColumn = 0;
					// end region - changed by faris - redeclare array and its
					// count
					for (int j = 0; j < savedFingerprints.size(); j++) {
						if (activeBeacons.get(i).getMacAddress().equals(savedFingerprints.get(j).getMacAddress())) {
							// region changed by farissyariati
							tempMatrix[countColumn][0] = Math.pow(PointUtils.KRIGING_GAMMA, 2) * Math.exp(-1 * PointUtils.KRIGING_ALPHA * Math.pow(getDistance(particle, savedFingerprints.get(j)), PointUtils.KRIGING_BETA));
							tempMatrixRssi[countColumn][0] = savedFingerprints.get(j).getRssi();
							countColumn++;
							// end region - changed by farissyariati
						}
					}
					// region - added by farissyariati
					tempMatrix[savedFingerprintsSize][0] = 1;
					tempMatrixRssi[savedFingerprintsSize][0] = 1;
					// end region - added by farissyariati
					Matrix distanceMatrix = new Matrix(tempMatrix);
					matrixInverse = this.getMatrixInverse();
					double[][] tempinverse = matrixInverse.times(distanceMatrix).getArrayCopy();
					double tempMatrixRssiResult = 0;
					for (int z = 0; z < savedFingerprintsSize; z++) {
						tempMatrixRssiResult += tempinverse[z][0] * tempMatrixRssi[z][0];
					}
					matrixRssiList.add(new MatrixRSSI(activeBeacons.get(i).getMacAddress(), tempMatrixRssiResult));
					// end region - added by farissyariati
				}
				particle.setmMatrixRSSIVoList(matrixRssiList);
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Move Particle Toward Azimuth No RSSI Needed
	 */
	public void moveParticleTowardAzimuth() {
		Random random = new Random();
		double dStandardDeviation = Math.pow(StepDetectorUtils.MAP_DISTANCE_VARIANCE, 0.5);
		double aStandardDeviation = Math.pow(StepDetectorUtils.MAP_AZIMUTH_VARIANCE, 0.5);

		for (Particle particle : particleFilterList) {

			double originalX = particle.getParticle_x();
			double originalY = particle.getParticle_y();
			GeoPoint originalPoistion = particle.getGeoPoint();

			double azimuthGaussian = (dStandardDeviation * random.nextGaussian()) + this.azimuth;
			double walkDistanceGaussian = ((aStandardDeviation * random.nextGaussian()) + StepDetectorUtils.WALK_DISTANCE);
			if (walkDistanceGaussian < 0)
				walkDistanceGaussian *= -1;

			particle.setParticle_x(particle.getParticle_x() + (Math.sin(Math.toRadians(azimuthGaussian)) * walkDistanceGaussian * PointUtils.MAP_DISTANCE_SCALE));
			particle.setParticle_y(particle.getParticle_y() + (Math.cos(Math.toRadians(azimuthGaussian)) * walkDistanceGaussian * PointUtils.MAP_DISTANCE_SCALE));

			particle = this.updateParticleGeoPointFromMeterPosition(particle);

			int distanceInMeters = GeoPositionHelper.getParticleDistanceFromCenterMap(particle);

			if (distanceInMeters > MapsUtils.ROTC_MAX_DISTANCE_FROM_CENTER) {
				particle.setParticle_x(originalX);
				particle.setParticle_y(originalY);
				particle.setGeoPoint(originalPoistion);
			}
		}
		// this.updateGeoPointFromMeterPosition();
	}

	/**
	 * Change particle On Step
	 */
	public void changeParticleOnStep(double azimuth) {

		double rssiVariance = PointUtils.RSSI_VARIANCE;
		double distanceVariance = PointUtils.MAP_DISTANCE_VARIANCE;
		double azimuthVariance = PointUtils.MAP_AZIMUTH_VARIANCE;

		this.rssiVarience = rssiVariance;
		Random random = new Random();
		double dStandardDeviation = Math.pow(distanceVariance, 0.5);
		double aStandardDeviation = Math.pow(azimuthVariance, 0.5);

		for (Particle pfvo : this.particleFilterList) {

			double originalX = pfvo.getParticle_x();
			double originalY = pfvo.getParticle_y();
			GeoPoint originalPoistion = pfvo.getGeoPoint();

			double azimuthGaussian = (dStandardDeviation * random.nextGaussian()) + azimuth;
			double walkDistanceGaussian = ((aStandardDeviation * random.nextGaussian()) + StepDetectorUtils.WALK_DISTANCE);
			if (walkDistanceGaussian < 0)
				walkDistanceGaussian *= -1;

			pfvo.setParticle_x(pfvo.getParticle_x() + (Math.sin(Math.toRadians(azimuthGaussian)) * walkDistanceGaussian * PointUtils.MAP_DISTANCE_SCALE));
			pfvo.setParticle_y(pfvo.getParticle_y() + (Math.cos(Math.toRadians(azimuthGaussian)) * walkDistanceGaussian * PointUtils.MAP_DISTANCE_SCALE));

			pfvo = this.updateParticleGeoPointFromMeterPosition(pfvo);

			int distanceInMeters = GeoPositionHelper.getParticleDistanceFromCenterMap(pfvo);

			if (distanceInMeters > MapsUtils.ROTC_MAX_DISTANCE_FROM_CENTER) {
				pfvo.setParticle_x(originalX);
				pfvo.setParticle_y(originalY);
				pfvo.setGeoPoint(originalPoistion);
			}
		}

		for (Particle particleFilterVO : this.particleFilterList) {
			Particle tempParticle = new Particle();
			double minDistance = 9999;
			for (Particle particleGridVO : this.getParticleGrid()) {
				// save to temp value to avoid shallow copy and goes infinity
				Particle tempParticle2 = new Particle();
				tempParticle2.setParticle_x(particleGridVO.getParticle_x());
				tempParticle2.setParticle_y(particleGridVO.getParticle_y());
				double min = ParticleFilterHelper.getDistanceBetweenParticles(particleFilterVO, tempParticle2, DISTANCE_SCALE);
				if (minDistance > min) {
					minDistance = min;
					tempParticle = particleGridVO;
				}
			}
			particleFilterVO.setmMatrixRSSIVoList(tempParticle.getmMatrixRSSIVoList());
		}

		for (Particle particleFilterVO : this.particleFilterList) {
			double totalNormalDistribution = 1;
			for (MatrixRSSI matrixRssiVO : particleFilterVO.getmMatrixRSSIVoList()) {
				for (Beacon preloadBeaconVO : this.stageReferenceBeacon) {
					if (matrixRssiVO.getMatrixMAC().equals(preloadBeaconVO.getMacAddress())) {
						double matrixRssiVORSSI = matrixRssiVO.getMatrixRssi();
						double preLoadBeaconVoRSSI = preloadBeaconVO.getRssi();
						double tempNormalDistribution = ParticleFilterHelper.getNormalDistribution(matrixRssiVORSSI, preLoadBeaconVoRSSI);
						totalNormalDistribution *= tempNormalDistribution;
						break;
					}
				}
			}
			particleFilterVO.setNormalDistribution(totalNormalDistribution);
			particleFilterVO.setParticle_weight(particleFilterVO.getParticle_weight() * particleFilterVO.getNormalDistribution());
		}

		// end region set normal distribution

		this.statisticsHelper = new ParticleFilterStatisticsHelper(this.particleFilterList);
		double totalWeight = this.statisticsHelper.getTotal();
		// end region get total weight

		// set new weight maintain total particle weight = particles size
		for (Particle particleFilterVO : this.particleFilterList) {
			particleFilterVO.setParticle_weight(particleFilterVO.getParticle_weight() / totalWeight * this.particleFilterList.size());
		}

		this.statisticsHelper = new ParticleFilterStatisticsHelper(this.particleFilterList);
		totalWeight = this.statisticsHelper.getTotal();

		// new particle removal
		Collections.sort(this.particleFilterList, new ComparePaticlesByWeight());

		// Particle maxWeightedParticle = this.particleFilterList.get(0);
		// double minValue = maxWeightedParticle.getParticle_weight() * 0.01;

		int lowParticles = this.statisticsHelper.getTotalLowStandardCollection(this.particleFilterList, PointUtils.MINIMUM_PARTICLE_WEIGHT_MULTIPLIER);
		ArrayList<Particle> particleListAfterDeletion = new ArrayList<Particle>();

		for (int i = 0; i < this.particleFilterList.size() - lowParticles; i++) {
			particleListAfterDeletion.add(this.particleFilterList.get(i));
		}

		this.particleFilterList = new ArrayList<Particle>();
		this.particleFilterList = particleListAfterDeletion;

		for (int i = 0; i < lowParticles; i++) {
			double initialWeight = this.particleFilterList.get(i).getParticle_weight();
			this.particleFilterList.get(i).setParticle_weight(initialWeight / 2);
			Particle oldParticle = this.particleFilterList.get(i);
			this.particleFilterList.add(this.getParticleCopy(oldParticle));
		}

		// maintain particle once more

		this.statisticsHelper = new ParticleFilterStatisticsHelper(this.particleFilterList);
		totalWeight = this.statisticsHelper.getTotal();
		// end region get total weight

		// set new weight maintain total particle weight = particles size
		for (Particle particleFilterVO : this.particleFilterList) {
			particleFilterVO.setParticle_weight(particleFilterVO.getParticle_weight() / totalWeight * this.particleFilterList.size());
		}

		// get max and min particle weight;

		double maxWeight = -9999;
		double minWeight = 9999;

		for (Particle particleFilterVO : this.particleFilterList) {
			maxWeight = Math.max(maxWeight, particleFilterVO.getParticle_weight());
			minWeight = Math.min(minWeight, particleFilterVO.getParticle_weight());
		}

		this.statisticsHelper = new ParticleFilterStatisticsHelper(this.particleFilterList);
		totalWeight = this.statisticsHelper.getTotal();
		for (Particle particleFilterVO : this.particleFilterList) {
			particleFilterVO.setRgbColor((int) ((particleFilterVO.getParticle_weight() - minWeight) / (maxWeight - minWeight) * 255));
		}

		this.updateGeoPointFromMeterPosition();

		if (this.isStepRecorded && this.currentStep <= StepDetectorUtils.MAX_STEP_RECORD) {
			this.currentStep++;
			try {
				if (this.currentFileDumpingText == null) {
					this.currentFileDumpingText = ParticleFilterHelper.getNewFileName();
				}

				if (this.currentFileDumpingText != null) {
					ParticleFilterHelper.appendParticleDataToTextFile(context, this.currentFileDumpingText, this.particleFilterList, this.currentStep);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			//Record to Json
			ParticleFilterStatisticsHelper helper = new ParticleFilterStatisticsHelper(this.particleFilterList);
			Particle averageParticle = helper.getAverageLocationParticle();
			this.particleStepRecord.add(new ParticleStep(averageParticle.getParticle_x(), averageParticle.getParticle_y()));
		}

		this.totalWeightAndNormalDistribution = statisticsHelper.getTotalWeightAndNormalDistribution();

		// Check Whether The Position is Totally Wrong
		
		this.selfFixingPosition();
		
		// End oF Check
	}

	public void changeParticleRadius() {
		double rssiVariance = PointUtils.RSSI_VARIANCE;
		this.rssiVarience = rssiVariance;
		for (Particle particleFilterVO : this.particleFilterList) {
			Particle tempParticle = new Particle();
			double minDistance = 9999;
			for (Particle particleGridVO : this.getParticleGrid()) {
				// save to temp value to avoid shallow copy and goes infinity
				Particle tempParticle2 = new Particle();
				tempParticle2.setParticle_x(particleGridVO.getParticle_x());
				tempParticle2.setParticle_y(particleGridVO.getParticle_y());
				double min = ParticleFilterHelper.getDistanceBetweenParticles(particleFilterVO, tempParticle2, DISTANCE_SCALE);
				if (minDistance > min) {
					minDistance = min;
					tempParticle = particleGridVO;
				}
			}
			particleFilterVO.setmMatrixRSSIVoList(tempParticle.getmMatrixRSSIVoList());
		}

		// normal distribution still same
		// region set normal distribution

		for (Particle particleFilterVO : this.particleFilterList) {
			double totalNormalDistribution = 1;
			for (MatrixRSSI matrixRssiVO : particleFilterVO.getmMatrixRSSIVoList()) {
				for (Beacon preloadBeaconVO : this.stageReferenceBeacon) {
					if (matrixRssiVO.getMatrixMAC().equals(preloadBeaconVO.getMacAddress())) {
						double matrixRssiVORSSI = matrixRssiVO.getMatrixRssi();
						double preLoadBeaconVoRSSI = preloadBeaconVO.getRssi();
						double tempNormalDistribution = ParticleFilterHelper.getNormalDistribution(matrixRssiVORSSI, preLoadBeaconVoRSSI);
						totalNormalDistribution *= tempNormalDistribution;
						break;
					}
				}
			}
			particleFilterVO.setNormalDistribution(totalNormalDistribution);
			particleFilterVO.setParticle_weight(particleFilterVO.getParticle_weight() * particleFilterVO.getNormalDistribution());
		}

		// end region set normal distribution

		this.statisticsHelper = new ParticleFilterStatisticsHelper(particleFilterList);
		double totalWeight = this.statisticsHelper.getTotal();
		// end region get total weight

		// set new weight maintain total particle weight = particles size
		for (Particle particleFilterVO : this.particleFilterList) {
			particleFilterVO.setParticle_weight(particleFilterVO.getParticle_weight() / totalWeight * particleFilterList.size());
		}

		double maxWeight = -9999;
		double minWeight = 9999;

		for (Particle particleFilterVO : this.particleFilterList) {
			maxWeight = Math.max(maxWeight, particleFilterVO.getParticle_weight());
			minWeight = Math.min(minWeight, particleFilterVO.getParticle_weight());
		}

		this.statisticsHelper = new ParticleFilterStatisticsHelper(this.particleFilterList);
		totalWeight = this.statisticsHelper.getTotal();
		for (Particle particleFilterVO : particleFilterList) {
			particleFilterVO.setRgbColor((int) ((particleFilterVO.getParticle_weight() - minWeight) / (maxWeight - minWeight) * 255));
			particleFilterVO.setParticle_view_weight((double) particleFilterVO.getParticle_weight() / (double) totalWeight * 100);
		}
	}

	/**
	 * Load Saved Fingerprint Data This function is ported from PDRPractive
	 * Project
	 * 
	 * @param totalActiveBeacon
	 * @param activeBeacons
	 * @param alpha
	 * @param beta
	 * @param gamma
	 */
	public void loadSavedFingerprintData(int totalActiveBeacon, List<Beacon> activeBeacons, double alpha, double beta, double gamma) {
		this.setAlpha(alpha);
		this.setBeta(beta);
		this.setGamma(gamma);

		this.setSavedFingerprints(this.fingerPrintService.getAll());
		int savedFingerprintSize = this.getSavedFingerprints().size() / totalActiveBeacon;
		if (!this.getSavedFingerprints().isEmpty() && savedFingerprintSize > 1) {
			this.setMatrix(savedFingerprints, activeBeacons, savedFingerprintSize);
			this.setFingerprintNotEmpty(true);
		}
	}

	// End Public Internal Method

	// Region Private Internal Method
	/**
	 * Set The Matrix
	 * 
	 * @param savedFingerprints
	 * @param activedBeacons
	 * @param savedFingerprintsSize
	 */
	public void setMatrix(List<Fingerprint> savedFingerprints, List<Beacon> activedBeacons, int savedFingerprintsSize) {
		int savedFingerprintsSizePlus = savedFingerprintsSize + 1;

		double[][] arrSaveBeacon = new double[savedFingerprintsSizePlus][savedFingerprintsSizePlus];
		// we just need to make 1 matrix as pivot point because the value will
		// be the same
		// due the correlation -> kriging
		String thisMac = activedBeacons.get(0).getMacAddress();
		// row
		int countJ = 0;
		for (int j = 0; j < savedFingerprints.size(); j++) {
			// column
			int countZ = 0;
			for (int z = 0; z < savedFingerprints.size(); z++) {
				if (thisMac.equals(savedFingerprints.get(j).getMacAddress()) && thisMac.equals(savedFingerprints.get(z).getMacAddress())) {
					// if the current mac is the same it will set to gamma^2
					// (diagonal will be the same)
					if (savedFingerprints.get(z).getPointX() == savedFingerprints.get(j).getPointX() && savedFingerprints.get(z).getPointY() == savedFingerprints.get(j).getPointY()) {
						arrSaveBeacon[countJ][countZ] = Math.pow(gamma, 2);
					} else {
						// kriging calculation
						// each corelation
						arrSaveBeacon[countJ][countZ] = Math.pow(gamma, 2) * Math.exp(-1 * alpha * Math.pow(getDistance(savedFingerprints.get(z), savedFingerprints.get(j)), beta));
					}
					countZ++;
				}
				if (countZ == savedFingerprintsSize) {
					arrSaveBeacon[countJ][countZ] = 1;
					countJ++;
					countZ = 0;
				}
			}
		}
		if (countJ == savedFingerprintsSize) {
			for (int z = 0; z < savedFingerprintsSizePlus; z++) {
				if (z == savedFingerprintsSize) {
					arrSaveBeacon[countJ][z] = 0;
				} else {
					arrSaveBeacon[countJ][z] = 1;
				}
			}

		}
		Matrix matrixNormal = new Matrix(arrSaveBeacon);
		matrixNormal.print(savedFingerprintsSize, savedFingerprintsSize);
		matrixInverse = matrixNormal.inverse();
	}

	/**
	 * Check Whether Step Is Occur
	 * 
	 * @param accelLinearData
	 * @param accelData
	 * @param magneticData
	 * @return
	 */
	public boolean isStepping(float[] accelLinearData, float[] accelData, float[] magneticData) {
		boolean result = false;
		int azimuth = this.getAzimuthDirection(accelData, magneticData);
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
					// Step Detected Set Result to True
					if (this.particleGrid != null) {
						this.changeParticleOnStep(azimuth);
					} else {
						this.moveParticleTowardAzimuth();
					}

					result = true;
					lastCheckTime = currentTime;
				}
			}
		}
		return result;
	}

	/**
	 * Search Method Fetch Newest RSSI
	 * 
	 * @param newestNearbyBeacons
	 * @param currentComparedBeacon
	 * @return
	 */
	private double fetchNewestRSSI(List<com.wisewells.iamzone.blelibrary.Beacon> newestNearbyBeacons, Beacon currentComparedBeacon) {
		double finalRSSI = -200;
		for (com.wisewells.iamzone.blelibrary.Beacon b : newestNearbyBeacons) {
			if (b.getMacAddress().equals(currentComparedBeacon.getMacAddress())) {
				finalRSSI = b.getRssi();
				break;
			}
		}
		return finalRSSI;
	}

	/**
	 * create particle each point of Kriging calculation This Method is Ported
	 * From PDRPractice Project
	 */
	private void initializeGridParticleVO() {
		this.setParticleGrid(new ArrayList<Particle>());
		for (int y = 0; y < MapsUtils.ROTC_3RD_FLOOR_HEIGHT; y++) {
			for (int x = 0; x < MapsUtils.ROTC_3RD_FLOOR_WIDTH; x++) {
				this.getParticleGrid().add(new Particle(x, y, 1, 0, 10));
			}
		}
	}

	/**
	 * Get Azimuth Direction
	 * 
	 * @param accelData
	 * @param magneticData
	 * @return
	 */
	private int getAzimuthDirection(float accelData[], float magneticData[]) {
		azimuth = 0;
		if (accelData != null && magneticData != null) {
			SensorManager.getRotationMatrix(rotationData, null, accelData, magneticData);
			SensorManager.getOrientation(rotationData, resultData);
			azimuth = (int) Math.toDegrees(resultData[0]);
			azimuth -= PointUtils.BALANCER;
			// it doesnt need
			/*
			 * if (azimuth < 0) { azimuth += 360; }
			 */
		}

		return azimuth;
	}

	/**
	 * Get The Distance Between Fingerprint and Particle
	 * 
	 * @param vo1
	 * @param vo2
	 * @return
	 */
	private double getDistance(Particle vo1, Fingerprint vo2) {
		return Math.pow((Math.pow((vo1.getParticle_x() - vo2.getPointX()), 2) + Math.pow((vo1.getParticle_y() - vo2.getPointY()), 2)), 0.5) * 1;
	}

	/**
	 * Get Distance Between 2 Fingerprint
	 * 
	 * @param vo1
	 * @param vo2
	 * @return
	 */
	private double getDistance(Fingerprint vo1, Fingerprint vo2) {
		return Math.pow((Math.pow((vo1.getPointX() - vo2.getPointX()), 2) + Math.pow((vo1.getPointY() - vo2.getPointY()), 2)), 0.5) * 1;
	}

	/**
	 * Get the Copy of Particle
	 * 
	 * @param oldParticle
	 * @return
	 */
	private Particle getParticleCopy(Particle oldParticle) {
		Particle newParticle = new Particle();
		newParticle.setmMatrixRSSIVoList(oldParticle.getmMatrixRSSIVoList());
		newParticle.setNormalDistribution(oldParticle.getNormalDistribution());
		newParticle.setParticle_x(oldParticle.getParticle_x());
		newParticle.setParticle_y(oldParticle.getParticle_y());
		newParticle.setParticle_weight(oldParticle.getParticle_weight());
		newParticle.setParticle_view_weight(oldParticle.getParticle_view_weight());
		newParticle.setRgbColor(oldParticle.getRgbColor());
		return newParticle;
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
		android.graphics.Matrix matrix = new android.graphics.Matrix();
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
		android.graphics.Matrix matrix = new android.graphics.Matrix();
		matrix.postRotate(azimuth);
		matrix.postScale(0.3f, 0.3f);
		return Bitmap.createBitmap(bitmap, pointX, pointY, width, height, matrix, true);
	}

	/**
	 * This function will be called when particle's x,y in meter is changed
	 */
	private void updateGeoPointFromMeterPosition() {
		for (Particle particle : this.particleFilterList) {
			GeoPoint particleLatLon = GeoPositionHelper.convertXYMilesToLatLon(GeoPositionHelper.magnifiedPosition(GeoPositionHelper.convertMeterToMiles(new PointF((float) particle.getParticle_x(), (float) particle.getParticle_y())), 100),
					MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_00);
			particle.setGeoPoint(particleLatLon);
		}
	}

	/**
	 * Update particle GeoPoint
	 * 
	 * @param particle
	 * @return
	 */
	private Particle updateParticleGeoPointFromMeterPosition(Particle particle) {
		GeoPoint particleLatLon = GeoPositionHelper.convertXYMilesToLatLon(GeoPositionHelper.magnifiedPosition(GeoPositionHelper.convertMeterToMiles(new PointF((float) particle.getParticle_x(), (float) particle.getParticle_y())), 100),
				MapsUtils.ROTC_3RD_FLOOR_GEOPOINT_00);
		particle.setGeoPoint(particleLatLon);
		return particle;
	}

	/**
	 * Self Fixing Position
	 */
	private void selfFixingPosition() {
		if (this.afterReset && this.trialAttempReset == StepDetectorUtils.MAXIMUM_TRIAL_ATTEMP_RESET) {
			this.trialAttempReset = 0;
			this.afterReset = false;
		}

		if (this.totalWeightAndNormalDistribution[3] < StepDetectorUtils.MINIMUM_DISTANCE_TOLERANCE && this.trialAttempReset == 0) {
			this.disperseParticleOnRandomPlacesWithRadius(true);
			this.afterReset = true;
		}

		if (this.afterReset) {
			this.trialAttempReset++;
		}
	}

	// End Private Internal Method

	// Region Inner Class
	/**
	 * Class Compare Particle By Weight
	 * 
	 * @author SeoulTech Application Software Lab
	 *
	 */
	public class ComparePaticlesByWeight implements Comparator<Particle> {
		@Override
		public int compare(Particle a, Particle b) {
			if (a.getParticle_weight() > b.getParticle_weight()) {
				return -1;
			} else if (a.getParticle_weight() == b.getParticle_weight()) {
				return 0;
			} else {
				return 1;
			}
		}
	}
	// End Region Inner Class
}
