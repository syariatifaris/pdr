package com.seoultechappsoftlab.wireloc.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;

import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.entities.MatrixRSSI;
import com.seoultechappsoftlab.wireloc.entities.Particle;
import com.seoultechappsoftlab.wireloc.entities.Stage;
import com.seoultechappsoftlab.wireloc.helpers.ParticleFilterHelper;
import com.seoultechappsoftlab.wireloc.helpers.ParticleFilterStatisticsHelper;
import com.seoultechappsoftlab.wireloc.services.BeaconService;
import com.seoultechappsoftlab.wireloc.services.FingerprintService;
import com.seoultechappsoftlab.wireloc.services.StageService;
import com.seoultechappsoftlab.wireloc.utilities.PointUtils;

import Jama.Matrix;

/**
 * Particle Filter's Model
 * 
 * @author SeoulTech Application Software Lab
 *
 */
@SuppressLint("UseSparseArrays")
public class ParticleFilterModel {
	// Region Private Variable
	private double rssiVarience;
	private Matrix matrixInverse;

	// Region Dynamic Kriging Calculation
	private HashMap<Integer, Matrix> inverseMatrices;
	private HashMap<Integer, List<Particle>> particleGrids;
	// End Region Dynamic Kriging Calculation

	private List<Fingerprint> savedFingerprints;
	private boolean isFingerprintNotEmpty;
	private double alpha;
	private double beta;
	private double gamma;
	private ParticleFilterStatisticsHelper statisticsHelper;
	private List<Particle> particlesGrid;
	private FingerprintService fingerPrintService;
	private BeaconService beaconService;
	private StageService stageService;
	// End Region Private Variable

	// Region Constants
	private static final double DISTANCE_SCALE = 1;

	// End Region Constants
	/**
	 * The Constructor
	 */
	public ParticleFilterModel(Context context) {
		this.setRssiVarience(0.0);
		this.fingerPrintService = new FingerprintService(context);
		this.stageService = new StageService(context);
		this.beaconService = new BeaconService(context);
		this.inverseMatrices = new HashMap<Integer, Matrix>();
		this.particleGrids = new HashMap<Integer, List<Particle>>();
	}

	// Region Getters and Setters
	/**
	 * Get The RSSI Variance
	 * 
	 * @return
	 */
	public double getRssiVarience() {
		return this.rssiVarience;
	}

