package com.waicung.wayfinding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Main activity of Wayfinding
 * Main feature:
 * - Route instruction display (ListView)
 * - Login support(In Action Bar)
 * - Start Experiment and Finish Experiment button
 * - Interact with user(instruction achieved and get lose)
 */
public class MainActivity extends AppCompatActivity {
    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the main activity layout
        setContentView(R.layout.activity_main);
        //Set toolbar(Action Bar) and its layout
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //Set ListView for direction steps display.
        ListView steps_listView = (ListView)findViewById(R.id.steps_listView);
        if(checkUser()){
            autoLogin();
            this.status = checkStatus();
            String message;
            if (status == 1110){
                displayInstruction(steps_listView);
            }
            else if (status == 1000){
                //TODO uploadDirection();
            }
            else {
                switch (status) {
                    case 0000:
                        message = "Please contact administrator";
                        break;
                    case 1100:
                        message = "Instructions are preparing";
                        break;
                    case 1111:
                        message = "All test finished";
                        break;
                    default:
                        message = "unknown error";
                        break;
                }
                Snackbar snackBar= Snackbar.make(findViewById(R.id.myCoordinatorLayout), message, Snackbar.LENGTH_LONG);
                snackBar.show();
            }

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Set menu item for login
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);
        //Check if a user exist and set actionbar icon accordingly
        MenuItem userIcon = menu.findItem(R.id.new_user);
        if(checkUser()){
            userIcon.setIcon(R.drawable.ic_user);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_user:
                //display user info is have existing user
                //otherwise present login activity
                if(checkUser()){showInfo();}
                else{newUser();}
                return true;
            case R.id.dbmanager:
                //for debugging purpose
                openDbManager();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Open the login screen
    private void newUser() {
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
    }

    //OPen the user info screen
    private void showInfo(){
        Intent intent = new Intent(this, UserInfoActivity.class);
        this.startActivity(intent);
    }

    //To check if the a primary user exist
    private boolean checkUser(){
        //check if there is a user name in sharedPreference file
        SharedPreferences sharePref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String auth = sharePref.getString(getString(R.string.preference_authenN_response), null);
        if(auth != null){
            return true;
        }
        else {
            return false;
        }
        //TODO old method to be deleted
/*        Cursor cursor;
        cursor = mydb.getDataBy("primary_user", "1");
        if(cursor.getCount()<=0){
                cursor.close();
            return false;}

        else{
            return true;
        }*/
    }

    private void autoLogin(){
        SharedPreferences sharePref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String auth = sharePref.getString(getString(R.string.preference_authenN_response), null);
        System.out.println("here is the sharedpreference: " + auth);
        String username = sharePref.getString("username", "wrong");
        String password = sharePref.getString("password", "wrong");
        new LoginAsyncTask(this).execute(username,password);
    }

    private int checkStatus(){
        int status;
        SharedPreferences sharePref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String auth = sharePref.getString(getString(R.string.preference_authenN_response), null);
        if(auth != null){
            System.out.println(auth);
            Gson gson = new Gson();
            AuthenNResponse response = gson.fromJson(auth, AuthenNResponse.class);
            status = response.getStatus();
            return status;
        }
        else{
            return 0;
        }
    }

    private void displayInstruction(ListView lv){
        Route route;
        try {
            route = (Route)new LoadRouteAsyncTask(this).execute(status).get();
            List<String> instructions = route.getInstruction();
            ArrayAdapter adapter = new ArrayAdapter<>(MainActivity.this,R.layout.list_item,R.id.tv_step,instructions);
            lv.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void uploadDirection() {
       /* try {
            Route route = (Route) new LoadRouteAsyncTask(this).execute(status).get();
            new UploadingAysncTask().execute(route);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
    }

    //retrieving Google direction API response and display it on ListView
    private void displaySteps(ListView lv) {
        ArrayList<String> steps;
        try{
            steps = (ArrayList<String>) new LoadRouteAsyncTask(this).execute().get();
            new SaveRouteAsyncTask().execute(getApplicationContext(),"testfile.txt",steps.toString());
        }
        catch (InterruptedException e){}
        catch (ExecutionException e){}

        try {
            String instructions = (String) new readRouteAsyncTask().execute(this,"testfile.txt").get();
            ArrayList ins_array = new ArrayList();
            ins_array.add(instructions);
            ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.list_item,R.id.tv_step,ins_array);
            lv.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    //for opening the database manager for debugging
    private  void openDbManager(){
        Intent dbmanager = new Intent(this,AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

//Check if a user exist and set actionbar icon accordingly

}
