package com.seoultechappsoftlab.wireloc.models;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.seoultechappsoftlab.wireloc.activity.BeaconRadarActivity;
import com.seoultechappsoftlab.wireloc.activity.CompassActivity;
import com.seoultechappsoftlab.wireloc.activity.DatabaseImportActivity;
import com.seoultechappsoftlab.wireloc.activity.FingerprintManagerActivity;
import com.seoultechappsoftlab.wireloc.activity.PDRActivity;
import com.seoultechappsoftlab.wireloc.activity.ParticleFilterActivity;
import com.seoultechappsoftlab.wireloc.activity.ParticleFilterMapActivity;
import com.seoultechappsoftlab.wireloc.activity.R;
import com.seoultechappsoftlab.wireloc.activity.WifiSignalReaderActivity;
import com.seoultechappsoftlab.wireloc.entities.HomeIcon;

/**
 * Home Model
 * 
 * @author SeoulTech Application Software Lab
 *
 */
public class HomeModel {

	// Region Private Field

	private List<HomeIcon> homeIcons;
	private Context context;

	// End Region Private Field

	// Region Constructors

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public HomeModel(Context context) {
		this.context = context;
		this.setHomeIcons();
	}

	// End Region Constructors

	// Region Getters and Setters

	/**
	 * Get Home Icons
	 * 
	 * @return Home Icons
	 */
	public List<HomeIcon> getHomeIcons() {
		return this.homeIcons;
	}

	/**
	 * Set Home Icons
	 * 
	 * @param homeIcons
	 */
	public void setHomeIcons(List<HomeIcon> homeIcons) {
		this.homeIcons = homeIcons;
	}

	// End Region Getters and Setters

	// Region Private Method

	/**
	 * Set Initialize Home Icons
	 */
	private void setHomeIcons() {
		this.homeIcons = new ArrayList<HomeIcon>();
		this.homeIcons.add(new HomeIcon(this.context.getString(R.string.pdr_activity), R.drawable.steps, PDRActivity.class));
		//this.homeIcons.add(new HomeIcon(this.context.getString(R.string.beacon_radar_activity), R.drawable.ble, BeaconRadarActivity.class));
		this.homeIcons.add(new HomeIcon(this.context.getString(R.string.particle_filter_activity), R.drawable.person, ParticleFilterActivity.class));
		this.homeIcons.add(new HomeIcon(this.context.getString(R.string.particle_map_activity), R.drawable.map, ParticleFilterMapActivity.class));
		this.homeIcons.add(new HomeIcon(this.context.getString(R.string.database_import_activity), R.drawable.sqlite, DatabaseImportActivity.class));
		this.homeIcons.add(new HomeIcon(this.context.getString(R.string.compass_activity), R.drawable.compass, CompassActivity.class));
		this.homeIcons.add(new HomeIcon(this.context.getString(R.string.fingeprint_manager_activity), R.drawable.fingerprint, FingerprintManagerActivity.class));		
		this.homeIcons.add(new HomeIcon(this.context.getString(R.string.wifi_signal_activity), R.drawable.wifi, WifiSignalReaderActivity.class));

		for (HomeIcon icon : this.homeIcons) {
			icon.setIconBitmap(icon.getImageResourceId() != 0 ? BitmapFactory.decodeResource(this.context.getResources(), icon.getImageResourceId())
					: null);
		}
	}
	// End Region Private Method
}
