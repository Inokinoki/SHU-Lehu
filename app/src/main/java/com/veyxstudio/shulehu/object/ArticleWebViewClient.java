package com.veyxstudio.shulehu.object;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.veyxstudio.shulehu.ArticleActivity;
import com.veyxstudio.shulehu.ArticleListActivity;
import com.veyxstudio.shulehu.MainActivity;
import com.veyxstudio.shulehu.R;
import com.veyxstudio.shulehu.util.URLHelper;

/**
 * Created by Veyx Shaw on 2016/3/28.
 * For listview with webview.
 */
public class ArticleWebViewClient extends WebViewClient {
    private static final String LOG_TAG = "ArticleWebViewClient";

    /**
     *
     * Constructor
     *
     * @param context The WebView in which context.
     *
     */

    public ArticleWebViewClient(Context context) {
        super();
        mContext = context;
    }

    private Context mContext;

    private final boolean DEBUG = true;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.contains(URLHelper.baseArticle)){
            Log.i(LOG_TAG, "Start ArticleActivity");
            Intent intent = new Intent(mContext, ArticleActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("URL",url);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }else if(url.contains(URLHelper.baseList)){
            Log.i(LOG_TAG, "Start ArticleListActivity");
            Intent intent = new Intent(mContext, ArticleListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("URL",url);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        } else {
            if (DEBUG)
                Toast.makeText(mContext, "Can't read:"+url, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext, "Can't resolve", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}