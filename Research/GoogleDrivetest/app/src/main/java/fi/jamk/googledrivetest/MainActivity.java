package fi.jamk.googledrivetest;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.util.ArrayList;

//IMPORTANT!
//Google Drive API:lla saa vain tällä ohjelmalla tehtyjä tietoja
//Google Drive REST API:lla saa haettua kaikki tiedot

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_RESOLUTION = 404;
    private static final int REQUEST_SIGN_IN = 4;

    //Receive result codes
    final private static int MY_RESULT_FILES = 6543;

    private GoogleApiClient GAC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GAC = new GoogleApiClient
                .Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                //.addScope(Drive.SCOPE_FILE)
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
                    Toast.makeText(this, "Sign in exception!", Toast.LENGTH_SHORT).show();
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
                    if(GAC.isConnecting()) GAC.connect();
                    else
                        Toast.makeText(this, "Verify your OAuth is created or Google Drive API is enabled!", Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_RESULT_FILES:
                if(resultCode == Activity.RESULT_OK){
                    String result = data.getDataString();
                    Toast.makeText(this, result, Toast.LENGTH_SHORT);
                }
                break;
            default:
                Toast.makeText(this, "Default switch in onActivityResult!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    //Public methods
    public boolean createFolder(String foldername){
        syncGDrive();
        DriveFolder folder;
        folder = Drive.DriveApi.getRootFolder(GAC);

        //Set metadata to folder
        MetadataChangeSet set = new MetadataChangeSet.Builder()
                //https://www.sitepoint.com/web-foundations/mime-types-complete-list/
                .setTitle(foldername).build();

        //Callback class
        ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
                ResultCallback<DriveFolder.DriveFolderResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFolderResult result) {
                        if (!result.getStatus().isSuccess()) {
                            Toast.makeText(getApplicationContext(), "Error while trying to create the folder", Toast.LENGTH_SHORT);
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Created a folder", Toast.LENGTH_SHORT);
                    }
                };
        try {
            folder.createFolder(GAC, set).setResultCallback(folderCreatedCallback);
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    public boolean createFile(String filename){
        syncGDrive();
        Drive.DriveApi.requestSync(GAC);
        DriveFolder folder;
        folder = Drive.DriveApi.getRootFolder(GAC);

        //Set metadata for file
        MetadataChangeSet set = new MetadataChangeSet.Builder()
                //https://www.sitepoint.com/web-foundations/mime-types-complete-list/
                .setMimeType("text/plain")
                .setTitle(filename).build();

        //Callback class
        ResultCallback<DriveFolder.DriveFileResult> fileCreatedCallback = new
                ResultCallback<DriveFolder.DriveFileResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFileResult result) {
                        if (!result.getStatus().isSuccess()) {
                            note("Error while trying to create the folder");
                            return;
                        }
                        //note("Created a file: " + result.getDriveFile().getDriveId());
                        Toast.makeText(getApplicationContext(),
                                "Created a file",
                                Toast.LENGTH_SHORT);
                    }
                };
        //TODO Jotenkin contents pitäisi alustaa...
        //DriveContents contents;
        //folder.createFile(GAC, set, contents).setResultCallback(fileCreatedCallback);
        return true;
    }

    public void findFile(String filename){
        syncGDrive();
        Query query = new Query.Builder()
                .addFilter(Filters.or(Filters.eq(SearchableField.TITLE, filename), (Filters.contains(SearchableField.TITLE, filename))))
                //.addFilter(Filters.contains(SearchableField.TITLE, filename))
                .build();

        Drive.DriveApi.query(GAC, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        //If query fails
                        if(!result.getStatus().isSuccess()){
                            note("Query failed.\n" + result.getStatus().getStatusMessage());
                            result.release();
                            return;
                        }
                        // Success! Handle the query result.
                        //TODO handling query results! But how is it handled?
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        String str = "Löytyi "+buffer.getCount()+" kpl\n";
                        for(Metadata m : buffer){
                            if(m.isFolder()) str += "Folder, ";
                            str += m.getTitle() + "\n";
                        }
                        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
                        buffer.release();
                        result.release();
                    }
                });
    }

    public boolean listFiles(){
        syncGDrive();
        DriveFolder folder;
        folder = Drive.DriveApi.getRootFolder(GAC);

        final ResultCallback<DriveApi.MetadataBufferResult> listContents = new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                if(!result.getStatus().isSuccess()){
                    note("Error while trying to get contents");
                    return;
                }
                //Log.wtf("Count", ""+data.getCount());
                ArrayList list = new ArrayList();
                MetadataBuffer data = result.getMetadataBuffer();
                for (Metadata m : data){
                    //list.add(m.getTitle().toString());
                    //if(m.isDataValid()) {
                    //m.getDriveId();
                    //Check if it is folder or extension file with different types
                    note(m.getTitle());
                    list.add(m.getTitle());
                    list.add(m.getCreatedDate());
                    if (m.isFolder()) list.add("Folder"); //Picture
                    else list.add("Not folder");
                    //list.add(m.getFileSize());
                    //list.add(m.getMimeType());
                    //}
                }
                Toast.makeText(getApplicationContext(), list.toArray().toString(), Toast.LENGTH_SHORT);
                ShowFileList(new ArrayList(list));
                //list.clear();
                //data.release();
            }
        };
        //folder.listChildren(GAC).setResultCallback(listContents);
        return true;
    }

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

    //Button methods
    public void getPress1(View view) {
        //Search file
        EditText et;
        //Button press search
        et = (EditText) findViewById(R.id.etxtFilename);
        findFile(et.getText().toString());
    }
    public void getPress2(View view) {
        //List files
        listFiles();
    }
    public void getPress3(View view) {
        //Create folder
        EditText et;
        et = (EditText) findViewById(R.id.etxtFilename2);
        createFolder(et.getText().toString());
    }
    public void getPress4(View view) {
        //Create file
        EditText et;
        et = (EditText) findViewById(R.id.etxtFilename2);
        createFile(et.getText().toString());
    }
    public void getPress5(View view) {
        //Use intent
        findFileIntent();
    }

    public void note(String notification){
        Toast.makeText(getApplicationContext(), "GDrive: " + notification, Toast.LENGTH_SHORT);
    }

    public void ShowFileList(ArrayList array){
        Toast.makeText(getApplicationContext(), array.toString(), Toast.LENGTH_SHORT);
        TableLayout tl = (TableLayout) findViewById(R.id.Table);
        TextView tv;
        TableRow tr;
        for(int i = 0; i < array.size(); i++){
            tr = new TableRow(this);
            tv = new TextView(this);
            tv.setText(array.get(i).toString());
            tr.addView(tv);
            tl.addView(tr);

        }
    }

    //Private methods
    private void syncGDrive(){
        Drive.DriveApi.requestSync(GAC);
    }

