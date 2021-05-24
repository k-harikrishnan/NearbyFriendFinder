package com.project.friendfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class IpSettingsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_settings_page);
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipadr=((EditText)findViewById(R.id.ipfl)).getText().toString();
                Constants.URL=ipadr;
                Log.i("Tag",Constants.URL+"URl"+ipadr);
                Intent inten=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(inten);
            }
        });
    }
}
