package com.electricsheep.amora.runtracker.Activities;

import android.support.v4.app.Fragment;

import com.electricsheep.amora.runtracker.Fragments.RunMapFragment;

/**
 * Created by amora on 1/2/2016.
 */
public class RunMapActivity extends SingleFragmentActivity {
    /** a key for passing a run ID as a long */
    public static final String EXTRA_RUN_ID =
            "com.electicsheep.amora.runtracker.run_id";

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (runId != -1){
            return RunMapFragment.newInstance(runId);
        }else{
            return new RunMapFragment();
        }
    }
}
