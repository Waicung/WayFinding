package com.waicung.wayfinding.models;

/**
 * Created by waicung on 28/04/2016.
 */
public class LocationRecord {
    private double lat;
    private double lng;
    private long time;
    private int step_number;
    private String event;

    public LocationRecord(double lat, double lng, long time, int step_number, String event){
        this.lat = lat;
        this.lng = lng;
        this.time = time;
        this.step_number = step_number;
        this.event = event;
    }
}
