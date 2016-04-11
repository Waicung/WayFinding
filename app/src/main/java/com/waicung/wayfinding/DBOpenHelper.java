package com.waicung.wayfinding;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.MatrixCursor;

import java.util.ArrayList;

/**
 * Created by waicung on 30/03/2016.
 * a helper for sqlite database handling:
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    //database name
    public static final String DATABASE_NAME = "FindWay.db";
    //information of user table:(id,password,primary_user)
    //primary indicate if a user is the main user of the app, whose activity will be recorded
    public static final String USER_TABLE_NAME = "users";
    public static final String USER_ID = "id";
    public static final String USER_PASSWORD = "password";
    public static final String PRIMARY_USER = "primary_user";
    //create user table query
    private static final String USER_TABLE_CREATE =
            "CREATE TABLE " + USER_TABLE_NAME + " (" +
                    USER_ID + " TEXT PRIMARY KEY, " +
                    USER_PASSWORD + " TEXT NOT NULL," + PRIMARY_USER + " INTEGER);";

    DBOpenHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create user table
        db.execSQL(USER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop user table and create a new one
        db.execSQL("DROP TABLE IF EXITS" + USER_TABLE_NAME);
        onCreate(db);
    }

    //method for user record insertion
    public boolean insertUser (String user, String password, int key){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_ID, user);
        contentValues.put(USER_PASSWORD, password);
        contentValues.put(PRIMARY_USER,key);
        db.insert(USER_TABLE_NAME, null, contentValues);
        Log.v("Account Info", user +" " + password);
        return true;
    }

    //get record according to a value in specific column
    public Cursor getDataBy(String column, String key){
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
        if(column=="primary_user"){
            int x = Integer.parseInt(key);
            cursor = db.rawQuery("SELECT * FROM users where " + column + "=" + x, null);
        }
        else{
        cursor = db.rawQuery("SELECT * FROM users where " + column + "=" + key, null);}
        return cursor;

    }

    //Check if a user record is in the database
    public boolean checkIfUserExist(String user_name){
        Cursor cursor = this.getDataBy("id", "'"+user_name+"'");
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }


    //Update a user as the main user or should be regards as normal user
    public boolean asPrimary(String user_name,int key){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Update " + USER_TABLE_NAME +
                " SET " + PRIMARY_USER +" = " + key +
                " WHERE " + USER_ID + " = '" + user_name + "'";
        db.execSQL(query);
        return true;
    }

    //method for debugging purpose
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}