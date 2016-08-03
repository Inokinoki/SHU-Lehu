package com.veyxstudio.shulehu.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.veyxstudio.shulehu.LoginActivity;
import com.veyxstudio.shulehu.MainActivity;
import com.veyxstudio.shulehu.R;
import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.util.URLHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by Veyx Shaw on 16-1-10.
 * Handle UI refresh.
 */
public class MainHandler extends Handler {
    private static final String LOG_TAG = "MainHandler";

    public MainHandler(MainActivity mainActivity){
        this.currentActivity = new WeakReference<>(mainActivity);
    }

    public final static int SHORT_INFO_FINISHED_EVENT         =   0;
    public final static int HOME_PAGE_FINISHED_EVENT          =   1;
    public final static int CATEGORY_REFRESH_FINISHED_EVENT   =   2;
    public final static int PERSONAL_INFO_FINISHED_EVENT      =   3;


    private WeakReference<MainActivity> currentActivity;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case SHORT_INFO_FINISHED_EVENT:
                handleShortInfo();
                break;
            case HOME_PAGE_FINISHED_EVENT:
                handleHome();
                break;
            case CATEGORY_REFRESH_FINISHED_EVENT:
                handleCategory();
                break;
            case PERSONAL_INFO_FINISHED_EVENT:
                handleInfo();
                break;
            case KeyWordHelper.NO_NETWORK:
                Toast.makeText(currentActivity.get(), R.string.no_network,Toast.LENGTH_SHORT).show();
                break;
            case KeyWordHelper.STATE_OUT:
                // Delete login state and start LoginActivity.
                MainActivity activity = currentActivity.get();
                activity.getSharedPreferences(KeyWordHelper.pFileName, Context.MODE_PRIVATE)
                        .edit().putBoolean(KeyWordHelper.pValidate, false).apply();
                Toast.makeText(activity,R.string.error_password,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                break;
            case KeyWordHelper.LOAD_OK:
                currentActivity.get().getSharedPreferences(KeyWordHelper.pFileName,Context.MODE_PRIVATE)
                        .edit().putString(KeyWordHelper.pPassport,(String)msg.obj);
                break;
        }
    }

    private void handleHome(){
        MainActivity activity = currentActivity.get();
        activity.updateHome();
        Log.i(LOG_TAG, "Updated home content.");
    }

    private void handleShortInfo(){

    }

    private void handleCategory(){
        MainActivity activity = currentActivity.get();
        activity.updateCate();
        Log.i(LOG_TAG, "Updated category content.");
    }

    private void handleInfo(){

    }

}
