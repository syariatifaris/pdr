package com.wisewells.iamzone.blelibrary;

import android.os.Parcel;
import android.os.Parcelable;


public class Beacon implements Parcelable {

	// static values(stored data)
	private String mCode;
	private String mProductName;
	private String mName;
	private String mBeaconGroupCode;
	private String mMacAddress;
	private String mProximityUUID;
	private int mMajor;
	private int mMinor;
	private double mBattery;
	private double mTxPower;
	private double mMeasuredPower;
	private double mInterval;
	private String mImage;
	private String mUpdateDate;
	private String mUpdateTime;
	private String mRegisterDate;
	private String mRegisterTime;
	private boolean mIsEncryption;
	private int mEncryptionSequence;
	private double mTemperature;
	private String mHadwareVer;
	private String mFirmwareVer;

	// dynamic values(not sotred data)
	private double mRssi;
	private double mDistance;

	public static final Parcelable.Creator<Beacon> CREATOR = new Parcelable.Creator<Beacon>() {
		public Beacon createFromParcel(Parcel source) {
			return new Beacon(source);
		}
		public Beacon[] newArray(int size) {
			return new Beacon[size];
		}
	};

//	public Beacon(String proximityUUID, String macAddress, 
//			int major, int minor, double measuredPower, double rssi, double distance) {
//		mProximityUUID = BeaconUtils.normalizeProximityUUID(proximityUUID);
//		mMacAddress = macAddress;
//		mMajor = major;
//		mMinor = minor;
//		mMeasuredPower = measuredPower;
//		mRssi = rssi;
//		mDistance = distance;
//	}
	
	/**
	 * From BeaconTracker
	 */
	public Beacon(String productName, String macAddress, String uuid, int major, int minor, 
			double measuredPower, double rssi, double distance) {
		this(productName, macAddress, uuid, major, minor, measuredPower, rssi);
		mDistance = distance;
	}

	/**
	 * From Bluethooth signal 
	 */
	public Beacon(String productName, String macAddress, String uuid, int major, int minor, double measuredPower, double rssi) {
		mProductName = productName;
		mMacAddress = macAddress;
		mProximityUUID = uuid;
		mMajor = major;
		mMinor = minor;
		mMeasuredPower = measuredPower;
		mRssi = rssi;
	}
	
	/**
	 * From SQLite
	 */
	public Beacon(String code, String productName, String name,
			String groupCode, String macAddress, String uuid, int major,
			int minor, double battery, double txPower, double measuredPower,
			double interval, String image, String updateDate,
			String updateTime, String registerDate, String registerTime,
			boolean isEnc, int encSeq, double temperature, String hwVer,
			String fwVer) {

		mCode = code;
		mProductName = productName;
		mName = name;
		mBeaconGroupCode = groupCode;
		mMacAddress = macAddress;
		mProximityUUID = uuid;
		mMajor = major;
		mMinor = minor;
		mBattery = battery;
		mTxPower = txPower;
		mMeasuredPower = measuredPower;
		mInterval = interval;
		mImage = image;
		mUpdateDate = updateDate;
		mUpdateTime = updateTime;
		mRegisterDate = registerDate;
		mRegisterTime = registerTime;
		mIsEncryption = isEnc;
		mEncryptionSequence = encSeq;
		mTemperature = temperature;
		mHadwareVer = hwVer;
		mFirmwareVer = fwVer;
	}

