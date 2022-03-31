package com.example.booklisting;

import androidx.annotation.NonNull;

public class Book {
    private final String mTitle;
    private final String mAuthors;
    private final String mDate;
    private final String mUrl;
    private final String mImagelink;
    public Book(String title, String authors, String date, String url, String image){
        mTitle = title;
        mAuthors = authors;
        mDate = date;
        mUrl = url;
        mImagelink = image;
    }
    public String getmTitle(){
        return mTitle;
    }

    public String getmAuthors(){
        return mAuthors;
    }

    public String getmDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getmImagelink() {
        return mImagelink;
    }

    @NonNull
    @Override
    public String toString() {
        return "Book{" +
                "Title='" + mTitle + '\'' +
                ", Author='" + mAuthors + '\'' +
                ", Date=" + mDate +
                '}';
    }
}
