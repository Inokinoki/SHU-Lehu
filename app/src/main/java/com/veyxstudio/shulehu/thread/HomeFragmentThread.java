package com.veyxstudio.shulehu.thread;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.veyxstudio.shulehu.R;
import com.veyxstudio.shulehu.handler.MainHandler;
import com.veyxstudio.shulehu.util.HomeHelper;
import com.veyxstudio.shulehu.util.HttpHelperException;
import com.veyxstudio.shulehu.util.KeyWordHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Veyx Shaw on 2016/2/9.
 * Get home page and sen the message.
 */
public class HomeFragmentThread extends Thread{
    private static final String LOG_TAG = "HomeFragmentThread";

    private MainHandler handler;
    private Context context;

    public HomeFragmentThread(MainHandler mainHandler, Context context){
        this.context = context;
        this.handler = mainHandler;
    }

    @Override
    public void run() {
        HomeHelper homeHelper = new HomeHelper();
        try {
            homeHelper.home();

            // Store in cache file.
            String fileName = context.getCacheDir().toString() + "/" + KeyWordHelper.cacheHome;
            Log.i(LOG_TAG, fileName);
            File cacheFile = new File(fileName);
            // If not exist, create new file.
            if (!cacheFile.exists()) {
                try {
                    cacheFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Try to write data to the file.
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(cacheFile, false);
                fileWriter.write("<html><head><link rel=\"stylesheet\" type=\"text/css\" href='file:///android_asset/home.css'>" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"></head><body>"
                        + homeHelper.getHTML()
                        + "</body></html>");
                fileWriter.flush();
                Log.i(LOG_TAG, "Refresh time: " + System.currentTimeMillis());
                SharedPreferences sharedPreferences =
                        context.getSharedPreferences(KeyWordHelper.cFileName, Context.MODE_PRIVATE);
                sharedPreferences.edit()
                        .putLong(KeyWordHelper.cHomeTime, System.currentTimeMillis())
                        .apply();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(LOG_TAG, "Open FileWriter failed.");
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(LOG_TAG, "Close FileWriter failed.");
                    }
                }
            }
            handler.sendEmptyMessage(MainHandler.HOME_PAGE_FINISHED_EVENT);
        } catch (HttpHelperException e){
            e.printStackTrace();
            handler.sendEmptyMessage(KeyWordHelper.NO_NETWORK);
        }
    }
}
