package com.waicung.wayfinding.models;

/**
 * Created by waicung on 28/04/2016.
 */
public class LocationRecord {
    private double lat;
    private double lng;
    private long time;
    private int step_number;

    public LocationRecord(double lat, double lng, long time, int step_number){
        this.lat = lat;
        this.lng = lng;
        this.time = time;
        this.step_number = step_number;
    }
}
