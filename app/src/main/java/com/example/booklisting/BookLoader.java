package com.example.booklisting;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.AsyncTaskLoader;
import android.util.Log;


import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    /** Tag for log messages */
    private static final String LOG_TAG = BookLoader.class.getName();

    /** Query URL */
    private final String mQuery;

    public BookLoader(@NonNull Context context, String query) {
        super(context);
        mQuery=query;
    }
    public void onStartLoading(){
        forceLoad();
        Log.d(LOG_TAG, "onStartLoading: ");
    }

    @Nullable
    @Override
    public List<Book> loadInBackground() {
        if(mQuery == null){
            return null;
        }
        List<Book> earthquakes = QueryUtils.fetchBookData(mQuery);
        Log.d(LOG_TAG, "loadInBackground: ");
        return earthquakes;

    }
}
