package com.seoultechappsoftlab.wireloc.activity.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.seoultechappsoftlab.wireloc.activity.R;
import com.seoultechappsoftlab.wireloc.infrastructures.DialogBase;

public class ConfirmationDialog extends DialogBase implements android.view.View.OnClickListener {
	
	private boolean isOK;
	// Component
	
	private Button btn_cancel;
	private Button btn_delete;
	//private TextView textView;

	public ConfirmationDialog(Context context) {
		super(context);
		setOK(false);
	}
	
	/**
	 * On Create Event
	 * 
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_confirmation, true);

		// Set View Element
		this.btn_cancel = (Button) findViewById(R.id.dialog_point_cancel);
		this.btn_delete = (Button) findViewById(R.id.dialog_point_ok);
		//this.textView = (TextView) findViewById(R.id.dialog_point_text);

		// Set Listener
		this.btn_cancel.setOnClickListener(this);
		this.btn_delete.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int getID = v.getId();
		if (getID == R.id.dialog_point_ok) {
			this.setOK(true);
			dismiss();
		} else if (getID == R.id.dialog_point_cancel) {
			this.setOK(false);
			dismiss();
		}
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}

}
