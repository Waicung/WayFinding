package com.waicung.wayfinding;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.MatrixCursor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import com.waicung.wayfinding.models.LocationRecord;

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
    public static final String LOCATION_TABLE_NAME = "locations";
    public static final String LOCATION_TIMESTAMP = "time_stamp";
    public static final String LOCATION_LAT = "latitude";
    public static final String LOCATION_LNG = "longitude";
    public static final String LOCATION_STEP = "step";
    //create user table query
    private static final String LOCATION_TABLE_CREATE =
            "CREATE TABLE " + LOCATION_TABLE_NAME + " (" +
                    "id" + " INTEGER PRIMARY KEY," +
                    LOCATION_TIMESTAMP + " Long NOT NULL," +
                    LOCATION_LAT + " DOUBLE NOT NULL," +
                    LOCATION_LNG + " DOUBLE NOT NULL," +
                    LOCATION_STEP + " INT NOT NULL);";

    DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create a new table for each task
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
        db.execSQL(LOCATION_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop user table and create a new one
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
        onCreate(db);
    }

    public void newRecord(){
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }

    //method for user record insertion
    public boolean insertLocation (Double lagitude, Double longitude, long time, int step){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCATION_LAT, lagitude);
        contentValues.put(LOCATION_LNG, longitude);
        contentValues.put(LOCATION_TIMESTAMP, time);
        contentValues.put(LOCATION_STEP, step);
        db.insert(LOCATION_TABLE_NAME, null, contentValues);
        return true;
    }

    //return all the record
    public ArrayList<LocationRecord> getData(){
        ArrayList<LocationRecord> locations = new ArrayList<>();
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + LOCATION_TABLE_NAME + " LIMIT 10", null);
        while (cursor.moveToNext()){
            double lat = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(DBOpenHelper.LOCATION_LAT)
            );
            double lng = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(DBOpenHelper.LOCATION_LNG)
            );
            long time = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DBOpenHelper.LOCATION_TIMESTAMP)
            );
            int step_number = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DBOpenHelper.LOCATION_STEP)
            );
            LocationRecord location = new LocationRecord(lat,lng,time,step_number);
            locations.add(location);
        }
        return locations;

    }


    //TODO delete. method for debugging purpose
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