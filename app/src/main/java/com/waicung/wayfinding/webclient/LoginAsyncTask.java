package com.waicung.wayfinding.webclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.waicung.wayfinding.config.ConfigHandler;
import com.waicung.wayfinding.models.AuthenNResponse;
import com.waicung.wayfinding.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * used in Version 1.1
 *  Authentication through remote "php" server
 *  Task of authenticating user for accessing route info
 *  and store the response if success
 *
 */
public class LoginAsyncTask extends AsyncTask<String,Void,String>{
    //The context of the database operation
    private Context context;
    private String api;
    String postData;
    ProgressDialog pd;
    String TAG = "LoginAsyncTask";

    public LoginAsyncTask(Context context) {
        this.context = context;
        ConfigHandler config = new ConfigHandler(context, "config");
        this.api = config.getApi(context.getString(R.string.api_authentication));

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setMessage("Loging in");
        pd.show();

    }

    @Override
    protected String doInBackground(String... params) {
        //check if success
        String username = params[0];
        String password = params[1];
        Log.i(TAG,"asking for authentication: " + "username: " + username +" password: " + password);
        try {
            postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                    "&password=" + URLEncoder.encode(password,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Make a HTTP post request for authentication and route information
        HttpRequestHandler HH = new HttpRequestHandler("POST",api,postData);
        String jsonString = HH.postRequest();
        //Convert the response to accordance object
        Gson gson = new Gson();
        try {
            AuthenNResponse response = gson.fromJson(jsonString, AuthenNResponse.class);
            String result;
            //Check if the authentication is success
            if (response != null && response.getSuccess()) {
                //store the response if it is a correct credential
                SharedPreferences sharedPref = context.getSharedPreferences(
                        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.commit();
                editor.putString(context.getString(R.string.preference_authenN_response), jsonString);
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putInt("status", response.getStatus());
                editor.commit();
                result = context.getString(R.string.login_success);
            } else if (response == null) {
                result = context.getString(R.string.connection_error);
            } else {
                result = context.getString(R.string.login_fail);
            }
            return result;
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        //do nothing
        return context.getString(R.string.connection_error);


    }

    @Override
    protected void onPostExecute(String result){
        if (pd != null)
        {
            pd.dismiss();
        }
        Toast toast = Toast.makeText(this.context,result,Toast.LENGTH_SHORT);
        toast.show();


    }



}