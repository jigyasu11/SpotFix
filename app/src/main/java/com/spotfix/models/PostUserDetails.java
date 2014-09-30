package com.spotfix.models;

import com.google.gson.Gson;

/**
 * Created by rarya on 9/29/14.
 */
public class PostUserDetails {

    private String mobile;
    private String email;
    private static final Gson gson = new Gson();

    public PostUserDetails(String mobile, String email) {
        this.email = email;
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String toJsonString() {
        return gson.toJson(this);
    }
}
