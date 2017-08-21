package com.example.android.booklistening;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
/**
 * Created by Santosh on 11-03-2017.
 */public class BookAdapter extends ArrayAdapter<data> {
    private Bitmap bitmap;
    public BookAdapter(Activity context, ArrayList<data> newlist) {
        super(context, 0, newlist);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.bookdata, parent, false);
        }
        data current = getItem(position);
        TextView title = (TextView) listView.findViewById(R.id.bookname);
        title.setText(current.getbookname());
        TextView authr = (TextView) listView.findViewById(R.id.author);
        authr.setText(current.getAuthorName());
        TextView descript = (TextView) listView.findViewById(R.id.price);
        descript.setText(current.getDescription());
        ImageView bookimage = (ImageView) listView.findViewById(R.id.bookkimage);
        Picasso.with(getContext()).load(current.getImgResourceId()).into(bookimage);

        return listView;
    }


        }




