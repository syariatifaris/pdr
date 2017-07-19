package com.seoultechappsoftlab.wireloc.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.seoultechappsoftlab.wireloc.entities.Particle;
import com.seoultechappsoftlab.wireloc.infrastructures.StatisticsBase;

/**
 * Statistics Calculation Helper for Particle Filter
 * @author SeoulTech Application Software Lab
 *
 */
public class ParticleFilterStatisticsHelper extends StatisticsBase<List<Particle>> {
	
	/**
	 * Constructor
	 * @param dataCollection
	 */
	public ParticleFilterStatisticsHelper(List<Particle> dataCollection) {
		super(dataCollection);
	}

	/**
	 * Override Get Mean
	 */
	@Override
	public double getMean() {
		return this.getTotal() / this.getDataCollection().size();
	}

	/**
	 * Override get variance
	 */
	@Override
	public double getVariance() {
		double mean = this.getMean();
		double temp = 0;
		for(Particle a : this.getDataCollection()){
			temp += Math.pow((mean - a.getParticle_weight()), 2);
		}
		return temp / this.getDataCollection().size();	
	}
	
	/*
	 * Get Standard Deviation
	 */
	public double getStandardDeviation(){
		return this.getStandarDeviation();
	}
	
	/**
	 * Get Total Particle's Weight and Normal Distribution
	 * @return
	 */
	public double[] getTotalWeightAndNormalDistribution(){
		double[] values = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		
		for(Particle a : this.getDataCollection()){
			values[0] += a.getParticle_weight();
			values[1] += a.getNormalDistribution();
		}
		
		values[2] = values[1] / this.getDataCollection().size();
		Collections.sort(this.getDataCollection(), new CompareParticleByNormalDistribution());
		values[3] = this.getDataCollection() != null ? this.getDataCollection().get(0).getNormalDistribution() : 0.0;
		values[4] = this.getDataCollection() != null ? this.getDataCollection().get(this.getDataCollection().size()-1).getNormalDistribution() : 0.0;
		return values;
	}
	
	@Override
	public double getTotal() {
		double sum = 0.0;
		for(Particle a : this.getDataCollection()){
			sum += a.getParticle_weight();
		}
		return sum;
	}
	
	/**
	 * Get particle which its weight is too low
	 */
	@Override
	public List<Particle> getLowStandardCollection() {
		List<Particle> lowWeightParticles = new ArrayList<Particle>();
		double lowPass = this.getMean() - this.getStandarDeviation();
		for(Particle particle : this.getDataCollection()){
			if(particle.getParticle_weight() < lowPass){
				lowWeightParticles.add(particle);
			}
		}
		return lowWeightParticles;
	}
	
	/**
	 * Get Total Low Weighted Particle for Deletion Purpose
	 * @param sortedParticlesbyWeight
	 * @param minimumMultiplier
	 * @return
	 */
	public int getTotalLowStandardCollection(List<Particle> sortedParticlesbyWeight, double minimumMultiplier){
		if(sortedParticlesbyWeight == null || sortedParticlesbyWeight.size() == 0){
			return 0;
		}
		
		int lowParticle = 0;
		Particle maxParticleFilterWeight = sortedParticlesbyWeight.get(0);
		double minimumParticleWeightValue = maxParticleFilterWeight.getParticle_weight() * minimumMultiplier;
		
		for(Particle particle : sortedParticlesbyWeight){
			if(particle.getParticle_weight() < minimumParticleWeightValue){
				lowParticle++;
			}
		}
		
		return lowParticle;
	}
	
	/**
	 * Get particle which its weight is too high
	 */
	@Override
	public List<Particle> getHighStandardCollection() {
		List<Particle> highWeightParticles = new ArrayList<Particle>();
		double highPass = this.getMean() + this.getStandarDeviation();
		for(Particle particle : this.getDataCollection()){
			if(particle.getParticle_weight() > highPass){
				highWeightParticles.add(particle);
			}
		}
		return highWeightParticles;
	}


	/**
	 * Return average location point of particles
	 * @return particleFilterVO
	 */
	public Particle getAverageLocationParticle() {
		Particle averageLocationParticle = new Particle();
		double weightedAxisX = 0;
		double weightedAxisY = 0;
		//set to constant
		double weightedRadius = 10;
		for(Particle particle : this.getDataCollection()){
			weightedAxisX += particle.getParticle_x() * particle.getParticle_weight();
			weightedAxisY += particle.getParticle_y() * particle.getParticle_weight();
			//weightedRadius += particle.getParticle_view_weight() * particle.getParticle_weight();
		}
		averageLocationParticle.setParticle_x(weightedAxisX/this.getTotal());
		averageLocationParticle.setParticle_y(weightedAxisY/this.getTotal());
		//averageLocationParticle.setParticle_view_weight(weightedRadius/this.getTotal());
		averageLocationParticle.setParticle_view_weight(weightedRadius);
		
		return averageLocationParticle;
	}
	
	/**
	 * Internal Class Compare Particle By Normal Distribution
	 * @author farissyariati
	 *
	 */
	public class CompareParticleByNormalDistribution implements Comparator<Particle>{
		@Override
		public int compare(Particle a, Particle b) {
			if (a.getNormalDistribution() > b.getNormalDistribution()) {
				return -1;
			} else if (a.getNormalDistribution() == b.getNormalDistribution()) {
				return 0;
			} else {
				return 1;
			}
		}
	}
}
