package com.electricsheep.amora.runtracker.Modules;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by amora on 1/2/2016.
 */
public abstract class SQLiteCursorLoader extends AsyncTaskLoader {

    private Cursor mCursor;

    public SQLiteCursorLoader(Context context) {
        super(context);
    }

    protected abstract Cursor loadCursor();

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = loadCursor();
        if(cursor != null){
            //Ensure tha the content window is filled
            cursor.getCount();
        }
        return cursor;
    }


    public void deliverResult(Cursor data) {
        Cursor oldCursor = mCursor;
        mCursor = data;

        if(isStarted()){
            super.deliverResult(data);
        }

        if(oldCursor != null && oldCursor != data && !oldCursor.isClosed()){
            oldCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if(mCursor != null){
            deliverResult(mCursor);
        }
        if(takeContentChanged() || mCursor == null){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        //Attempt to cancel the current load task if possible
        cancelLoad();
    }

    public void onCanceled(Cursor cursor) {
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        //Ensure that the loader is stopped
        onStopLoading();

        if(mCursor != null && !mCursor.isClosed()){
            mCursor.close();
        }
        mCursor = null;

    }
}
