package com.example.android.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.URL;
import java.util.List;


public class ResultsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static String LOG_TAG = "ResultsActivity";
    ListView listView;

    Book bookAdapter;

    private String query;
    String queryURL;
    //Maximum no of results to list
    int maxResults = 25;
    //Query prefix:
    String queryPrefix = "https://www.googleapis.com/books/v1/volumes?q=";
    //Next static part of query:
    String queryPart2 = "&maxResults=";


    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "Starting loader");
        return new BookLoader(this, queryURL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, final List<Book> books) {
        Log.i(LOG_TAG, "onLoadFinished");

        //Hide the progress bar
        ProgressBar mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        //If there are no books, bail out early.
        if (books == null) {
            //Check the network status to see whether that's the cause of the issue.
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Utils.doNetworkToast(getBaseContext());
                finish();
            } else {
                //If we are connected to the network, and no results have been returned, show an empty
                //list message.
                Toast.makeText(this, "No results... please check your search query and try again.",
                        Toast.LENGTH_LONG).show();
                //Finish this activity and return to the previous...
                finish();
            }
        }

        BookAdapter bookAdapter = new BookAdapter(getBaseContext(), books);

        ListView listView = findViewById(R.id.list);

        try {
            listView.setAdapter(bookAdapter);
        } catch (NullPointerException e) {
            //Catches the empty list case.  We can toast the error with a network or other message..
            e.printStackTrace();
            finish();
        }

        //Set a listener to open the google page for the chose book.
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Click", books.get(position).getTitle());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                URL clickedURL = books.get(position).getURL();
                //Weirdly, I seem to have to change the URL to a string and back again...
                intent.setData(Uri.parse(clickedURL.toString()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {

        Log.i(LOG_TAG, "onLoaderReset");
        listView.setAdapter(new BookAdapter(this, null));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        query = intent.getExtras().getString("query");

        Bundle extras = getIntent().getExtras();
        query = extras.getString("query");

        //Build the query URL:
        queryURL = queryPrefix + query + queryPart2 + maxResults;

        //Get a loader manager
        getLoaderManager().initLoader(0, null, this);
    }
}
