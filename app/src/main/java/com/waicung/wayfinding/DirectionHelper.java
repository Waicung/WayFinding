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
 * The constructor take two coordinates as parameter to form a query
 * The only method: getJSONStr will return the response from Google Direction API
 */
public class DirectionHelper {
    private String GOOGLE_API_KEY = "AIzaSyAZEyaeSOnH8dcVq646GIyUQbxGKHza_dc";
    public String origin = "";
    public String destination = "";
    private final String MODE = "walking";
    String parameter = "";
    URL url;

    public static void main(String arg[]){
        DirectionHelper DH = new DirectionHelper();
        String output = DH.getJSONStr();
        System.out.print(output);
    }


    DirectionHelper(){
        origin = "-37.799538,144.958053";
        destination = "-37.799901,144.943267";
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

    }

    public String getJSONStr(){
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
