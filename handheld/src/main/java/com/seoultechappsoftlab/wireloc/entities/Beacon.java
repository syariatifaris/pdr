package com.seoultechappsoftlab.wireloc.entities;

import com.seoultechappsoftlab.wireloc.infrastructures.EntityBase;

public class Beacon extends EntityBase {
    private String macAddress;
    private int isActive;
    private String label;
    private double rssi;
    private int pointX;
    private int pointY;
    private int stageId;

    public Beacon(String macAddress, String label, int pointX, int pointY, double rssi, int isActive) {
        this.macAddress = macAddress;
        this.label = label;
        this.pointX = pointX;
        this.pointY = pointY;
        this.rssi = rssi;
        this.isActive = isActive;
    }

    public Beacon() {
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int isActive() {
        return isActive;
    }

    public void setActive(int isActive) {
        this.isActive = isActive;
    }

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public int getPointX() {
        return pointX;
    }

    public void setPointX(int pointX) {
        this.pointX = pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public void setPointY(int pointY) {
        this.pointY = pointY;
    }
}
