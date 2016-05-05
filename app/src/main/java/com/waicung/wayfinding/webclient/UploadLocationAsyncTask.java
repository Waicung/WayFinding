package com.waicung.wayfinding.webclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.waicung.wayfinding.R;
import com.waicung.wayfinding.config.ConfigHandler;
import com.waicung.wayfinding.models.LocationRecord;
import com.waicung.wayfinding.models.Route;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by waicung on 28/04/2016.
 */
public class UploadLocationAsyncTask extends AsyncTask {
    private Context context;
    //private String api = "http://wayfinding.magicjane.org/receiveLocations.php";
    private  String api; //= "http://192.168.43.12:8080/wayfinding/receiveLocations.php";

    String postData;
    ProgressDialog pd;

    public UploadLocationAsyncTask(Context context){
        this.context = context;
        ConfigHandler config = new ConfigHandler(context, "config");
        api = config.getApi(context.getString(R.string.api_upload_location));

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setMessage("Uploading results");
        pd.show();

    }

    @Override
    protected Object doInBackground(Object[] params) {
        ArrayList<LocationRecord> locations = (ArrayList<LocationRecord>) params[1];
        String assignment_id = String.valueOf(params[0]);
        Gson gson = new Gson();
        try {
            Log.i("Locations", gson.toJson(locations));
            postData = "locations=" + URLEncoder.encode(gson.toJson(locations),"UTF-8") +
                    "&assignment_id=" + URLEncoder.encode(assignment_id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
