package com.spotfix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.spotfix.custom_list_view.CustomListAdapter;
import com.spotfix.dao.DoPost;
import com.spotfix.dao.HttpCalls;
import com.spotfix.dao.PostTypeEnum;
import com.spotfix.models.PostUserDetails;
import com.spotfix.models.SpotFixApproved;
import com.spotfix.models.UserId;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by rarya on 9/26/14.
 */

/*
    Display feeds for user
 */
public class MainActivity extends FragmentActivity implements AsyncResponse {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String MESSAGE_FOR_SPOT_FIX_DETAIL = "com.spotfix.MainActivity.item";
    private List<SpotFixApproved> spotFixFeeds = new ArrayList<SpotFixApproved>();
    private ListView listView;
    private CustomListAdapter adapter;
    private ProgressDialog pDialog;


    private static final int SPLASH = 0;
    private static final int SELECTION = 1;
    private static final int FRAGMENT_COUNT = SELECTION +1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

    private boolean isResumed = false;
    private boolean feedLoaded = false;
    private String firstName;
    private String lastName;
    private String id;

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback =
            new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    onSessionStateChange(session, state, exception);
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        FragmentManager fm = getSupportFragmentManager();
        fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
        fragments[SELECTION] = fm.findFragmentById(R.id.homeFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();
        listView = (ListView) findViewById(R.id.feedsView);
        adapter = new CustomListAdapter(this, spotFixFeeds);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SpotFixApproved sf = (SpotFixApproved) adapter.getItem(position);

                Intent intent = new Intent(getBaseContext(), GetSpotFixDetailActivity.class);
                intent.putExtra(MESSAGE_FOR_SPOT_FIX_DETAIL, sf.toJsonString());
                startActivity(intent);
            }
        });
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        // Only make changes if the activity is visible
        Log.i(TAG, "session state changed flag:" + isResumed + String.format(" Session opened %b", session.isOpened()));
        if (isResumed) {
            if (session.isOpened()) {
                // make request to the /me API
                Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, com.facebook.Response response) {
                        Log.i(TAG, "I AM AT TOP " + user);
                        if (user != null) {
                            //TextView welcome = (TextView) findViewById(R.id.welcome);
                            //welcome.setText("Hello " + user.getName() + "!");
                            firstName = user.getFirstName();
                            lastName = user.getLastName();
                            id = user.getId();
                            Log.i(TAG, "Name " + firstName + "," + lastName + "," + id);
                            if (AppController.getInstance().getUserId() == null) {
                                storeUserId();
                            }
                        }
                    }
                }).executeAsync();
            }
            FragmentManager manager = getSupportFragmentManager();
            // Get the number of entries in the back stack
            int backStackSize = manager.getBackStackEntryCount();
            // Clear the back stack
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            if (state.isOpened()) {
                // If the session state is open:
                // Show the authenticated fragment
                showFragment(SELECTION, false);
                if (!feedLoaded) {
                    loadFeeds();
                    feedLoaded = true;
                }
            } else if (state.isClosed()) {
                // If the session state is closed:
                // Show the fb_login fragment
                Log.i(TAG, "session state changed showing splash fragment:" + isResumed);
                showFragment(SPLASH, false);
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Log.i(TAG, "on resume called");
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment
            showFragment(SELECTION, false);
        } else {
            // otherwise present the splash screen
            // and ask the person to fb_login.
            showFragment(SPLASH, false);
        }
    }

    private void storeUserId() {
        // call backend to fetch user-id

        String email = firstName + "," + lastName;
        String mobile = id;
        PostUserDetails details = new PostUserDetails(mobile, email);
        DoPost doPost = new DoPost(PostTypeEnum.CREATE_USER, this);
        doPost.execute(details.toJsonString());
    }

    private void loadFeeds() {
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // changing action bar color
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#1b1b1b")));

        // Creating volley request obj
        Log.i(TAG, "creating a request " + HttpCalls.base_url + HttpCalls.get_spotfixes);
        JsonArrayRequest spotFeedReq = new JsonArrayRequest(HttpCalls.base_url+HttpCalls.get_spotfixes,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "got back data " + response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Gson gson = new Gson();
                                SpotFixApproved spotFix =  gson.fromJson(response.getString(i), SpotFixApproved.class);
                                spotFix.setPictureId(HttpCalls.fetch_image_base_url+spotFix.getPictureId());
                                spotFixFeeds.add(spotFix);
                            } catch (Exception e) {
                                Log.e(TAG, "error in deserializing spot fix data", e);
                            }

                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error occoured: " + error.getMessage());
                Log.e(TAG, "Error occoured", error.getCause());
                hidePDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(spotFeedReq);
    }


    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "on destroy called");
        uiHelper.onDestroy();
        Session.getActiveSession().close();
        feedLoaded = false;
        hidePDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem propose = menu.findItem(R.id.menu_propose);
        if (!Session.getActiveSession().isOpened()) {
            propose.setVisible(false);
        } else {
            propose.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (Session.getActiveSession().isOpened()) {
            // Handle presses on the action bar items
            switch (item.getItemId()) {
                case R.id.menu_home:
                    return true;
                case R.id.menu_propose:
                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.user_feed:
                    Intent intent2 = new Intent(getBaseContext(), UserFeedActivity.class);
                    startActivity(intent2);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void processFinish(String output, PostTypeEnum type) {
        Log.i(TAG, "Output is " + output);
        if (output != null) {
            UserId user = new Gson().fromJson(output, UserId.class);
            AppController.getInstance().setUserId(user.getUserId());
            Log.i("USER ID: ", AppController.getInstance().getUserId());
        }
    }
}
