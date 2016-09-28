package com.example.h8672.harj4_2;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer = null;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // start playing audio from the resource
    public void resourceAudio(View view) {
        // release possible previous playing
        if (mediaPlayer != null) mediaPlayer.release();
        // create audio file from resource
        mediaPlayer = MediaPlayer.create(this,R.raw.test);
        // start playing
        mediaPlayer.start();
    }

    // start playing audio from sd card
    public void sdcardAudio(View view) {
        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = new MediaPlayer();

        try {
            // set data source of audio
            Uri myUri = Uri.parse("file:///sdcard/test.mp3");
            mediaPlayer.setDataSource(getApplicationContext(), myUri);
            // prepare
            mediaPlayer.prepare();
            // start
            mediaPlayer.start();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Cannot start audio!", Toast.LENGTH_SHORT).show();
        }
    }

    // start playing audio from internet
    public void internetAudio(View view) {
        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = new MediaPlayer();

        try {
            // set data source of audio
            Uri myUri = Uri.parse("http://ptm.fi/android/test.mp3");
            mediaPlayer.setDataSource(getApplicationContext(), myUri);
            // prepare
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Cannot start audio!", Toast.LENGTH_SHORT).show();
        }
    }

    // called when media player is ready
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }


    // stop audio
    public void stopAudio(View view) {
        if (mediaPlayer != null){
            position = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
    }

    // pause audio
    public void pauseAudio(View view) {
        if (mediaPlayer != null){
            position = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    // restart audio
    public void restartAudio(View view) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(position);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        // release media player
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
