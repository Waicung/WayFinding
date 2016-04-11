package com.waicung.wayfinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by waicung on 03/04/2016.
 * A helper for receiving response from Google Direction API
 * not an active activity
 */
public class DirectionHelper {
    private String GOOGLE_API_KEY = "AIzaSyAZEyaeSOnH8dcVq646GIyUQbxGKHza_dc";
    private String d_origin = "-37.799538,144.958053";
    private String d_destination = "-37.799901,144.943267";
    public String origin = "";
    public String destination = "";
    private final String MODE = "walking";
    String parameter = "";
    URL url;


/*    DirectionHelper(){
        new DirectionHelper(d_origin,d_destination);
    }

    DirectionHelper(String origin,String destination){
        //a constructor got the longitude and latitude of the origin and destination
        this.origin = origin;
        this.destination = destination;
        try{
            //url = new URL("http://www.google.com/");}
            parameter = "origin="+origin+"&destination="+destination+"&mode="+MODE+"&key="+GOOGLE_API_KEY;
            url = new URL("https://maps.googleapis.com/maps/api/directions/json?"+parameter);}
        catch (MalformedURLException e){}

    }*/

    public String getJSONStr(){
        this.origin = d_origin;
        this.destination = d_destination;
        try{
            //url = new URL("http://www.google.com/");}
            parameter = "origin="+origin+"&destination="+destination+"&mode="+MODE+"&key="+GOOGLE_API_KEY;
            url = new URL("https://maps.googleapis.com/maps/api/directions/json?"+parameter);}
        catch (MalformedURLException e){}
        URLConnection conn;
        StringBuilder Str = new StringBuilder();
        try {
            System.out.print(url);
            conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = in.readLine();
            while(line!=null){
                Str.append(line+'\n');
                line=in.readLine();
            }
        }
        catch (IOException e){}
        return Str.toString();
    }


}
