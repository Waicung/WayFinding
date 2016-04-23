package com.waicung.wayfinding;

import java.util.List;

/**
 * Created by waicung on 20/04/2016.
 * Remote authentication response
 */
public class AuthenNResponse {
    private boolean success;
    private String message;
    private String user_id;
    private String route_id;
    private List<Point> points;

    public boolean getSuccess(){
        return success;
    }

    public String getMessage(){
        return message;
    }

    public String getUser_id(){
        return user_id;
    }

    public String getRoute_id(){
        return route_id;
    }

    public List<Point> getPoints(){
        return points;
    }

    public Point getStart(){
        List<Point> points = getPoints();
        return points.get(0);
    }

    public Point getEnd(){
        List<Point> points = getPoints();
        return points.get(1);
    }

}
