package com.seoultechappsoftlab.wireloc.activity;

import com.seoultechappsoftlab.wireloc.activity.canvases.BeaconRadarCanvas;
import com.seoultechappsoftlab.wireloc.activity.dialogs.FingerprintDialog;
import com.seoultechappsoftlab.wireloc.activity.dialogs.FingerprintDialog.ovdListener;
import com.seoultechappsoftlab.wireloc.controllers.BeaconRadarController;
import com.seoultechappsoftlab.wireloc.utilities.BluetoothUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Beacon Radar Activity
 * @author SeoulTech Application Software Lab
 *
 */
@SuppressLint("ClickableViewAccessibility")
public class BeaconRadarActivity extends Activity implements OnTouchListener{
	
	//Region Private Variable
	private BeaconRadarCanvas canvas;
	private TextView rssiInformationTextView;
	private BeaconRadarController controller;
	
	private static final int DELAY = 500;
	//End Region Private Variable
	
	/**
	 * On Create Event
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.controller = new BeaconRadarController(this);
		this.canvas = new BeaconRadarCanvas(this);
		setContentView(R.layout.activity_beacon_radar);
		
		this.rssiInformationTextView = (TextView)findViewById(R.id.point_text);
		this.canvas = (BeaconRadarCanvas)findViewById(R.id.pointView);
		this.canvas.setOnTouchListener(this);
		//Initialize Grid Point on Canvas
		this.canvas.setPoint();
		
		//Initialize Bluetooth Low Energy
		this.initBluetoothLowEnergy();
		
		//Load Fingerprint For the First Time
		this.controller.actionLoadSavedFingerprints();
	}
	
	/**
	 * On Touch Event
	 * @param v
	 * @param event
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.pointView) {
			int pointX = (int) (event.getX() / com.seoultechappsoftlab.wireloc.utilities.PointUtils.SCALE_X);
			int pointY = (int) (event.getY() / com.seoultechappsoftlab.wireloc.utilities.PointUtils.SCALE_Y);
			ovdListener ovdlListener = new ovdListener() {
				@Override
				public void ovdResult() {
					controller.actionLoadSavedFingerprints();
				}
			};
			FingerprintDialog psd = new FingerprintDialog(this, this.controller.getActiveBeacons(), true, pointX, pointY, this.controller.getCurrentStageId(), ovdlListener);
			psd.setTitle(this.getString(R.string.dialog_fingerprint_title));
			psd.show();
		}
		return false;
	}
	
	/**
	 * On Resume Event
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.controller.actionActivateBluetoothReceiverIfNotActive();
		updateHandler.post(updateNearbyBeacons);
	}
	
	/**
	 * On Pause Event
	 */
	@Override
	protected void onPause() {
		super.onPause();
		this.controller.actionActivateBluetoothReceiverIfNotActive();
		updateHandler.removeCallbacks(updateNearbyBeacons);
	}
	
	//Region Thread and Handler
	
	final Handler updateHandler = new Handler();	
	private Runnable updateNearbyBeacons = new Runnable() {
		@Override
		public void run() {
			controller.actionListenNearbyBeacons();
			controller.actionSetParticles(canvas.getPointList());
			controller.actionUpdateBeaconRadar();
			rssiInformationTextView.setText(controller.getLatestRSSIInformation());
			canvas.setPointList(controller.getLatestParticles());
			updateHandler.postDelayed(this, DELAY);
		}
	};
	
	//End Region Thread and Handler
	
	//Region Private Variable
	
	/**
	 * Initialize Bluetooth
	 */
	private void initBluetoothLowEnergy() {
		BluetoothUtils bluetoothUtils = new BluetoothUtils(this);
		if (!bluetoothUtils.getReceiver().isSupprotBLE()) {
			Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}
		
		if (!bluetoothUtils.getReceiver().isBluetoothOn()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1000);
		}
	}
	
	//End Region Private Variable
}
