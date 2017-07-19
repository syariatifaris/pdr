package com.seoultechappsoftlab.wireloc.infrastructures;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.seoultechappsoftlab.wireloc.activity.dialogs.ViewMapper;

public class DialogBase extends Dialog {
	
	public DialogBase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
    	//requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	public void setContentView(int layoutResID,boolean isViewMap) {
		if (isViewMap == true) {
			setContentView(layoutResID);			
			ViewMapper.mapLayout(this, getWindow().getDecorView());
		} else {
			setContentView(layoutResID);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
}
