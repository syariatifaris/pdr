package com.seoultechappsoftlab.wireloc.infrastructures;

/**
 * Base Class Staticstics
 * @author farissyariati
 *
 * @param <C>
 */
public abstract class StatisticsBase<C> {
	
	private C datas;
	
	/**
	 * Constructor
	 * @param dataCollection
	 */
	public StatisticsBase(C dataCollection) {
		this.datas = dataCollection;
	}
	
	/**
	 * Get collection's mean
	 * @return
	 */
	public abstract double getMean();
	
	/**
	 * Get Collections Variance
	 * @return
	 */
	public abstract double getVariance();
	
	/**
	 * Get Total
	 * @return
	 */
	public abstract double getTotal();
	
	/**
	 * Get Data which are too low
	 * @return
	 */
	public abstract C getLowStandardCollection();
	
	/**
	 * Get data which are too high
	 * @return
	 */
	public abstract C getHighStandardCollection();
	
	/**
	 * Get Standard Deviation
	 * @return
	 */
	protected double getStandarDeviation(){
		return Math.sqrt(getVariance());
	}
	
	/**
	 * Get Data Collection
	 * @return
	 */
	protected C getDataCollection(){
		return this.datas;
	}
	
	/**
	 * Set Data Collections
	 * @param collections
	 */
	protected void setDataCollection(C collections){
		this.datas = collections;
	}
}
