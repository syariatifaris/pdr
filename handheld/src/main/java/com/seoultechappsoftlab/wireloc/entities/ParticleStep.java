package com.seoultechappsoftlab.wireloc.entities;

/**
 * Created by Faris on 7/29/2015.
 */
public class ParticleStep {

    private double x;
    private double y;

    public ParticleStep(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
