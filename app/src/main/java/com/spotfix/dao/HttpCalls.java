package com.spotfix.dao;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;

/**
 * Created by rarya on 9/27/14.
 */
public class HttpCalls {

    public static final String base_url = "http://ec2-54-169-91-68.ap-southeast-1.compute.amazonaws.com:8080/tui";

    private static final String TAG = "Fetching data from backend";

    public static final String create_new_user = "/users/";

    public static final String get_feed = "/feed/";

    public static final String get_spotfixes = "/spotfixes/";


    public String doPost(String end_point, String payload) {
        try {
            String url = this.base_url + end_point;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(payload, HTTP.UTF_8);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            return executeRequest(httpPost);
        } catch (Exception e) {
            Log.e(TAG, "Error in POST", e);
        }
        return null;
    }


    public String doGet(String end_point) {
        try {
            return executeRequest(new HttpGet(this.base_url + end_point));
        } catch (Exception e) {
            Log.e(TAG, "Error in GET", e);
        }
        return null;
    }

    private String executeRequest(HttpRequestBase request) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(request);
        StatusLine statusLine = response.getStatusLine();
        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            out.close();
            String responseString = out.toString();
            response.getEntity().getContent().close();
            return responseString;
        } else {
            response.getEntity().getContent().close();
        }
        return null;
    }
}
