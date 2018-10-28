package com.example.ahmed.egytour.activity;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahmed.egytour.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> countryList;
    ArrayList<Integer> logos;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, ArrayList<String> countryList, ArrayList<Integer> logos) {
        this.context = context;
        this.countryList = countryList;
        this.logos = logos;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_listview, null);
        TextView country = (TextView) view.findViewById(R.id.textView);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        country.setText(countryList.get(i));
        icon.setImageResource(logos.get(i));
        return view;
    }}