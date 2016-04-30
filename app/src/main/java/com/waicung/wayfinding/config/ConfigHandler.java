package com.waicung.wayfinding.config;

import android.content.Context;
import android.util.Log;

import com.waicung.wayfinding.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by waicung on 30/04/2016.
 */
public class ConfigHandler {
    private String path;
    private Context context;
    private String TAG = "ConfigHandler";
    private String fileName;

    public ConfigHandler(Context context, String fileName){
        this.context = context;
        this.path = context.getFilesDir().getPath();
        Log.i(TAG, path);
        this.fileName = fileName;
        //TODO set up when the app is installed
        Properties config = new Properties();
        String locationApi  = "http://wayfinding.magicjane.org/receiveLocations.php";
        config.put(context.getString(R.string.api_upload_location),locationApi);
        String routeApi  = "http://wayfinding.magicjane.org/receiveRoute.php";
        config.put(context.getString(R.string.api_upload_route),routeApi);
        String authApi  = "http://wayfinding.magicjane.org/authenticationAPI.php";
        config.put(context.getString(R.string.api_authentication),authApi);
        saveConfig(config);
    }

    public Properties loadConfig() {
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = context.openFileInput(fileName);
            properties.loadFromXML(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }

    public boolean saveConfig(Properties properties) {
        try {
            File file = new File(fileName);
            if (!file.exists())
                file.createNewFile();
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            properties.storeToXML(outputStream, "Apis");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getApi(String ApiName){
        Properties properties = loadConfig();
        try {
            Log.i(TAG, (String) properties.get(ApiName));
            return (String) properties.get(ApiName);
        }catch (NullPointerException e){
            Log.i(TAG, e.getMessage());
            return null;
        }

    }
}
