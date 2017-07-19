package com.seoultechappsoftlab.wireloc.controllers;

import java.util.List;

import com.seoultechappsoftlab.wireloc.entities.HomeIcon;
import com.seoultechappsoftlab.wireloc.models.HomeModel;

import android.content.Context;

/**
 * Controller Home
 * @author SeoulTech Application Software Lab
 *
 */
public class HomeController {
	
	//Region Private Variables
	
	private Context context;
	private List<HomeIcon> homeIcons;
	private HomeModel homeModel;
	
	//End Region Private Variables
	
	/**
	 * Controller
	 * @param context Context
	 */
	public HomeController(Context context){
		this.context = context;
		this.homeModel = new HomeModel(context);
		this.homeIcons = this.homeModel.getHomeIcons();
	}
	
	//Region Getters and Setters
	
	/**
	 * Get The Context
	 * @return Context
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * Set The Context
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	
	/**
	 * Get Home Icons
	 * @return Home Icons
	 */
	public List<HomeIcon> getHomeIcons() {
		return homeIcons;
	}
	
	/**
	 * Set Home Icons
	 * @param homeIcons
	 */
	public void setHomeIcons(List<HomeIcon> homeIcons) {
		this.homeIcons = homeIcons;
	}
	//End Region Getters and Setters
}
