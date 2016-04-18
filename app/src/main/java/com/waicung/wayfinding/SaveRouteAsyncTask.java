package com.waicung.wayfinding;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by waicung on 15/04/2016.
 * Save route information as a file
 */
public class SaveRouteAsyncTask extends AsyncTask {
    String filename;
    String content;
    Context context;

    @Override
    protected Boolean doInBackground(Object[] params) {
        context = (Context)params[0];
        filename = (String)params[1];
        content = (String)params[2];
        File file = new File(context.getFilesDir(), filename);
        if(file.exists()){
            return false;
        }
        else{
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;}
    }
}
