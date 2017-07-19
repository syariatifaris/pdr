package com.wisewells.iamzone.blelibrary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;

import com.wisewells.iamzone.blelibrary.utils.BeaconUtils;
import com.wisewells.iamzone.blelibrary.utils.L;

/**
 * @file	BeaconReceiver.java
 * @author 	Mingook
 * @date	2014. 8. 23.
 * @description
 */
public class BeaconReceiver {
	
	private Context mContext;
	private BluetoothAdapter mAdapter;
	private BluetoothAdapter.LeScanCallback mLeScanCallback;
	private AlarmManager mAlarmManager;
	
	private HandlerThread mScanProcessThread;
	private Handler mHandler;
	private long mScanTimeMillis;
	private long mIdleTimeMillis;
	private BroadcastReceiver mBluetoothReceiver;
	private BroadcastReceiver mScanReceiver;
	private BroadcastReceiver mIdleReceiver;
	private PendingIntent mScanPendingIntent;
	private PendingIntent mIdlePendingIntent;
	private BeaconTracker mTracker;
	private boolean mActive; // True when the ble scan is activated.
	private boolean mBluetoothOn; // True when the bluetooth device is ON.
	private boolean mScanning; // True when it is in the scanning state.

	/**
	 * @param context
	 * @param tracker 
	 * 			신호를 받을때마다 tracker 객체에 비콘의 정보가 update 된다.
	 */
	public BeaconReceiver(Context context, BeaconTracker tracker) {
		mContext = context;
		
		BluetoothManager manager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
		mAdapter = manager.getAdapter();		
		if (mAdapter == null || !mAdapter.isEnabled()) { 
			mBluetoothOn = false;
		}
		else { 
			mBluetoothOn = true;
		}
		
		mLeScanCallback = new ScanCallback();
		
		mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		
		mScanProcessThread = new HandlerThread("Receiver");
		mScanProcessThread.start();
		mHandler = new Handler(mScanProcessThread.getLooper());
		
		mScanTimeMillis = 1000; // default value
		mIdleTimeMillis = 0; // default value
		
		mBluetoothReceiver = new BluetoothBroadcastReceiver();
		mScanReceiver = new ScanBroadcastReceiver();
		mIdleReceiver = new IdleBroadcastReceiver();
		
		mScanPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("beaconReceiver.SCAN"), 0);
		mIdlePendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("beaconReceiver.IDLE"), 0);
		
		mTracker = tracker;
		mActive = false;
		mScanning = false;

		mContext.registerReceiver(mBluetoothReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"), null, mHandler);
		mContext.registerReceiver(mScanReceiver, new IntentFilter("beaconReceiver.SCAN"), null, mHandler);
		mContext.registerReceiver(mIdleReceiver, new IntentFilter("beaconReceiver.IDLE"), null, mHandler);
	}

	
	/**
	 *	BeaconReceiver 객체를 생성한 Component에서 
	 *	생명주기에 맞춰서 destroy될때 반드시 호출해 준다. 
	 */
	public void close() {
		mContext.unregisterReceiver(this.mBluetoothReceiver);
		mContext.unregisterReceiver(this.mScanReceiver);
		mContext.unregisterReceiver(this.mIdleReceiver);
		mScanProcessThread.quit();
	}

	public boolean setScanIdleTimeMillis(long nScanTimeMillis, long nIdleTimeMillis) {
		if (nScanTimeMillis <= 0 || nIdleTimeMillis < 0)
			return false;
		
		mScanTimeMillis = nScanTimeMillis;
		mIdleTimeMillis = nIdleTimeMillis;
		
		return true;
	}
	
	public boolean isSupprotBLE() {
		return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
	}

	public boolean isActive() {
		return mActive;
	}
	
	public boolean isBluetoothOn() {
		return mBluetoothOn;
	}
	
	/**
	 * @return true -> Scan Time<br>
	 * 			false -> Idel Time
	 */
	public boolean isScanning() {
		return mScanning;
	}

	public void activate() {
		mActive = true;

		removeAlarm();
		startScan();
		
		L.w("Beacon Receiver Activate");
	}

	public void deactivate() {
		mActive = false;
		
		removeAlarm();
		startIdel();
		
		L.w("Beacon Receiver Deactivate");
	}

	private void startScan() {
		L.e("Scan");
		mHandler.post(new Runnable() {
			public void run() {
				if (mBluetoothOn == true && mActive == true) {
					mAdapter.startLeScan(mLeScanCallback);
					mScanning = true;
					if (mIdleTimeMillis > 0)
						setAlarm(mIdlePendingIntent, mScanTimeMillis);
				}
			}
		});
	}

	private void startIdel() {
		L.e("Idel");
		mHandler.post(new Runnable() {
			public void run() {
				mAdapter.stopLeScan(mLeScanCallback);
				mScanning = false;
				if (mBluetoothOn == true && mActive == true)
					setAlarm(mScanPendingIntent, mIdleTimeMillis);
			}
		});
	}

	private void setAlarm(PendingIntent pendingIntent, long delayMillis) {
		mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + delayMillis, pendingIntent);
	}

	private void removeAlarm() {
		mAlarmManager.cancel(mScanPendingIntent);
		mAlarmManager.cancel(mIdlePendingIntent);
	}
	
	private class ScanCallback implements BluetoothAdapter.LeScanCallback {
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			mHandler.post(new ScanProcessing(mTracker, device, rssi, scanRecord));
		}
	}

	public static class ScanProcessing implements Runnable {
		BeaconTracker tracker;
		BluetoothDevice device;
		int rssi;
		byte[] scanRecord;

		public ScanProcessing(BeaconTracker nTracker, BluetoothDevice nDevice, int nRssi, byte[] nScanRecord) {
			tracker = nTracker;
			device = nDevice;
			rssi = nRssi;
			scanRecord = nScanRecord;
		}

		public void run() {
			Beacon beacon = BeaconUtils.beaconFromLeScan(device, rssi, scanRecord);
			if(beacon == null) {
				Log.e("HW", rssi + "");
				return;
			}
			
			tracker.update(beacon);
		}
	}

	private class BluetoothBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction())) {
				int state = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1);
				if (state == 12) { // Bluetooth is ON
					mBluetoothOn = true;
					removeAlarm();
					startScan();
				} else if (state == 10) { // Bluetooth is OFF
					mBluetoothOn = false;
					removeAlarm();
					startIdel();
				}
			}
		}
	}

	private class ScanBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			BeaconReceiver.this.startScan();
		}
	}

	private class IdleBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			BeaconReceiver.this.startIdel();
		}
	}
}
