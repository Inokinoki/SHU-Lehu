package com.veyxstudio.shulehu.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.veyxstudio.shulehu.ArticleListActivity;
import com.veyxstudio.shulehu.LoginActivity;
import com.veyxstudio.shulehu.MainActivity;
import com.veyxstudio.shulehu.R;
import com.veyxstudio.shulehu.thread.HomeFragmentThread;
import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.util.URLHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Veyx Shaw on 2016/2/16.
 * Do category.
 */
public class CategoryFragment extends Fragment{
    private final String LOG_TAG = "CategoryFragment";

    private Snackbar snackbar;

    public void closeSnackBar(){
        if (snackbar!=null)
            snackbar.dismiss();
    }

    private String html;

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        read();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cate, container, false);
        WebView webView = (WebView)view.findViewById(R.id.webview_cate);
        webView.setWebViewClient(new CateWebViewClient());
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        if (html != null) {
            Log.i(LOG_TAG,"Load category");
            webView.loadDataWithBaseURL(URLHelper.base, html, "text/html", "utf-8", null);
        }
        return view;
    }

    private class CateWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (((MainActivity)getActivity()).getLoginState()) {
                Log.i(LOG_TAG,"Logined, start ArticleActivity");
                Intent intent = new Intent(getActivity(), ArticleListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("URL",url);
                intent.putExtras(bundle);
                startActivity(intent);
            }else{
                Log.i(LOG_TAG, "Not login, show snackbar");
                openSnackBar();
            }

            return true;
        }
    }

    private void openSnackBar(){
        snackbar = Snackbar.make(getView(),
                R.string.snackbar_log, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.action_login, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                snackbar.setText(R.string.snackbar_logging);
                // Start login activity.
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                startActivity(intent);
                snackbar.dismiss();
                snackbar = null;
            }
        });
        snackbar.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (snackbar!=null) {
            snackbar.dismiss();
            snackbar = null;
        }
        if (view != null) {
            view = null;
        }
    }

    private void read(){
        // Read from cache file.
        if (getActivity()!=null){
            String fileName = getActivity().getCacheDir().toString()+ "/"+ KeyWordHelper.cacheCate;
            Log.i(LOG_TAG, fileName);
            File cacheFile = new File(fileName);
            // If not exist, show the tips.
            if (!cacheFile.exists()){
                Toast.makeText(getActivity(), R.string.no_cache_file, Toast.LENGTH_SHORT).show();
                new HomeFragmentThread(((MainActivity)getActivity()).getHandler(),getActivity()).start();
            } else {
                // Try to read data from the file.
                FileReader fileReader = null;
                try {
                    fileReader = new FileReader(cacheFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line = null;
                    StringBuilder stringBuilder = new StringBuilder();
                    try {
                        line = bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(LOG_TAG, "Create cate cache file failed.");
                    }
                    while (line != null) {
                        stringBuilder.append(line);
                        try {
                            line = bufferedReader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    html = stringBuilder.toString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.i(LOG_TAG, "Open FileReader failed.");
                } finally {
                    try {
                        if (fileReader != null) {
                            fileReader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(LOG_TAG, "Close FileReader failed.");
                    }
                }
            }
        }
    }

    public void update(){
        read();
        if (html != null) {
            if (view != null) {
                WebView focusView = (WebView) view.findViewById(R.id.webview_cate);
                focusView.loadDataWithBaseURL(URLHelper.base, html, "text/html", "utf-8", null);
            } else {
                Log.i(LOG_TAG, "View null");
            }
        } else {
            Log.e(LOG_TAG, "Content null");
        }
    }
}
