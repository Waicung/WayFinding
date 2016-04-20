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
        AuthenHelper AH = new AuthenHelper("","");
        AuthenNResponse response = AH.authentication();
        String message = response.getMessage();
        System.out.println("Response message: " + message);
        System.out.println("Success: " + response.getSuccess());
        /*System.out.print("User_id: " + response.getUser_id());
        System.out.print("Route_id: " + response.getRoute_id());
        List<Point> points = response.getPoints();
        for(Point p: points){
            System.out.println(p.toString());
        }*/

    }

    public AuthenNResponse authentication(){
        StringBuilder Str = new StringBuilder();
        System.out.println("user" + username);
        try {
            //TODO parameter to be added
            url = new URL("http://192.168.1.8:8080/wayfinding/authenticationAPI.php");
        } catch (MalformedURLException e){}
        try{
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
            //set post data(change into byte data)
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
                Str.append(line);
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

    private String readInputStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        String TAG = "WayFinding";

        try {
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        }
        catch (Exception e) {
            Log.i(TAG, "Error reading InputStream");
            result = null;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    Log.i(TAG, "Error closing InputStream");
                }
            }
        }

        return result;
    }


}
