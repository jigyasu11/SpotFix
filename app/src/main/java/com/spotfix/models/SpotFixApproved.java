package com.spotfix.models;

import com.google.gson.Gson;

/**
 * Created by rarya on 9/27/14.
 */
/*
    Class to provide feed on the home_fragment screen
 */
public class SpotFixApproved {

    private String id;
    private String location;
    private String pictureId;
    private String desc;
    private String state;
    private String userId;
    private Long scheduledOn;

    private static final Gson gson = new Gson();

    public String getPictureId() {
        return pictureId;
    }

    public String getDesc() {
        return desc;
    }

    public String getState() {
        return state;
    }


    private Double getDouble(String num) {
        return Double.parseDouble(num);
    }
    public Double getLatitude() {
        return getDouble(location.split(",")[0]);
    }

    public Double getLongitude() {
        return getDouble(location.split(",")[1]);
    }

    public String getId() {
        return id;
    }

    public void setPictureId(String url) {
        pictureId = url;
    }

    public String getUserId() {
        return userId;
    }

    public String toJsonString() {
        return gson.toJson(this);
    }

    public Long getScheduledOn() {
        return scheduledOn;
    }
}
