package com.veyxstudio.shulehu.util;

import android.util.Log;

/**
 * Created by Veyx Shaw on 2016/4/6.
 * Help to send quick reply message.
 */
public class ReplyHelper extends HttpHelper{

    private static final String LOG_TAG = "ReplyHelper";

    public ReplyHelper(){
        super(HttpHelper.METHOD_POST, URLHelper.baseArticle, "utf-8");
        super.addParam("action","quickreply",true);
        super.setNeedEncode(false);
    }

    private int aid = 0;
    private String title ="来自随享乐乎客户端";
    private String replyContent = "";

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    /**
     * private String replyURL = "http://bbs.lehu.shu.edu.cn/Article.aspx?action=quickreply";
     * private String templateAid = "aid=";
     * private String templateTitle ="&Title=";
     * private String templateReplyContent = "&ReplyContent=";
     * private String replyContent = "";
     */

    public void reply() throws HttpHelperException{
        super.addParam(KeyWordHelper.replyAid, aid+"", false);
        super.addParam(KeyWordHelper.replyTitle, title, false);
        super.addParam(KeyWordHelper.replyContent, replyContent, false);
        super.start();
        Log.i(LOG_TAG, super.getResult());
    }

}
