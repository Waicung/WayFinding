package com.waicung.wayfinding.webclient;

import com.waicung.wayfinding.models.Point;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * Used in Version 1.1
 * Created by waicung on 03/04/2016.
 * A helper for sending http request and receiving response from Google Direction API
 * The constructor take two Points as parameter to form a query
 * The only method: getJSONStr will return the response from Google Direction API as string
 */
public class GGDirectionHelper {
    //Google API key
    private String GOOGLE_API_KEY = "AIzaSyAZEyaeSOnH8dcVq646GIyUQbxGKHza_dc";
    //Two location as origin and destination point
    public String origin = "";
    public String destination = "";
    //Travel mode as parameter for Google Direction API
    private final String MODE = "walking";
    String parameter = "";
    URL url;

    //TODO testing main method
    public static void main(String arg[]){
        GGDirectionHelper DH = new GGDirectionHelper();
        String output = DH.getJSONStr();
        System.out.print(output);
    }

    //When no parameter passed for a new instance,
    //default value will be used
    public GGDirectionHelper(){
        //origin = "-37.799538,144.958053";
        origin = "-37.790950,144.927464";
        //destination = "-37.799901,144.943267";
        destination = "-37.7909247,144.9228723";
    }

    public GGDirectionHelper(Point origin, Point destination){
        //a constructor got the longitude and latitude of the origin and destination
        this.origin = origin.toString();
        this.destination = destination.toString();
    }

    //the only method for sending the request and receiving the response
    public String getJSONStr(){
        try{
            //set parameter and Google Direction API(url)
            parameter = "origin="+origin+"&destination="+destination+"&mode="+MODE+"&key="+GOOGLE_API_KEY;
            url = new URL("https://maps.googleapis.com/maps/api/directions/json?"+parameter);}
        catch (MalformedURLException e){}
        URLConnection conn;
        StringBuilder Str = new StringBuilder();
        try {
            System.out.println("API: " + url);
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
