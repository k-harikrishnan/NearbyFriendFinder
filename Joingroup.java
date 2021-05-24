package com.project.friendfinder;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Joingroup extends AppCompatActivity {
    ArrayList<ListModel> data;
    List<NameValuePair> nameValuePairs;
      ListView listv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joingroup);
        listv=(ListView)findViewById(R.id.list1);
        PostToServer();

    }


    private void PostToServer() {
        nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tag", "ListUnJoined"));
        nameValuePairs.add(new BasicNameValuePair("userid",Constants.MEMID));

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
                data=new ArrayList<>();

                JSONArray json=new JSONArray(result);
                for(int i=0;i<json.length();i++){
                    ListModel listModel = null;
                    jsonObject = json.getJSONObject(i);
                stat = jsonObject.getString("status");
                if (stat.equals("success")) {
                    listModel=new ListModel();

                    listModel.setName(jsonObject.getString("groupname"));
                    listModel.setId(jsonObject.getString("id")) ;
                    listModel.setStatus(jsonObject.getString("Joinstatus"));
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

            CustomListAdapter cus=new CustomListAdapter(Joingroup.this,data);
            listv.setAdapter(cus);

        }
    }

 }
