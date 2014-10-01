package com.spotfix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Session;
import com.google.gson.Gson;
import com.spotfix.dao.HttpCalls;
import com.spotfix.models.UserFeed;
import com.spotfix.models.UserFeedList;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rarya on 9/29/14.
 */
public class UserFeedActivity extends Activity {

    private static final String TAG = UserFeedActivity.class.getSimpleName();
    private ListView view;
    private List<String> items;
    private ArrayAdapter<String> adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_feed_activity);
        items = new ArrayList<String>();


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        view = (ListView) findViewById(R.id.userFeedView);

        view.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        // Use volley to get data

        Log.i(TAG, "onResume called");

        JsonObjectRequest userFeedReq = new JsonObjectRequest(HttpCalls.base_url+ HttpCalls.get_feed,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "userfeedactivity got back data " + response.toString());
                        // Parsing json
                        UserFeedList userFeedList = new Gson().fromJson(response.toString(), UserFeedList.class);
                        if (items != null && items.size() > 0) {
                            items.clear();
                        }
                        for (UserFeed u: userFeedList.getItems()) {
                            items.add(u.getContent());
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
                }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(userFeedReq);
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
                Intent intent2 = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent2);
                return true;
            case R.id.user_feed:
                return true;
            case R.id.logout:
                Session session = Session.getActiveSession();
                session.close();
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
