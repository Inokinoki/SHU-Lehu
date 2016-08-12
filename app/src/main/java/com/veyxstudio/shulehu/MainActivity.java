package com.veyxstudio.shulehu;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.veyxstudio.shulehu.fragment.AboutFragment;
import com.veyxstudio.shulehu.fragment.CategoryFragment;
import com.veyxstudio.shulehu.fragment.MarkFragment;
import com.veyxstudio.shulehu.fragment.SettingFragment;
import com.veyxstudio.shulehu.handler.MainHandler;
import com.veyxstudio.shulehu.fragment.DiscoverFragment;
import com.veyxstudio.shulehu.fragment.HomeFragment;
import com.veyxstudio.shulehu.thread.CategoryFragmentThread;
import com.veyxstudio.shulehu.thread.HomeFragmentThread;
import com.veyxstudio.shulehu.util.AMarkDataBaseHelper;
import com.veyxstudio.shulehu.util.CategoryHelper;
import com.veyxstudio.shulehu.util.KeyWordHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, Runnable{
    private static final String LOG_TAG = "MainActivity";

    private int fragmentPointer;
    private long lastHomeRefreshTime;
    private long lastCateRefreshTime;

    public boolean getLoginState() {return loginState;}

    private boolean loginState;

    public MainHandler getHandler() { return handler; }

    private MainHandler handler;

    private AboutFragment aboutFragment;
    private HomeFragment homeFragment;
    private DiscoverFragment discoverFragment;
    private CategoryFragment categoryFragment;
    private MarkFragment markFragment;
    private SettingFragment settingFragment;

    public void updateHome(){
        if (homeFragment != null){
            homeFragment.update();
        }
    }
    public void updateCate(){
        if (categoryFragment != null) {
            categoryFragment.update();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComp();
        initData();
        initFragment();
    }

    private void initData(){
        SharedPreferences sharedPreferences =
                getSharedPreferences(KeyWordHelper.pFileName,
                        Context.MODE_PRIVATE);
        // Init login state.
        loginState = sharedPreferences
                .getBoolean(KeyWordHelper.pValidate, false);
        Log.i(LOG_TAG, "Login state: " + loginState);
        // Init login info.
        if (!sharedPreferences.
                getString(KeyWordHelper.pNickname," ").equals(" ")){
            ((TextView)findViewById(R.id.nav_header_studentName))
                    .setText(sharedPreferences.
                            getString(KeyWordHelper.pNickname, " "));
            Log.i(LOG_TAG, "Nick name: " + sharedPreferences.
                    getString(KeyWordHelper.pNickname, " "));
        }
        ((TextView)findViewById(R.id.nav_header_studentNo))
                .setText(sharedPreferences.
                        getString(KeyWordHelper.pUsername, " "));
        Log.i(LOG_TAG, "User name: " + sharedPreferences.
                getString(KeyWordHelper.pUsername, " "));
        // Init cache file expire.
        sharedPreferences = getSharedPreferences(KeyWordHelper.cFileName,
                        Context.MODE_PRIVATE);
        lastHomeRefreshTime= sharedPreferences.getLong(KeyWordHelper.cHomeTime, 0);
        Log.i(LOG_TAG, "Last Home Refresh Time: " + lastHomeRefreshTime);
        lastCateRefreshTime= sharedPreferences.getLong(KeyWordHelper.cCateTime, 0);
        Log.i(LOG_TAG, "Last Cate Refresh Time: " + lastCateRefreshTime);
    }
    private void initComp(){
        // Init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Init drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // Init drawer item(navigation)
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Init click event.
        ((Button)findViewById(R.id.nav_header_login)).setOnClickListener(this);
        ((TextView)findViewById(R.id.article_search_bar)).setOnClickListener(this);
        // Init handler.
        handler = new MainHandler(this);
    }
    private void initFragment(){
        // Init fragment.
        this.fragmentPointer = R.id.nav_home;
        aboutFragment = new AboutFragment();
        homeFragment = new HomeFragment();
        discoverFragment = new DiscoverFragment();
        categoryFragment = new CategoryFragment();
        markFragment = new MarkFragment();
        settingFragment = new SettingFragment();

        // Set the first one to be the home fragment.
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.commit();
        fragmentPointer = R.id.nav_home;
        checkHomeData();
    }
    private void checkHomeData() {
        if (System.currentTimeMillis() - lastHomeRefreshTime > 300000l) {
            // More than 5 minutes, refresh.
            Log.i(LOG_TAG, "Refresh home content.");
            new HomeFragmentThread(handler, this).start();
        } else {
            Log.i(LOG_TAG, "Home content not out of date.");
        }
    }
    private void checkCateDate(){
        if (System.currentTimeMillis()-lastCateRefreshTime>2592000000l){
            // More than 1 month, refresh.
            Log.i(LOG_TAG,"Refresh cate content.");
            new CategoryFragmentThread(handler, this).start();
        } else {
            Log.i(LOG_TAG, "Cate content not out of date.");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nav_header_login){
            if (!loginState) {
                // Login
                if (fragmentPointer == R.id.nav_home){
                    homeFragment.closeSnackBar();
                } else if (fragmentPointer == R.id.nav_category){
                    categoryFragment.closeSnackBar();
                } else if (fragmentPointer == R.id.nav_discover){
                    discoverFragment.closeSnackBar();
                }
                Intent intent =
                        new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                // Tips logout.
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(this);
                builder.setMessage(R.string.exit_confirm);
                builder.setTitle(R.string.app_name);
                builder.setPositiveButton(R.string.exit_confirm_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(MainActivity.this).start();
                            }
                        });
                builder.setNegativeButton(R.string.exit_confirm_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        }else if(v.getId()==R.id.article_search_bar){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void run() {
        // Delete login state.
        SharedPreferences sharedPreferences =
                getSharedPreferences(KeyWordHelper.pFileName,
                        Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        // Delete Database
        SQLiteDatabase database =
                new AMarkDataBaseHelper(MainActivity.this,
                        KeyWordHelper.amDatabase, null,1)
                        .getWritableDatabase();
        database.execSQL("DELETE FROM " + AMarkDataBaseHelper.tableName);
        database.close();

        // Restart.
        Intent intent = new Intent(MainActivity.this,
                InitActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Check the pointer and switch to the target fragment.
        if (id == R.id.nav_home) {
            if(fragmentPointer != R.id.nav_home){
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container
                        , homeFragment);
                fragmentTransaction.commit();
                fragmentPointer = R.id.nav_home;
                checkHomeData();
            }
        } else if (id == R.id.nav_category) {
            if(fragmentPointer != R.id.nav_category){
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container
                        , categoryFragment);
                fragmentTransaction.commit();
                fragmentPointer = R.id.nav_category;
                checkCateDate();
            }
        } else if (id == R.id.nav_discover) {
            if(fragmentPointer != R.id.nav_discover){
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container
                        , discoverFragment);
                fragmentTransaction.commit();
                fragmentPointer = R.id.nav_discover;
            }
        } else if (id == R.id.nav_mark){
            if(fragmentPointer != R.id.nav_mark){
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container
                        , markFragment);
                fragmentTransaction.commit();
                fragmentPointer = R.id.nav_mark;
            }
        } else if (id == R.id.nav_about) {
            if(fragmentPointer != R.id.nav_about){
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container
                        , aboutFragment);
                fragmentTransaction.commit();
                fragmentPointer = R.id.nav_about;
            }
        } else if (id == R.id.nav_setting) {
            if(fragmentPointer != R.id.nav_setting){
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container
                        , settingFragment);
                fragmentTransaction.commit();
                fragmentPointer = R.id.nav_setting;
            }
        }
        // Close the drawer.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private long exitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), R.string.exit_again, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
