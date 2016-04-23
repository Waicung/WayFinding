package com.waicung.wayfinding;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * used in Version 1.1
 *  Authentication through remote "php" server
 *  Task of authenticating user for accessing route info
 *  and store the response if success
 *
 */
public class SigninAsyncTask extends AsyncTask<String,Void,String>{
    //The context of the database operation
    private Context context;
    private String api = "http://10.0.2.2:8080/wayfinding/authenticationAPI.php";
    String postData;

    public SigninAsyncTask(Context context) {
        this.context = context;

    }

    @Override
    protected String doInBackground(String... params) {
        //check if success
        System.out.println("asking for authentication: " + "username: " + params[0]+" password: " + params[1]);
        try {
            postData = "username=" + URLEncoder.encode(params[0], "UTF-8") +
                    "&password=" + URLEncoder.encode(params[1],"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpRequestHandler HH = new HttpRequestHandler("POST",api,postData);
        String jsonString = HH.postRequest();
        Gson gson = new Gson();
        AuthenNResponse response = gson.fromJson(jsonString,AuthenNResponse.class);
        String result;
        if(response!=null&&response.getSuccess()){
            //store user_id, route_id, start and end point
            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            //store the whole respnse
            editor.putString(context.getString(R.string.preference_authenN_response), response.getUser_id());
            editor.commit();
            result = "Success";
        }
        else{
            result = "Login failed";
        }
        //do nothing
        return result;


    }

    @Override
    protected void onPostExecute(String result){
        Toast toast = Toast.makeText(this.context,result,Toast.LENGTH_SHORT);
        toast.show();

    }



}