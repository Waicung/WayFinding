package com.waicung.wayfinding;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    DBOpenHelper mydb = new DBOpenHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setting toolbar and its layout
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //setting ListView to display direction steps.
        ListView lv = (ListView)findViewById(R.id.instructions);
        displaySteps(lv);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);
        //Check if a user exist and set actionbar icon accordingly
        MenuItem userIcon = menu.findItem(R.id.new_user);
        if(checkUser()){userIcon.setIcon(R.drawable.ic_user);}
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
        Cursor cursor;
        cursor = mydb.getDataBy("primary_user", "1");
        if(cursor.getCount()<=0){
                cursor.close();
            return false;}

        else{
            return true;
        }
    }

    //retrieving Google direction API response and display it on ListView
    private void displaySteps(ListView lv) {
        ArrayList<String> steps;
        try{
            steps = (ArrayList<String>) new LoadStepAsyncTask().execute().get();
        ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.list_item,R.id.tv_step,steps);
        lv.setAdapter(adapter);}
        catch (InterruptedException e){}
        catch (ExecutionException e){}
    }

    //for opening the database manager for debugging
    private  void openDbManager(){
        Intent dbmanager = new Intent(this,AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

//Check if a user exist and set actionbar icon accordingly

}
