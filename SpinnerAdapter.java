package com.project.friendfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class SpinnerAdapter extends ArrayAdapter<SpinnerModel> {

    ArrayList<SpinnerModel>data;
    private Context ctx;
    public SpinnerAdapter(Context context, ArrayList<SpinnerModel>SpinnerModels) {
        super(context,  R.layout.custom_spinner_text, R.id.gpname, SpinnerModels);
        this.ctx = context;
        this.data=SpinnerModels;

    }

    public SpinnerModel getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.custom_spinner_text, parent, false);

        TextView textView = (TextView) row.findViewById(R.id.tvLanguage);
        textView.setText(data.get(position).getGpname());


        return row;
    }
}