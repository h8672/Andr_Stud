package com.example.h8672.googledrivetestproj;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //findFileIntent();
        sendMessageIntent();
    }

    //Receive result codes
    final private static int MY_RESULT_FILES = 6543;

    /* Yleisiä MIME tyyppejä...
    image/png
    text/plain
    text/XML
    text/html
    audio/mpeg */

    //Gets file from sources
    private void findFileIntent(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //Accept any type of mime file
        intent.setType("*/*");
        // verify that the intent will resolve to an activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, MY_RESULT_FILES);
        }

    }

    //Send message to certain number...
    private void sendMessageIntent(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("sms://"));
        intent.putExtra("address", "+358440126100");
        intent.putExtra("sms_body", "Your message here");
        // verify that the intent will resolve to an activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //Receive activity results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == MY_RESULT_FILES){
            String result = data.getDataString();
            TextView tv = (TextView) findViewById(R.id.txtView);
            tv.setText(result);
        }
    }
}
