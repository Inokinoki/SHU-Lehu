package com.veyxstudio.shulehu.thread;

import android.content.Context;
import android.content.SharedPreferences;

import com.veyxstudio.shulehu.handler.ArticleHandler;
import com.veyxstudio.shulehu.util.HttpHelperException;
import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.util.ReplyHelper;

/**
 * Created by Veyx Shaw on 2016/4/6.
 * To quick reply.
 */
public class ReplyThread extends Thread{
    private static final String LOG_TAG = "ReplyThread";

    private ArticleHandler handler;
    private Context context;
    private int aid = 0;
    private String replyContent = "";

    public ReplyThread(ArticleHandler mainHandler, Context context, int aid, String replyContent){
        this.context = context;
        this.handler = mainHandler;
        this.aid = aid;
        this.replyContent = replyContent;
    }

    /**
     * private String replyURL = "http://bbs.lehu.shu.edu.cn/Article.aspx?action=quickreply";
     * private String templateAid = "aid=";
     * private String templateTitle ="&Title=";
     * private String templateReplyContent = "&ReplyContent=";
     * private String replyContent = "";
     */

    @Override
    public void run() {
        ReplyHelper replyHelper = new ReplyHelper();
        SharedPreferences sharedPreference =
                context.getSharedPreferences(KeyWordHelper.pFileName, Context.MODE_PRIVATE);
        replyHelper.addCookie(KeyWordHelper.pPassport,
                sharedPreference.getString(KeyWordHelper.pPassport, ""));
        try {
            replyHelper.setAid(this.aid);
            replyHelper.setReplyContent(this.replyContent);
            replyHelper.reply();
            handler.sendEmptyMessage(KeyWordHelper.SEND_OK);
        } catch (HttpHelperException e){
            e.printStackTrace();
            handler.sendEmptyMessage(KeyWordHelper.NO_NETWORK);
        }
    }
}
