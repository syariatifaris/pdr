package com.seoultechappsoftlab.wireloc.entities;

import java.util.List;

import com.seoultechappsoftlab.wireloc.infrastructures.EntityBase;

public class Particle extends EntityBase {

	// region private variable
	private double particle_x;
	private double particle_y;
	private double particle_weight;
	private double particle_log_weight;
	private double particle_view_weight;
	private double particle_matrix_rssi;
	
	private List<MatrixRSSI> mMatrixRSSIVoList;
	private double normalDistribution;
	
	private int rgbColor;
	
	// end region - private variable
	
	/**
	 * Is The Heighest Particle
	 */
	private boolean isHighest;
	
	/**
	 * Whether is heighest
	 * @return
	 */
	public boolean isHighest() {
		return this.isHighest;
	}
	
	/**
	 * Set Heighest
	 * @param isHighest
	 */
	public void setHighest(boolean isHighest) {
		this.isHighest = isHighest;
	}

	public Particle() {
	}

	public Particle(double particle_x, double particle_y, double particle_weight) {
		super();
		this.particle_x = particle_x;
		this.particle_y = particle_y;
		this.particle_weight = particle_weight;
	}

	public Particle(double particle_x, double particle_y, double particle_weight, double particle_log_weight, double particle_view_weight) {
		super();
		this.particle_x = particle_x;
		this.particle_y = particle_y;
		this.particle_weight = particle_weight;
		this.particle_log_weight = particle_log_weight;
		this.particle_view_weight = particle_view_weight;
	}

	public double getParticle_x() {
		return particle_x;
	}

	public void setParticle_x(double particle_x) {
		this.particle_x = particle_x;
	}

	public double getParticle_y() {
		return particle_y;
	}

	public void setParticle_y(double particle_y) {
		this.particle_y = particle_y;
	}

	public double getParticle_weight() {
		return particle_weight;
	}

	public void setParticle_weight(double particle_weight) {
		this.particle_weight = particle_weight;
	}

	public double getParticle_log_weight() {
		return particle_log_weight;
	}

	public void setParticle_log_weight(double particle_log_weight) {
		this.particle_log_weight = particle_log_weight;
	}

	public double getParticle_view_weight() {
		return particle_view_weight;
	}

	public void setParticle_view_weight(double particle_view_weight) {
		this.particle_view_weight = particle_view_weight;
	}

	public double getParticle_matrix_rssi() {
		return particle_matrix_rssi;
	}

	public void setParticle_matrix_rssi(double particle_matrix_rssi) {
		this.particle_matrix_rssi = particle_matrix_rssi;
	}
	
	/**
	 * Get Matrices RSSI Relative by Beacon
	 * @return
	 */
	public List<MatrixRSSI> getmMatrixRSSIVoList() {
		return this.mMatrixRSSIVoList;
	}
	
	/**
	 * Set Matrices RSSI Relative by Beacon
	 * @param mMatrixRSSIVoList
	 */
	public void setmMatrixRSSIVoList(List<MatrixRSSI> mMatrixRSSIVoList) {
		this.mMatrixRSSIVoList = mMatrixRSSIVoList;
	}
	
	/**
	 * Get particle's normal distribution
	 * @return
	 */
	public double getNormalDistribution() {
		return normalDistribution;
	}
	
	/**
	 * Set particle's normal distribution
	 * @param normalDistribution
	 */
	public void setNormalDistribution(double normalDistribution) {
		this.normalDistribution = normalDistribution;
	}
	
	/**
	 * Get RGB Color
	 * @return
	 */
	public int getRgbColor() {
		return rgbColor;
	}

	/**
	 * Set RGB Color
	 * @param rgbColor
	 */
	public void setRgbColor(int rgbColor) {
		this.rgbColor = rgbColor;
	}
	
}
