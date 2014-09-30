package com.spotfix;

import com.spotfix.dao.PostTypeEnum;

/**
 * Created by rarya on 9/29/14.
 */
public interface AsyncResponse {
    void processFinish(String output, PostTypeEnum type);
}