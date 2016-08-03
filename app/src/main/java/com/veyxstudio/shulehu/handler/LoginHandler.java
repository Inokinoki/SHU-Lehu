package com.veyxstudio.shulehu.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.veyxstudio.shulehu.LoginActivity;
import com.veyxstudio.shulehu.R;
import com.veyxstudio.shulehu.util.KeyWordHelper;

import java.lang.ref.WeakReference;

/**
 * Created by Veyx Shaw on 16-1-11.
 * Handle first login finished event and update UI.
 */
public class LoginHandler extends Handler{
    public LoginHandler(LoginActivity currentActivity){
        activityReference = new WeakReference<>(currentActivity);
    }

    private WeakReference<LoginActivity> activityReference;

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == KeyWordHelper.LOAD_OK) {
            store();
            activityReference.get().closeDialog();
            activityReference.get().finish();
        } else if (msg.what == KeyWordHelper.STATE_OUT) {
            // Here I use STATE_OUT state to show wrong password
            activityReference.get().closeDialog();
            Toast.makeText(activityReference.get(),
                    R.string.error_password,
                    Toast.LENGTH_SHORT);
        } else if (msg.what == KeyWordHelper.NO_NETWORK) {
            activityReference.get().closeDialog();
            Toast.makeText(activityReference.get(),
                    R.string.no_network,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void store(){
        SharedPreferences sharedPreferences =
                activityReference.get()
                        .getSharedPreferences(KeyWordHelper.pFileName,
                                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KeyWordHelper.pPassport,
                activityReference.get().getPassport());
        editor.putString(KeyWordHelper.pUsername,
                activityReference.get().getUsername());
        editor.putString(KeyWordHelper.pNickname,
                activityReference.get().getNickname());
        editor.putString(KeyWordHelper.pPassword,
                activityReference.get().getPassword());
        editor.putBoolean(KeyWordHelper.pValidate, true);
        editor.apply();
    }
}
