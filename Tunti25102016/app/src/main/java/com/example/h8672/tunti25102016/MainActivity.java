package com.example.h8672.tunti25102016;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static int styleID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(styleID);
        setContentView(R.layout.activity_main);
    }

    void buttonPress(View view){
        switch (view.getId()){
            case R.id.button:
                styleID = R.style.AppTheme;
                break;
            case R.id.button2:
                styleID = R.style.AppTheme2;
                break;
            case R.id.button3:
                styleID = R.style.AppTheme3;
                break;
            default:
                break;
        }
        restartThisView();
    }

    private void restartThisView(){
        this.finish();
        this.startActivity(new Intent(this, this.getClass()));
    }
}
