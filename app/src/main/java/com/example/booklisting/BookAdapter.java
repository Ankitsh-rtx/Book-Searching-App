package com.example.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {


    public BookAdapter(@NonNull Context context, List<Book> resource) {
        super(context,0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.book, parent, false);
        }
        Book currentBook = getItem(position);

        TextView titleTextView = listItemView.findViewById(R.id.title);
        titleTextView.setText(currentBook.getmTitle());

        TextView authorTextView = listItemView.findViewById(R.id.authors);
        authorTextView.setText(currentBook.getmAuthors());

        TextView dateTextView = listItemView.findViewById(R.id.date);
        dateTextView.setText(currentBook.getmDate());

        ImageView imageLink = listItemView.findViewById(R.id.imageView);
        try
        {
            URL imageurl = new URL(currentBook.getmImagelink());
            Glide.with(getContext()).load(imageurl).into(imageLink);

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return listItemView;
    }

}
