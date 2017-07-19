package com.seoultechappsoftlab.wireloc.entities;

import android.graphics.Point;

public class Obstacle {
	
	private String label;
	private String resource;
	private int width;
	private int height;
	private Point initPoint;
	
	public Obstacle(){
		
	}

	public Obstacle(String label, String resource, int width, int height,
			Point initPoint) {
		super();
		this.label = label;
		this.resource = resource;
		this.width = width;
		this.height = height;
		this.initPoint = initPoint;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Point getInitPoint() {
		return initPoint;
	}

	public void setInitPoint(Point initPoint) {
		this.initPoint = initPoint;
	}
	
}
