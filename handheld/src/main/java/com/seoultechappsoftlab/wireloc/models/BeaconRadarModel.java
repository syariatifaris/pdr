package com.seoultechappsoftlab.wireloc.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import Jama.Matrix;
import android.content.Context;
import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.entities.MatrixRSSI;
import com.seoultechappsoftlab.wireloc.entities.Particle;
import com.seoultechappsoftlab.wireloc.entities.PointBeacon;
import com.seoultechappsoftlab.wireloc.helpers.BeaconHelper;
import com.seoultechappsoftlab.wireloc.helpers.ParticleFilterHelper;
import com.seoultechappsoftlab.wireloc.services.BeaconService;
import com.seoultechappsoftlab.wireloc.services.FingerprintService;
import com.seoultechappsoftlab.wireloc.utilities.CustomCharactersUtils;
import com.seoultechappsoftlab.wireloc.utilities.PointUtils;

/**
 * Beacon Radar Activity Model
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class BeaconRadarModel {

	// Region Private Variable
	private List<Beacon> activeBeacons;
	private List<com.wisewells.iamzone.blelibrary.Beacon> nearbyBeacons;
	private List<PointBeacon> particles;
	private PointBeacon mostProbablyLocation;
	private List<Fingerprint> fingerprints;
	private Matrix inverseMatrix;
	
	private String rssiInformation;
	private String sampleBeaconMac;
	
	private FingerprintService fingerprintService;
	private BeaconService beaconService;
	
	private static final int WORKING_STAGE_ID = 1;
	private static final double SCALE = 0.5;
	private static final int RSSI_VARIANCE = 20;
	
	private List<Particle> particlesGrid;
	
	// End Region Private Variable
	
	/**
	 * Constructor
	 * @param context
	 */
	public BeaconRadarModel(Context context){
		this.fingerprintService = new FingerprintService(context);
		this.beaconService = new BeaconService(context);
		
		//Set the active beacon
		this.initializeActiveBeacon();
	}
	
	//Region Getters and Setters
	
	/**
	 * Get Actives Beacon
	 * @return Beacon(s)
	 */
	public List<Beacon> getActiveBeacons() {
		return activeBeacons;
	}
	
	/**
	 * Set Active Beacons
	 * @param activeBeacons
	 */
	public void setActiveBeacons(List<Beacon> activeBeacons) {
		this.activeBeacons = activeBeacons;
	}
	
	/**
	 * Get Nearby Beacons
	 * @return Beacon(s)
	 */
	public List<com.wisewells.iamzone.blelibrary.Beacon> getNearbyBeacons() {
		return nearbyBeacons;
	}
	
	/**
	 * Set The Nearby Beacons
	 * @param nearbyBeacons
	 */
	public void setNearbyBeacons(
			List<com.wisewells.iamzone.blelibrary.Beacon> nearbyBeacons) {
		this.nearbyBeacons = nearbyBeacons;
	}
	
	/**
	 * Get Particles
	 * @return Point Beacon
	 */
	public List<PointBeacon> getParticles() {
		return particles;
	}

	/**
	 * Set Particles
	 * @param particles
	 */
	public void setParticles(List<PointBeacon> particles) {
		this.particles = particles;
	}
	
	/**
	 * Get Fingerprint Point
	 * @return
	 */
	public List<Fingerprint> getFingerprints() {
		return fingerprints;
	}
	
	/**
	 * Set Fingerprint Point
	 * @param fingerprints
	 */
	public void setFingerprints(List<Fingerprint> fingerprints) {
		this.fingerprints = fingerprints;
	}
	
	/**
	 * Get Inverse Matrix
	 * @return Matrix
	 */
	public Matrix getInverseMatrix() {
		return inverseMatrix;
	}
	
	/**
	 * Set Inverse Matrix
	 * @param inverseMatrix
	 */
	public void setInverseMatrix(Matrix inverseMatrix) {
		this.inverseMatrix = inverseMatrix;
	}
	
	/**
	 * Get RSSI Information
	 * @return String
	 */
	public String getRssiInformation() {
		return rssiInformation;
	}
	
	/**
	 * Set RSSI Information
	 * @param rssiInformation
	 */
	public void setRssiInformation(String rssiInformation) {
		this.rssiInformation = rssiInformation;
	}
	
	/**
	 * Get Sample Beacon MAC Address
	 * @return String
	 */
	public String getSampleBeaconMac() {
		return sampleBeaconMac;
	}
	
	/**
	 * Set Sample Beacon MAC Address
	 * @param sampleBeaconMac
	 */
	public void setSampleBeaconMac(String sampleBeaconMac) {
		this.sampleBeaconMac = sampleBeaconMac;
	}
	
	/**
	 * Get Current Working Stage ID
	 * @return Integer
	 */
	public int getCurrentStageId(){
		return WORKING_STAGE_ID;
	}
	
	//End Region Getters and Setters
	
	//Region Public Method
	
	/**
	 * Load Saved Fingerprint Data
	 */
	public void loadSavedFingerprintData(){
		this.fingerprints = this.fingerprintService.getAllByStageId(WORKING_STAGE_ID);
		if (!this.fingerprints.isEmpty() && this.fingerprints.size() > 1){
			this.setInverseMatrix();
		}
	}
	
	/**
	 * Run Update Beacon Radar in Some Delay
	 */
	public void updateBeaconRadarInDelay(){
		StringBuffer sb = new StringBuffer();
		int count = 0;
		for(Beacon activeBeacon : this.activeBeacons){
			for(com.wisewells.iamzone.blelibrary.Beacon nearbyBeacon : this.nearbyBeacons){
				if(activeBeacon.getMacAddress().equals(nearbyBeacon.getMacAddress())){
					activeBeacon.setRssi(BeaconHelper.lowPassFilter(nearbyBeacon.getRssi(), activeBeacon.getRssi()));
					sb.append(activeBeacon.getMacAddress() + CustomCharactersUtils.SPACE_DASH_SPACE + activeBeacon.getRssi());
					count++;
					if (count == 2) {
						sb.append(CustomCharactersUtils.NEW_LINE);
						count = 0;
					} else {
						sb.append(CustomCharactersUtils.SPACE_COMMA_SPACE);
					}
				}
			}
		}
		this.rssiInformation = sb.toString();
		
		for (PointBeacon particleFilterVO : particles) {
			Particle tempParticle = new Particle();
			double minDistance = 9999;
			for (Particle particleGridVO : this.particlesGrid) {
				// save to temp value to avoid shallow copy and goes infinity
				Particle tempParticle2 = new Particle();
				tempParticle2.setParticle_x(particleGridVO.getParticle_x() * PointUtils.SCALE_X);
				tempParticle2.setParticle_y(particleGridVO.getParticle_y() * PointUtils.SCALE_Y);
				double min = ParticleFilterHelper.getDistanceBetweenParticlesForRadar(particleFilterVO, tempParticle2, 1.0);
				if (minDistance > min) {
					minDistance = min;
					tempParticle = particleGridVO;
				}
			}
			particleFilterVO.setMatrixRssiList((ArrayList<MatrixRSSI>)tempParticle.getmMatrixRSSIVoList());
		}

		// normal distribution still same
		// region set normal distribution

		for (PointBeacon particleFilterVO : particles) {
			double totalNormalDistribution = 1;
			for (MatrixRSSI matrixRssiVO : particleFilterVO.getMatrixRssiList()) {
				for (Beacon preloadBeaconVO : this.activeBeacons) {
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
		}
		
		double highValue = -9999;
		double lowValue = 9999;
		
		for (PointBeacon pointBeaconVO : this.particles) {
			highValue = Math.max(highValue, pointBeaconVO.getNormalDistribution());
			lowValue = Math.min(lowValue, pointBeaconVO.getNormalDistribution());
		}

		// get the most probably point location
		this.mostProbablyLocation = Collections.max(this.particles, new ComparePointBeacon());

		for (PointBeacon pointBeaconVO : this.particles) {
			if (pointBeaconVO.getX() == mostProbablyLocation.getX() && pointBeaconVO.getY() == mostProbablyLocation.getY()) {
				pointBeaconVO.setMostProbably(true);
			} else {
				pointBeaconVO.setMostProbably(false);
			}
			
			pointBeaconVO.setRgbColor((int) ((pointBeaconVO.getNormalDistribution() - lowValue) / (highValue - lowValue) * 255));
		}
	}
	
	//End Region Public Method
	
	//Region Private Method
	
	/**
	 * Initialize Active Beacon
	 * Set to Stage 1
	 */
	private void initializeActiveBeacon(){
		//Beacon Radar Works on Lab 1
		this.activeBeacons = this.beaconService.getAll(WORKING_STAGE_ID);
	}
	
	/**
	 * Set The Matrix Inverse
	 */
	private void setInverseMatrix(){
		int savedFingerprintsSize = this.fingerprints.size() / this.activeBeacons.size();
		int savedFingerprintsSizePlus = savedFingerprintsSize + 1;
		double[][] arrSaveBeacon = new double[savedFingerprintsSizePlus][savedFingerprintsSizePlus];
		String thisMac = this.activeBeacons.get(0).getMacAddress();
		int countJ = 0;
		for (int j = 0; j < this.fingerprints.size(); j++) {
			// column
			int countZ = 0;
			for (int z = 0; z < this.fingerprints.size(); z++) {
				if (thisMac.equals(this.fingerprints.get(j).getMacAddress())
						&& thisMac.equals(this.fingerprints.get(z).getMacAddress())) {
					// if the current mac is the same it will set to gamma^2
					// (diagonal will be the same)
					if (this.fingerprints.get(z).getPointX() == this.fingerprints.get(j).getPointX()
							&& this.fingerprints.get(z).getPointY() == this.fingerprints.get(j).getPointY()) {
						arrSaveBeacon[countJ][countZ] = Math.pow(PointUtils.KRIGING_GAMMA, 2);
					} else {
						// kriging calculation
						// each corelation
						arrSaveBeacon[countJ][countZ] = Math.pow(PointUtils.KRIGING_GAMMA, 2)
								* Math.exp(-1 * PointUtils.KRIGING_ALPHA * Math.pow(getDistance(this.fingerprints.get(z), this.fingerprints.get(j)), PointUtils.KRIGING_BETA));
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
		this.inverseMatrix = matrixNormal.inverse();
	}
	
	/**
	 * Initialize Grid Result For Kriging
	 */
	private void initializeParticleGrid(){
		this.particlesGrid = new ArrayList<Particle>();
		for (int y = 0; y < 31; y++) {
			for (int x = 0; x < 17; x++) {
				this.particlesGrid.add(new Particle(x, y, 1, 0, 10));
			}
		}
	}
	
	/**
	 * Run Kriging Calculation
	 * Called at the first time
	 */
	public void initializeKriging(){
		this.initializeParticleGrid();
		List<Beacon> currentStageActiveBeacons = this.beaconService.getAll(1);
		List<Fingerprint> currentStageFingerprints = this.fingerprintService.getAllByStageId(1);
		int currentStageFingerprintsSize = currentStageFingerprints.size() / currentStageActiveBeacons.size();
		if (currentStageFingerprints.size() > 0) {
			for (Particle particle : this.particlesGrid) {
				ArrayList<MatrixRSSI> matrixRssiList = new ArrayList<MatrixRSSI>();
				for (int i = 0; i < currentStageActiveBeacons.size(); i++) {
					// region - changed by faris - redeclare array and
					// its
					// count
					double[][] tempMatrix = new double[currentStageFingerprintsSize + 1][1];
					double[][] tempMatrixRssi = new double[currentStageFingerprintsSize + 1][1];
					int countColumn = 0;
					// end region - changed by faris - redeclare array
					// and
					// its
					// count
					for (int j = 0; j < currentStageFingerprints.size(); j++) {
						if (currentStageActiveBeacons.get(i).getMacAddress().equals(currentStageFingerprints.get(j).getMacAddress())) {
							// region changed by farissyariati
							tempMatrix[countColumn][0] = Math.pow(PointUtils.KRIGING_GAMMA, 2)
									* Math.exp(-1 * PointUtils.KRIGING_ALPHA
											* Math.pow(getDistance(particle, currentStageFingerprints.get(j)), PointUtils.KRIGING_BETA));
							tempMatrixRssi[countColumn][0] = currentStageFingerprints.get(j).getRssi();
							countColumn++;
							// end region - changed by farissyariati
						}
					}
					// region - added by farissyariati
					tempMatrix[currentStageFingerprintsSize][0] = 1;
					tempMatrixRssi[currentStageFingerprintsSize][0] = 1;
					// end region - added by farissyariati
					Matrix distanceMatrix = new Matrix(tempMatrix);
					Matrix currentInverseMatrix = this.getInverseMatrix();
					double[][] tempinverse = currentInverseMatrix.times(distanceMatrix).getArrayCopy();
					double tempMatrixRssiResult = 0;
					for (int z = 0; z < currentStageFingerprintsSize; z++) {
						tempMatrixRssiResult += tempinverse[z][0] * tempMatrixRssi[z][0];
					}
					matrixRssiList.add(new MatrixRSSI(currentStageActiveBeacons.get(i).getMacAddress(), tempMatrixRssiResult));
					// end region - added by farissyariati
				}
				particle.setmMatrixRSSIVoList(matrixRssiList);
			}
		}
	}
	
	/**
	 * Get Distance Between 2 Fingerprint
	 * 
	 * @param vo1
	 * @param vo2
	 * @return
	 */
	private static double getDistance(Fingerprint vo1, Fingerprint vo2) {
		return Math.pow((Math.pow((vo1.getPointX() - vo2.getPointX()), 2) + Math.pow((vo1.getPointY() - vo2.getPointY()), 2)), SCALE) * 1;
	}
	
	/**
	 * Get The Distance Between Fingerprint and Particle
	 * 
	 * @param vo1
	 * @param vo2
	 * @return
	 */
	private double getDistance(Particle vo1, Fingerprint vo2) {
		return Math.pow((Math.pow((vo1.getParticle_x() - vo2.getPointX()), 2) + Math.pow((vo1.getParticle_y() - vo2.getPointY()), 2)), SCALE) * 1;
	}
	
	/**
	 * Get Normal Distribution Value Between 2 RSSI
	 * @param standardRssiValue
	 * @param rssiValue
	 * @return Double
	 */
	public static double getNormalDistribution(double standardRssiValue, double rssiValue) {
		return Math.exp((Math.pow((standardRssiValue - rssiValue), 2) / (-2 * RSSI_VARIANCE)));
	}
	
	//End Region Private Variable
	
	/**
	 * internal class. check the maximum normal distribution in point beacon VO
	 * collection
	 * 
	 * @author Seoul Tech Application Software Lab
	 *
	 */
	protected class ComparePointBeacon implements Comparator<PointBeacon> {
		@Override
		public int compare(PointBeacon a, PointBeacon b) {
			if (a.getNormalDistribution() < b.getNormalDistribution()) {
				return -1;
			} else if (a.getNormalDistribution() == b.getNormalDistribution()) {
				return 0;
			} else {
				return 1;
			}
		}
	}
}
