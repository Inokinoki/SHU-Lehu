package com.veyxstudio.shulehu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.veyxstudio.shulehu.handler.TagArticleListHandler;
import com.veyxstudio.shulehu.object.ArticleList;
import com.veyxstudio.shulehu.util.HttpHelperException;
import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.util.TagArticleListHelper;
import com.veyxstudio.shulehu.util.URLHelper;
import com.veyxstudio.shulehu.view.BackOnClickListener;
import com.veyxstudio.shulehu.view.TagArticleListPullListener;

import java.util.List;

/**
 * Created by Veyx Shaw on 2016/2/19.
 * Handle article list.
 */
public class TagArticleListActivity extends AppCompatActivity implements Runnable,
        AdapterView.OnItemClickListener{
    private static final String LOG_TAG = "TagArticleListActivity";

    private Snackbar loadSnackBar;

    public void closeSnackBar(){
        if (loadSnackBar!=null)
            loadSnackBar.dismiss();
    }

    private AlertDialog pageJumpDialog;

    private TagArticleListHelper articleListHelper;
    private TagArticleListHandler articleListHandler;

    public List<ArticleList> getArticleListList() { return articleListList; }

    private List<ArticleList> articleListList;
    public void clearBusy() {this.busy = false;}

    private boolean busy = false;
    private String key;
    private int page = 1;
    private int maxPage = 1;

    public void setAids(int aid, int index) {
        this.aids[index-1] = aid;
    }

    public void switchToNextPage(){
        if (page<maxPage){
            if(!busy) {
                page++; this.busy = true;
                loadSnackBar = Snackbar.make(findViewById(R.id.article_list_layout),
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
                loadSnackBar = Snackbar.make(findViewById(R.id.article_list_layout),
                        R.string.article_load, Snackbar.LENGTH_INDEFINITE);
                loadSnackBar.show();
                new Thread(this).start();
            }
        }else
            Toast.makeText(this, R.string.first_page, Toast.LENGTH_SHORT).show();
    }

    private int[] aids;

    private String articleListTitle = "-";

    public String getArticleListTitle() {return articleListTitle;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        init();
    }

    private void init(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.article_list_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_action_arrow_left);
        toolbar.setNavigationOnClickListener(new BackOnClickListener(this));
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("slip_to_switch_page",true)) {
            findViewById(R.id.article_list_list).setOnTouchListener(new TagArticleListPullListener(this));
        } else {
            Toast.makeText(this ,R.string.no_slip_page_tip,Toast.LENGTH_SHORT).show();
        }
        articleListHandler = new TagArticleListHandler(this);
        page = 1; aids = new int[60];
        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("key","默认");
        articleListTitle = key;
        busy = true;
        loadSnackBar = Snackbar.make(findViewById(R.id.article_list_layout),
                R.string.article_load, Snackbar.LENGTH_INDEFINITE);
        loadSnackBar.show();
        new Thread(this).start();
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
                                loadSnackBar = Snackbar.make(findViewById(R.id.article_list_layout),
                                        R.string.article_load, Snackbar.LENGTH_INDEFINITE);
                                loadSnackBar.show();
                                new Thread(TagArticleListActivity.this).start();
                            }
                        }
                    }
                }).create();
    }

    @Override
    public void run() {
        if(articleListHelper==null){
            articleListHelper = new TagArticleListHelper(key);
            SharedPreferences sharedPreference =
                    getSharedPreferences(KeyWordHelper.pFileName, Context.MODE_PRIVATE);
            articleListHelper.addCookie(KeyWordHelper.pPassport,
                    sharedPreference.getString(KeyWordHelper.pPassport, ""));
            articleListHelper.addParam("type","2",true);
        }
        articleListHelper.setPage(page);
        try{
            articleListHelper.load();
            this.articleListList = articleListHelper.getArticleList();
            this.maxPage = articleListHelper.getMaxPage();
            articleListHandler.sendEmptyMessage(KeyWordHelper.LOAD_OK);
        }catch (HttpHelperException e){
            e.printStackTrace();
            articleListHandler.sendEmptyMessage(KeyWordHelper.NO_NETWORK);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i(LOG_TAG, i + "th item on click.");
            Intent intent = new Intent(this, ArticleActivity.class);
            Bundle bundle = new Bundle();
            String url = URLHelper.baseArticle + "?aid=" + aids[i];
            bundle.putString("URL", url);
            intent.putExtras(bundle);
            startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
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
        }
        return true;
    }

}
