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
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.spotfix.dao.DoPost;
import com.spotfix.dao.PostTypeEnum;
import com.spotfix.models.SpotFixApproved;

import java.sql.Timestamp;

/**
 * Created by rarya on 9/29/14.
 */
public class GetSpotFixDetailActivity extends Activity implements AsyncResponse {


    private NetworkImageView view;
    private UiLifecycleHelper uiHelper;

    private static final String TAG = GetSpotFixDetailActivity.class.getSimpleName();

    private Button join;
    private ImageButton share;
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


        String timestamp = new Timestamp(sf.getScheduledOn()).toString();

        String snippet = sf.getState() + "\n" + timestamp;
        map.addMarker(new MarkerOptions()
                .title(sf.getDesc())
                .snippet(snippet)
                .position(location));

        view =(NetworkImageView) findViewById(R.id.doneImage);
        view.setImageUrl(sf.getPictureId(), AppController.getInstance().getImageLoader());

        join = (Button) findViewById(R.id.joinButton);
        share = (ImageButton) findViewById(R.id.shareFacebook);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Join the movement
                DoPost post = new DoPost(PostTypeEnum.JOIN_SPOTFIX, GetSpotFixDetailActivity.this);
                post.execute(sf.getId(), "{\"user_id\":\"" + sf.getUserId() + "\"}");
                Toast toast = Toast.makeText(GetSpotFixDetailActivity.this, "You have successfully joined", Toast.LENGTH_LONG);
                toast.show();
                GetSpotFixDetailActivity.this.finish();
            }
        });


        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, " I AM HERE " + sf.getDesc() + " and " + sf.getScheduledOn());
                FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(GetSpotFixDetailActivity.this)
                        .setDescription(sf.getDesc() + " on " + new Timestamp(sf.getScheduledOn()).toString())
                        .setLink("https://www.facebook.com/theugl.yindian")
                        .build();
                GetSpotFixDetailActivity.this.uiHelper.trackPendingDialogCall(shareDialog.present());
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
                Intent intent3 = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent3);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
}
