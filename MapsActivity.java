package com.project.friendfinder;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oguzbabaoglu.fancymarkers.MarkerManager;
import com.project.friendfinder.location.CustomListAdapter;
import com.project.friendfinder.location.ListModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;
import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnLocationUpdatedListener, OnActivityUpdatedListener {
    static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    NetworkMarker networkMarker;
    private MarkerManager<NetworkMarker> networkMarkerManager;
    private static ImageLoader imageLoader;
    private GoogleMap mMap;
    //private String ImageUrl = "https://s3.amazonaws.com/uifaces/faces/twitter/jsa/128.jpg";
    private String ImageUrl = "http://192.168.1.3:8000/Friendfinder/img/pic.jpg";
    SpinnerAdapter spinnerAdapter;
    Spinner sp;
    Context ctx;
    MemberLocations models;
    private View mCustomMarkerView;
    Bitmap bitmap1;
    private ImageView mMarkerImageView;
    int x1;
    List<NameValuePair> nameValuePairs;
    static double latitude = 0;
    static double longitude = 0;
    ArrayList<SpinnerModel> data;
    ArrayList<MemberLocations> dataLoc;
    ArrayList<MemberLocations> dataLoc1;
    LocationTracker tracker;
    private LocationGooglePlayServicesProvider provider;

    private static final int LOCATION_PERMISSION_ID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sp = (Spinner) findViewById(R.id.spinner);
        android.support.v7.app.ActionBar ab = getSupportActionBar();

        ab.setTitle(Constants.name);
        ab.setSubtitle(Constants.phone);
        ab.setLogo(R.drawable.dp);
        mCustomMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.profile_image);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);
        ctx = MapsActivity.this;
        PostToServer();
        if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You need to ask the user to enable the permissions
            final List<String> permissionsList = new ArrayList<String>();

            permissionsList.add("android.permission.ACCESS_FINE_LOCATION");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                ActivityCompat.requestPermissions(MapsActivity.this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
                return;

            }
        } else {
            startLoc();

        }
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                networkMarkerManager.clear();
                String id = data.get(i).getId();
                PostToServerShortest(id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void startLoc() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }
        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location(provider).start(this);


    }

    private void stopLocation() {
        SmartLocation.with(this).location().stop();

        SmartLocation.with(this).activity().stop();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create) {
            Intent intent = new Intent(getApplicationContext(), Creategroup.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.action_join) {

            Intent intent = new Intent(getApplicationContext(), Joingroup.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.action_Joined) {

            Intent intent = new Intent(getApplicationContext(), Joinedgroup.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        networkMarkerManager = new MarkerManager<>(googleMap);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                9.15, 76.72), 12.0f));
        if (imageLoader == null) {

            final RequestQueue queue = Volley.newRequestQueue(this);
            imageLoader = new ImageLoader(queue, new NoImageCache());
        }
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {

    }

    @Override
    public void onLocationUpdated(Location location) {
        Log.i("TAG","lat"+location.getLatitude()+location.getLongitude());

          latitude=location.getLatitude();
        longitude=location.getLongitude();
         PostLocationToServer();
    }

    private static class NoImageCache implements ImageLoader.ImageCache {

        @Override
        public Bitmap getBitmap(String url) {
            return null;
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            // Do nothing
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startLoc();
                } else {
                    // Permission Denied
                    Toast.makeText(MapsActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void PostToServer() {
        nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tag", "GetGroups"));
        nameValuePairs.add(new BasicNameValuePair("userid", Constants.MEMID));

        new BackTask().execute();
    }

    public class BackTask extends AsyncTask<Void, Void, Void> {
        HttpResponse response;
        HttpEntity entity;
        String result = "";
        JSONObject jsonObject;

        String id, username, phoneno, stat, emailid, otp;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            InputStream is = null;


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constants.URL);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(httpPost);
                entity = response.getEntity();
                is = entity.getContent();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                is.close();
                Log.i("RESPONSE", "-->" + result.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // parse json data
            try {
                data = new ArrayList<>();

                JSONArray json = new JSONArray(result);
                for (int i = 0; i < json.length(); i++) {
                    SpinnerModel listModel = null;
                    jsonObject = json.getJSONObject(i);
                    stat = jsonObject.getString("status");
                    if (stat.equals("success")) {
                        listModel = new SpinnerModel();

                        listModel.setGpname(jsonObject.getString("groupname"));
                        listModel.setId(jsonObject.getString("id"));
                        data.add(listModel);
                    }

                }
                // status=jsonObject.getString("status");
                Log.i("jkl", stat);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {

            spinnerAdapter = new SpinnerAdapter(getApplicationContext(), data);
            sp.setAdapter(spinnerAdapter);

        }
    }

    private void PostToServerShortest(String gpid) {
        nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tag", "NearestMem"));
        nameValuePairs.add(new BasicNameValuePair("userid", Constants.MEMID));
        nameValuePairs.add(new BasicNameValuePair("gpid", gpid));

        new BackTask1().execute();
    }

    private void PostLocationToServer() {
        nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tag", "UpdateLocation"));
        nameValuePairs.add(new BasicNameValuePair("userid", Constants.MEMID));
        nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
        nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));

        new Locationtask().execute();
    }

    public class Locationtask extends AsyncTask<Void, Void, Void> {
        HttpResponse response;
        HttpEntity entity;
        String result = "";
        JSONObject jsonObject;

        String id, username, phoneno, stat, emailid, otp;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            InputStream is = null;


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constants.URL);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(httpPost);
                entity = response.getEntity();
                is = entity.getContent();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                is.close();
                Log.i("RESPONSE", "-->" + result.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // parse json data
            try {


                jsonObject = new JSONObject(result);

                stat = jsonObject.getString("status");
                if (stat.equals("success")) {

                    Log.i("RESPONSErad", "-->");


                }
                // status=jsonObject.getString("status");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {


        }
    }


    public class BackTask1 extends AsyncTask<Void, Void, Void> {
        HttpResponse response;
        HttpEntity entity;
        String result = "";
        JSONObject jsonObject;

        String id, username, phoneno, stat, emailid, otp;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            InputStream is = null;


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constants.URL);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(httpPost);
                entity = response.getEntity();
                is = entity.getContent();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                is.close();
                Log.i("RESPONSE", "-->" + result.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // parse json data


            return null;
        }

        protected void onPostExecute(Void result1) {
            try {
                dataLoc = new ArrayList<>();
                dataLoc1 = new ArrayList<>();
                JSONArray json = new JSONArray(result);
                for (int i = 0; i < json.length(); i++) {
                    MemberLocations listModel = null;
                    jsonObject = json.getJSONObject(i);

                    stat = jsonObject.getString("status");
                    if (stat.equals("success")) {
                        double lon1 = 76.67847;
                        double lat1 = 9.225066;
                        double rad = distFrom(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"), latitude, longitude);
                        Log.i("RESPONSErad", "-->" + rad + latitude + longitude);
                        if (rad > 0 && rad < 1.5) {
                            Log.i("RESPONSE rad inside", "-->" + rad + latitude + longitude);
                            listModel = new MemberLocations();
                            listModel.setUserid(jsonObject.getString("id"));
                            listModel.setMemname(jsonObject.getString("name"));
                            listModel.setGpname(jsonObject.getString("groupname"));
                            listModel.setLatitude(jsonObject.getDouble("latitude"));
                            listModel.setLongitude(jsonObject.getDouble("longitude"));
                            listModel.setImgUrl(ImageUrl);
                            dataLoc.add(listModel);

                        }
                    }

                }
                // status=jsonObject.getString("status");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            networkMarkerManager.addMarkers(createNetworkMarkers());


        }
    }

    public double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0;//use 6371000 for getting distance in meter
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double ccc = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * ccc;

        return dist;
    }




    private ArrayList<NetworkMarker> createNetworkMarkers() {

        final ArrayList<NetworkMarker> networkMarkers = new ArrayList<>(dataLoc1.size());

        for (int i = 0; i < dataLoc.size(); i++) {
            networkMarkers.add(new NetworkMarker(this, new LatLng(dataLoc.get(i).getLatitude(), dataLoc.get(i).getLongitude()), dataLoc.get(i).getImgUrl(), dataLoc.get(i).getMemname(), imageLoader));
        }

        return networkMarkers;
    }

}

