package com.veyxstudio.shulehu.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.veyxstudio.shulehu.R;
import com.veyxstudio.shulehu.util.KeyWordHelper;

import cn.domob.android.ads.AdEventListener;
import cn.domob.android.ads.AdManager;
import cn.domob.android.ads.AdView;

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
        FrameLayout adContainer = (FrameLayout)view.findViewById(R.id.about_banner_ad);
        AdView adView = new AdView(this.getActivity(), KeyWordHelper.PublishID, KeyWordHelper.InlineID);
        adView.setAdEventListener(new AdEventListener() {
            @Override
            public void onEventAdReturned(AdView adView) {
                Log.i(LOG_TAG,"AD event");
            }

            @Override
            public void onAdFailed(AdView adView, AdManager.ErrorCode errorCode) {
                Log.i(LOG_TAG,"AD failed");
            }

            @Override
            public void onAdOverlayPresented(AdView adView) {
                Log.i(LOG_TAG,"AD overlayPresent");
            }

            @Override
            public void onAdOverlayDismissed(AdView adView) {

            }

            @Override
            public void onLeaveApplication(AdView adView) {

            }

            @Override
            public void onAdClicked(AdView adView) {
                Log.i(LOG_TAG,"AD Clicked");
            }

            @Override
            public Context onAdRequiresCurrentContext() {
                return null;
            }
        });
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adView.setLayoutParams(params);
        adContainer.addView(adView);
        return view;
    }
}