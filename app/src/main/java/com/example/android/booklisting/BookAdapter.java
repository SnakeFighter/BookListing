package com.example.android.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BookAdapter extends ArrayAdapter {

    public BookAdapter(@NonNull Context context, List objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Check if the exisitng view is being reused, otherwise inflate the view.
        View bookItemView = convertView;
        if (bookItemView == null) {
            bookItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Book currentBook = (Book) getItem(position);

        //Display the title
        TextView titleView = bookItemView.findViewById(R.id.book_title);
        titleView.setText(currentBook.getTitle());

        //And the author
        //First deal with the mulitple author case:
        String authors = currentBook.getAuthors()[0];
        if (currentBook.getAuthors().length > 1) {
            authors = authors + " et al";
        } else if
        //Deal with a book with no authors.
         (currentBook.getAuthors().length == 0 || authors == null) {
            authors = "No author found";
        }
        TextView authorView = bookItemView.findViewById(R.id.book_author);
        authorView.setText(authors);

        //Set the thumbnail image.
        ImageView thumbView = bookItemView.findViewById(R.id.thumb_img);
        thumbView.setImageDrawable(currentBook.getThumbImg());

        //Return the book view.
        return bookItemView;
    }
}
