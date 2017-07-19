package com.wisewells.iamzone.blelibrary;


/**
 * Beacon Tracking을 할때 비콘을 구분하기 위한
 * Key 클래스.
 * @author 민국
 */
public class BeaconKey {

	private Region mRegion;
	private String mProductName;
	private String mMacAddress;
	private boolean mEncryption;
	
	public BeaconKey(Beacon beacon) {
		mRegion = beacon.getRegion();
		mProductName = beacon.getProductName();
		mMacAddress = beacon.getMacAddress();
		mEncryption = beacon.isEncryption();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		
		if( !(o instanceof BeaconKey) ) {
			return false;
		}
		
		BeaconKey comp = (BeaconKey) o;
		
		if(mEncryption) {
			return mMacAddress.equals(comp.getMacAddress());
		} else {
			return mMacAddress.equals(comp.getMacAddress());
		}
	}
	
	@Override
	public int hashCode() {
		if(mEncryption) {
			return mMacAddress.hashCode();
		} else {
			return mMacAddress.hashCode();
		}
	}

	public Region getRegion() {
		return mRegion;
	}

	public void setRegion(Region mRegion) {
		this.mRegion = mRegion;
	}

	public String getProductName() {
		return mProductName;
	}

	public void setProductName(String mProductName) {
		this.mProductName = mProductName;
	}

	public String getMacAddress() {
		return mMacAddress;
	}

	public void setMacAddress(String mMacAddress) {
		this.mMacAddress = mMacAddress;
	}

	public boolean ismEncryption() {
		return mEncryption;
	}

	public void setEncryption(boolean mEncryption) {
		this.mEncryption = mEncryption;
	}
}
