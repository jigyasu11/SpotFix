package com.spotfix.dao;

import android.os.AsyncTask;
import android.util.Log;

import com.spotfix.AsyncResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;


/**
 * Created by rarya on 9/28/14.
 */
public class DoPost extends AsyncTask<String, Void, String> {

    private PostTypeEnum type;
    private AsyncResponse responseListener;
    private static final String TAG = DoPost.class.getSimpleName();

    public DoPost(PostTypeEnum type, AsyncResponse responseListener) {
        this.responseListener = responseListener;
        this.type = type;
    }

    @Override
    protected String doInBackground(String... params) {
        switch(type) {
            case CREATE_USER:
                return doPost(HttpCalls.base_url+HttpCalls.create_new_user, params[0]);
            case POST_REPORT:
                return doPost(HttpCalls.base_url+HttpCalls.submit_report, params[0]);
            case POST_PICTURE:
                return doMultiPartPost(HttpCalls.base_url + HttpCalls.post_picture, params[0]);
            case JOIN_SPOTFIX:
                return doPost(HttpCalls.base_url+HttpCalls.join_spot_fix + "/" + params[0], params[1]);
            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        responseListener.processFinish(result, type);
    }

    private String doPost(String url, String payload) {
        try {
            Log.i(TAG, "payload " + payload);
            HttpCalls calls = new HttpCalls();
            return calls.doPost(url, payload);
        } catch (Exception e) {
            Log.e(TAG, "Error in POST", e);
        }
        return null;
    }

    private String doMultiPartPost(String url, String fileName) {

        try {
            HttpClient httpClient = new DefaultHttpClient();

            File file = new File(fileName);
            Log.i(TAG, "image file is " + fileName + " and url " + url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, fileName);
            HttpEntity entity = builder.build();

            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(entity);
            HttpResponse response = httpClient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            Log.i(TAG, "image upload response IS " + statusLine.getStatusCode() + " " + statusLine.getStatusCode() + " " +
                    HttpStatus.SC_OK);
            Log.i(TAG, String.format("checking %b",statusLine.getStatusCode() ==  HttpStatus.SC_OK));
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                Log.i(TAG,"uptop inside if");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Log.i(TAG,"uptop inside if 2");
                response.getEntity().writeTo(out);
                Log.i(TAG,"uptop inside if 3");
                String responseString = out.toString();
                Log.i(TAG,"uptop inside if 4");

                out.close();

                Log.i(TAG,"response string is " + responseString);
                response.getEntity().getContent().close();
                Log.i(TAG,"response string is " + responseString);
                return responseString;
            } else {
                response.getEntity().getContent().close();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in image upload", e);
        }
        return null;
    }
}
