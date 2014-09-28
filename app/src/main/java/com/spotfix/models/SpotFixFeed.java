package com.spotfix.models;

/**
 * Created by rarya on 9/27/14.
 */
/*
    Class to provide feed on the home_fragment screen
 */
public class SpotFixFeed {

    private String pictureId;
    private String desc;
    private String state;

    public SpotFixFeed(String pictureId, String desc, String state) {
        this.pictureId = pictureId;
        this.desc = desc;
        this.state = state;
    }

    public String getPictureId() {
        return pictureId;
    }

    public String getDesc() {
        return desc;
    }

    public String getState() {
        return state;
    }
}
