package fi.jamk.imageinternet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView img;
    private TextView txt;
    private ProgressBar progbar;

    private String[] images = {
            "http://student.labranet.jamk.fi/~H8672/kuvat/logo.jpg",
            "http://student.labranet.jamk.fi/~H8672/kuvat/shakki/King.png",
            "http://student.labranet.jamk.fi/~H8672/kuvat/shakki/Torni.png"
    };
    private int imgIndex;
    private DownloadImageTask task;
    private float x1, x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.imageview);
        txt = (TextView) findViewById(R.id.textview);
        progbar = (ProgressBar) findViewById(R.id.progressview);
        imgIndex = 0;
        showImage();
    }

    private void showImage(){
        task = new DownloadImageTask();
        task.execute(images[imgIndex]);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected void onPreExecute() {
            //show proggressbar
            progbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL imageURL;
            Bitmap bitmap = null;
            try{
                imageURL = new URL(urls[0]);
                InputStream in = imageURL.openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception ex){
                Log.e("<<loadimage>>", ex.getMessage());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            img.setImageBitmap(bitmap);
            txt.setText("Image " + (imgIndex + 1) + "/" + images.length);
            //hide proggressbar
            progbar.setVisibility(View.INVISIBLE);
        }
    }

    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                if(x1 < x2){
                    imgIndex--;
                    if(imgIndex < 0) imgIndex = images.length - 1;
                } else{
                    imgIndex++;
                    if(imgIndex > (images.length - 1)) imgIndex = 0;
                }
                showImage();
                break;
        }
        return false;
    }
}