	private Beacon(Parcel in) {
		mCode = in.readString();
		mProductName = in.readString();
		mName = in.readString();
		mBeaconGroupCode = in.readString();
		mMacAddress = in.readString();
		mProximityUUID = in.readString();	   
		mMajor = in.readInt();
		mMinor = in.readInt();
		mBattery = in.readDouble();
		mTxPower = in.readDouble();
		mMeasuredPower = in.readDouble();
		mInterval = in.readDouble();
		mImage = in.readString();
		mRssi = in.readDouble();
		mDistance = in.readDouble();
		mUpdateDate = in.readString();
		mUpdateTime = in.readString();
		mRegisterDate = in.readString();
		mRegisterTime = in.readString();
		mIsEncryption = (Boolean) in.readValue(Boolean.class.getClassLoader());
		mEncryptionSequence = in.readInt();
		mTemperature = in.readDouble();
		mHadwareVer = in.readString();
		mFirmwareVer = in.readString();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mCode);
		dest.writeString(mProductName);
		dest.writeString(mName);
		dest.writeString(mBeaconGroupCode);
		dest.writeString(mMacAddress);
		dest.writeString(mProximityUUID);		
		dest.writeInt(mMajor);
		dest.writeInt(mMinor);
		dest.writeDouble(mBattery);
		dest.writeDouble(mTxPower);
		dest.writeDouble(mMeasuredPower);
		dest.writeDouble(mInterval);
		dest.writeString(mImage);
		dest.writeDouble(mRssi);
		dest.writeDouble(mDistance);
		dest.writeString(mUpdateDate);
		dest.writeString(mUpdateTime);
		dest.writeString(mRegisterDate);
		dest.writeString(mRegisterTime);
		dest.writeValue(mIsEncryption);
		dest.writeInt(mEncryptionSequence);
		dest.writeDouble(mTemperature);
		dest.writeString(mHadwareVer);
		dest.writeString(mFirmwareVer);
	}

	 @Override
	 public String toString() {
		 if(mName != null && !mName.equals("")) return mName;
		 return mProductName + " > " + mMacAddress;
//		 return Objects.toStringHelper(this).add("macAddress", mMacAddress).add("proximityUUID", mProximityUUID).add("major", mMajor).add("minor", mMinor).add("measuredPower", mMeasuredPower).add("rssi", mRssi).toString();
	 }

	 public boolean equals(Object o) {
		 if (this == o) return true;
		 if ((o == null) || (getClass() != o.getClass())) return false;

		 Beacon beacon = (Beacon)o;

		 if (mMajor != beacon.mMajor) return false;
		 if (mMinor != beacon.mMinor) return false;
		 return mProximityUUID.equals(beacon.mProximityUUID);
	 }

	 public int hashCode() {
		 int result = mProximityUUID.hashCode();
		 result = 31 * result + mMajor;
		 result = 31 * result + mMinor;
		 return result;
	 }

	public String getCode() {
		return mCode;
	}

	public void setCode(String mCode) {
		this.mCode = mCode;
	}

	public String getProductName() {
		return mProductName;
	}

	public void setProductName(String mProductName) {
		this.mProductName = mProductName;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getBeaconGroupCode() {
		return mBeaconGroupCode;
	}

	public void setBeaconGroupCode(String mBeaconGroupCode) {
		this.mBeaconGroupCode = mBeaconGroupCode;
	}

	public String getMacAddress() {
		return mMacAddress;
	}

	public void setMacAddress(String mMacAddress) {
		this.mMacAddress = mMacAddress;
	}

	public String getProximityUUID() {
		return mProximityUUID;
	}

	public void setProximityUUID(String mProximityUUID) {
		this.mProximityUUID = mProximityUUID;
	}

	public int getMajor() {
		return mMajor;
	}

	public void setMajor(int mMajor) {
		this.mMajor = mMajor;
	}

	public int getMinor() {
		return mMinor;
	}

	public void setMinor(int mMinor) {
		this.mMinor = mMinor;
	}

	public double getBattery() {
		return mBattery;
	}

	public void setBattery(double mBattery) {
		this.mBattery = mBattery;
	}

	public double getTxPower() {
		return mTxPower;
	}

	public void setTxPower(double mTxPower) {
		this.mTxPower = mTxPower;
	}

	public double getMeasuredPower() {
		return mMeasuredPower;
	}

	public void setMeasuredPower(double mMeasuredPower) {
		this.mMeasuredPower = mMeasuredPower;
	}

	public double getInterval() {
		return mInterval;
	}

	public void setInterval(double mInterval) {
		this.mInterval = mInterval;
	}

	public String getImage() {
		return mImage;
	}

	public void setImage(String mImage) {
		this.mImage = mImage;
	}

	public String getUpdateDate() {
		return mUpdateDate;
	}

	public void setUpdateDate(String mUpdateDate) {
		this.mUpdateDate = mUpdateDate;
	}

	public String getUpdateTime() {
		return mUpdateTime;
	}

	public void setUpdateTime(String mUpdateTime) {
		this.mUpdateTime = mUpdateTime;
	}

	public String getRegisterDate() {
		return mRegisterDate;
	}

	public void setRegisterDate(String mRegisterDate) {
		this.mRegisterDate = mRegisterDate;
	}

	public String getRegisterTime() {
		return mRegisterTime;
	}

	public void setRegisterTime(String mRegisterTime) {
		this.mRegisterTime = mRegisterTime;
	}

	public boolean isEncryption() {
		return mIsEncryption;
	}

	public void setIsEncryption(boolean mIsEncryption) {
		this.mIsEncryption = mIsEncryption;
	}

	public int getEncryptionSequence() {
		return mEncryptionSequence;
	}

	public void setEncryptionSequence(int mEncryptionSequence) {
		this.mEncryptionSequence = mEncryptionSequence;
	}

	public double getTemperature() {
		return mTemperature;
	}

	public void setTemperature(double mTemperature) {
		this.mTemperature = mTemperature;
	}

	public String getHadwareVer() {
		return mHadwareVer;
	}

	public void setHadwareVer(String mHadwareVer) {
		this.mHadwareVer = mHadwareVer;
	}

	public String getFirmwareVer() {
		return mFirmwareVer;
	}

	public void setFirmwareVer(String mFirmwareVer) {
		this.mFirmwareVer = mFirmwareVer;
	}

	public double getRssi() {
		return mRssi;
	}

	public void setRssi(double mRssi) {
		this.mRssi = mRssi;
	}

	public double getDistance() {
		return mDistance;
	}

	public void setDistance(double mDistance) {
		this.mDistance = mDistance;
	}
	
	public Region getRegion() {
		return new Region(mProximityUUID, mMajor, mMinor);
	}
}
