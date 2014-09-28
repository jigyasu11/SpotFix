package com.spotfix.custom_list_view;

/**
 * Created by rarya on 9/27/14.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.spotfix.AppController;
import com.spotfix.R;
import com.spotfix.models.SpotFixFeed;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<SpotFixFeed> spotFixFeeds;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<SpotFixFeed> spotFixFeeds) {
        this.activity = activity;
        this.spotFixFeeds = spotFixFeeds;
    }

    @Override
    public int getCount() {
        return spotFixFeeds.size();
    }

    @Override
    public Object getItem(int location) {
        return spotFixFeeds.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.status);

        // getting spot fix data for the row
        SpotFixFeed m = spotFixFeeds.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getPictureId(), imageLoader);

        // title
        title.setText(m.getDesc());

        // status
        rating.setText("Status: " + String.valueOf(m.getState()));

        return convertView;
    }

}