/*
    private void findFile(String filename){
        Query query = new Query.Builder()
                .addFilter(Filters.or(Filters.eq(SearchableField.TITLE, filename), (Filters.contains(SearchableField.TITLE, filename))))
                //.addFilter(Filters.contains(SearchableField.TITLE, filename))
                .build();

        Drive.DriveApi.query(GAC, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        if(!result.getStatus().isSuccess()) return;
                        // Success! Handle the query result.
                        //TODO handling query results! But how is it handled?
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        String str = "\n";
                        for(Metadata m : buffer){
                            //if(m.isFolder())
                            str += m.getTitle() + "\n";
                            //yell("Metadata title" + m.getTitle().toString());
                        }
                        //yell("Löytyi "+ buffer.getCount() + " kpl, "+str);
                    }
                });
        //yell("Nothing");
    }

    private void loadFile(String filename) {
        Drive.DriveApi.requestSync(GAC);
        // Create a query for a specific filename in Drive.

        //Creating folder to root folder in Google Drive
        DriveFolder folder;
        MetadataChangeSet set = new MetadataChangeSet.Builder()
                //https://www.sitepoint.com/web-foundations/mime-types-complete-list/
                .setTitle("New Folder").build();
        MetadataChangeSet setfile = new MetadataChangeSet.Builder()
                //https://www.sitepoint.com/web-foundations/mime-types-complete-list/
                .setMimeType("text/plain")
                .setTitle("New File").build();
        //Callback class
        ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
                ResultCallback<DriveFolder.DriveFolderResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFolderResult result) {
                        if (!result.getStatus().isSuccess()) {
                            yell("Error while trying to create the folder");
                            return;
                        }
                        yell("Created a folder: " + result.getDriveFolder().getDriveId());
                    }
                };
        ResultCallback<DriveFolder.DriveFileResult> fileCreatedCallback = new
                ResultCallback<DriveFolder.DriveFileResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFileResult result) {
                        if (!result.getStatus().isSuccess()) {
                            yell("Error while trying to create the folder");
                            return;
                        }
                        yell("Created a file: " + result.getDriveFile().getDriveId());
                    }
                };
        //DriveContents contents;
        final ResultCallback<DriveApi.MetadataBufferResult> listContents = new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                if(!result.getStatus().isSuccess()){
                    yell("Error while trying to get contents");
                    return;
                }
                //Log.wtf("Count", ""+data.getCount());
                ArrayList list = new ArrayList();
                MetadataBuffer data = result.getMetadataBuffer();
                for (Metadata m : data){
                    //list.add(m.getTitle().toString());
                    //if(m.isDataValid()) {
                        //m.getDriveId();
                        //Check if it is folder or extension file with different types
                        yell(m.getTitle());
                        list.add(m.getTitle());
                        list.add(m.getCreatedDate());
                        if (m.isFolder()) list.add("Folder"); //Picture
                        else list.add("Not folder");
                        //list.add(m.getFileSize());
                        //list.add(m.getMimeType());
                    //}
                }
                ShowFileList(list);
                list.clear();
                data.release();
            }
        };
        folder = Drive.DriveApi.getRootFolder(GAC);
        //Create folder with callback
        //folder.createFolder(GAC, set).setResultCallback(folderCreatedCallback);
        //TODO Create file, Does not work!!!
        //folder.createFile(GAC, setfile, contents).setResultCallback(fileCreatedCallback);
        //Drive.DriveApi.newDriveContents(GAC);
        //Cant find anything with this
        //Drive.DriveApi.getRootFolder(GAC).queryChildren(GAC, query).setResultCallback(listContents);
        //Finds created folders!
        folder.listChildren(GAC).setResultCallback(listContents);

        //yell();
        /*folder.queryChildren(GAC, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                int items = result.getMetadataBuffer().getCount();
                yell("Containts: " + items + " items and string: " + result.toString());
            }
        });*//*
        //Drive.DriveApi.fetchDriveId(GAC, "");
    // Invoke the query asynchronously with a callback method


    }
*/
}
