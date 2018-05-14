package com.example.android.booklisting;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

//Class for static methods.
public final class Utils {

    private final static String LOG_TAG = "Utils method";

    /**
     * master method to get book data.
     *
     * @param queryURL
     * @return
     */
    public static ArrayList<Book> fetchBookData(Context context, String queryURL) {
        Log.i(LOG_TAG, "fetchBookData");
        //ArrayList mBookList = new ArrayList<Book>();

        //Create a URL
        URL url = createUrl(queryURL);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHTTPRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }


        //Get the Books from the extractBooks method.
        Log.i(LOG_TAG, "Trying to get books");
        ArrayList mBooks;// = new ArrayList<Book>();
        mBooks = extractBooks(context, jsonResponse);
        return mBooks;
    }


    /**
     * @param url
     * @return
     * @throws IOException
     */
    private static String makeHTTPRequest(URL url) throws IOException {
        Log.i(LOG_TAG, "utils.makeHTTPRequest");
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL ", e);
        }
        return url;
    }

    public static ArrayList<Book> extractBooks(Context context, String jsonResponse) {

        // Create an empty ArrayList that we can start adding books to
        ArrayList<Book> downloadedBooks = new ArrayList<>();

        // Try to parse the JSON response. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        JSONArray bookArray;

        try {
            //Get the JSON object, passed as a string to this method.
            JSONObject mainObject = new JSONObject(jsonResponse);
            //Now the books (called items in the list)...
            bookArray = mainObject.getJSONArray("items");
        } catch (JSONException e) {
            //If that hasn't worked, we have no JSON list to work with, so we'll have to exit.
            Log.e(LOG_TAG, "Unable to load JSON Objects");
            return null;
        }

        //Loop through the items to get the earthquake data we need.
        for (int i = 0; i < bookArray.length(); i++) {

            try {
                JSONObject thisBookJSON = bookArray.getJSONObject(i);
                //volumeInfo is a JSON object within the "items" object.
                JSONObject volumeInfo = thisBookJSON.getJSONObject("volumeInfo");
                // Get details from the object.
                String title = volumeInfo.getString("title");

                //The authors are an array, so parse that.
                String authors[];
                try {
                    JSONArray authorsJSON = volumeInfo.getJSONArray("authors");
                    authors = new String[authorsJSON.length()];
                    for (int j = 0; j < authorsJSON.length(); j++) {
                        authors[j] = authorsJSON.getString(j);
                    }
                } catch (JSONException e) {
                    //If there are no authors found, write the same as the author.
                    authors = new String[1];
                    authors[0] = "No authors found";
                }

                //Get the home page URL for the book.
                String urlStr;
                urlStr = volumeInfo.getString("canonicalVolumeLink");
                URL url = createUrl(urlStr);

                //Time to get the thumbnail URL, which is part of the imageLinks object
                //Defualt URL if one doesn't exist...
                String thumbUrlStr;
                URL thumbURL = null;
                try {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    thumbUrlStr = imageLinks.getString("smallThumbnail");
                    thumbURL = createUrl(thumbUrlStr);
                } catch (JSONException e) {
                    Log.i(LOG_TAG, "Unable to find thumbnail URL" + e);
                }

                //Get the book thumbnail image.
                //Get the thumbnail picture using the method in Utils class.
                Drawable thumbImg = getThumbPic(context, thumbURL);

                //Create a book and add it to the lsit.
                Book thisBook = new Book(title, authors, url, thumbURL, thumbImg);
                downloadedBooks.add(thisBook);
                Log.i(LOG_TAG,"Found "+thisBook.getTitle());

            } catch (JSONException e) {
                // Log the error if there's an issue.  Continue with the iterations.
                Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
            }
        }
        // Return the list of books
        return downloadedBooks;
    }

    /**
     * Returns a thumbnail image.
     *
     * @param url
     * @return
     */
    public static Drawable getThumbPic(Context context, URL url) {
        //Method to return the book thumb from the URL.
        Drawable d;
        try {
            InputStream is = (InputStream) url.getContent();
            d = Drawable.createFromStream(is, "Thumb URL");
        } catch (NullPointerException e) {
            Log.i("Utils", "No thumbnail image found");
            //Return the book icon if no drawable is found.
            d = ContextCompat.getDrawable(context, R.drawable.no_book_img);
        } catch (IOException e) {
            Log.i("Utils", "No thumbnail image found");
            //Return the book icon if no drawable is found.
            d = ContextCompat.getDrawable(context, R.drawable.no_book_img);
        }
        return d;
    }

    //Show a "not connectected message"
    public static void doNetworkToast (Context context) {
        Toast.makeText(context, "App unavailable without network connection",
                Toast.LENGTH_LONG).show();
    }

}
