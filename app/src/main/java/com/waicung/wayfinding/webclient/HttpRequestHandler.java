package com.waicung.wayfinding.webclient;

import android.util.Log;

import com.google.gson.Gson;
import com.waicung.wayfinding.models.AuthenNResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * used in Version 1.1
 * Created by waicung on 23/04/2016.
 * A Http request handler for GET and POST request
 * If POST method, required encoding
 */
public class HttpRequestHandler {
    private URL url;
    private String data;
    private HttpURLConnection conn;
    private final String TAG = "HttprequestHandler";

    public static void main(String args[]){
        //test get request
        /*String GOOGLE_API_KEY = "AIzaSyAZEyaeSOnH8dcVq646GIyUQbxGKHza_dc";
        String MODE = "walking";
        String origin = "-37.790950,144.927464";
        String destination = "-37.7909247,144.9228723";
        String api = "https://maps.googleapis.com/maps/api/directions/json?";
        String parameter = "origin="+origin+"&destination="+destination+"&mode="+MODE+"&key="+GOOGLE_API_KEY;
        HttpRequestHandler HH = new HttpRequestHandler("GET", api, parameter);
        String output = HH.getRequest();
        System.out.println(output);*/

        //test post request
        String data  = null;
        try {
            data = "username=" + URLEncoder.encode("tuser", "UTF-8") +
                    "&password=" + URLEncoder.encode("tpass","UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String testurl ="http://localhost:8080/wayfinding/authenticationAPI.php";
        String realurl = "http://wayfinding.magicjane.org/authenticationAPI.php";
        HttpRequestHandler HP = new HttpRequestHandler("POST", testurl, data);
        String poutput = HP.postRequest();
        //Log.i(TAG, poutput);
        Gson gson = new Gson();
        AuthenNResponse response = gson.fromJson(poutput, AuthenNResponse.class);


    }

    public HttpRequestHandler(String method, String api, String parameters){
        this.data = parameters;
        switch (method){
            case "GET":
            try {
                this.url = new URL(api + data);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
                break;
            case "POST":
            try {
                this.url = new URL(api);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
                break;
            default:
                System.out.println("Wrong method");
                break;
        }
    }

    public String getRequest(){
        String response;
        StringBuilder Str = new StringBuilder();
        try {
            conn = (HttpURLConnection) this.url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = in.readLine();
            while(line!=null){
                Str.append(line+'\n');
                line=in.readLine();
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        response = Str.toString();
        return response;
    }

    public String postRequest(){
        String response;
        StringBuilder Str = new StringBuilder();
        try{
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //set properties of the connection
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //set post data(change it to byte data)
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            wr.close();
            //get response from the connection
            Log.i(TAG, Integer.valueOf(conn.getResponseCode()).toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = reader.readLine();
            //read response as string
            while(line!=null){
                Str.append(line + '\n');
                line=reader.readLine();
            }
        }
        catch (IOException e){
            e.getStackTrace();
            Log.e(TAG, e.getMessage());
        }
        response = Str.toString();
        Log.i(TAG , response);
        return response;
    }

}
