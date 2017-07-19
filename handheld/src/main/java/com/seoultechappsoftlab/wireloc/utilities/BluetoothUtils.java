package com.seoultechappsoftlab.wireloc.utilities;

import com.wisewells.iamzone.blelibrary.BeaconReceiver;
import com.wisewells.iamzone.blelibrary.BeaconTracker;

import android.content.Context;

/**
 * Bluetooth Utility
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class BluetoothUtils {
	private Context context;
	private BeaconTracker tracker;
	private BeaconReceiver receiver;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public BluetoothUtils(Context context) {
		this.setContext(context);
		this.setTracker();
		this.setReceiver();
	}
	
	//Region Getters and Setters
	public BeaconTracker getTracker() {
		return tracker;
	}

	public void setTracker() {
		this.tracker = BeaconTracker.getInstance();
	}

	public BeaconReceiver getReceiver() {
		return receiver;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setReceiver() {
		this.receiver = new BeaconReceiver(this.getContext(), this.getTracker());
	}
	//End Region Getters and Setters
	
	/**
	 * Check whether the bluetooth is on or off
	 * @return
	 */
	public boolean isBluetoothEnabled() {
		return this.getReceiver().isBluetoothOn();
	}

	public void destroyReceiver(){
		this.receiver.close();
	}
}
