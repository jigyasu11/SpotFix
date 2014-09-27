package com.spotfix;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by rarya on 9/25/14.
 */
public class CreateEventActivity extends Activity {

    TextView description;
    Button location;
    Button save;
    Button uploadPhotos;
    static final int MAP_LOCATION_REQUEST = 1;  // The request code


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);

        description = (EditText) findViewById(R.id.editText);
        location = (Button) findViewById(R.id.locationButton);
        save = (Button) findViewById(R.id.saveButton);
        uploadPhotos = (Button) findViewById(R.id.uploadPhotoButton);


        // add location call back to open google maps

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start a new map activity and get the location from there
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                startActivityForResult(intent, MAP_LOCATION_REQUEST);
            }
        });

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

                // create an async task to upload photos


            }
        });




    }
}
