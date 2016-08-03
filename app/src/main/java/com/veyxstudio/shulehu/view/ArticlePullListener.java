package com.veyxstudio.shulehu.view;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.veyxstudio.shulehu.ArticleActivity;

import java.lang.ref.WeakReference;

/**
 * Created by Veyx Shaw on 2016/4/9.
 * Pull to switch page.
 */
public class ArticlePullListener implements View.OnTouchListener{
    private static final String LOG_TAG = "ArticlePullListener";

    public ArticlePullListener(ArticleActivity articleActivity){
        activityReference = new WeakReference<>(articleActivity);
    }

    private WeakReference<ArticleActivity> activityReference;

    private float start_x,offset_x,start_y,offset_y;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                start_x=event.getX();
                start_y=event.getY();
                break;

            case MotionEvent.ACTION_UP:
                offset_x=event.getX()-start_x;
                offset_y=event.getY()-start_y;
                if(offset_x>150&&Math.abs(offset_x)>Math.abs(offset_y)) {
                    activityReference.get().switchToNextPage();
                    Log.i(LOG_TAG, "Next Page: Offset_x: "+ offset_x);
                } else if (offset_x<-150&&Math.abs(offset_x)>Math.abs(offset_y)) {
                    activityReference.get().switchToPreviousPage();
                    Log.i(LOG_TAG, "Previous Page: Offset_x: "+ offset_x);
                }
                break;

            default:
                break;
        }


        //special touch event


        return false;
    }
}
