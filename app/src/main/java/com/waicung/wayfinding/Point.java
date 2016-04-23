package com.waicung.wayfinding;

/**
 * Created by waicung on 14/04/2016.
 * A Point representing class
 * A point consist of latitude and longitude
 */
public class Point {
    private Double lat;
    private Double lng;

    //constructor Point(lat, lng)
    public Point(Double lat, Double lon){
        this.lat = lat;
        this.lng = lon;
    }

    public Point(String lat, String lon){
        this.lat = Double.parseDouble(lat);
        this.lng = Double.parseDouble(lon);
    }

    public String toString(){
        String location = lat.toString() + "," + lng.toString();
        return location;
    }

    public Point clone(){
        Double new_lat = this.lat;
        Double new_lon = this.lng;
        return new Point(new_lat, new_lon);
    }
}
