package com.seoultechappsoftlab.wireloc.activity.dialogs;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.seoultechappsoftlab.wireloc.activity.R;
import com.seoultechappsoftlab.wireloc.entities.Beacon;
import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.infrastructures.DialogBase;
import com.seoultechappsoftlab.wireloc.services.FingerprintService;
import com.seoultechappsoftlab.wireloc.utilities.CustomCharactersUtils;

public class ViewFingerprintDialog extends DialogBase implements android.view.View.OnClickListener {

	
	// Component
	
	private Button btn_cancel;
	private Button btn_delete;
	private Button btn_enable;
	private TextView pointViewText;
	
	// Global Variable
	
	int pointX;
	int pointY;
	int stageId;
	
	List<Fingerprint> pointList;
	List<Beacon> beaconList;
	
	// Listener and Services
	
	//ParticleFilterController controller;
	FingerprintService service;
	
	
	// Constructor 
	
	public ViewFingerprintDialog(Context context) {
		super(context);
		this.service = new FingerprintService(context);
	}

	public ViewFingerprintDialog(Context context, int x, int y) {
		super(context);
		this.service = new FingerprintService(context);
		this.pointX = x;
		this.pointY = y;
	}
	
	// End Region Constructor
	
	/**
	 * On Create Event
	 * 
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_point_delete, true);

		// Set View Element
		this.btn_cancel = (Button) findViewById(R.id.dialog_point_cancel);
		this.btn_delete = (Button) findViewById(R.id.dialog_point_delete);
		this.btn_enable = (Button) findViewById(R.id.dialog_point_enable);
		pointViewText = (TextView) findViewById(R.id.dialog_point_delete_text);

		// Set Listener
		this.btn_cancel.setOnClickListener(this);
		this.btn_delete.setOnClickListener(this);
		this.btn_enable.setOnClickListener(this);
	}
	

	/**
	 * On Start Event
	 */
	@Override
	protected void onStart() {
		super.onStart();
		pointList = this.service.getByXAndY(pointX, pointY);
		
		if(pointList.size() > 0){
			StringBuffer sb = new StringBuffer();
			String xyPosition = this.getString(R.string.dialog_fingerprint_position_format);
			xyPosition = xyPosition.replace("[x]", Integer.valueOf(pointX).toString());
			xyPosition = xyPosition.replace("[y]", Integer.valueOf(pointY).toString());
			sb.append(xyPosition + CustomCharactersUtils.DOUBLE_NEW_LINE);
			for (Fingerprint vo : pointList) {
				sb.append(this.getString(R.string.dialog_fingerprint_mac) + vo.getMacAddress() + CustomCharactersUtils.NEW_LINE);
				sb.append(this.getString(R.string.dialog_fingerprint_rssi) + vo.getRssi() + CustomCharactersUtils.NEW_LINE);
				sb.append(this.getString(R.string.dialog_fingerprint_stage_id) + vo.getStageId() + CustomCharactersUtils.DOUBLE_NEW_LINE);
			}
			pointViewText.setText(sb.toString());
			
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int getID = v.getId();
		if (getID == R.id.dialog_point_delete) {
			boolean result = service.deleteByXAndY(pointX, pointY);
			this.finishDeleteData(result);
		} else if (getID == R.id.dialog_point_enable){
			int isActive;
			if(pointList.get(0).isActive()){
				isActive = 0;
			} else {
				isActive = 1;
			}
			boolean result = service.updateIsActive(pointX, pointY, isActive);
			this.finishDeleteData(result);
		}
		else if (getID == R.id.dialog_point_cancel) {
			dismiss();
		}
	}
	
	/**
	 * Finish Insert Data
	 * @param result
	 */
	private void finishDeleteData(boolean result){
		if (result) {
			Toast.makeText(getContext(), getString(R.string.dialog_fingerprint_button_delete), Toast.LENGTH_SHORT).show();
			dismiss();
		} else {
			Toast.makeText(getContext(), getString(R.string.dialog_fingerprint_button_cancel), Toast.LENGTH_SHORT).show();
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
	
}
