package com.example.android.booklistening;

/**
 * Created by Santosh on 11-03-2017.
 */

public class data {

    private String bookName;
    private String authorName;
    private String description;
    private String imgResourceId;

    data(String y, String z, String m, String s) {
        bookName = y;
        authorName = z;
        description = m;
        imgResourceId = s;
    }

    public String getbookname() {
        return bookName;
    }

    public String getImgResourceId() {
        return imgResourceId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getDescription() {
        return description;
    }
}
