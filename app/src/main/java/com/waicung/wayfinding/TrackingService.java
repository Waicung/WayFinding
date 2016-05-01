package com.waicung.wayfinding;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class TrackingService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double mCurrentLatitude;
    private double mCurrentLongitude;
    private DBOpenHelper db;
    private long interval = 10 * 1000;   // 10 seconds, in milliseconds
    private long fastestInterval = 1 * 1000;  // 1 second, in milliseconds
    private float minDisplacement;
    private long currentTime;
    private int currentStep;
    private final String TAG = "TrackingService";

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        TrackingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TrackingService.this;
        }
    }

    public TrackingService() {

    }
    @Override
    public void onCreate() {
        super.onCreate();
        db = new DBOpenHelper(getApplicationContext());
        //when the service is created
        interval = 10 * 1000;   // 10 seconds, in milliseconds
        fastestInterval = 1 * 1000;  // 1 second, in milliseconds
        minDisplacement = 0;

//         Check if has GPS
       /* LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }*/

        mGoogleApiClient = createGoogleApiClient();
        mLocationRequest = createLocationRequest();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mGoogleApiClient.connect();
        Log.i(TAG, "starting connection");
        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public IBinder onBind(Intent intent) {
        if (db.getData()!=null){
            db.newTable();
        }
        mGoogleApiClient.connect();
        Log.i(TAG, "starting connection");
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopSelf();
        return super.onUnbind(intent);
    }

    public GoogleApiClient createGoogleApiClient(){
        GoogleApiClient googleApiClient  = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        return googleApiClient;
    }

    public LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(interval)
                .setFastestInterval(fastestInterval)
                .setSmallestDisplacement(minDisplacement);
        return locationRequest;
    }

    protected void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationAvailability(LocationAvailability locationAvailability) {
                        Log.i(TAG," " + locationAvailability);
                    }
                },null);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        setStep(1);
    }

    private void handleNewLocation(Location location,String event) {
        if (location != null) {
            Log.i(TAG, "get a location update for step: " + currentStep);
            mCurrentLatitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
            currentTime = System.currentTimeMillis() / 1000;
            db.insertLocation(mCurrentLatitude, mCurrentLongitude, currentTime, currentStep, event);
        }
        else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult  .getErrorCode());

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location,"");
    }

    public void setStep(int step){
        Log.i(TAG, "setStep:" + currentStep);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        handleNewLocation(location, "");
        this.currentStep =  step;
    }

    public void setLog(int step, String event){
        Log.i(TAG, "setLog: "  + currentStep + " ," + event);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        handleNewLocation(location, event);
        this.currentStep = step;

    }

    public void setFeedback(String event){
        Log.i(TAG, "feedback for step: " + this.currentStep);

    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        stopSelf();
        super.onDestroy();
        Log.i(TAG,"Tracking Service is destroyed");
    }
}
