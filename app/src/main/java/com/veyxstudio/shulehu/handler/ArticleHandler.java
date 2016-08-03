package com.veyxstudio.shulehu.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.veyxstudio.shulehu.ArticleActivity;
import com.veyxstudio.shulehu.LoginActivity;
import com.veyxstudio.shulehu.R;
import com.veyxstudio.shulehu.object.Article;
import com.veyxstudio.shulehu.object.ArticleAdapter;
import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.util.URLHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

/**
 * Created by Veyx Shaw on 2016/2/22.
 * Handle article.
 */
public class ArticleHandler extends Handler{
    private static final String LOG_TAG = "ArticleHandler";

    public ArticleHandler(ArticleActivity currentActivity){
        activityReference = new WeakReference<>(currentActivity);
    }

    private WeakReference<ArticleActivity> activityReference;

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == KeyWordHelper.LOAD_OK) {
            ArticleActivity activity = activityReference.get();
            Toolbar toolbar = (Toolbar)activity.findViewById(R.id.article_toolbar);
            toolbar.setTitle(activity.getArticleTitle());
            generateArticleList();
            activity.clearBusy();activity.closeSnackBar();
        } else if (msg.what == KeyWordHelper.STATE_OUT) {
            Toast.makeText(activityReference.get(),
                    R.string.no_SHUPassport,
                    Toast.LENGTH_SHORT).show();
            ArticleActivity activity = activityReference.get();
            activity.clearBusy();activity.closeSnackBar();
            Intent intent = new Intent(activity, LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(KeyWordHelper.relogin,1);
            intent.putExtras(bundle);
            activity.startActivity(intent);
            activity.finish();
        } else if (msg.what == KeyWordHelper.NO_NETWORK) {
            Toast.makeText(activityReference.get(),
                    R.string.no_network, Toast.LENGTH_SHORT).show();
            ArticleActivity activity = activityReference.get();
            activity.clearBusy();activity.closeSnackBar();
        } else if (msg.what == KeyWordHelper.SEND_OK) {
            Toast.makeText(activityReference.get(),
                    R.string.send_ok, Toast.LENGTH_SHORT).show();
            ArticleActivity activity = activityReference.get();
            ((EditText)activity.findViewById(R.id.edit_text)).setText("");
            activity.closeSnackBar();
            activity.setToMaxPage();
            activity.refresh();
        }
    }

    private void generateArticleList(){
        ArticleActivity activity = activityReference.get();
        // Generate Article List
        Log.i(LOG_TAG, "Generate Article List");
        ListView listView = (ListView)activity.findViewById(R.id.article_list);
        ArticleAdapter adapter;
        String[] keys = {"Username", "Date", "Content", "Signature"};
        int[] ids = {R.id.article_username, R.id.article_time,
                R.id.article_content , R.id.article_signature};
        ListIterator<Article> iterator = activity.getArticleList().listIterator();
        ArrayList<HashMap<String,Object>> listItem = new ArrayList<>();
        while (iterator.hasNext()){
            HashMap<String, Object> map = new HashMap<>();
            Article article = iterator.next();
            map.put(keys[0], article.getUsername());
            map.put(keys[1], article.getDate());
            map.put(keys[2] ,article.getContent());
            map.put(keys[3], article.getSignature());
            listItem.add(map);
        }
        if(PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("no_picture",false)) {
            adapter = new ArticleAdapter(activity,
                    listItem, R.layout.list_article, keys, ids, "");
            Log.i(LOG_TAG,"No picture");
        } else {
            adapter = new ArticleAdapter(activity,
                    listItem, R.layout.list_article, keys, ids, URLHelper.baseArticle);
            Log.i(LOG_TAG,"With picture");
        }
        listView.setAdapter(adapter);
    }
}
