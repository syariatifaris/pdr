package com.seoultechappsoftlab.wireloc.entities;

/**
 * Created by farissyariati on 7/13/15.
 */
public class RSSIRecord {
    private int sequence;
    private int wifiRssi;
    private int beaconRssi;

    public RSSIRecord(int sequence, int wifiRssi, int beaconRssi){
        this.sequence = sequence;
        this.wifiRssi = wifiRssi;
        this.beaconRssi = beaconRssi;
    }

    public int getSequence(){
        return this.sequence;
    }

    public int getWifiRssi(){
        return this.wifiRssi;
    }

    public int getBeaconRssi(){
        return this.beaconRssi;
    }
}
