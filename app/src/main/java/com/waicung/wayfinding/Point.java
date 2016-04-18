package com.waicung.wayfinding;

/**
 * Created by waicung on 14/04/2016.
 */
public class Point {
    private Float longitude;
    private Float latitude;

    public Point(Float lon, Float lat){
        this.longitude = lon;
        this.latitude = lat;
    }

    public Point(String lon, String lat){
        this.longitude = Float.parseFloat(lon);
        this.latitude = Float.parseFloat(lat);
    }

    public String toString(){
        String location = longitude.toString() + latitude.toString();
        return location;
    }

    public Point clone(){
        Float new_lon = this.longitude;
        Float new_lat = this.latitude;
        return new Point(new_lon,new_lat);
    }
}
