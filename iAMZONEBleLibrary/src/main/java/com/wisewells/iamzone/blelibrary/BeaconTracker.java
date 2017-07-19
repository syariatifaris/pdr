package com.wisewells.iamzone.blelibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 비콘을 트래킹하는 클래스
 * 트래킹한 Data를 통해서 비콘의 신호, 거리등을 계산
 * @author 민국
 */
public class BeaconTracker {
	private ConcurrentHashMap<BeaconKey, BeaconTrace> beaconMap;
	private static BeaconTracker inst = null;

	private BeaconTracker() {
		beaconMap = new ConcurrentHashMap<BeaconKey, BeaconTracker.BeaconTrace>();
	}

	public static BeaconTracker getInstance() {
		if(inst == null) inst = new BeaconTracker();
		return inst;
	}

	/**
	 * 비콘의 현재 RSSI
	 * @param beacon
	 * @return 비콘의 현재 RSSI, 해당 비콘의 신호를 수신하지 못한다면 null
	 */
	public Double getRssi(Beacon beacon) {
		BeaconKey key = new BeaconKey(beacon);

		if(isNearby(beacon)) {
			return Double.valueOf(beaconMap.get(key).getRssi());
		} else {
			return null;
		}
	}

	/**
	 * 트래킹을 통한 비콘의 평균 RSSI
	 * @param beacon
	 * @return 트래킹을 통한 비콘의 평균 RSSI, 해당 비콘의 신호를 수신하지 못한다면 null
	 */
	public Double getAvgRssi(Beacon beacon) {
		BeaconKey key = new BeaconKey(beacon);

		if(isNearby(beacon)) {
			return Double.valueOf(beaconMap.get(key).getAvgRssi());
		} else {
			return null;
		}
	}

	/**
	 * 비콘과 디바이스간의 거리
	 * @param beacon
	 * @return 비콘과 디바이스간의 거리, 해당 비콘의 신호를 수신하지 못한다면 null
	 */
	public Double getAvgDist(Beacon beacon) {
		BeaconKey key = new BeaconKey(beacon);

		if(isNearby(beacon)) {
			return Double.valueOf(beaconMap.get(key).getAvgDist());
		} else {
			return null;
		}
	}

	/**
	 * 비콘이 주변에 있는지 확인
	 * @param beacon
	 * @return 있으면 true, 없으면 false
	 */
	public boolean isNearby(Beacon beacon) {
		BeaconKey key = new BeaconKey(beacon);
		BeaconTrace trace = beaconMap.get(key);
		if(trace == null) {
			beaconMap.remove(key);
			return false;
		}

		return trace.isNearby();
	}

	/**
	 * 현재 신호를 받고 있는 모든 비콘
	 * @return 주변의 모든 비콘들
	 */
	public ArrayList<Beacon> getAllNearbyBeacons() {
		ArrayList<Beacon> beacons = new ArrayList<Beacon>();
		for(Map.Entry<BeaconKey, BeaconTrace> entry : beaconMap.entrySet()) {
			BeaconKey key = entry.getKey();
			BeaconTrace trace = entry.getValue();
			Region region = key.getRegion();
			
			if(!trace.isNearby()) {
				beaconMap.remove(key);
				continue;
			}

			Beacon beacon = new Beacon(key.getProductName(), key.getMacAddress(), 
					region.getProximityUUID(), region.getMajor(), region.getMinor(), 
					trace.getMeasuredPower(), trace.getAvgRssi(), trace.getAvgDist());
			
			beacons.add(beacon);
		}

		return beacons;
	}
	
	/**
	 * 가장 가까운 비콘을 찾음
	 * @return 가장 가까운 비콘
	 */
	public Beacon getMostNearbyBeacon() {
		ArrayList<Beacon> beacons = getAllNearbyBeacons();
		
		Collections.sort(beacons, new Comparator<Beacon>() {

			@Override
			public int compare(Beacon lhs, Beacon rhs) {
				// TODO Auto-generated method stub
				if(lhs.getDistance() > rhs.getDistance()) {
					return 1;
				} else if(lhs.getDistance() < rhs.getDistance()) {
					return -1;
				}
				
				return 0;
			}
		});
		
		if(beacons.size() == 0) return null;

		return beacons.get(0);
	}

