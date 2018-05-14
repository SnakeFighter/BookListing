package com.example.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BookLoader extends AsyncTaskLoader {

    private static String LOG_TAG = "BookLoader";

    private String strings [];  //Holder for input strings to constructor.

    /**
     *
     * @param context
     * @param strings
     */
    public BookLoader(Context context, String... strings) {
        super(context);
        this.strings = strings;
    }

    @Override
    public List<Book> loadInBackground() {
        Log.i(LOG_TAG,"loadInBackground");
        //Load in background method to get books.
        ArrayList mBooks;
        mBooks = Utils.fetchBookData(getContext(), strings[0]);
        return mBooks;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG,"onStartLoading");
        //This line from the earthquake project!
        forceLoad();
    }


}
