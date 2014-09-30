package com.spotfix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.spotfix.dao.HttpCalls;
import com.spotfix.models.UserFeed;
import com.spotfix.models.UserFeedList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rarya on 9/29/14.
 */
public class UserFeedActivity extends Activity {

    private ListView view;
    private List<String> items;
    private ArrayAdapter<String> adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items = new ArrayList<String>();

        HttpCalls call = new HttpCalls();

        String result = call.doGet(HttpCalls.get_feed);

        if (result != null) {
            UserFeedList list = new Gson().fromJson(result, UserFeedList.class);

            for(UserFeed u: list.getItems()) {
                items.add(u.getContent());
            }
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        view = (ListView) findViewById(R.id.userFeedView);

        view.setAdapter(adapter);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
