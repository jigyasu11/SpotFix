package com.spotfix;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.google.gson.Gson;
import com.spotfix.dao.DoPost;
import com.spotfix.dao.PostTypeEnum;
import com.spotfix.models.PictureId;
import com.spotfix.models.PostReport;
import com.spotfix.models.SpotLocation;

/**
 * Created by rarya on 9/25/14.
 */
public class ProposeSpotFixActivity extends Activity implements AsyncResponse {

    TextView description;
    Button save;
    Button uploadPhotos;

    private ProgressDialog pDialog;
    private String pictureId;

    private static final String TAG  = ProposeSpotFixActivity.class.getSimpleName();
    private static final Integer SELECT_PICTURE = 1;
    private static String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);

        Intent intent = getIntent();
        final double[] latLong = intent.getDoubleArrayExtra(MapsActivity.EXTRA_MESSAGE);

        description = (EditText) findViewById(R.id.editText);
        save = (Button) findViewById(R.id.saveButton);
        uploadPhotos = (Button) findViewById(R.id.uploadPhotoButton);
        pDialog = new ProgressDialog(this);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the data somewhere
            }
        });


        // upload photo will upload photos from gallery. Can add camera support later
        // this is an async task
        uploadPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.setMessage("Uploading image...");
                pDialog.show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImagePath == null) {
                    Toast toast = Toast.makeText(ProposeSpotFixActivity.this, "Please upload an image first...", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                pDialog.setMessage("Uploading report...");
                pDialog.show();
                // Make a post request to backend server
                // create the spot fix feed object
                Log.i(TAG, "received location" + latLong[0] + " " + latLong[1] + " user id " + AppController.getInstance().getUserId());
                SpotLocation location = new SpotLocation(String.valueOf(latLong[0]), String.valueOf(latLong[1]));
                PostReport report = new PostReport(pictureId, description.getText().toString(), location, AppController.getInstance().getUserId());
                DoPost post = new DoPost(PostTypeEnum.POST_REPORT, ProposeSpotFixActivity.this);
                post.execute(new Gson().toJson(report));
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "on activity result " + resultCode + " " + requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                Log.i(TAG, "image is " + selectedImagePath + " and " + selectedImageUri);
                // Upload the image
                if (selectedImagePath != null) {
                    DoPost post = new DoPost(PostTypeEnum.POST_PICTURE, ProposeSpotFixActivity.this);
                    post.execute(selectedImagePath);
                }
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_home:
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_propose:
                return true;
            case R.id.user_feed:
                Intent intent2 = new Intent(getBaseContext(), UserFeedActivity.class);
                startActivity(intent2);
                return true;
            case R.id.logout:
                Session session = Session.getActiveSession();
                session.close();
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void processFinish(String output, PostTypeEnum type) {
        pDialog.hide();
        switch(type) {
            case POST_PICTURE:
                Toast toast = Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT);
                toast.show();
                this.pictureId = new Gson().fromJson(output, PictureId.class).getPictureId();
                Log.i(TAG, "Picture id is "+ pictureId);
                break;
            case CREATE_USER:
                break;
            case POST_REPORT:
                toast = Toast.makeText(this, "Report uploaded successfully", Toast.LENGTH_SHORT);
                toast.show();
                //Close the activity after saving the form
                ProposeSpotFixActivity.this.finish();
                break;
            case JOIN_SPOTFIX:
                break;

            default:
                break;
        }
    }
}
