package com.waicung.wayfinding;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

/* Authentication through remote php server
* By comparing the input with the record in remote database
* Update local database with the result of the authentication
* */
public class SigninAsyncTask extends AsyncTask<String,Void,String>{
    //The context of the database operation
    private Context context;


    public SigninAsyncTask(Context context) {
        this.context = context;

    }

    @Override
    protected String doInBackground(String... params) {
        //check if success
        System.out.println("username: " + params[0]+" password: " + params[1]);
        AuthenHelper AH = new AuthenHelper(params[0],params[1]);
        AuthenNResponse response = AH.authentication();
        System.out.print(response);
        String result;
        if(response!=null&&response.getSuccess()){
            //store user_id, route_id, start and end point
            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("user_id", response.getUser_id());
            editor.putString("route_id", response.getRoute_id());
            editor.putString("start_point", response.getStart().toString());
            editor.putString("end_point", response.getEnd().toString());
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