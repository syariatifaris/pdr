package com.seoultechappsoftlab.wireloc.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Environment;

import com.seoultechappsoftlab.wireloc.activity.R;
import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.entities.Particle;
import com.seoultechappsoftlab.wireloc.entities.PointBeacon;
import com.seoultechappsoftlab.wireloc.utilities.CustomCharactersUtils;
import com.seoultechappsoftlab.wireloc.utilities.PointUtils;
import com.seoultechappsoftlab.wireloc.utilities.StepDetectorUtils;

/**
 * Particle Filter Helper
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class ParticleFilterHelper {
	/**
	 * Get Distance Between 2 Fingerprint Point
	 * 
	 * @param vo1
	 * @param vo2
	 * @param distanceScale
	 * @return
	 */
	public static double getFingerprintPointDistance(Fingerprint vo1, Fingerprint vo2, double distanceScale) {
		return Math.pow((Math.pow((vo1.getPointX() - vo2.getPointY()), 2) + Math.pow((vo1.getPointY() - vo2.getPointY()), 2)), 0.5) * distanceScale;
	}

	/**
	 * Get Distance Between Particle and Fingerprint Point
	 * 
	 * @param vo1
	 * @param vo2
	 * @param distanceScale
	 * @return
	 */
	public static double getParticleAndFingerprintDistance(Particle vo1, Fingerprint vo2, double distanceScale) {
		return Math.pow((Math.pow((vo1.getParticle_x() - vo2.getPointX()), 2) + Math.pow((vo1.getParticle_y() - vo2.getPointY()), 2)), 0.5)
				* distanceScale;
	}

	/**
	 * Get Distance Between Particles
	 * 
	 * @param vo1
	 * @param vo2
	 * @return
	 */
	public static double getDistanceBetweenParticles(Particle vo1, Particle vo2, double distanceScale) {
		return Math.pow((Math.pow((vo1.getParticle_x() - vo2.getParticle_x()), 2) + Math.pow((vo1.getParticle_y() - vo2.getParticle_y()), 2)), 0.5)
				* distanceScale;
	}

	public static double getDistanceBetweenParticlesForRadar(PointBeacon vo1, Particle vo2, double distanceScale) {
		return Math.pow(
				(Math.pow(((vo1.getX() * PointUtils.SCALE_X) - vo2.getParticle_x()), 2) + Math.pow(
						((vo1.getY() * PointUtils.SCALE_Y) - vo2.getParticle_y()), 2)), 0.5)
				* distanceScale;
	}

	/**
	 * Get Normal Distribution
	 * 
	 * @param standardRssiValue
	 * @param rssiValue
	 * @return
	 */
	public static double getNormalDistribution(double standardRssiValue, double rssiValue) {
		return Math.exp((Math.pow((standardRssiValue - rssiValue), 2) / (-2 * PointUtils.VARIANCE)));
	}

	/**
	 * Saved Particle
	 * 
	 * @param recordedStepParctiles
	 * @return
	 */
	public static boolean saveParticleRecordToSDCard(HashMap<Integer, List<Particle>> recordedStepParctiles, Context context, boolean showAllParticle) {
		boolean result = false;

		File pathRoot = Environment.getExternalStorageDirectory();
		File savedDirectory = new File(pathRoot.getAbsolutePath() + StepDetectorUtils.PARTICLE_RECORD_DIR);

		if (!savedDirectory.exists()) {
			savedDirectory.mkdir();
		}

		String fileName = System.currentTimeMillis() + StepDetectorUtils.PARTICLE_RECORD_FILE_NAME_AND_EXTENSION;
		File recordFile = new File(savedDirectory, fileName);

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(recordFile);
			PrintWriter printWriter = new PrintWriter(fileOutputStream);

			for (int i = 0; i < recordedStepParctiles.size(); i++) {
				printWriter.print((i + 1) + CustomCharactersUtils.DOT);
				printWriter.println();

				if (showAllParticle) {
					for (Particle currentParticle : recordedStepParctiles.get(i + 1)) {
						String position = context.getString(R.string.step_recording_coordinate_format);
						position = position.replace("[x]", Double.toString(currentParticle.getParticle_x()));
						position = position.replace("[y]", Double.toString(currentParticle.getParticle_y()));
						printWriter.print(position);
						// sb.append(position);

						String normalDistributionAndWeight = context.getString(R.string.step_recording_normal_distribution_and_weight);
						normalDistributionAndWeight = normalDistributionAndWeight.replace("[n]",
								Double.toString(currentParticle.getNormalDistribution()));
						normalDistributionAndWeight = normalDistributionAndWeight.replace("[w]",
								Double.toString(currentParticle.getParticle_weight()));
						// sb.append(CustomCharactersUtils.DASH +
						// normalDistributionAndWeight);
						printWriter.print(CustomCharactersUtils.DASH + normalDistributionAndWeight);
						// sb.append(CustomCharactersUtils.NEW_LINE);
						printWriter.println();
					}
				}
				List<Particle> currentParticles = recordedStepParctiles.get(i + 1);
				ParticleFilterStatisticsHelper helper = new ParticleFilterStatisticsHelper(currentParticles);
				double totalValues[] = helper.getTotalWeightAndNormalDistribution();

				Particle averageParticle = helper.getAverageLocationParticle();

				String position = context.getString(R.string.step_recording_coordinate_format);
				position = position.replace("[x]", Double.toString(averageParticle.getParticle_x()));
				position = position.replace("[y]", Double.toString(averageParticle.getParticle_y()));
				printWriter.print(position);

				printWriter.println();
				String totalWeighAndNormalDistribution = context.getString(R.string.step_recording_total_for_weight_and_normal_distribution);
				totalWeighAndNormalDistribution = totalWeighAndNormalDistribution.replace("[tw]", Double.toString(totalValues[0]));
				totalWeighAndNormalDistribution = totalWeighAndNormalDistribution.replace("[tn]", Double.toString(totalValues[1]));
				printWriter.print(totalWeighAndNormalDistribution);
				printWriter.println();
				printWriter.println();
			}
			printWriter.flush();
			printWriter.close();
			fileOutputStream.close();
			result = true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return result;
	}

	// Region Dumping Data Purpose

	/**
	 * Get new file name;
	 * 
	 * @return
	 */
	public static String getNewFileName() {
		return System.currentTimeMillis() + StepDetectorUtils.PARTICLE_RECORD_FILE_NAME_AND_EXTENSION;
	}

	/**
	 * Append Dump Information Text
	 * 
	 * @param context
	 * @param fileName
	 * @param particles
	 * @param stepNo
	 */
	public static void appendParticleDataToTextFile(Context context, String fileName, List<Particle> particles, int stepNo) {
		File pathRoot = Environment.getExternalStorageDirectory();
		File savedDirectory = new File(pathRoot.getAbsolutePath() + StepDetectorUtils.PARTICLE_RECORD_DIR);

		if (!savedDirectory.exists()) {
			savedDirectory.mkdir();
		}

		File recordFile = new File(savedDirectory, fileName);

		try {
			String previousContent = CustomCharactersUtils.EMPTY_STRING;
			boolean fileAlreadyExists = recordFile.exists();

			if (fileAlreadyExists) {
				previousContent = readFile(recordFile.getAbsolutePath());
			}

			FileOutputStream fileOutputStream = new FileOutputStream(recordFile);
			PrintWriter printWriter = new PrintWriter(fileOutputStream);

			if (fileAlreadyExists) {
				printWriter.print(previousContent);
			}

			printWriter.print(stepNo + CustomCharactersUtils.DOT);
			printWriter.println();

			ParticleFilterStatisticsHelper helper = new ParticleFilterStatisticsHelper(particles);
			Particle averageParticle = helper.getAverageLocationParticle();
			double totalValues[] = helper.getTotalWeightAndNormalDistribution();

			String position = context.getString(R.string.step_recording_coordinate_format);
			position = position.replace("[x]", Double.toString(averageParticle.getParticle_x()));
			position = position.replace("[y]", Double.toString(averageParticle.getParticle_y()));
			printWriter.print(position);

			printWriter.println();
			String totalWeighAndNormalDistribution = context.getString(R.string.step_recording_total_for_weight_and_normal_distribution);
			totalWeighAndNormalDistribution = totalWeighAndNormalDistribution.replace("[tw]", Double.toString(totalValues[0]));
			totalWeighAndNormalDistribution = totalWeighAndNormalDistribution.replace("[tn]", Double.toString(totalValues[1]));
			printWriter.print(totalWeighAndNormalDistribution);
			printWriter.println();
			printWriter.println();

			printWriter.flush();
			printWriter.close();
			fileOutputStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Read The File
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static String readFile(String file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			reader.close();
			return stringBuilder.toString();
		} catch (IOException eox) {
			return CustomCharactersUtils.EMPTY_STRING;
		}
	}

	// End Region Dumping Data Purpose
}
