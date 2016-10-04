package com.example.h8672.mp3player;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView listView;
    private String mediaPath;
    private List<String> songs = new ArrayList<String>();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private LoadSongsTask task;
    private String directoryIs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        if(Environment.MEDIA_MOUNTED.isEmpty()){
            mediaPath = Environment.getExternalStorageDirectory().getPath() + "/Music/";
        }else mediaPath = Environment.DIRECTORY_MUSIC;
        directoryIs = "External directory " + mediaPath + " Contains: " + Environment.getExternalStorageDirectory().getAbsolutePath();

        // item listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try
                {
                    mediaPlayer.reset();
                    // in recursive version
                    mediaPlayer.setDataSource(songs.get(position));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
                catch (IOException e) {
                    Toast.makeText(getBaseContext(),"Cannot start audio!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        task = new LoadSongsTask();
        task.execute();
    }

    private class LoadSongsTask extends AsyncTask<Void, String, Void>{
        private List<String> loadedSongs = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            updateSongsListRecursive(new File(mediaPath));
            return null;
        }

        public void updateSongsListRecursive(File path){
            if(path.isDirectory()){
                for(int i = 0; i < path.listFiles().length; i++){
                    File file = path.listFiles()[i];
                    updateSongsListRecursive(file);
                }
            } else{
                String name = path.getAbsolutePath();
                publishProgress(name);
                if(name.endsWith(".mp3")){
                    loadedSongs.add(name);
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayAdapter<String> songList =
                    new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1, loadedSongs);
            listView.setAdapter(songList);
            songs = loadedSongs;
            Toast.makeText(getApplicationContext(), "Songs=" + songs.size() + " from " + directoryIs, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer.isPlaying()) mediaPlayer.reset();
    }
}
