package com.example.android.booklisting;

import android.graphics.drawable.Drawable;

import java.net.URL;

//Class holding all the relevant info re the books we're listing.
public class Book {
    private String mTitle;  //Book title
    private String [] mAuthors; //Authors
    private URL mURL;    //URL of book's google web page.
    private URL mThumbURL;  //Thumbnail picture link.
    private Drawable mThumbImg; //Thumbnail picture.

    public Book(String title, String [] authors, URL url, URL thumbURL, Drawable thumbImg) {
        mTitle = title;
        mAuthors = authors;
        mURL = url;
        mThumbURL = thumbURL;
        mThumbImg = thumbImg;
    }

    public String getTitle() {
        return mTitle;
    }

    public String [] getAuthors() {
        return mAuthors;
    }

    public URL getURL() {
        return mURL;
    }

    public URL getmThumbURL() {
        return mThumbURL;
    }

    public Drawable getThumbImg() {
        return mThumbImg;
    }
}
