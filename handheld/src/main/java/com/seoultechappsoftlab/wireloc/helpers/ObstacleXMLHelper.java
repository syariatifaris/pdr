package com.seoultechappsoftlab.wireloc.helpers;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.seoultechappsoftlab.wireloc.entities.Obstacle;
import android.graphics.Point;

/**
 * Class: Handle XMl parser - obctacle.xml
 * @author SeoulTechAppSoftLab
 *
 */
public class ObstacleXMLHelper extends DefaultHandler {
	private boolean currentElement;
	private Obstacle obstacle;
	private ArrayList<Obstacle> obstacles;

	private String currentValue;
	
	/**
	 * The Constructor
	 */
	public ObstacleXMLHelper() {
		super();
		this.currentValue = "";
	}
	
	/**
	 * Read start element (open tag)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		this.currentElement = true;
		if (qName.equalsIgnoreCase("stage")) {
			this.setObstacles(new ArrayList<Obstacle>());
		} else if (qName.equalsIgnoreCase("obstacle")) {
			this.obstacle = new Obstacle();
		}
	}
	
	/**
	 * Read end element (close tag)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		this.currentElement = false;

		if (qName.equalsIgnoreCase("label")) {
			this.obstacle.setLabel(this.currentValue.trim());
		} else if (qName.equalsIgnoreCase("resource")) {
			this.obstacle.setResource(this.currentValue.trim());
		} else if (qName.equalsIgnoreCase("width")) {
			this.obstacle.setWidth(Integer.parseInt(this.currentValue.trim()));
		} else if (qName.equalsIgnoreCase("height")) {
			this.obstacle.setHeight(Integer.parseInt(this.currentValue.trim()));
		} else if (qName.equals("initpoint")) {
			String pointData = this.currentValue.trim();
			String point[] = pointData.split(" ");
			this.obstacle.setInitPoint(new Point(Integer.parseInt(point[0]) + 3, Integer.parseInt(point[1])));
		} else if (qName.equalsIgnoreCase("obstacle")) {
			this.getObstacles().add(obstacle);
		}

		this.currentValue = "";
	}
	
	/**
	 * Read character item inside tag
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (this.currentElement) {
			this.currentValue = currentValue + new String(ch, start, length);
		}
	}
	
	/**
	 * Get the obstacle
	 * @return
	 */
	public ArrayList<Obstacle> getObstacles() {
		return this.obstacles;
	}
	
	/**
	 * set the obstacle
	 * @param obstacles
	 */
	public void setObstacles(ArrayList<Obstacle> obstacles) {
		this.obstacles = obstacles;
	}
}
