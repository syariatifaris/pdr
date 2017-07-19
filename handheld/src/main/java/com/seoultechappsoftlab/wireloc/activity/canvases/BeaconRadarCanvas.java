package com.seoultechappsoftlab.wireloc.activity.canvases;

import java.util.ArrayList;

import com.seoultechappsoftlab.wireloc.entities.PointBeacon;   
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BeaconRadarCanvas extends View {

	private ArrayList<PointBeacon> pList;

	public BeaconRadarCanvas(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		pList = new ArrayList<PointBeacon>();
	}
	
	public BeaconRadarCanvas(Context context, AttributeSet attr) {
		super(context, attr);
		// TODO Auto-generated constructor stub
		pList = new ArrayList<PointBeacon>();
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
		Paint pnt = new Paint();
		if (pList.size() > 0) {
			for (PointBeacon vo : pList){
				if(vo.getX()== 0 && vo.getY() == 0){
					pnt.setColor(Color.rgb(0, 255, 0));
				} 
				else if(vo.getX()== 15 && vo.getY() == 1){
					pnt.setColor(Color.rgb(0, 255, 0));
				}
				else if(vo.getX()== 15 && vo.getY() == 27){
					pnt.setColor(Color.rgb(0, 255, 0));
				}
				else if(vo.getX()== 0 && vo.getY() == 27){
					pnt.setColor(Color.rgb(0, 255, 0));
				}
				else {
					pnt.setColor(Color.rgb(255, 255-vo.getRgbColor(), 255-vo.getRgbColor()));	
				}
				
				if(vo.isMostProbably()){
					pnt.setColor(Color.rgb(0, 0, 255));
				}
				
				canvas.drawCircle(vo.getX() * 70 + 12 , vo.getY() * 50 + 12, 10, pnt);
			}
		}
	}
	
	public void setPoint() {
		for (int y = 0; y < 31; y++) {
			for (int x = 0; x < 17; x++) {
				pList.add(new PointBeacon(x, y));
			}
		}
		this.invalidate();
	}

	public ArrayList<PointBeacon> getPointList() {
		return pList;
	}

	public void setPointList(ArrayList<PointBeacon> pList) {
		this.pList = pList;
		this.invalidate();
	}
}
