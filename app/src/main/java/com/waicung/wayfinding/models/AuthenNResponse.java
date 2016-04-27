package com.waicung.wayfinding.models;

import java.util.List;

/**
 * Created by waicung on 20/04/2016.
 * Remote authentication response
 */
public class AuthenNResponse {
    //code: steps_info; if modified: if tested
    public final int CODE_NO_ROUTE = 0000;
    public final int CODE_NO_STEP = 1000;
    public final int CODE_NOT_MODIFIED = 1100;
    public final int CODE_NOT_TESTED = 1110;
    public final int CODE_ALL_TESTED = 1111;


    private int status;
    private boolean success;
    private String message;
    private String user_id;
    private String route_id;
    private List<Point> points;
    private List<Step> steps;

    public boolean getSuccess(){
        return success;
    }

    public String getMessage(){
        return message;
    }

    public String getUser_id(){
        return user_id;
    }

    public int getStatus(){
        return this.status;
    }

    public String getRoute_id(){
        return route_id;
    }

    public List<Point> getPoints(){
        return points;
    }

    public  List<Step> getSteps(){
        return this.steps;
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