	/**
	 * Tracking을 위해 비콘을 업데이트
	 * @param beacon
	 */
	public void update(Beacon beacon) {
		BeaconKey key = new BeaconKey(beacon);
		if(beaconMap.containsKey(key)) {
			beaconMap.get(key).updateTrace(beacon.getRssi(), beacon.getMeasuredPower());
		} else {
			beaconMap.put(key, new BeaconTrace(beacon.getRssi(), beacon.getMeasuredPower()));			
		}
	}

	private static class BeaconTrace {
		private double rssi; //in dB
		private double measuredPower; //in dB
		private double avgRssi; //in dB
		private long lastUpdate; //in nanoseconds
		//Constant
		private static final double MV_AVG_COEF = 0.3D; //Update coefficient for moving avg. for 100 ms span
		private static final double MV_DEVI_COEF = 0.1D;
		private static final double GAIN = -8.0D; //Gain in dB for path loss calculation
		private static final double PL_COEF = 4.0D; //Path loss coefficient
		private static final long TIME_OUT_SECONDS = 6; //Time out for a nearby state in seconds

		//Constructor
		public BeaconTrace(double nRssi, double nTxPower) {
			lastUpdate = -1;
			updateTrace(nRssi, nTxPower);
		}

		private double deviation;	// 편차
		private ArrayList<Double> rssiForInitialDeviation = new ArrayList<Double>(); //초기 편차값 계산을 위한 rssi 배열
		private boolean filtering;	// 표준편차에 따라 필터링 여부

		//Update average RSSI
		public void updateTrace(double nRssi, double nMeasuredPower) {
			rssi = nRssi;
			measuredPower = nMeasuredPower;
			//Calculate the average RSSI
			long curTime = System.nanoTime();
			double timeDiff100Millis = (double)TimeUnit.NANOSECONDS.toMillis(curTime - lastUpdate)/100D; //in 100 ms
			if(lastUpdate == -1 || timeDiff100Millis < 0) {
				avgRssi = rssi;
			} else {
				/**
				 * alpha - rssi window에서 현재 rssi의 반영률
				 * MV_AVG_COEF가 클수록 반영률도 커짐.
				 * Math.pow는 비콘에 따라 rssi의 신호 수신 빈도가 다르므로 이를 어느정도 일정하게 반영하기 위한 과정
				 */
				double alpha = 1 - Math.pow(1 - MV_AVG_COEF, timeDiff100Millis);
				double beta = 1 - Math.pow(1 - MV_DEVI_COEF, timeDiff100Millis); 
				if(!filtering) {
					avgRssi = alpha*rssi + (1-alpha)*avgRssi;
				}
				else {
					double gap = rssi - avgRssi;
					double filteredRssi = rssi;
					if(Math.abs(gap) > deviation) {
						// 편차를 벗어난 RSSI는 경계값으로 설정.
						filteredRssi= avgRssi + (0.1 * (gap > 0 ? deviation : (-1 * deviation)));
						// L.e(String.format("Filtering!! --> gap:%f, prev:%f, now:%f, deviation:%f, avg:%f", gap, rssi, bufRssi, deviation, avgRssi));
					}
					avgRssi = alpha*filteredRssi + (1-alpha)*avgRssi;
					deviation = beta*Math.abs(rssi-avgRssi) + (1-beta)*deviation;
				}
			}

			lastUpdate = curTime;

			if(filtering == false) {
				prepareFiltering();
			}
			if(deviation == 0) {
				// 편차가 0으로 수렴하게 될경우 다시 deviation을 계산하기 위함.
				filtering = false;
			}
		}

		private void prepareFiltering() {
			rssiForInitialDeviation.add(rssi);
			if(rssiForInitialDeviation.size() == 10) {
				double sum = 0;
				for(Double d : rssiForInitialDeviation) {
					double gap = Math.pow(d - avgRssi, 2);
					sum += gap;
				}
				deviation = sum / 10.0;
				filtering = true;
				rssiForInitialDeviation.clear();
			}
		}

		//Getter
		public double getRssi() { return rssi; }
		public double getAvgRssi() { return avgRssi; }
		public double getAvgDist() {
			//Calculate the distance
			double diff = measuredPower - avgRssi + GAIN; //Path loss (in dB) calculation
			return Math.pow(10, diff/(10*PL_COEF));
		}
		public double getMeasuredPower() { return measuredPower; }

		public boolean isNearby() {
			long timeDiffSeconds = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - lastUpdate); //in seconds
			boolean isNearby = timeDiffSeconds < TIME_OUT_SECONDS ? true : false;
			return isNearby;
		}
	}
}
