package com.electricsheep.amora.runtracker.Modules;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by amora on 12/28/2015.
 */
//Singleton
public class RunManager {
    private static final String TAG = "RunManager";

    private static final String PREFS_FILE = "runs";
    private static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";

    public static final String ACTION_LOCATION =
            "com.electricsheep.runtracker.ACTION_LOCATION";

    private static RunManager        sRunManager;
    private        Context           mAppContext;
    private        LocationManager   mLocationManager;
    private        RunDatabaseHelper mHelper;
    private        SharedPreferences mPrefs;
    private        long              mCurrentRunId;

    public static RunManager get(Context c) {

        if (sRunManager == null) {
            sRunManager = new RunManager(c.getApplicationContext());
        }

        return sRunManager;
    }


    private RunManager(Context appContext) {
        mAppContext      = appContext;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mHelper          = new RunDatabaseHelper(mAppContext);
        mPrefs           = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentRunId    = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);

    }

    public Run startNewRun(){
        //Insert a run into the db
        Run run = insertRun();
        Log.d(TAG, "Starting new run. id= " + run.getId());
        //Start tracking
        startTrackingRun(run);
        return run;
    }

    public void startTrackingRun(Run run){
        //Keep the id
        mCurrentRunId = run.getId();
        //Store it in shared preferences
        mPrefs.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentRunId).commit();
        //start location updates
        startLocationUpdates();
    }

    public void stopRun(){
        stopLocationUpdates();
        mCurrentRunId = -1;
        mPrefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }

    private Run insertRun(){
        Run run = new Run();
        run.setId(mHelper.insertRun(run));
        return run;
    }

    public RunDatabaseHelper.RunCursor queryRuns(){
        return mHelper.queryRuns();
    }

    public Run getRun(long id){
        Run run = null;
        RunDatabaseHelper.RunCursor cursor = mHelper.queryRun(id);
        cursor.moveToFirst();
        //if you got a row, get a run
        if(!cursor.isAfterLast()){
            run = cursor.getRun();
        }
        cursor.close();
        return run;
    }

    public Location getLastLocationForRun(long runId){
        Location location = null;
        RunDatabaseHelper.LocationCursor cursor = mHelper.queryLastLocationForRun(runId);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            location = cursor.getLocation();
        }
        cursor.close();
        return location;
    }

    public RunDatabaseHelper.LocationCursor queryLocationsForRun(long runId){
        return mHelper.queryLocationsForRun(runId);
    }

    public boolean isTrackingRun(Run run){
        return (run != null && run.getId() == mCurrentRunId);
    }

    public void insertLocation(Location loc){
        if(mCurrentRunId != -1){
            Log.d(TAG, String.format("Inserting location to database at time=" + loc.getTime()));
            mHelper.insertLocation(mCurrentRunId, loc);
        }else{
            Log.e(TAG, "Location received with no tracking run; ignoring");
        }
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {

        String provider = LocationManager.GPS_PROVIDER;

        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if(lastKnown != null){
            //Reset the time to now
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }

        //Start updates from the location manager
        PendingIntent pi = getLocationPendingIntent(true);
        /*if (ActivityCompat.checkSelfPermission(mAppContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(mAppContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/

        try{

            mLocationManager.requestLocationUpdates(provider, 0, 0, pi);

        }catch(SecurityException se){
            Log.e(TAG, "Security exception!");

        }


    }
    private void broadcastLocation(Location location){
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }


    public void stopLocationUpdates(){
        PendingIntent pi = getLocationPendingIntent(false);
        if(pi != null){
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    public boolean isTrackingRun(){
        return getLocationPendingIntent(false) != null;
    }
}
