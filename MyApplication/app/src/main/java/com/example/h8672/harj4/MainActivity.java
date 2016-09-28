package com.example.h8672.harj4;

import android.icu.text.IDNA;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button asyncTaskButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find our button and progressbar
        asyncTaskButton = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void asynctaskButtonClicked(View view){
        //Toast.makeText(this, "Button pressed!", Toast.LENGTH_SHORT);
        asyncTaskButton.setEnabled(false);
        //start a new thread by using AsyncTask
        //new MyTask().execute("a","b","c"); //Antaa parametrejä params muuttujalle joka on doInBackground metodissa
        new MyTask().execute();
    }

    //our own class is extending from the AsyncTask class

    private class MyTask extends AsyncTask<Void,Integer,Void>{

        @Override
        protected  void  onPreExecute(){
            //do something doInBackground thread is launched
            //you can modify UI from here
        }

        @Override
        protected Void doInBackground(Void... params) {
            //params[0]; //"a"
            for(int i = 1; i <= 100; i++){
                //we want to show this number on progressbar
                publishProgress(i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex){
                    //Dont Toast here!
                    Log.e("ASYNCTASKEXAMPLE", "Background thread is interrupted!");
                }
            }
            return null;
        }

        //here you can modify UI
        @Override
        protected void onProgressUpdate(Integer... params){ progressBar.setProgress(params[0]); }

        //called after doInBackground is finished
        @Override
        protected void onPostExecute(Void params){
            asyncTaskButton.setEnabled(true);
        }
    }

}
