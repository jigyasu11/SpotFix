package com.spotfix.models;

/**
 * Created by rarya on 9/28/14.
 */
public class PostReport {
    private String picture_id;
    private String desc;
    private SpotLocation location;
    private String user_id;

    public PostReport(String picture_id, String desc, SpotLocation location, String user_id) {
        this.picture_id = picture_id;
        this.desc = desc;
        this.desc = desc;
        this.user_id = user_id;
        this.location = location;
    }

    public String getPicture_id() {
        return picture_id;
    }

    public String getDesc() {
        return desc;
    }

    public SpotLocation getLocation() {
        return location;
    }

    public String getUser_id() {
        return user_id;
    }

}
