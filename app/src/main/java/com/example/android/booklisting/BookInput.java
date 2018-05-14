package com.example.android.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SearchView;

public class BookInput extends AppCompatActivity {

    private String query;
    private boolean isConnected;
    private NetworkInfo activeNetwork;
    ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_input);

        //Get a CM to check our network connection
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Set a listener for the search button
        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get contents of input box
                SearchView queryView = findViewById(R.id.editText);
                query = queryView.getQuery().toString();

                //Inform the user the button has been clicked
                Intent resultsIntent = new Intent(BookInput.this, ResultsActivity.class);
                resultsIntent.putExtra("query", query);

                //Check network connection just before starting results activity...
                activeNetwork = cm.getActiveNetworkInfo();
                isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    startActivity(resultsIntent);
                } else {
                    Utils.doNetworkToast(getBaseContext());
                }
            }
        });

        //Check for network connection. If we're not connected, do a toast.
        activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Utils.doNetworkToast(getBaseContext());
        }
    }
}
