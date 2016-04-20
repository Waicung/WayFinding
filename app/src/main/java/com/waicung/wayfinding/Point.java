package com.waicung.wayfinding;

/**
 * Created by waicung on 14/04/2016.
 */
public class Point {
    private Double lng;
    private Double lat;

    public Point(Double lon, Double lat){
        this.lng = lon;
        this.lat = lat;
    }

    public Point(String lon, String lat){
        this.lng = Double.parseDouble(lon);
        this.lat = Double.parseDouble(lat);
    }

    public String toString(){
        String location = lng.toString() + "," + lat.toString();
        return location;
    }

    public Point clone(){
        Double new_lon = this.lng;
        Double new_lat = this.lat;
        return new Point(new_lon,new_lat);
    }
}
