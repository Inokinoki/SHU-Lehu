package com.veyxstudio.shulehu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.veyxstudio.shulehu.util.AMarkDataBaseHelper;
import com.veyxstudio.shulehu.util.KeyWordHelper;

import cn.domob.android.ads.RTSplashAd;
import cn.domob.android.ads.RTSplashAdListener;
import cn.domob.android.ads.SplashAd;

/**
 * Created by Veyx Shaw on 16-1-27.
 * Init files and states.
 */
public class InitActivity extends Activity{
    private static final String LOG_TAG = "InitActivity";

    RTSplashAd rtSplashAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_init);

        rtSplashAd = new RTSplashAd(this, KeyWordHelper.PublishID, KeyWordHelper.StartID,
                SplashAd.SplashMode.SplashModeFullScreen);
        rtSplashAd.setRTSplashAdListener(new RTSplashAdListener() {
            @Override
            public void onRTSplashDismiss() {
                Log.i("DomobSDKDemo", "onRTSplashClosed");
                jump();
                rtSplashAd.closeRTSplash();
            }

            @Override
            public void onRTSplashLoadFailed() {
                Log.i("DomobSDKDemo", "onRTSplashLoadFailed");
            }

            @Override
            public void onRTSplashPresent() {
                Log.i("DomobSDKDemo", "onRTSplashStart");
            }

        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rtSplashAd.splash(InitActivity.this, InitActivity.this.findViewById(R.id.init_splash));
            }
        }, 1);

        // Run new thread.
        // new Thread(this).start();
    }

    public void jump() {
        // Check first login
        SharedPreferences use =
                getSharedPreferences(KeyWordHelper.uFileName,
                        Context.MODE_PRIVATE);
        int useTime = use.getInt(KeyWordHelper.uLoginTime,-1);
        if (useTime<0){
            // Start help and declare.
            Log.i(LOG_TAG, "First use.");
            // App files path.
            Log.i(LOG_TAG,getCacheDir().toString());
            // Refresh device state.
            SharedPreferences.Editor editor = use.edit();
            editor.putString(KeyWordHelper.uVersion, Build.VERSION.RELEASE);
            editor.putInt(KeyWordHelper.uSDK, Build.VERSION.SDK_INT);
            editor.putString(KeyWordHelper.uModel, Build.MODEL);
            editor.putInt(KeyWordHelper.uLoginTime, 0);
            editor.apply();
        }
        // Add use time.
        useTime = useTime + 1;
        SharedPreferences.Editor editor = use.edit();
        editor.putInt(KeyWordHelper.uLoginTime, useTime);
        editor.apply();
        Log.i(LOG_TAG, "Use times:" + useTime);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
