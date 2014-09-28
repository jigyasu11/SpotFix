package com.spotfix.fb_login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spotfix.R;

/**
 * Created by rarya on 9/26/14.
 */
public class LoginFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fb_login,
                container, false);
        return view;
    }
}
