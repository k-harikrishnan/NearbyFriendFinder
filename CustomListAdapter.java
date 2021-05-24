package com.project.friendfinder.location;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.friendfinder.Constants;
import com.project.friendfinder.MapsActivity;
import com.project.friendfinder.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    public Resources res;
    ListModel tempValues = null;

    List<NameValuePair> nameValuePairs;
    android.support.v7.app.AlertDialog.Builder alertDialog;
    Object p;
    View view;
    private Activity activity;
    private ArrayList<ListModel> data;
    private Context context;
    private Context clickcontext;


    //constructors
    public CustomListAdapter(Context context) {
        this.context = context;
    }

    public CustomListAdapter(Activity a, ArrayList<ListModel> d) {
        //Take passed values
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    //return arraylist size
    public int getCount() {

        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    //inflate ach list row
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null && data.size() > 0) {
            vi = inflater.inflate(R.layout.listrow, parent, false);
            holder = new ViewHolder();
            holder.gpname = (TextView) vi.findViewById(R.id.gpname);
            holder.status = (TextView) vi.findViewById(R.id.status);
            vi.setTag(holder);
            tempValues = null;
            tempValues = (ListModel) data.get(position);
            p = data.get(position);
            holder.gpname.setText(((ListModel) p).getName());
            holder.status.setText(((ListModel) p).getStatus());
            vi.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.status.getText().toString().equalsIgnoreCase("not joined")) {
                        showSettingsAlert("Join group", view.getContext(), ((ListModel) p).getId());

                    } else if (holder.status.getText().toString().equalsIgnoreCase("joined")) {
                        showSettingsAlert("Exit group", view.getContext(), ((ListModel) p).getId());

                    }
                }
            });
        }
        return vi;
    }

    //view holder for layout row
    public static class ViewHolder {
        public TextView gpname, status;
    }

    private void PostLocationToServer(String tagname, String id) {
        nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tag", tagname));
        nameValuePairs.add(new BasicNameValuePair("userid", Constants.MEMID));
        nameValuePairs.add(new BasicNameValuePair("gpid", id));

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
                    Intent intent = new Intent(clickcontext, MapsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    clickcontext.startActivity(intent);


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


    public void showSettingsAlert(final String message, final Context c, final String id) {
        alertDialog = new android.support.v7.app.AlertDialog.Builder(c);
        clickcontext = c;

        // Setting Dialog Title
        alertDialog
                .setTitle(message);

        // Setting Dialog Message
        alertDialog
                .setMessage("Do you want to " + message + "?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (message.equalsIgnoreCase("Join group")) {
                            PostLocationToServer("JoinGroup", id);
                        } else {
                            PostLocationToServer("UnJoinGroup", id);
                        }
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();

    }
}