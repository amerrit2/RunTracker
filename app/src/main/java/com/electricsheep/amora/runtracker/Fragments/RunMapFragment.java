package com.electricsheep.amora.runtracker.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by amora on 1/2/2016.
 */
public class RunMapFragment extends SupportMapFragment {

    private static final String ARG_RUN_ID = "RUN_ID";
    private static final String TAG = "RunMapFragment";

    private GoogleMap mGoogleMap;

    public static RunMapFragment newInstance(long runId) {

        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        Log.e(TAG, "Creating new map with id: " + runId);
        RunMapFragment rf = new RunMapFragment();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        //Stash a reference to the GoogleMap
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;

                mGoogleMap.setMyLocationEnabled(true);
            }
        });


        return v;
    }
}


