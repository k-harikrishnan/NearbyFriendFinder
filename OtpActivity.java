//package com.project.friendfinder;
/*
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class OtpActivity extends AppCompatActivity {
    String otp = "",phoneno;
    EditText otp_text;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        otp_text = (EditText) findViewById(R.id.otptext);
        submit = (Button) findViewById(R.id.submit);
        try {
            if (getIntent() != null) {
                Intent in = getIntent();
                otp = in.getStringExtra("otp");
                phoneno = in.getStringExtra("phone");
                Log.i("OTP", "-->"+ otp );
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("OTP", otp+ "-->" +otp_text.getText().toString());
                if (otp_text.getText() != null) {
                    if(otp.equalsIgnoreCase(otp_text.getText().toString())){
                        SharedPreferences pref =
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor prefEdit = pref.edit();
                        prefEdit.putString("otp_state","checked");
                        prefEdit.putString("phone_no",phoneno);
                        prefEdit.apply();
                        Intent intent=new Intent(OtpActivity.this,MapsActivity.class);
                        startActivity(intent);
                    }
                } else {

                }
            }
        });

    }
}
*/