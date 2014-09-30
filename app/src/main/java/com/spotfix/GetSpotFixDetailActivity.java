package com.spotfix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.spotfix.dao.DoPost;
import com.spotfix.dao.HttpCalls;
import com.spotfix.dao.PostTypeEnum;
import com.spotfix.models.SpotFixApproved;

/**
 * Created by rarya on 9/29/14.
 */
public class GetSpotFixDetailActivity extends Activity implements AsyncResponse {


    private NetworkImageView view;

    private static final String TAG = GetSpotFixDetailActivity.class.getSimpleName();

    private Button join;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spot_fix_detail);


        Intent intent = getIntent();

        String details = intent.getStringExtra(MainActivity.MESSAGE_FOR_SPOT_FIX_DETAIL);

        final SpotFixApproved sf = new Gson().fromJson(details, SpotFixApproved.class);

        // Get a handle to the Map Fragment
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.doneMap)).getMap();

        LatLng location = new LatLng(sf.getLatitude(), sf.getLongitude());

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

        map.addMarker(new MarkerOptions()
                .title(sf.getDesc())
                .snippet(sf.getState())
                .position(location));

        view =(NetworkImageView) findViewById(R.id.doneImage);
        view.setImageUrl(HttpCalls.fetch_image_base_url + sf.getPictureId(), AppController.getInstance().getImageLoader());

        join = (Button) findViewById(R.id.joinButton);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Join the movement
                DoPost post = new DoPost(PostTypeEnum.JOIN_SPOTFIX, GetSpotFixDetailActivity.this);
                post.execute(sf.getId(), "{" + sf.getUserId() + "}");
            }
        });
    }

    @Override
    public void processFinish(String output, PostTypeEnum type) {

        Log.i(TAG, "Joined the spot fix event " + output);
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
                Intent intent3 = new Intent(getBaseContext(), ProposeSpotFixActivity.class);
                startActivity(intent3);
                return true;
            case R.id.user_feed:
                Intent intent2 = new Intent(getBaseContext(), UserFeedActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
