package com.seoultechappsoftlab.wireloc.activity;

import com.seoultechappsoftlab.wireloc.activity.adapter.HomeGridViewAdapter;
import com.seoultechappsoftlab.wireloc.controllers.HomeController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * Main Activity
 * 
 * @author Seoul Tech Application Software Lab
 *
 */
public class HomeActivity extends Activity {
	
	//Region Private Variable
	
	private GridView gridView;
	private HomeGridViewAdapter gridAdapter;
	private HomeController controller;

	//End Region Private Variable
	
	/**
	 * On Create Event
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		this.controller = new HomeController(this);
		this.gridView = (GridView) findViewById(R.id.menuGrid);
		this.gridAdapter = new HomeGridViewAdapter(this,
				R.layout.row_grid_home, this.controller.getHomeIcons());
		this.gridView.setAdapter(this.gridAdapter);

		this.gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent nextActivity = new Intent(getBaseContext(), controller
						.getHomeIcons().get(position).getActionClass());
				startActivity(nextActivity);
			}
		});
	}
}
