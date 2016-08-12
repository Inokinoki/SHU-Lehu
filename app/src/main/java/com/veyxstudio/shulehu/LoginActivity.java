package com.veyxstudio.shulehu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.veyxstudio.shulehu.handler.LoginHandler;
import com.veyxstudio.shulehu.util.HttpHelper;
import com.veyxstudio.shulehu.util.HttpHelperException;
import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.util.LoginHelper;
import com.veyxstudio.shulehu.util.URLHelper;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import cn.domob.android.ads.AdManager;
import cn.domob.android.ads.InterstitialAd;
import cn.domob.android.ads.InterstitialAdListener;

/**
 * Created by Veyx Shaw on 16-1-11.
 * First login.
 */
public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener, Runnable{
    private final String LOG_TAG = "LoginActivity";

    private InterstitialAd interstitialAd;

    private LoginHandler handler;
    private ProgressDialog progressDialog;
    private String username;
    private String nickname;
    private String password;
    private String passport;
    private boolean onLogin;

    public String getPassport() {return passport;}
    public String getPassword() {return password;}
    public String getUsername() {return username;}
    public String getNickname() {return nickname;}

    public void closeDialog(){
        progressDialog.dismiss();
        onLogin = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadAD();
        readAccount();
        // Init ProgressDialog.
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login");
        // Init Handler.
        handler = new LoginHandler(this);
        // Init click event.
        onLogin = false;
        findViewById(R.id.login_button).setOnClickListener(this);
    }

    private void readAccount(){
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            int relogin = bundle.getInt("relogin", 0);
            if (relogin == 1) {
                SharedPreferences sharedPreferences =
                        this.getSharedPreferences(KeyWordHelper.pFileName,
                                Context.MODE_PRIVATE);
                username = sharedPreferences.getString(KeyWordHelper.pUsername, "");
                password = sharedPreferences.getString(KeyWordHelper.pPassword, "");
                ((EditText) findViewById(R.id.login_account)).setText(username);
                ((EditText) findViewById(R.id.login_password)).setText(password);
            }
        }
    }

    private void loadAD(){
        interstitialAd = new InterstitialAd(this, KeyWordHelper.PublishID, KeyWordHelper.LoginID);
        interstitialAd.setInterstitialAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialAdReady() {
                Log.i(LOG_TAG, "onInterstitialAdReady");
            }

            @Override
            public void onInterstitialAdFailed(AdManager.ErrorCode errorCode) {
                Log.i(LOG_TAG, "onInterstitialAdFailed "+ errorCode);
            }

            @Override
            public void onInterstitialAdPresent() {
                Log.i(LOG_TAG, "onInterstitialAdPresent");
            }

            @Override
            public void onInterstitialAdDismiss() {
                Log.i(LOG_TAG, "onInterstitialAdDismiss");
            }

            @Override
            public void onLandingPageOpen() {
                Log.i(LOG_TAG, "onInterstitialAdOn");
            }

            @Override
            public void onLandingPageClose() {
                Log.i(LOG_TAG, "onInterstitialAdClose");
            }

            @Override
            public void onInterstitialAdLeaveApplication() {

            }

            @Override
            public void onInterstitialAdClicked(InterstitialAd interstitialAd) {
                Log.i(LOG_TAG, "onInterstitialAdClicked");
            }
        });
        interstitialAd.loadInterstitialAd();
    }

    public void run() {
        LoginHelper loginHelper =
                new LoginHelper(username, password);
        try{
            passport = loginHelper.getPassport();
            switch (passport) {
                case "1":
                    handler.sendEmptyMessage(KeyWordHelper.STATE_OUT);
                    break;
                default:
                    // Get nickname.
                    HttpHelper nameHelper =
                            new HttpHelper(URLHelper.userinfo);
                    nameHelper.addCookie(KeyWordHelper.pPassport
                            , passport);
                    nameHelper.start();
                    String result = nameHelper.getResult();
                    Parser parser = Parser.createParser(result, "utf-8");
                    HasAttributeFilter hasAttributeFilter =
                            new HasAttributeFilter("class", "user");
                    try {
                        NodeList nodeList =
                                parser.extractAllNodesThatMatch(hasAttributeFilter);
                        if (nodeList.size() >= 1) {
                            Node[] nodes = nodeList.toNodeArray();
                            nickname = nodes[0].toPlainTextString();
                            System.out.println(nickname);
                        }
                    } catch (ParserException e) {
                        e.printStackTrace();
                        nickname = getResources()
                                .getString(R.string.default_nickname);
                    } finally {
                        this.onLogin = false;
                        handler.sendEmptyMessage(KeyWordHelper.LOAD_OK);
                    }
            }
        }catch (HttpHelperException e){
            e.printStackTrace();
            handler.sendEmptyMessage(KeyWordHelper.NO_NETWORK);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_button){
            this.username = ((EditText)findViewById(R.id.login_account))
                    .getText().toString();
            this.password = ((EditText)findViewById(R.id.login_password))
                    .getText().toString();
            if (!this.username.isEmpty() &&
                    this.password.length()>4 &&
                    !this.onLogin ){
                if(interstitialAd.isInterstitialAdReady()) {
                    interstitialAd.showInterstitialAd(this);
                } else {
                    Log.i(LOG_TAG, "Read Login AD");
                    interstitialAd.loadInterstitialAd();
                }
                new Thread(this).start();
                progressDialog.show();
                onLogin = true;
            }
        }
    }
}
