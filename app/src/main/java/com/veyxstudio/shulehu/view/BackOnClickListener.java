package com.veyxstudio.shulehu.view;

import android.app.Activity;
import android.view.View;

/**
 * Created by Veyx Shaw on 2016/4/3.
 * Back to last activity.
 */
public class BackOnClickListener implements View.OnClickListener {

    public BackOnClickListener(Activity activity){
        mActivity = activity;
    }

    Activity mActivity;

    @Override
    public void onClick(View v) {
        if(mActivity!=null)
            mActivity.finish();
    }
}
