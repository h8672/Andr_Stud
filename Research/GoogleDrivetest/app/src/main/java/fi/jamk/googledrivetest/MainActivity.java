package fi.jamk.googledrivetest;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private static final int REQUEST_CODE_RESOLUTION = 404;
    private static final int REQUEST_SIGN_IN = 4;

    private GoogleApiClient GAC;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GAC = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GAC.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GAC.disconnect();
    }

    //Yhdistetään onStart jälkeen
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // onConnected
        Toast.makeText(this, "Connection successful!", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onConnectionSuspended(int i) {
        //TODO onConnectionSuspended
        Toast.makeText(this, "onConnectionSuspended("+i+")", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            if(connectionResult.getErrorCode() == connectionResult.SIGN_IN_REQUIRED) {
                Toast.makeText(this, "Sign in connect required", Toast.LENGTH_SHORT).show();
                try {
                    connectionResult.startResolutionForResult(this, REQUEST_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    //TODO Unable to resolve, message user appropriately
                }
            }
            else {
                try {
                    //TODO only shows switch error, because there's no case for this.
                    connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
                } catch (IntentSender.SendIntentException e) {
                    //TODO Unable to resolve, message user appropriately
                }
            }
        }
        else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
        Toast.makeText(this, "Connection requires resolution!", Toast.LENGTH_SHORT).show();
    }

    //Yhdistetään onConnectionFailed jälkeen
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_SIGN_IN:
                if(resultCode == RESULT_OK){
                    //Sign in! Tulee tänne vain, jos kysyy eri käyttäjiä ensimmäisellä käynnistys kerralla tai vaihtaa käyttäjää.
                    //Ensimmäinen kirjautumiskerta ei vie loppuun connectia... en tiiä miksi...
                    GAC.connect();
                } else {
                    Toast.makeText(this, "Verify your OAuth is created or Google Drive API is enabled!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, "Default switch in onActivityResult!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void getPress(View view){
        //Button press search
        //if(GAC.isConnected()) GAC.reconnect();
        //else if(GAC.isConnecting()) GAC.connect();
        //else Toast.makeText(this, "Connection failure?", Toast.LENGTH_SHORT).show();

        Toast.makeText(this, "Button press! I'm connected? " + GAC.isConnected(), Toast.LENGTH_SHORT).show();
        EditText et = (EditText) findViewById(R.id.etxtFilename);
        loadFile(et.getText().toString());
    }
    private void loadFile(String filename) {
    // Create a query for a specific filename in Drive.
    Query query = new Query.Builder()
            .addFilter(Filters.eq(SearchableField.TITLE, filename))
            .build();
    // Invoke the query asynchronously with a callback method
    Drive.DriveApi.query(GAC, query)
            .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    // Success! Handle the query result.
                    //TODO handling query results! But how is it handled?
                    int items = result.getMetadataBuffer().getCount();

                    yell("Hohoi! Täällä on jotain! "+items+" kpl, "+result.toString());
                }
            });

    }

    //Just want to yell!
    private void yell(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
