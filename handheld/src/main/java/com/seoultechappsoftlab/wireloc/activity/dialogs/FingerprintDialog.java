package com.seoultechappsoftlab.wireloc.activity.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.seoultechappsoftlab.wireloc.activity.R;
import com.seoultechappsoftlab.wireloc.controllers.ParticleFilterController;
import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.infrastructures.DialogBase;
import com.seoultechappsoftlab.wireloc.services.FingerprintService;
import com.seoultechappsoftlab.wireloc.utilities.CustomCharactersUtils;
import com.seoultechappsoftlab.wireloc.utilities.RSSIUtils;

public class FingerprintDialog extends DialogBase implements android.view.View.OnClickListener {

	// Region Declare View
	// View Injection

	/**
	 * @DeclareView(id = R.id.dialog_point_no, click = "this") Button btn_no;
	 * @DeclareView(id = R.id.dialog_point_ok, click = "this") Button btn_ok;
	 * @DeclareView(id = R.id.dialog_point_text) TextView pointSaveText;
	 **/

	// End Region Declare View

	// Region View Item

	Button btn_no;
	Button btn_ok;
	private Button setUnreachable;
	
	TextView pointSaveText;

	// End Region View Item

	// Region Global Properties

	boolean isPointActivity = false;
	int pointX;
	int pointY;
	int stageId;

	List<Fingerprint> arrPointSave;
	List<Beacon> mPointSave;

	private ovdListener ovdlistener;
	ParticleFilterController controller;
	FingerprintService service;

	// End Region Global Properties
	
	//Region Constructors
	
	/**
	 * Constructor
	 * @param context
	 * @param arrPointSave
	 */
	public FingerprintDialog(Context context, ArrayList<Fingerprint> arrPointSave) {
		super(context);
		this.arrPointSave = arrPointSave;
	}
	
	/**
	 * Constructor
	 * @param context
	 * @param setBeacon
	 * @param isPointActivity
	 * @param pointX
	 * @param pointY
	 */
	public FingerprintDialog(Context context, List<Beacon> setBeacon, boolean isPointActivity, int pointX, int pointY) {
		super(context);
		this.service = new FingerprintService(context);
		this.mPointSave = setBeacon;
		this.isPointActivity = isPointActivity;
		this.pointX = pointX;
		this.pointY = pointY;
	}
	
	/**
	 * Constructor
	 * @param context
	 * @param setBeacon
	 * @param isPointActivity
	 * @param pointX
	 * @param pointY
	 * @param stageId
	 * @param ovdlistener
	 */
	public FingerprintDialog(Context context, List<Beacon> setBeacon, boolean isPointActivity, int pointX, int pointY, int stageId,
			ovdListener ovdlistener) {
		super(context);
		this.service = new FingerprintService(context);
		this.mPointSave = setBeacon;
		this.isPointActivity = isPointActivity;
		this.pointX = pointX;
		this.pointY = pointY;
		this.stageId = stageId;
		this.ovdlistener = ovdlistener;
	}
	
	//End Region Constructors
	
