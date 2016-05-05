package com.waicung.wayfinding;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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
public class MainActivity extends AppCompatActivity implements NotificationDialogFragment.NoticeDialogListener,
        DialogInterface.OnClickListener{
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
    private int last_step;
    private TrackingService tService;
    private boolean mBound = false;
    private final String TAG = "MainActivity";
    private ListView steps_listView;
    private int dialogTag = 0;// means finish dialog
    private FloatingActionButton fab;
    private TextView thanks_tv;

    private BroadcastReceiver stepReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            step = intent.getIntExtra("step" , 0)+1;
            int success = intent.getIntExtra("success" , 0);
            Log.i(TAG, "Step change to: " + step);
            if (step > last_step&&success!=2){
                showNotificationDialog(getString(R.string.dialog_finish));
                dialogTag = 0;
                //TODO record end time
            }
            else {
                switch (success) {
                    case 0:
                        String event = intent.getStringExtra("event");
                        tService.setLog(step, event);
                        displayInstruction(steps_listView);
                        break;
                    case 2:
                        //TODO open dialog
                        showFeedbackDialog();
                        break;
                    default:
                        Log.e(TAG,"success = "+ success);
                        tService.setStep(step);
                        displayInstruction(steps_listView);
                        break;

                }
            }
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
        thanks_tv = (TextView)findViewById(R.id.thanks_textView);
        //Set toolbar(Action Bar) and its layout
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO stop the tracking
                showNotificationDialog("Do you get lose?");
                dialogTag = 1;

            }
        });
        //Set ListView for direction steps display.
        steps_listView = (ListView)findViewById(R.id.steps_listView);
        show_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInstruction(steps_listView);
                //steps_listView.setSelection(0);
                startShow();
                show_button.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkUser()){
            if(autoLogin()) {
                this.status = checkStatus();
                String message;
                if (status == 1110) {
                    this.route_id = getRoute_id();
                    this.assignment_id = getAssignmentID();
                    setLastStep();
                    show_button.setVisibility(View.VISIBLE);
                    LocalBroadcastManager.getInstance(this).registerReceiver(stepReceiver,
                            new IntentFilter("Achieve"));
                } else if (status == 1000) {
                    this.route_id = getRoute_id();
                    //TODO
                    uploadDirection();
                } else {
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
                    Snackbar snackBar = Snackbar.make(findViewById(R.id.mainCoordinatorLayout), message, Snackbar.LENGTH_LONG);
                    snackBar.show();
                }
            }

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void setLastStep() {
        SharedPreferences sharePref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String auth = sharePref.getString(getString(R.string.preference_authenN_response), null);
        if(auth != null){
            Gson gson = new Gson();
            AuthenNResponse response = gson.fromJson(auth, AuthenNResponse.class);
            last_step = response.getSteps().size();
            Log.i(TAG, "getLastStep: " + last_step);
        }
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

    private boolean autoLogin(){
        SharedPreferences sharePref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String auth = sharePref.getString(getString(R.string.preference_authenN_response), null);
        Log.i(TAG, "autologin");
        String username = sharePref.getString("username", "wrong");
        String password = sharePref.getString("password", "wrong");
        try {
            String result = (String)new LoginAsyncTask(this).execute(username,password).get();
            if (result==getString(R.string.connection_error)){
                SharedPreferences.Editor editor = sharePref.edit();
                editor.clear();
                editor.commit();
            }
            else {return true;}
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
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
            Log.i(TAG, "instruction list size" + hide);
            while(hide>step+3){
                hide--;
                steps.remove(hide);
                Log.i(TAG, "delete" + hide);
            }*/
            CustomAdapter adapter = new CustomAdapter(MainActivity.this,steps, step);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            lv.setSelection(step-1);
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

    private void showNotificationDialog(String message) {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment noticeFragment = new NotificationDialogFragment();
        if (message!=null){
            Bundle bundle = new Bundle();
            bundle.putString("message", message);
            noticeFragment.setArguments(bundle);
        }
        noticeFragment.show(getSupportFragmentManager(), "Mission Complete");
    }

    private void showFeedbackDialog() {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment feedbackFragment = new FeedbackFragment();
        feedbackFragment.show(fm, "Feedback");
    }


    //TODO delete. for opening the database manager for debugging
    private  void openDbManager(){
        Intent dbmanager = new Intent(this,AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    /*@Override
    public void onFinish(int result) {
        if(result==0){
            Log.i(TAG,"No");
            step=step-1;
            tService.setStep(step);
        }
        if(result==1){
            tService.setStep(step);
            Log.i(TAG,"DONE");
            unbindService(serviceConn);
            UploadResult();
            if (dialogTag==1) {
                Snackbar.make(findViewById(R.id.mainCoordinatorLayout), "Get lose and Stop experiment", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }*/

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.i(TAG,"DONE");
        if (dialogTag==1) {
            tService.setLog(step,"get lose");
            Snackbar.make(findViewById(R.id.mainCoordinatorLayout), "Get lose and Stop experiment", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{
            tService.setStep(step);
        }
        unbindService(serviceConn);
        UploadResult();
        steps_listView.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        thanks_tv.setVisibility(View.VISIBLE);


    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if (dialogTag==0){
            step-=1;
        }
        Log.i(TAG,"No");
        tService.setStep(step);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.i(TAG,"feedback is working");
        String[] feedbacks = getResources().getStringArray(R.array.feedback_array);
        step-=1;
        tService.setLog(step, feedbacks[which]);
    }
}
