package com.seoultechappsoftlab.wireloc.helpers;

import java.util.ArrayList;
import java.util.List;
import com.seoultechappsoftlab.wireloc.infrastructures.StatisticsBase;
import com.wisewells.iamzone.blelibrary.Beacon;

/**
 * Statistics Calculation For Wisewell's Beacon
 * @author SeoulTech Application Software Lab
 *
 */
public class BeaconStatisticsHelper extends StatisticsBase<List<Beacon>> {
	
	public BeaconStatisticsHelper(List<Beacon> nearbyBeacons) {
		super(nearbyBeacons);
	}
	
	@Override
	public double getMean() {
		return this.getTotal() / this.getDataCollection().size();
	}

	@Override
	public double getVariance() {
		double mean = this.getMean();
		double temp = 0;
		for(Beacon b : this.getDataCollection()){
			temp += Math.pow((mean - b.getRssi()), 2);
		}
		return temp / this.getDataCollection().size();
	}
	
	/*
	 * Get Standard Deviation
	 */
	public double getStandardDeviation(){
		return this.getStandarDeviation();
	}
	
	@Override
	public double getTotal() {
		double sum = 0.0;
		for(Beacon b : this.getDataCollection()){
			sum += b.getRssi();
		}
		return sum;
	}

	@Override
	public List<Beacon> getLowStandardCollection() {
		List<Beacon> lowWeightBeacons = new ArrayList<Beacon>();
		double lowPass = this.getMean() - this.getStandarDeviation();
		for(Beacon beacon : this.getDataCollection()){
			if(beacon.getRssi() < lowPass){
				lowWeightBeacons.add(beacon);
			}
		}
		return lowWeightBeacons;
	}

	@Override
	public List<Beacon> getHighStandardCollection() {
		List<Beacon> highWeightBeacons = new ArrayList<Beacon>();
		double highPass = this.getMean() + this.getStandarDeviation();
		for(Beacon beacon : this.getDataCollection()){
			if(beacon.getRssi() > highPass){
				highWeightBeacons.add(beacon);
			}
		}
		return highWeightBeacons;
	}

}