	/**
	 * On Create Event
	 * 
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_point_save, true);

		// Set View Element
		btn_no = (Button) findViewById(R.id.dialog_point_no);
		btn_ok = (Button) findViewById(R.id.dialog_point_ok);
		this.setUnreachable = (Button) this.findViewById(R.id.dialog_point_unreachable);
		pointSaveText = (TextView) findViewById(R.id.dialog_point_text);

		// Set Listener
		btn_no.setOnClickListener(buttonNoOnClickListener());
		btn_ok.setOnClickListener(buttonOkOnClickListener());
		this.setUnreachable.setOnClickListener(this);
	}

	/**
	 * Set Cancel Button Listener
	 * @return View
	 */
	private android.view.View.OnClickListener buttonNoOnClickListener() {
		android.view.View.OnClickListener buttonClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(), getString(R.string.dialog_fingerprint_button_cancel), Toast.LENGTH_SHORT).show();
				dismiss();
			}
		};
		return buttonClick;
	}

	/**
	 * Set OK Button Listener
	 * @return View
	 */
	private android.view.View.OnClickListener buttonOkOnClickListener() {
		android.view.View.OnClickListener buttonClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				long result = service.insert(mPointSave, pointX, pointY, stageId);
				if (result == 1) {
					Toast.makeText(getContext(), getString(R.string.dialog_fingerprint_button_save), Toast.LENGTH_SHORT).show();
					ovdlistener.ovdResult();
					dismiss();
				} else if (result == -1) {
					Toast.makeText(getContext(), getString(R.string.dialog_fingerprint_button_cancel), Toast.LENGTH_SHORT).show();
				} else if (result == -2) {
					Toast.makeText(getContext(), getString(R.string.dialog_fingerprint_data_exists), Toast.LENGTH_SHORT).show();
				}
			}
		};
		return buttonClick;
	}

	/**
	 * On Start Event
	 */
	@Override
	protected void onStart() {
		super.onStart();
		if (!isPointActivity) {
			if (arrPointSave.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (Fingerprint vo : arrPointSave) {
					sb.append(this.getString(R.string.dialog_fingerprint_distance) + vo.getId() + CustomCharactersUtils.DOUBLE_NEW_LINE);
					sb.append(this.getString(R.string.dialog_fingerprint_mac) + vo.getMacAddress() + CustomCharactersUtils.NEW_LINE);
					sb.append(this.getString(R.string.dialog_fingerprint_stage_id) + stageId + CustomCharactersUtils.DOUBLE_NEW_LINE);
				}
				pointSaveText.setText(sb.toString());
			}
		} else {
			if (mPointSave.size() > 0) {
				StringBuffer sb = new StringBuffer();
				String xyPosition = this.getString(R.string.dialog_fingerprint_position_format);
				xyPosition = xyPosition.replace("[x]", Integer.valueOf(pointX).toString());
				xyPosition = xyPosition.replace("[y]", Integer.valueOf(pointY).toString());
				sb.append(xyPosition + CustomCharactersUtils.DOUBLE_NEW_LINE);
				for (Beacon vo : mPointSave) {
					sb.append(this.getString(R.string.dialog_fingerprint_mac) + vo.getMacAddress() + CustomCharactersUtils.NEW_LINE);
					sb.append(this.getString(R.string.dialog_fingerprint_label) + vo.getLabel() + CustomCharactersUtils.NEW_LINE);
					sb.append(this.getString(R.string.dialog_fingerprint_rssi) + vo.getRssi() + CustomCharactersUtils.NEW_LINE);
					sb.append(this.getString(R.string.dialog_fingerprint_stage_id) + stageId + CustomCharactersUtils.DOUBLE_NEW_LINE);
				}
				pointSaveText.setText(sb.toString());
			}
		}
	}
	
	/**
	 * On Click Event
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int getID = v.getId();
		if (getID == R.id.dialog_point_ok) {
			long result = service.insert(this.mPointSave, this.pointX, this.pointY, this.stageId);
			this.finishInsertData(result);
		} else if (getID == R.id.dialog_point_no) {
			dismiss();
		} else if(getID == R.id.dialog_point_unreachable){
			long result = this.service.insert(this.getSavedBeaconWithLowRSSI(this.mPointSave), this.pointX, this.pointY, stageId);
			this.finishInsertData(result);
		}
	}

	/**
	 * Interface : OVD Listener
	 * @author SeoulTech Application Software Lab
	 *
	 */
	public interface ovdListener {
		public void ovdResult();
	}

	/**
	 * Get String From Resource ID
	 * 
	 * @param resourceId
	 * @return String
	 */
	private String getString(int resourceId) {
		return getContext().getString(resourceId);
	}
	
	/**
	 * Finish Insert Data
	 * @param result
	 */
	private void finishInsertData(long result){
		if (result == 1) {
			Toast.makeText(getContext(), getString(R.string.dialog_fingerprint_button_save), Toast.LENGTH_SHORT).show();
			ovdlistener.ovdResult();
			dismiss();
		} else if (result == -1) {
			Toast.makeText(getContext(), getString(R.string.dialog_fingerprint_button_cancel), Toast.LENGTH_SHORT).show();
		} else if (result == -2) {
			Toast.makeText(getContext(), getString(R.string.dialog_fingerprint_data_exists), Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Set Each Saved Beacon wit minimum possible rssi;
	 * @param mPointSave
	 * @return
	 */
	private List<Beacon> getSavedBeaconWithLowRSSI(List<Beacon> mPointSave){
		for (Beacon beacon : mPointSave) {
			beacon.setRssi(RSSIUtils.MINIMUM_RSSI_VALUE);
		}
		return mPointSave;
	}
}
