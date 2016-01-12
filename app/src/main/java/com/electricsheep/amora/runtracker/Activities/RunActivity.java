package com.electricsheep.amora.runtracker.Activities;

import android.support.v4.app.Fragment;

import com.electricsheep.amora.runtracker.Fragments.RunFragment;

public class RunActivity extends SingleFragmentActivity {

    public static final String EXTRA_RUN_ID =
            "com.electricsheep.runtracker.run_id";
    @Override
    protected Fragment createFragment() {

        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if(runId != -1){
            return RunFragment.newInstance(runId);
        }else{
            return new RunFragment();
        }
    }

}
