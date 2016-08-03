package com.veyxstudio.shulehu;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.veyxstudio.shulehu.handler.ArticleHandler;
import com.veyxstudio.shulehu.object.Article;
import com.veyxstudio.shulehu.thread.ReplyThread;
import com.veyxstudio.shulehu.util.AMarkDataBaseHelper;
import com.veyxstudio.shulehu.util.AccountOutOfDateException;
import com.veyxstudio.shulehu.util.ArticleHelper;
import com.veyxstudio.shulehu.util.HttpHelperException;
import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.util.URLHelper;
import com.veyxstudio.shulehu.view.ArticlePullListener;
import com.veyxstudio.shulehu.view.BackOnClickListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Veyx Shaw on 2016/2/12.
 * Show article.
 */
public class ArticleActivity extends AppCompatActivity implements Runnable{
    private static final String LOG_TAG = "ArticleActivity";

    private Snackbar loadSnackBar;

    public void closeSnackBar(){
        if (loadSnackBar!=null)
            loadSnackBar.dismiss();
    }

    private AlertDialog pageJumpDialog;

    private ArticleHelper articleHelper;
    private ArticleHandler articleHandler;
    private String articleTitle = "文章";

    public List<Article> getArticleList() {
        return articleList;
    }

    private List<Article> articleList;

    public void clearBusy() {this.busy = false;}

    private boolean busy = false;
    private int aid;

    public void setToMaxPage() {
        page = maxPage;
    }
    public void switchToNextPage(){
        if (page<maxPage){
            if(!busy) {
                page++; this.busy = true;
                loadSnackBar = Snackbar.make(findViewById(R.id.article_layout),
                        R.string.article_load, Snackbar.LENGTH_INDEFINITE);
                loadSnackBar.show();
                new Thread(this).start();
            }
        }else
            Toast.makeText(this, R.string.last_page, Toast.LENGTH_SHORT).show();
    }

    public void switchToPreviousPage(){
        if (page>1){
            if(!busy) {
                page--; this.busy = true;
                loadSnackBar = Snackbar.make(findViewById(R.id.article_layout),
                        R.string.article_load, Snackbar.LENGTH_INDEFINITE);
                loadSnackBar.show();
                new Thread(this).start();
            }
        }else
            Toast.makeText(this, R.string.first_page, Toast.LENGTH_SHORT).show();
    }

    private int page = 1;
    private int maxPage = 1;

    public String getArticleTitle() {return articleTitle;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        init();
    }

