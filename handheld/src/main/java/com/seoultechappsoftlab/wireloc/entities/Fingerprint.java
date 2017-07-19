package com.seoultechappsoftlab.wireloc.entities;
import com.seoultechappsoftlab.wireloc.infrastructures.EntityBase;

/**
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class Fingerprint extends EntityBase {
	//region private field
	private int beaconNo;
	private String macAddress;
	private int pointX;
	private int pointY;
	private double rssi;
	private int stageId;
	private boolean isActive;
	private boolean isSelected;
	//end region private filed

	/**
	 * Empty Constructor
	 */
	public Fingerprint(){
	}
	
	/**
	 * Default Constructor
	 * @param id
	 * @param beaconNo
	 * @param macAddress
	 * @param pointX
	 * @param pointY
	 * @param rssi
	 */
	public Fingerprint(int id, int beaconNo, String macAddress, int pointX, int pointY, double rssi){
		this.setId(id);
		this.setBeaconNo(beaconNo);
		this.macAddress = macAddress;
		this.pointX = pointX;
		this.pointY = pointY;
		this.rssi = rssi;		
	}
	
	/**
	 * Get The MAC Address
	 * @return
	 */
	public String getMacAddress() {
		return this.macAddress;
	}
	
	/**
	 * Set The MAC Address
	 * @param macAddress
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	/**
	 * Get Point X
	 * @return
	 */
	public int getPointX() {
		return this.pointX;
	}
	
	/**
	 * Set Point Y
	 * @param pointX
	 */
	public void setPointX(int pointX) {
		this.pointX = pointX;
	}
	
	/**
	 * Get Point Y
	 * @return
	 */
	public int getPointY() {
		return this.pointY;
	}
	
	/**
	 * Set Point Y
	 * @param pointY
	 */
	public void setPointY(int pointY) {
		this.pointY = pointY;
	}
	
	/**
	 * Get the RSSI
	 * @return
	 */
	public double getRssi() {
		return this.rssi;
	}
	
	/**
	 * Set The RSSI
	 * @param rssi
	 */
	public void setRssi(double rssi) {
		this.rssi = rssi;
	}
	
	/**
	 * Get Total Reference Beacon
	 * @return
	 */
	public int getBeaconNo() {
		return this.beaconNo;
	}

	/**
	 * Is Fingerprinting Record Active
	 * @return
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Set Total Reference Beacon
	 * @param beaconNo
	 */
	public void setBeaconNo(int beaconNo) {
		this.beaconNo = beaconNo;
	}
	
	/**
	 * Get Stage ID
	 * @return
	 */
	public int getStageId() {
		return stageId;
	}
	
	/**
	 * Set Stage ID
	 * @param stageId
	 */
	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	/**
	 * Set Fingerprinting Active or Not
	 * @param isActive
	 */
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Is Selected
	 * @return
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * Set Selected
	 * @param isSelected
	 */
	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
