package com.electricsheep.amora.runtracker.Activities;

import android.support.v4.app.Fragment;

import com.electricsheep.amora.runtracker.Fragments.RunListFragment;

/**
 * Created by amora on 12/29/2015.
 */
public class RunListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }
}
