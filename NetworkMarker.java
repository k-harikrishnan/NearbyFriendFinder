package com.project.friendfinder;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.oguzbabaoglu.fancymarkers.BitmapGenerator;
import com.oguzbabaoglu.fancymarkers.CustomMarker;


public class NetworkMarker extends CustomMarker implements ImageLoader.ImageListener {

    private static final String URL = "http://lorempixel.com/200/200?seed=";
    private static volatile int seed; // Bypass cache

    private LatLng position;
    private View view;
    private ImageView markerImage;
    private ImageView markerBackground;
    TextView name;
    String Url;
    private ImageLoader imageLoader;

    public NetworkMarker(Context context, LatLng position,String url,String v, ImageLoader imageLoader) {
        this.position = position;
         this.Url=url;
        view = LayoutInflater.from(context).inflate(R.layout.view_network_marker, null);
        markerImage = (ImageView) view.findViewById(R.id.marker_image);
        markerBackground = (ImageView) view.findViewById(R.id.marker_background);
        name = (TextView) view.findViewById(R.id.namev);
        name.setText(v);

        this.imageLoader = imageLoader;
    }

    @Override
    public void onAdd() {
        imageLoader.get(Url, this);
    }

    @Override
    public BitmapDescriptor getBitmapDescriptor() {
        return BitmapGenerator.fromView(view);
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public boolean onStateChange(boolean selected) {

        if (selected) {
            markerBackground.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

        } else {
            markerBackground.clearColorFilter();
        }

        return true;
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

        final Bitmap bitmap = response.getBitmap();

        // Set image and update view
        markerImage.setImageBitmap(bitmap);
        updateView();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // Ignore
    }
}