	/**
	 * Set The RSSI Variance
	 * 
	 * @param rssiVarience
	 */
	public void setRssiVarience(double rssiVarience) {
		this.rssiVarience = rssiVarience;
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
	 * Get The Alpha
	 * 
	 * @return
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * Set The Alpha
	 * 
	 * @param alpha
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * Get The Beta
	 * 
	 * @return
	 */
	public double getBeta() {
		return beta;
	}

	/**
	 * Set The Beta
	 * 
	 * @param beta
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}

	/**
	 * Get The Gamma
	 * 
	 * @return
	 */
	public double getGamma() {
		return gamma;
	}

	/**
	 * Set the Gamma
	 * 
	 * @param gamma
	 */
	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	/**
	 * Get Particles
	 * 
	 * @return
	 */
	public List<Particle> getParticleGrid() {
		return this.particlesGrid;
	}

	/**
	 * Set Particles
	 * 
	 * @param particles
	 */
	public void setParticleGrid(List<Particle> particleGrid) {
		this.particlesGrid = particleGrid;
	}

	/**
	 * Get Inverse Matrices (Hash Map)
	 * 
	 * @return
	 */
	public HashMap<Integer, Matrix> getInverseMatrices() {
		return inverseMatrices;
	}

	/**
	 * Set Inverse Matrices (Hash Map);
	 * 
	 * @param inverseMatrices
	 */
	public void setInverseMatrices(HashMap<Integer, Matrix> inverseMatrices) {
		this.inverseMatrices = inverseMatrices;
	}

	/**
	 * Get Particle Grids (Hash Map)
	 * 
	 * @return
	 */
	public HashMap<Integer, List<Particle>> getParticleGrids() {
		return particleGrids;
	}

	/**
	 * Set particle grids (Hash Maps)
	 * 
	 * @param particleGrids
	 */
	public void setParticleGrids(HashMap<Integer, List<Particle>> particleGrids) {
		this.particleGrids = particleGrids;
	}

	// End Region Getters and Setters

	// Public Internal Method
	/**
	 * Initialize particles at the first time into random position
	 * 
	 * @param particleFilterList
	 * @param canvasWidth
	 * @param canvasHeight
	 * @param particleSize
	 * @return
	 */
	public List<Particle> initializeParticles(List<Particle> particleFilterList, int canvasWidth, int canvasHeight, int particleSize) {
		Random randomXPosition = new Random();
		Random randomYPosition = new Random();
		// coordinate start from 0
		int low = 0;
		int highX = canvasWidth - 10;
		int highY = canvasHeight - 10;
		for (int i = 0; i < PointUtils.TOTAL_PARTICLE; i++) {
			int newXPosition = randomXPosition.nextInt(highX - low) + low;
			int newYPosition = randomYPosition.nextInt(highY - low) + low;
			particleFilterList.add(new Particle(newXPosition, newYPosition, 1, 0, 10));
		}
		return particleFilterList;
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
							tempMatrix[countColumn][0] = Math.pow(PointUtils.KRIGING_GAMMA, 2)
									* Math.exp(-1 * PointUtils.KRIGING_ALPHA
											* Math.pow(getDistance(particle, savedFingerprints.get(j)), PointUtils.KRIGING_BETA));
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
	 * Initialize All Stages Kriging when There Are More than One Stage
	 * 
	 * @return
	 */
	public boolean initializeAllStageKriging() {
		try {
			this.initializeAllStageGridParticleVO();
			// Get All Stage
			List<Stage> stages = this.stageService.getAll();
			for (Stage stage : stages) {
				List<Beacon> currentStageActiveBeacons = this.beaconService.getAll(stage.getId());
				List<Fingerprint> currentStageFingerprints = this.fingerPrintService.getAllByStageId(stage.getId());
				int currentStageFingerprintsSize = currentStageFingerprints.size() / currentStageActiveBeacons.size();
				if (currentStageFingerprints.size() > 0) {
					for (Particle particle : this.particleGrids.get(stage.getId())) {
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
							Matrix currentInverseMatrix = this.getInverseMatrices().get(stage.getId());
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
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Change Particle On Step Occur This Method is Ported From PDRPractice
	 * Project
	 * 
	 * @param particleFilterList
	 * @param preloadBeaconList
	 * @param distanceVariance
	 * @param azimuthVeriance
	 * @param rssiVariance
	 * @param walkDistance
	 * @param azimuth
	 * @return
	 */
	public List<Particle> changeParticle(List<Particle> particleFilterList, List<Beacon> preloadBeaconList, double distanceVariance,
			double azimuthVeriance, double rssiVariance, double walkDistance, int azimuth) {
		this.setRssiVarience(rssiVariance);
		Random random = new Random();
		double dStandardDeviation = Math.pow(distanceVariance, 0.5);
		double aStandardDeviation = Math.pow(azimuthVeriance, 0.5);
		for (Particle pfvo : particleFilterList) {
			double azimuthGaussian = (dStandardDeviation * random.nextGaussian()) + azimuth;
			double walkDistanceGaussian = ((aStandardDeviation * random.nextGaussian()) + walkDistance);
			if (walkDistanceGaussian < 0)
				walkDistanceGaussian *= -1;
			// x , y position will be times to scale so the distance will be
			// bigger
			pfvo.setParticle_x(pfvo.getParticle_x()
					+ (Math.cos(Math.toRadians(azimuthGaussian - 90)) * walkDistanceGaussian * PointUtils.DISTANCE_SCALE));
			pfvo.setParticle_y(pfvo.getParticle_y()
					+ (Math.sin(Math.toRadians(azimuthGaussian - 90)) * walkDistanceGaussian * PointUtils.DISTANCE_SCALE));
		}

		// uncomment this part if using initialization Kriging to search
		// correspondent point of each particle
		// this value not yet scaled
		// start search
		for (Particle particleFilterVO : particleFilterList) {
			Particle tempParticle = new Particle();
			double minDistance = 9999;
			for (Particle particleGridVO : this.getParticleGrid()) {
				// save to temp value to avoid shallow copy and goes infinity
				Particle tempParticle2 = new Particle();
				tempParticle2.setParticle_x(particleGridVO.getParticle_x() * PointUtils.SCALE_X);
				tempParticle2.setParticle_y(particleGridVO.getParticle_y() * PointUtils.SCALE_Y);
				double min = ParticleFilterHelper.getDistanceBetweenParticles(particleFilterVO, tempParticle2, DISTANCE_SCALE);
				if (minDistance > min) {
					minDistance = min;
					tempParticle = particleGridVO;
				}
			}
			particleFilterVO.setmMatrixRSSIVoList(tempParticle.getmMatrixRSSIVoList());
		}
		// end search

		// normal distribution still same
		// region set normal distribution

		for (Particle particleFilterVO : particleFilterList) {
			double totalNormalDistribution = 1;
			for (MatrixRSSI matrixRssiVO : particleFilterVO.getmMatrixRSSIVoList()) {
				for (Beacon preloadBeaconVO : preloadBeaconList) {
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
		for (Particle particleFilterVO : particleFilterList) {
			particleFilterVO.setParticle_weight(particleFilterVO.getParticle_weight() / totalWeight * particleFilterList.size());
		}

		this.statisticsHelper = new ParticleFilterStatisticsHelper(particleFilterList);
		int lowParticles = this.statisticsHelper.getLowStandardCollection().size();
		totalWeight = this.statisticsHelper.getTotal();

		// new particle removal
		Collections.sort(particleFilterList, new ComparePaticlesByWeight());
		ArrayList<Particle> particleListAfterDeletion = new ArrayList<Particle>();

		for (int i = 0; i < particleFilterList.size() - lowParticles; i++) {
			particleListAfterDeletion.add(particleFilterList.get(i));
		}

		particleFilterList = new ArrayList<Particle>();
		particleFilterList = particleListAfterDeletion;

		for (int i = 0; i < lowParticles; i++) {
			double initialWeight = particleFilterList.get(i).getParticle_weight();
			particleFilterList.get(i).setParticle_weight(initialWeight / 2);
			Particle oldParticle = particleFilterList.get(i);
			particleFilterList.add(this.getParticleCopy(oldParticle));
		}

		// maintain particle once more

		this.statisticsHelper = new ParticleFilterStatisticsHelper(particleFilterList);
		totalWeight = this.statisticsHelper.getTotal();
		// end region get total weight

		// set new weight maintain total particle weight = particles size
		for (Particle particleFilterVO : particleFilterList) {
			particleFilterVO.setParticle_weight(particleFilterVO.getParticle_weight() / totalWeight * particleFilterList.size());
		}

		// get max and min particle weight;

		double maxWeight = -9999;
		double minWeight = 9999;

		for (Particle particleFilterVO : particleFilterList) {
			maxWeight = Math.max(maxWeight, particleFilterVO.getParticle_weight());
			minWeight = Math.min(minWeight, particleFilterVO.getParticle_weight());
		}

		this.statisticsHelper = new ParticleFilterStatisticsHelper(particleFilterList);
		totalWeight = this.statisticsHelper.getTotal();
		for (Particle particleFilterVO : particleFilterList) {
			particleFilterVO.setRgbColor((int) ((particleFilterVO.getParticle_weight() - minWeight) / (maxWeight - minWeight) * 255));
			particleFilterVO.setParticle_view_weight((double) particleFilterVO.getParticle_weight() / (double) totalWeight * 700);
		}

		// end of new particle removal
		return particleFilterList;
	}

	/**
	 * Radius Activity
	 * 
	 * @param particleFilterList
	 * @param preloadBeaconList
	 * @param distanceVariance
	 * @param azimuthVeriance
	 * @param rssiVariance
	 * @param walkDistance
	 * @param azimuth
	 * @return
	 */
	public List<Particle> radiusParticle(List<Particle> particleFilterList, List<Beacon> preloadBeaconList, double distanceVariance,
			double azimuthVeriance, double rssiVariance) {
		this.setRssiVarience(rssiVariance);

		for (Particle particleFilterVO : particleFilterList) {
			Particle tempParticle = new Particle();
			double minDistance = 9999;
			for (Particle particleGridVO : this.getParticleGrid()) {
				// save to temp value to avoid shallow copy and goes infinity
				Particle tempParticle2 = new Particle();
				tempParticle2.setParticle_x(particleGridVO.getParticle_x() * PointUtils.SCALE_X);
				tempParticle2.setParticle_y(particleGridVO.getParticle_y() * PointUtils.SCALE_Y);
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

		for (Particle particleFilterVO : particleFilterList) {
			double totalNormalDistribution = 1;
			for (MatrixRSSI matrixRssiVO : particleFilterVO.getmMatrixRSSIVoList()) {
				for (Beacon preloadBeaconVO : preloadBeaconList) {
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
		for (Particle particleFilterVO : particleFilterList) {
			particleFilterVO.setParticle_weight(particleFilterVO.getParticle_weight() / totalWeight * particleFilterList.size());
		}

		double maxWeight = -9999;
		double minWeight = 9999;

		for (Particle particleFilterVO : particleFilterList) {
			maxWeight = Math.max(maxWeight, particleFilterVO.getParticle_weight());
			minWeight = Math.min(minWeight, particleFilterVO.getParticle_weight());
		}

		this.statisticsHelper = new ParticleFilterStatisticsHelper(particleFilterList);
		totalWeight = this.statisticsHelper.getTotal();
		for (Particle particleFilterVO : particleFilterList) {
			particleFilterVO.setRgbColor((int) ((particleFilterVO.getParticle_weight() - minWeight) / (maxWeight - minWeight) * 255));
			particleFilterVO.setParticle_view_weight((double) particleFilterVO.getParticle_weight() / (double) totalWeight * 700);
		}

		// end of new particle removal
		return particleFilterList;
	}

	/**
	 * Change Particle On Step Listen to Stage Id
	 * 
	 * @author farissyariati
	 * @param particleFilterList
	 * @param preloadBeaconList
	 * @param distanceVariance
	 * @param azimuthVeriance
	 * @param rssiVariance
	 * @param walkDistance
	 * @param azimuth
	 * @param stageId
	 * @return
	 */
	public List<Particle> changeParticleOnStep(List<Particle> particleFilterList, List<Beacon> preloadBeaconList, double distanceVariance,
			double azimuthVeriance, double rssiVariance, double walkDistance, int azimuth, int stageId) {
		try {
			this.setRssiVarience(rssiVariance);
			Random random = new Random();
			double dStandardDeviation = Math.pow(distanceVariance, 0.5);
			double aStandardDeviation = Math.pow(azimuthVeriance, 0.5);
			for (Particle pfvo : particleFilterList) {
				double azimuthGaussian = (dStandardDeviation * random.nextGaussian()) + azimuth;
				double walkDistanceGaussian = ((aStandardDeviation * random.nextGaussian()) + walkDistance);
				if (walkDistanceGaussian < 0)
					walkDistanceGaussian *= -1;
				// x , y position will be times to scale so the distance will be
				// bigger
				pfvo.setParticle_x(pfvo.getParticle_x()
						+ (Math.cos(Math.toRadians(azimuthGaussian - 90)) * walkDistanceGaussian * PointUtils.DISTANCE_SCALE));
				pfvo.setParticle_y(pfvo.getParticle_y()
						+ (Math.sin(Math.toRadians(azimuthGaussian - 90)) * walkDistanceGaussian * PointUtils.DISTANCE_SCALE));
			}

			for (Particle particleFilterVO : particleFilterList) {
				Particle tempParticle = new Particle();
				double minDistance = 9999;
				for (Particle particleGridVO : this.particleGrids.get(stageId)) {
					Particle tempParticle2 = new Particle();
					tempParticle2.setParticle_x(particleGridVO.getParticle_x() * PointUtils.SCALE_X);
					tempParticle2.setParticle_y(particleGridVO.getParticle_y() * PointUtils.SCALE_Y);
					double min = ParticleFilterHelper.getDistanceBetweenParticles(particleFilterVO, tempParticle2, DISTANCE_SCALE);
					if (minDistance > min) {
						minDistance = min;
						tempParticle = particleGridVO;
					}
				}
				particleFilterVO.setmMatrixRSSIVoList(tempParticle.getmMatrixRSSIVoList());
			}

			for (Particle particleFilterVO : particleFilterList) {
				double totalNormalDistribution = 1;
				for (MatrixRSSI matrixRssiVO : particleFilterVO.getmMatrixRSSIVoList()) {
					for (Beacon preloadBeaconVO : preloadBeaconList) {
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
			for (Particle particleFilterVO : particleFilterList) {
				particleFilterVO.setParticle_weight(particleFilterVO.getParticle_weight() / totalWeight * particleFilterList.size());
			}

			this.statisticsHelper = new ParticleFilterStatisticsHelper(particleFilterList);
			int lowParticles = this.statisticsHelper.getLowStandardCollection().size();
			totalWeight = this.statisticsHelper.getTotal();

			// new particle removal
			Collections.sort(particleFilterList, new ComparePaticlesByWeight());
			ArrayList<Particle> particleListAfterDeletion = new ArrayList<Particle>();

			for (int i = 0; i < particleFilterList.size() - lowParticles; i++) {
				particleListAfterDeletion.add(particleFilterList.get(i));
			}

			particleFilterList = new ArrayList<Particle>();
			particleFilterList = particleListAfterDeletion;

			for (int i = 0; i < lowParticles; i++) {
				double initialWeight = particleFilterList.get(i).getParticle_weight();
				particleFilterList.get(i).setParticle_weight(initialWeight / 2);
				Particle oldParticle = particleFilterList.get(i);
				particleFilterList.add(this.getParticleCopy(oldParticle));
			}

			// maintain particle once more

			this.statisticsHelper = new ParticleFilterStatisticsHelper(particleFilterList);
			totalWeight = this.statisticsHelper.getTotal();
			// end region get total weight

			// set new weight maintain total particle weight = particles size
			for (Particle particleFilterVO : particleFilterList) {
				particleFilterVO.setParticle_weight(particleFilterVO.getParticle_weight() / totalWeight * particleFilterList.size());
			}

			// get max and min particle weight;

			double maxWeight = -9999;
			double minWeight = 9999;

			for (Particle particleFilterVO : particleFilterList) {
				maxWeight = Math.max(maxWeight, particleFilterVO.getParticle_weight());
				minWeight = Math.min(minWeight, particleFilterVO.getParticle_weight());
			}

			this.statisticsHelper = new ParticleFilterStatisticsHelper(particleFilterList);
			totalWeight = this.statisticsHelper.getTotal();
			for (Particle particleFilterVO : particleFilterList) {
				particleFilterVO.setRgbColor((int) ((particleFilterVO.getParticle_weight() - minWeight) / (maxWeight - minWeight) * 255));
				particleFilterVO.setParticle_view_weight((double) particleFilterVO.getParticle_weight() / (double) totalWeight * 700);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// end of new particle removal
		return particleFilterList;
	}

	/**
	 * Change Radius on Step Detected
	 * 
	 * @author farissyariati
	 * @param particleFilterList
	 * @param preloadBeaconList
	 * @param distanceVariance
	 * @param azimuthVeriance
	 * @param rssiVariance
	 * @return
	 */
	public List<Particle> changeRadiusOnStep(List<Particle> particleFilterList, List<Beacon> preloadBeaconList, double distanceVariance,
			double azimuthVeriance, double rssiVariance, int stageId) {
		this.setRssiVarience(rssiVariance);

		for (Particle particleFilterVO : particleFilterList) {
			Particle tempParticle = new Particle();
			double minDistance = 9999;
			for (Particle particleGridVO : this.particleGrids.get(stageId)) {
				// save to temp value to avoid shallow copy and goes infinity
				Particle tempParticle2 = new Particle();
				tempParticle2.setParticle_x(particleGridVO.getParticle_x() * PointUtils.SCALE_X);
				tempParticle2.setParticle_y(particleGridVO.getParticle_y() * PointUtils.SCALE_Y);
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

		for (Particle particleFilterVO : particleFilterList) {
			double totalNormalDistribution = 1;
			for (MatrixRSSI matrixRssiVO : particleFilterVO.getmMatrixRSSIVoList()) {
				for (Beacon preloadBeaconVO : preloadBeaconList) {
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
		for (Particle particleFilterVO : particleFilterList) {
			particleFilterVO.setParticle_weight(particleFilterVO.getParticle_weight() / totalWeight * particleFilterList.size());
		}

		double maxWeight = -9999;
		double minWeight = 9999;

		for (Particle particleFilterVO : particleFilterList) {
			maxWeight = Math.max(maxWeight, particleFilterVO.getParticle_weight());
			minWeight = Math.min(minWeight, particleFilterVO.getParticle_weight());
		}

		this.statisticsHelper = new ParticleFilterStatisticsHelper(particleFilterList);
		totalWeight = this.statisticsHelper.getTotal();
		for (Particle particleFilterVO : particleFilterList) {
			particleFilterVO.setRgbColor((int) ((particleFilterVO.getParticle_weight() - minWeight) / (maxWeight - minWeight) * 255));
			particleFilterVO.setParticle_view_weight((double) particleFilterVO.getParticle_weight() / (double) totalWeight * 700);
		}

		// end of new particle removal
		return particleFilterList;
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
	public void loadSavedFingerprintData(int totalActiveBeacon, List<Beacon> activeBeacons, double alpha, double beta, double gamma, int stageId) {
		this.setAlpha(alpha);
		this.setBeta(beta);
		this.setGamma(gamma);

		// this.setSavedFingerprints(this.fingerPrintService.getAll(totalActiveBeacon));
		this.setSavedFingerprints(this.fingerPrintService.getAllByStageId(stageId));
		int savedFingerprintSize = this.getSavedFingerprints().size() / totalActiveBeacon;
		if (!this.getSavedFingerprints().isEmpty() && savedFingerprintSize > 1) {
			this.setMatrix(savedFingerprints, activeBeacons, savedFingerprintSize);
			this.setFingerprintNotEmpty(true);
		}
	}

	/**
	 * Load All Stage Saved Fingerprint for Kriging Purpose
	 * 
	 * @author farissyariati
	 * @param alpha
	 * @param beta
	 * @param gamma
	 */
	public void loadAllStagesSavedFingerpringData(double alpha, double beta, double gamma) {
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;

		List<Stage> stages = this.stageService.getAll();
		for (Stage currentStage : stages) {
			List<Beacon> activeBeaconInStage = this.beaconService.getAll(currentStage.getId());
			List<Fingerprint> fingerprintsInStage = this.fingerPrintService.getAllByStageId(currentStage.getId());
			int savedFingerprintSize = fingerprintsInStage.size() / activeBeaconInStage.size();
			if (!fingerprintsInStage.isEmpty() && fingerprintsInStage.size() > 1) {
				this.setAllStageInverseMatrices(fingerprintsInStage, activeBeaconInStage, savedFingerprintSize, currentStage.getId());
				this.isFingerprintNotEmpty = true;
			}
		}

	}

	// End Of Public Internal Method

	// Private Internal Method
	/**
	 * Set The Matrix
	 * 
	 * @param savedFingerprints
	 * @param activedBeacons
	 * @param savedFingerprintsSize
	 */
	public void setMatrix(List<Fingerprint> savedFingerprints, List<Beacon> activedBeacons, int savedFingerprintsSize) {
		int savedFingerprintsSizePlus = savedFingerprintsSize + 1;

		for (Fingerprint beaconVO : savedFingerprints)
			System.out.println(beaconVO.getMacAddress() + " , " + beaconVO.getRssi());

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
					if (savedFingerprints.get(z).getPointX() == savedFingerprints.get(j).getPointX()
							&& savedFingerprints.get(z).getPointY() == savedFingerprints.get(j).getPointY()) {
						arrSaveBeacon[countJ][countZ] = Math.pow(gamma, 2);
					} else {
						// kriging calculation
						// each corelation
						arrSaveBeacon[countJ][countZ] = Math.pow(gamma, 2)
								* Math.exp(-1 * alpha * Math.pow(getDistance(savedFingerprints.get(z), savedFingerprints.get(j)), beta));
					}
					System.out.println(countJ + ", " + countZ + " : " + arrSaveBeacon[countJ][countZ]);
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
	 * Set All Stage Inverse Matrices
	 * 
	 * @author farissyariati
	 * @param savedFingerprintsInStage
	 * @param activedBeaconsInStage
	 * @param savedFingerprintsSize
	 * @param stageId
	 */
	public void setAllStageInverseMatrices(List<Fingerprint> savedFingerprintsInStage, List<Beacon> activedBeaconsInStage, int savedFingerprintsSize,
			int stageId) {
		int savedFingerprintsSizePlus = savedFingerprintsSize + 1;
		double[][] arrSaveBeacon = new double[savedFingerprintsSizePlus][savedFingerprintsSizePlus];
		String thisMac = activedBeaconsInStage.get(0).getMacAddress();
		int countJ = 0;
		for (int j = 0; j < savedFingerprintsInStage.size(); j++) {
			// column
			int countZ = 0;
			for (int z = 0; z < savedFingerprintsInStage.size(); z++) {
				if (thisMac.equals(savedFingerprintsInStage.get(j).getMacAddress())
						&& thisMac.equals(savedFingerprintsInStage.get(z).getMacAddress())) {
					// if the current mac is the same it will set to gamma^2
					// (diagonal will be the same)
					if (savedFingerprintsInStage.get(z).getPointX() == savedFingerprintsInStage.get(j).getPointX()
							&& savedFingerprintsInStage.get(z).getPointY() == savedFingerprintsInStage.get(j).getPointY()) {
						arrSaveBeacon[countJ][countZ] = Math.pow(gamma, 2);
					} else {
						// kriging calculation
						// each corelation
						arrSaveBeacon[countJ][countZ] = Math.pow(gamma, 2)
								* Math.exp(-1 * alpha * Math.pow(getDistance(savedFingerprintsInStage.get(z), savedFingerprintsInStage.get(j)), beta));
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
		this.inverseMatrices.put(stageId, matrixNormal.inverse());
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
	 * create particle each point of Kriging calculation This Method is Ported
	 * From PDRPractice Project
	 */
	private void initializeGridParticleVO() {
		this.setParticleGrid(new ArrayList<Particle>());
		for (int y = 0; y < 31; y++) {
			for (int x = 0; x < 17; x++) {
				this.getParticleGrid().add(new Particle(x, y, 1, 0, 10));
			}
		}
	}

	/**
	 * Initialize All Grid For Stage Kriging
	 * 
	 * @author farissyariati
	 */
	private void initializeAllStageGridParticleVO() {
		List<Stage> stages = this.stageService.getAll();
		for (Stage stage : stages) {
			this.particleGrids.put(stage.getId(), new ArrayList<Particle>());
			for (int y = 0; y < 31; y++) {
				for (int x = 0; x < 17; x++) {
					this.particleGrids.get(stage.getId()).add(new Particle(x, y, 1, 0, 10));
				}
			}
		}
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

	// End Of Private Internal Method

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
