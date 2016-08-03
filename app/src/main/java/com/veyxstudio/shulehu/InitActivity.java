package com.veyxstudio.shulehu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.veyxstudio.shulehu.util.AMarkDataBaseHelper;
import com.veyxstudio.shulehu.util.KeyWordHelper;

/**
 * Created by Veyx Shaw on 16-1-27.
 * Init files and states.
 */
public class InitActivity extends Activity implements Runnable{
    private static final String LOG_TAG = "InitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_init);

        // Run new thread.
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
