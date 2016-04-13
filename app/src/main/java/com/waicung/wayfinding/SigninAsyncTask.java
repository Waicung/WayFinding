package com.waicung.wayfinding;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Objects;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
/* Authentication through remote php server
* By comparing the input with the record in remote database
* Update local database with the result of the authentication
* */
public class SigninAsyncTask extends AsyncTask<String,Void,String>{
    //A view for presenting the result
    private TextView statusField;
    //The context of the database operation
    private Context context;
    //A stamp for authentication
    private boolean auth = false;

    public SigninAsyncTask(Context context, TextView statusField) {
        this.context = context;
        this.statusField = statusField;
    }

    @Override
    protected String doInBackground(String... arg0) {
            try{
                // get username and password from input
                String username = (String)arg0[0];
                String password = (String)arg0[1];
                // Authentication link
                String authenticateAPI="http://wayfinding.magicjane.org/authen.php";
                //String authenticateAPI="http://putsreq.com/XGjokiQ4WGjfi0n0YFVY";
                String data  = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                // Establish http connection
                URL url = new URL(authenticateAPI);
                URLConnection conn = url.openConnection();
                // Post data
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();
                // get response from the connection
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                line = reader.readLine();
                Log.v("Output", line);

                // Read Server Response
                if(line != null)
                {
                    auth = true;
                    Log.v("Output", line);
                    DBOpenHelper mydb = new DBOpenHelper(context);
                    if(mydb.checkIfUserExist(username)){
                        mydb.asPrimary(username,1);
                    }
                    else{
                        mydb.insertUser(username,password,1);
                    }
                    return "true";

                }
                else{return "false";}
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