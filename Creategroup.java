package com.project.friendfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class Creategroup extends AppCompatActivity {
EditText name;
Button button;
List<NameValuePair> nameValuePairs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroup);
        name=(EditText)findViewById(R.id.name);
        button=(Button)findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            PostToServer();
            }
        });
    }
    private void PostToServer() {
        nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tag", "Newgroup"));
        nameValuePairs.add(new BasicNameValuePair("gpname", name.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("memid", Constants.MEMID));
        new BackTask().execute();
    }

    public class BackTask extends AsyncTask<Void, Void, Void> {
        HttpResponse response;
        HttpEntity entity;

        String result = "";
        JSONObject jsonObject;

        String res, stat;

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
                // status=jsonObject.getString("status");
                Log.i("jkl", stat);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {

            if (stat.equals("success")) {
                // editor.putString("status","true");
                //editor.putString("phno",""+phno);
                // editor.commit();
                Toast.makeText(getApplicationContext(), "group Created ", Toast.LENGTH_LONG).show();
                name.setText("");

            } else if (stat.equals("error")) {
                Toast.makeText(getApplicationContext(), "GROUP NOT CREATED!", Toast.LENGTH_LONG).show();

            }
        }
    }
}
