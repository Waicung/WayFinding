package com.waicung.wayfinding;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by waicung on 19/04/2016.
 * An authentication class for retrieving credential info from service
 * When revoke with correct credential
 * server return route_id, start_point, and end_point information for further activity
 */

public class AuthenHelper {
    private URL url;
    private String username, password;

    AuthenHelper(String username, String password){
        this.username = username;
        this.password = password;

    }

    public static void main(String arg[]){
        AuthenHelper AH = new AuthenHelper("tuser","tpass");
        AuthenNResponse response = AH.authentication();
        String message = response.getMessage();
        System.out.print("Response message: " + message);
        System.out.print("User_id: " + response.getUser_id());
        System.out.print("Route_id: " + response.getRoute_id());
        System.out.println("Success: " + response.getSuccess());
        List<Point> points = response.getPoints();
        for(Point p: points){
            System.out.println(p.toString());
        }

    }

    public AuthenNResponse authentication(){
        StringBuilder Str = new StringBuilder();
        try {
            //TODO parameter to be added
            url = new URL("http://localhost:8080/wayfinding/authenticationAPI.php");
        } catch (MalformedURLException e){}
        try{
            String postData = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            postData += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            URLConnection conn = url.openConnection();
            conn.setDoOutput( true );
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(postData);
            wr.flush();
            //get response from the connection
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = reader.readLine();
            //read response as string
            while(line!=null){
                Str.append(line);
                line=reader.readLine();
            }
        }
        catch (IOException e){}
        System.out.println("Original Data: " + Str.toString());
        Gson gson = new Gson();
        AuthenNResponse response = gson.fromJson(Str.toString(), AuthenNResponse.class);
        return response;
    }


}
