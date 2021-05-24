package com.project.friendfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {
    Button submit;
    TextView register;
    EditText phone;

    List<NameValuePair> nameValuePairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submit = (Button) findViewById(R.id.submit);
        register = (TextView) findViewById(R.id.reg);
        phone = (EditText) findViewById(R.id.phone);

        SharedPreferences prefSchool =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String otp_state = prefSchool.getString("otp_state", "");
        if (otp_state != null && otp_state == " ") {
            if (otp_state.equalsIgnoreCase("checked")) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } else {

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (phone.getText() != null) {

                            PostToServer();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter phone number!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {

                    }
                }
            });
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, Registration.class);

                    startActivity(intent);
                }
            });
        }
    }



    private void PostToServer() {
        nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tag", "Login"));
        nameValuePairs.add(new BasicNameValuePair("phone", phone.getText().toString()));
        new BackTask().execute();
    }

    public class BackTask extends AsyncTask<Void, Void, Void> {
        HttpResponse response;
        HttpEntity entity;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
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
                    username = jsonObject.getString("name");
                    id = jsonObject.getString("id");
                    emailid = jsonObject.getString("email");
                   // otp = jsonObject.getString("otp");
                    phoneno = jsonObject.getString("phone");
                    Constants.phone=phoneno;
                    Constants.MEMID=id;
                    Constants.name=username;
                }
                // status=jsonObject.getString("status");
                Log.i("jkl", stat);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {

            if (stat.equals("success")) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                intent.putExtra("otp", otp);
                intent.putExtra("phone", phoneno);*/
                startActivity(intent);
                finish();
            } else
                Toast.makeText(getApplicationContext(), "Please register your phone number!", Toast.LENGTH_LONG).show();

        }
    }
}
