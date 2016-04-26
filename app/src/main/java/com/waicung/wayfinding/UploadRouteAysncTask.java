package com.waicung.wayfinding;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by waicung on 23/04/2016.
 */
public class UploadRouteAysncTask extends AsyncTask{
    Context context;
    private String api = "http://10.0.2.2:8080/wayfinding/receiveRoute.php";
    String postData;
    ProgressDialog pd;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setMessage("Organizing data");
        pd.show();

    }

    UploadRouteAysncTask(Context context){
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Route route = (Route) params[1];
        String route_id = String.valueOf(params[0]);
        Gson gson = new Gson();
        try {
            postData = "route=" + URLEncoder.encode(gson.toJson(route),"UTF-8") +
                    "&route_id=" + URLEncoder.encode(route_id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("Upload Data: " + gson.toJson(route));
        HttpRequestHandler HH = new HttpRequestHandler("POST", api, postData);
        String output = HH.postRequest();
        System.out.println("Response from uploading: " + output);
        return output;
    }

    @Override
    protected void onPostExecute(Object result){
        String message;
        if (pd != null)
        {
            pd.dismiss();
        }
        //TODO check if upload success
        /*if((boolean)result) {
            message = "Please login 5 mins later";
        }else{
            message = "Please restart the app";
        }
        Toast toast = Toast.makeText(this.context, message, Toast.LENGTH_LONG);
        toast.show();*/


    }
}