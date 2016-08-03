package com.veyxstudio.shulehu.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.veyxstudio.shulehu.R;

/**
 * Created by Veyx Shaw on 2016/2/16.
 * Store settings.
 */
public class SettingFragment extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
