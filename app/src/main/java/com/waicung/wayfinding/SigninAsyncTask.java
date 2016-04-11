package com.waicung.wayfinding;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SigninAsyncTask extends AsyncTask<String,Void,String>{
    private TextView statusField,roleField;
    private Context context;
    private int byGetOrPost = 0;
    private boolean auth = false;

    //flag 0 means get and 1 means post.(By default it is get.)
    public SigninAsyncTask(Context context, TextView statusField) {
        this.context = context;
        this.statusField = statusField;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    protected void onPreExecute(){

    }

    @Override
    protected String doInBackground(String... arg0) {
            try{
                String username = (String)arg0[0];
                String password = (String)arg0[1];


                String link="http://wayfinding.magicjane.org/authen.php";
                String data  = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    sb.append(line);
                    auth = true;
                    Log.v("Output", line);
                    DBOpenHelper mydb = new DBOpenHelper(context);
                    if(mydb.checkIfUserExist(username)){
                        mydb.asPrimary(username,1);
                    }
                    else{
                        mydb.insertUser(username,password,1);
                    }
                    break;
                }
                return sb.toString();
            }
            catch(Exception e){
                Log.v("Exception", e.getMessage());

                return new String("Exception: " + e.getMessage());
            }

    }


    @Override
    protected void onPostExecute(String result){
        if (auth) {
            this.statusField.setText("Login Successful");
            this.statusField.setVisibility(View.VISIBLE);
        }
    }
}