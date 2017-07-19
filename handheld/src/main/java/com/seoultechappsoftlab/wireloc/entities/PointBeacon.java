package com.seoultechappsoftlab.wireloc.entities;

import java.util.ArrayList;

public class PointBeacon {

	private int _id;
	private int x;
	private int y;
	private double normalDistribution;
	private int rgbColor = 0;
	private String beaconMAC;
	private String beaconUUID;
	private int beaconno;
	private double rssiValue = -77;

	private boolean mostProbably;

	/**
	 * Boolean - Is Most Probably Location
	 * 
	 * @return
	 */
	public boolean isMostProbably() {
		return this.mostProbably;
	}
	
	/**
	 * Set whether the point is most probably location
	 * @param mostProbably
	 */
	public void setMostProbably(boolean mostProbably) {
		this.mostProbably = mostProbably;
	}

	private ArrayList<MatrixRSSI> MatrixRssiList;

	public PointBeacon() {
	}

	public PointBeacon(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public PointBeacon(int x, int y, double normalDistribution, int rgbColor, String beaconMAC, String beaconUUID) {
		this.x = x;
		this.y = y;
		this.normalDistribution = normalDistribution;
		this.rgbColor = rgbColor;
		this.beaconMAC = beaconMAC;
		this.beaconUUID = beaconUUID;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getNormalDistribution() {
		return normalDistribution;
	}

	public void setNormalDistribution(double normalDistribution) {
		this.normalDistribution = normalDistribution;
	}

	public int getRgbColor() {
		return rgbColor;
	}

	public void setRgbColor(int rgbColor) {
		this.rgbColor = rgbColor;
	}

	public String getBeaconMAC() {
		return beaconMAC;
	}

	public void setBeaconMAC(String beaconMAC) {
		this.beaconMAC = beaconMAC;
	}

	public String getBeaconUUID() {
		return beaconUUID;
	}

	public void setBeaconUUID(String beaconUUID) {
		this.beaconUUID = beaconUUID;
	}

	public double getRssiValue() {
		return rssiValue;
	}

	public void setRssiValue(double rssiValue) {
		this.rssiValue = rssiValue;
	}

	public int getBeaconno() {
		return beaconno;
	}

	public void setBeaconno(int beaconno) {
		this.beaconno = beaconno;
	}

	public ArrayList<MatrixRSSI> getMatrixRssiList() {
		return MatrixRssiList;
	}

	public void setMatrixRssiList(ArrayList<MatrixRSSI> matrixRssiList) {
		MatrixRssiList = matrixRssiList;
	}
}
