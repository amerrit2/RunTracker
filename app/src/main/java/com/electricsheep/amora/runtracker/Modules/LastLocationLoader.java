package com.electricsheep.amora.runtracker.Modules;

import android.content.Context;
import android.location.Location;

/**
 * Created by amora on 1/2/2016.
 */
public class LastLocationLoader extends DataLoader<Location> {
    private long mRunId;

    public LastLocationLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Location loadInBackground() {
        return RunManager.get(getContext()).getLastLocationForRun(mRunId);
    }
}
