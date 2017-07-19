package com.seoultechappsoftlab.wireloc.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import com.seoultechappsoftlab.wireloc.entities.HomeIcon;
import com.seoultechappsoftlab.wireloc.activity.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Grid View Adapter for Home Activity
 * @author SeoulTech Application Software Lab
 *
 */
public class HomeGridViewAdapter extends ArrayAdapter<HomeIcon> {
	
	//Region Private Variables
	
	private Context context;
	private int layoutResourceId;
	private List<HomeIcon> homeIcons = new ArrayList<HomeIcon>();
	
	//End Region Private Variables
	
	/**
	 * Constructor
	 * @param context
	 * @param resource
	 * @param homeIcons
	 */
	public HomeGridViewAdapter(Context context, int resource,
			List<HomeIcon> homeIcons) {
		super(context, resource, homeIcons);
		this.context = context;
		this.layoutResourceId = resource;
		this.homeIcons = homeIcons;
	}

	/**
	 * Set the view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		ViewHolder homeViewHolder = null;
		
		if(row == null){
			LayoutInflater inflater = ((Activity)this.context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			homeViewHolder = new ViewHolder();
			homeViewHolder.iconTitle = (TextView) row.findViewById(R.id.home_item_text);
			homeViewHolder.iconImage = (ImageView) row.findViewById(R.id.home_item_image);
			row.setTag(homeViewHolder);
		}else{
			homeViewHolder = (ViewHolder)row.getTag();
		}
		
		HomeIcon currentIcon = this.homeIcons.get(position);
		homeViewHolder.iconTitle.setText(currentIcon.getName());
		homeViewHolder.iconImage.setImageBitmap(currentIcon.getIconBitmap());
		return row;
	}
	
	/**
	 * Home Icon View Holder
	 * @author SeoulTech Application Software Lab
	 *
	 */
	static class ViewHolder{
		TextView iconTitle;
		ImageView iconImage;
	}
}
