package com.waicung.wayfinding;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by waicung on 15/04/2016.
 * Read route instruction file
 */
public class readRouteAsyncTask extends AsyncTask {
    String filename;
    Context context;

    @Override
    protected String doInBackground(Object[] params) {
        context = (Context)params[0];
        filename = (String)params[1];
        File file = new File(context.getFilesDir(), filename);
        byte[] content = new byte[(int)file.length()];
        try {
            FileInputStream fileinputstream = new FileInputStream(file);
            fileinputstream.read(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(content);
    }
}
