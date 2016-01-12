package com.electricsheep.amora.runtracker.Modules;

import android.content.Context;
import android.location.Location;

/**
 * Created by amora on 12/29/2015.
 */
public class TrackingLocationReceiver extends LocationReceiver {

    @Override
    protected void onLocationReceived(Context context, Location loc) {
        RunManager.get(context).insertLocation(loc);
    }
}
