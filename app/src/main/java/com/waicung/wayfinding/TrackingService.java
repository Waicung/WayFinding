package com.waicung.wayfinding;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class TrackingService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double mCurrentLatitude;
    private double mCurrentLongitude;
    private DBOpenHelper DB;
    private long interval = 10 * 1000;   // 10 seconds, in milliseconds
    private long fastestInterval = 1 * 1000;  // 1 second, in milliseconds
    private float minDisplacement;
    private long currentTime;
    private int currentStep;

    public TrackingService() {

    }
    @Override
    public void onCreate() {
        super.onCreate();
        DB = new DBOpenHelper(getApplicationContext());
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
        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
                mGoogleApiClient,mLocationRequest,this);
    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    private void handleNewLocation(Location location) {

        mCurrentLatitude = location.getLatitude();
        mCurrentLongitude = location.getLongitude();
        currentTime = System.currentTimeMillis();
        DB.insertLocation(mCurrentLatitude, mCurrentLongitude,currentTime, currentStep);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    public void setStep(int step){
        this.currentStep = step;
    }

}
