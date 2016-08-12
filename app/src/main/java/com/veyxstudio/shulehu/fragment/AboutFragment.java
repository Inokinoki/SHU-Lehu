package com.veyxstudio.shulehu.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veyxstudio.shulehu.R;

/**
 * Created by Inoki on 16-8-12.
 * Show about message.
 */
public class AboutFragment extends Fragment {
    private final String LOG_TAG = "AboutFragment";

    @Override
    public View onCreateView(LayoutInflater layoutInflater ,
                             ViewGroup container ,
                             Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.activity_about,
                container, false);
        return view;
    }
}