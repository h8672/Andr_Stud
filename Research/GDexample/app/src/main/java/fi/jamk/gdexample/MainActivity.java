package fi.jamk.gdexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    /** DRIVE_OPEN Intent action. */
    private static final String ACTION_DRIVE_OPEN = "com.google.android.apps.drive.DRIVE_OPEN";
    /** Drive file ID key. */
    private static final String EXTRA_FILE_ID = "resourceId";

    /** Drive file ID. */
    private String mFileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        // Get the action that triggered the intent filter for this Activity
        final Intent intent = getIntent();
        final String action = intent.getAction();

        // Make sure the Action is DRIVE_OPEN.
        if (ACTION_DRIVE_OPEN.equals(action)) {
            // Get the Drive file ID.
            mFileId = intent.getStringExtra(EXTRA_FILE_ID);
            getUserAccountAndProcessFile();
        } else {
            // Unknown action.
            finish();
        }*/
    }

    /**
     * Prompt the user to choose the account to use and process the file using the
     * Drive file ID stored in mFileId.
     */
    private void getUserAccountAndProcessFile() {
        // Implement the method.
        throw new UnsupportedOperationException(
                "The getUserAccountAndProcessFile method has not been implemented");
    }

    public void pressed(View view){
        Intent intentt = new Intent(Intent.ACTION_DIAL);

        startActivity(intentt);
    }

}
