package com.waicung.wayfinding;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.waicung.wayfinding.models.AuthenNResponse;
import com.waicung.wayfinding.models.Route;
import com.waicung.wayfinding.webclient.LoginAsyncTask;
import com.waicung.wayfinding.webclient.UploadLocationAsyncTask;
import com.waicung.wayfinding.webclient.UploadRouteAysncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.waicung.wayfinding.models.*;
/**
 * Main activity of Wayfinding
 * Main feature:
 * - Route instruction display (ListView)
 * - Login support(In Action Bar)
 * - Start Experiment and Finish Experiment button
 * - Interact with user(instruction achieved and get lose)
 */
public class MainActivity extends AppCompatActivity {
    DBOpenHelper DB = new DBOpenHelper(this);
    ServiceConnection serviceConn;
    private int status;
    private String assignment_id;
    private int route_id;
    private PopupWindow startPopup;
    private LayoutInflater layoutinflater;
    private CoordinatorLayout mainLayout;
    private Button show_button;
    private int step = 1;
    private TrackingService tService;
    private boolean mBound = false;
    private final String TAG = "MainActivity";
    private ListView steps_listView;

    private BroadcastReceiver stepReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            step = intent.getIntExtra("step" , 0)+1;
            int success = intent.getIntExtra("success" , 0);
            Log.i(TAG, "Step change to: " + step);
            switch (success){
                case 0:
                    String event = intent.getStringExtra("event");
                    tService.setLog(step,event);
                    break;
                default:
                    tService.setStep(step);
                    break;

            }
            displayInstruction(steps_listView);
            //TODO pass to tracking service

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the main activity layout
        setContentView(R.layout.activity_main);
        show_button = (Button) findViewById(R.id.show_button);
        mainLayout = (CoordinatorLayout) findViewById(R.id.mainCoordinatorLayout);
        //Set toolbar(Action Bar) and its layout
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //TODO stop the tracking
                unbindService(serviceConn);
                UploadResult();

            }
        });
        //Set ListView for direction steps display.
        steps_listView = (ListView)findViewById(R.id.steps_listView);
        if(checkUser()){
            autoLogin();
            this.status = checkStatus();
            String message;
            if (status == 1110){
                this.route_id = getRoute_id();
                this.assignment_id = getAssignmentID();
                show_button.setVisibility(View.VISIBLE);
                LocalBroadcastManager.getInstance(this).registerReceiver(stepReceiver,
                        new IntentFilter("Achieve"));
            }
            else if (status == 1000){
                this.route_id = getRoute_id();
                //TODO
                uploadDirection();
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
                Snackbar snackBar= Snackbar.make(findViewById(R.id.mainCoordinatorLayout), message, Snackbar.LENGTH_LONG);
                snackBar.show();
            }

        }
        show_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInstruction(steps_listView);
                startShow();
                show_button.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
            }
        });

    }

    private int getRoute_id() {
        int route_id;
        SharedPreferences sharePref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String auth = sharePref.getString(getString(R.string.preference_authenN_response), null);
        if(auth != null){
            Log.i(TAG, "get route id");
            Gson gson = new Gson();
            AuthenNResponse response = gson.fromJson(auth, AuthenNResponse.class);
            route_id = Integer.parseInt(response.getRoute_id());
            return route_id;
        }
        else{
            return 0;
        }
    }

    private void UploadResult() {
        ArrayList<LocationRecord> locations;
        locations = DB.getData();
        new UploadLocationAsyncTask(this).execute(assignment_id,locations);
    }

    private void startShow() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int)Math.round(dm.widthPixels);
        int height = (int)Math.round(dm.heightPixels);
        layoutinflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutinflater.inflate(R.layout.start_popup,null);
        TextView start = (TextView)container.findViewById(R.id.start_button);
        startPopup = new PopupWindow(container, width, height, true);
        startPopup.showAtLocation(mainLayout, Gravity.NO_GRAVITY,0,0);

        start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO start location update service
                Intent trackingIntent = new Intent(MainActivity.this,TrackingService.class);
                //startService(trackingIntent);
                /** Defines callbacks for service binding, passed to bindService() */
                serviceConn = new ServiceConnection() {

                    @Override
                    public void onServiceConnected(ComponentName className,
                                                   IBinder service) {
                        // We've bound to LocalService, cast the IBinder and get LocalService instance
                        TrackingService.LocalBinder binder = (TrackingService.LocalBinder) service;
                        tService = binder.getService();
                        mBound = true;
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName arg0) {
                        mBound = false;
                    }
                };
                bindService(trackingIntent, serviceConn, Context.BIND_AUTO_CREATE);
                startPopup.dismiss();
                return false;
            }
        });

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
        Log.i(TAG, "autologin");
        String username = sharePref.getString("username", "wrong");
        String password = sharePref.getString("password", "wrong");
        new LoginAsyncTask(this).execute(username,password);
    }

    private int checkStatus(){
        int status;
        SharedPreferences sharePref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String auth = sharePref.getString(getString(R.string.preference_authenN_response), null);
        if(auth != null){
            Gson gson = new Gson();
            AuthenNResponse response = gson.fromJson(auth, AuthenNResponse.class);
            status = response.getStatus();
            Log.i(TAG, "have status: " + status);
            return status;
        }
        else{
            return 0;
        }
    }

    private String getAssignmentID(){
        String assignment_id;
        SharedPreferences sharePref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String auth = sharePref.getString(getString(R.string.preference_authenN_response), null);
        if(auth != null){
            Log.i(TAG, "get assignment ID");
            Gson gson = new Gson();
            AuthenNResponse response = gson.fromJson(auth, AuthenNResponse.class);
            assignment_id = response.getAssignment_id();
            return assignment_id;
        }
        else{
            return "";
        }
    }

    private void displayInstruction(ListView lv){
        Route route;
        try {
            route = (Route)new LoadRouteAsyncTask(this).execute(status).get();
            ArrayList<Step> steps = route.getSteps();
            /*int hide = steps.size();
            while(hide>step){
                hide--;
                steps.remove(hide);
                Log.i(TAG, "delete" + hide);
            }*/
            CustomAdapter adapter = new CustomAdapter(MainActivity.this,steps, step);
            lv.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        DB.newRecord();

    }
    
    public void setStep(int step){
        this.step = step;
    }

    private void uploadDirection() {
        //require Direction from Google
        try {
            Route route = (Route)new LoadRouteAsyncTask(this).execute(status).get();
            new UploadRouteAysncTask(this).execute(route_id, route);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    //TODO delete. for opening the database manager for debugging
    private  void openDbManager(){
        Intent dbmanager = new Intent(this,AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


}