    private void init(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_action_arrow_left);
        toolbar.setNavigationOnClickListener(new BackOnClickListener(this));
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("slip_to_switch_page",true)) {
            findViewById(R.id.article_list).setOnTouchListener(new ArticlePullListener(this));
        } else {
            Toast.makeText(this ,R.string.no_slip_page_tip,Toast.LENGTH_SHORT).show();
        }
        articleHandler = new ArticleHandler(this);
        page = 1;
        Bundle bundle = getIntent().getExtras();
        String urls = bundle.getString("URL", " ");
        if (!urls.equals(" ")) {
            if (urls.contains(URLHelper.baseArticle)){
                try {
                    URL url = new URL(urls);
                    String query = url.getQuery();
                    String[] params = query.split("&");
                    for (String param:params) {
                        String[] dict = param.split("=");
                        for (int j = 0; j < dict.length; j++) {
                            if (dict[j].equals("aid")) {
                                aid = Integer.valueOf(dict[j + 1]);
                                Log.i(LOG_TAG, "Aid=" + aid);
                            } else if (dict[j].equals("page")) {
                                page = Integer.valueOf(dict[j + 1]);
                                Log.i(LOG_TAG, "Page=" + page);
                            }
                        }
                    }
                    busy = true;
                    loadSnackBar = Snackbar.make(findViewById(R.id.article_layout),
                            R.string.article_load, Snackbar.LENGTH_INDEFINITE);
                    loadSnackBar.show();
                    new Thread(this).start();
                } catch (MalformedURLException e) {
                    Toast.makeText(this, R.string.invalid_address, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    finish();
                }
            } else {
                Toast.makeText(this, R.string.unknown_page, Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(this, R.string.unknown_page, Toast.LENGTH_LONG).show();
            finish();
        }
    }
    private void initDialog(){
        String[] items = new String[maxPage];
        for (int i = 0; i < maxPage; i++) {
            items[i] = (i+1)+"";
        }
        pageJumpDialog = new AlertDialog.Builder(this).setTitle(R.string.article_jump)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which + 1 != page) {
                            page = which + 1;
                            if (!busy) {
                                busy = true;
                                loadSnackBar = Snackbar.make(findViewById(R.id.article_layout),
                                        R.string.article_load, Snackbar.LENGTH_INDEFINITE);
                                loadSnackBar.show();
                                new Thread(ArticleActivity.this).start();
                            }
                        }
                    }
                }).create();
    }

    @Override
    public void run() {
        if(articleHelper==null){
            articleHelper = new ArticleHelper(aid);
            SharedPreferences sharedPreference =
                    getSharedPreferences(KeyWordHelper.pFileName, Context.MODE_PRIVATE);
            articleHelper.addCookie(KeyWordHelper.pPassport,
                    sharedPreference.getString(KeyWordHelper.pPassport, ""));
        }
        articleHelper.setPage(page);
        try {
            articleHelper.load();
            this.articleTitle = articleHelper.getTitle();
            this.articleList = articleHelper.getArticleList();
            this.maxPage = articleHelper.getMaxPage();
            articleHandler.sendEmptyMessage(KeyWordHelper.LOAD_OK);
        }catch (HttpHelperException e){
            e.printStackTrace();
            articleHandler.sendEmptyMessage(KeyWordHelper.NO_NETWORK);
        }catch (AccountOutOfDateException e){
            e.printStackTrace();
            articleHandler.sendEmptyMessage(KeyWordHelper.STATE_OUT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_previous_page:
                switchToPreviousPage();
                break;
            case R.id.action_next_page:
                switchToNextPage();
                break;
            case R.id.action_jump_page:
                initDialog();
                pageJumpDialog.show();
                break;
            case R.id.action_mark:
                SQLiteDatabase database = new AMarkDataBaseHelper(this, KeyWordHelper.amDatabase,
                        null,1).getWritableDatabase();
                android.database.Cursor cursor= database.rawQuery("select * from " +
                        AMarkDataBaseHelper.tableName + " WHERE "
                        + AMarkDataBaseHelper.tagAColume + " = " + aid, null);
                if(cursor.getCount()<1) {
                    Log.i(LOG_TAG,"No such aid in database");
                    ContentValues content = new ContentValues();
                    content.put(AMarkDataBaseHelper.tagAColume, aid);
                    content.put(AMarkDataBaseHelper.tagBColume, this.articleTitle);
                    database.insert(AMarkDataBaseHelper.tableName, null, content);
                    Toast.makeText(this, R.string.database_mark, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.database_has_aid, Toast.LENGTH_SHORT).show();
                }
                cursor.close();
                database.close();
                break;
            case R.id.action_comment:
                String content = ((EditText)findViewById(R.id.edit_text)).getText().toString();
                if (!content.equals("")){
                    loadSnackBar = Snackbar.make(findViewById(R.id.article_layout),
                            R.string.article_send, Snackbar.LENGTH_INDEFINITE);
                    loadSnackBar.show();
                    new ReplyThread(articleHandler, this, aid, content).start();
                } else {
                    Toast.makeText(this, R.string.edit_no_empty, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode==0x125) {
                String content = data.getStringExtra(KeyWordHelper.replyContent);
                loadSnackBar = Snackbar.make(findViewById(R.id.article_layout),
                        R.string.article_send, Snackbar.LENGTH_INDEFINITE);
                loadSnackBar.show();
                new ReplyThread(articleHandler, this, aid, content).start();
            } else {
                Log.i(LOG_TAG, "Result code not match");
            }
    }

    public void refresh(){
        busy = true;
        loadSnackBar = Snackbar.make(findViewById(R.id.article_layout),
                R.string.article_load, Snackbar.LENGTH_INDEFINITE);
        loadSnackBar.show();
        new Thread(this).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

}
