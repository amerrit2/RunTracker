package com.electricsheep.amora.runtracker.Fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.electricsheep.amora.runtracker.Activities.RunActivity;
import com.electricsheep.amora.runtracker.Modules.Run;
import com.electricsheep.amora.runtracker.Modules.RunDatabaseHelper;
import com.electricsheep.amora.runtracker.Modules.RunManager;
import com.electricsheep.amora.runtracker.Modules.SQLiteCursorLoader;
import com.electricsheep.amora.runtracker.R;

/**
 * Created by amora on 12/29/2015.
 */

public class RunListFragment extends ListFragment implements LoaderManager.LoaderCallbacks {

    private static final int REQUEST_NEW_RUN = 0;

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new RunListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        //Create an adapter to point at this cursor
        RunCursorAdapter adapter = new RunCursorAdapter(getActivity(),
                (RunDatabaseHelper.RunCursor) data);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //Stop using the cursor via the adapter
        setListAdapter(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //Initialize the loader to load the list of runs
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_options, menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //The id argument will be the Run ID:; CursorAdapter gives us this for free
        Intent i = new Intent(getActivity(), RunActivity.class);
        i.putExtra(RunActivity.EXTRA_RUN_ID, id);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_new_run:
                Intent i = new Intent(getActivity(), RunActivity.class);
                startActivityForResult(i, REQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_NEW_RUN == requestCode){
            //Restart the loader to get any new run available
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    private static class RunListCursorLoader extends SQLiteCursorLoader {

        public RunListCursorLoader(Context context) {
            super(context);
        }

        @Override
        protected Cursor loadCursor() {
            //Query the list of runs
            return RunManager.get(getContext()).queryRuns();
        }
    }

    private class RunCursorAdapter extends CursorAdapter{

        private RunDatabaseHelper.RunCursor mCursor;

        public RunCursorAdapter(Context context, RunDatabaseHelper.RunCursor c) {
            super(context, c, 0);
            mCursor = c;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //Use a layout inflater to get a row view
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //Get the run for the current row
            Run run = mCursor.getRun();

            //Set up the start date text view
            TextView startDateTextView = (TextView) view;
            String cellText =
                    context.getString(R.string.cell_text, run.getStartDate());

            if(RunManager.get(getContext()).isTrackingRun(run)){
                cellText = "** " + cellText;
            }
            startDateTextView.setText(cellText);

        }
    }
}
