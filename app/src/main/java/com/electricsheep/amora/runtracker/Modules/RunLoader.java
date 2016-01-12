package com.electricsheep.amora.runtracker.Modules;

import android.content.Context;

/**
 * Created by amora on 1/2/2016.
 */
public class RunLoader extends DataLoader<Run> {

    private long mRunId;

    public RunLoader(Context context, long mRunId) {
        super(context);
        this.mRunId = mRunId;
    }

    @Override
    public Run loadInBackground() {
        return RunManager.get(getContext()).getRun(mRunId);
    }
}
