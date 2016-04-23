package com.waicung.wayfinding;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by waicung on 19/04/2016.
 * A Http request class for accessing server side authentication API
 * When revoke with correct credential
 * server return information such as assignment route_id, start_point, and end_point
 */

public class AuthenHelper {
    //remote authentication API
    private URL url;
    //user credential as parameter for HTTP post request
    private String username, password;

    AuthenHelper(String username, String password){
        this.username = username;
        this.password = password;

    }

    //TODO testing main method
    public static void main(String arg[]){
        AuthenHelper AH = new AuthenHelper("","");
        AuthenNResponse response = AH.getAuthenNResponse();
        String message = response.getMessage();
        System.out.println("Response message: " + message);
        System.out.println("Success: " + response.getSuccess());
    }

    //Actual method for the http request
    public AuthenNResponse getAuthenNResponse(){
        StringBuilder Str = new StringBuilder();
        System.out.println("user" + username);
        try {
            //TODO insert correct API url
            //for use of android emulator
             url = new URL("http://10.0.2.2:8080/wayfinding/authenticationAPI.php");
            //for use of local host
            //url = new URL("http://localhost:8080/wayfinding/authenticationAPI.php");
        } catch (MalformedURLException e){}
        try{
            //original way of encoding
            /*String postData = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            postData += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");*/
            String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                    "&password=" + URLEncoder.encode(password,"UTF-8");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //set properties of the connection
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //set post data(change it to byte data)
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(postData);
            wr.flush();
            wr.close();
            //get response from the connection
            System.out.println("Server return code: " + conn.getResponseCode());
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = reader.readLine();
            //read response as string
            while(line!=null){
                System.out.println("stream line: " + line);
                Str.append(line + '\n');
                line=reader.readLine();
            }
        }
        catch (IOException e){
            System.out.println("IOException : " + e.getMessage());
        }
        System.out.println("Original Data: " + Str.toString());
        Gson gson = new Gson();
        AuthenNResponse response = gson.fromJson(Str.toString(), AuthenNResponse.class);
        return response;
    }


}
