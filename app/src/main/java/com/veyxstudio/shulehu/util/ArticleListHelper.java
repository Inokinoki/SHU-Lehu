package com.veyxstudio.shulehu.util;

import android.util.Log;

import com.veyxstudio.shulehu.object.Article;
import com.veyxstudio.shulehu.object.ArticleList;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Veyx Shaw on 2016/2/17.
 * Show articles in one page.
 */
public class ArticleListHelper extends HttpHelper{
    private static final String LOG_TAG = "ArticleListHelper";

    public ArticleListHelper(int cid){
        super(METHOD_GET, "http://bbs.lehu.shu.edu.cn/ArticleList.aspx", "utf-8");
        super.addParam("cid", cid + "", true);
    }
    public ArticleListHelper(int aid, int page){
        this(aid);
        super.addParam("page", page + "", true);
    }

    public int getPage() {return page;}
    public void setPage(int page){this.page = page;}
    public String getTitle(){return  this.title;}
    public List<ArticleList> getArticleList(){return this.articleListList;}
    public int getMaxPage() { return maxPage; }

    private String title = "";
    private List<ArticleList> articleListList;
    private int page = 1;



    private int maxPage = 1;

    public void load() throws HttpHelperException, AccountOutOfDateException{
        super.addParam("page", page + "", true);
        super.start();

        String result = super.getResult();
        // Parse title.
        if(!result.contains(KeyWordHelper.notLogin)) {
            Parser titleParser = Parser.createParser(result, "utf-8");
            TagNameFilter titleTag = new TagNameFilter("title");
            try {
                NodeList titleNode = titleParser.extractAllNodesThatMatch(titleTag);
                SimpleNodeIterator iterator = titleNode.elements();
                while (iterator.hasMoreNodes()) {
                    title += iterator.nextNode().toPlainTextString();
                    Log.i(LOG_TAG, title);
                }
                if (title.contains(" - 上海大学乐乎论坛")) {
                    title = title.substring(0, title.indexOf(" - 上海大学乐乎论坛"));
                }
            } catch (ParserException e) {
                e.printStackTrace();
                Log.i(LOG_TAG, "Cannot get title.");
            }
            // Get items.
            Parser parser = Parser.createParser(result, "utf-8");
            articleListList = new ArrayList<>();
            TagNameFilter articleTable = new TagNameFilter("tbody");
            try {
//            NodeList itemNode = parser.extractAllNodesThatMatch(articleTable);
//            Node[]items = itemNode.toNodeArray();
//            if (items.length>0) {
//                Parser trParser = Parser.createParser(items[0].toHtml(), "utf-8");
                TagNameFilter trFilter = new TagNameFilter("tr");
                NodeList trList = parser.extractAllNodesThatMatch(trFilter);
                Node[] trItems = trList.toNodeArray();
                if (trItems.length > 1) {
                    for (int i = 1; i < trItems.length; i++) {
                        // Log.i(LOG_TAG, trItems[i].toHtml());
                        ArticleList articleList = new ArticleList(trItems[i].toHtml());
                        articleListList.add(articleList);
                    }
                }
                //}
            } catch (ParserException e) {
                e.printStackTrace();
                Log.i(LOG_TAG, "Cannot get items.");
            }

            Parser pagerParser = Parser.createParser(result, "utf-8");
            HasAttributeFilter pagerFilter = new HasAttributeFilter("class", "pager");
            try {
                NodeList pagerNode = pagerParser.extractAllNodesThatMatch(pagerFilter);
                Node[] items = pagerNode.toNodeArray();
                if (items.length > 0) {
                    // Get pager
                    Node[] pagerChilds = items[0].getChildren().toNodeArray();
                    if (pagerChilds.length > 1) {
                        //Log.i(LOG_TAG, pagerChilds[pagerChilds.length-3].toHtml());
                        String tempPage;
                        for (int i = pagerChilds.length - 1; i >= 0; i--) {
                            if (!pagerChilds[i].toPlainTextString().equals(" ")) {
                                if (!pagerChilds[i].toPlainTextString().contains("下一页")) {
                                    // If not single item.
                                    tempPage = pagerChilds[i].toPlainTextString();
                                    Log.i(LOG_TAG, "tempPage:" + tempPage);
                                    if (tempPage != null) {
                                        maxPage = Integer.valueOf(tempPage);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                        /*
                if (pagerChilds.length>1){
                    //Log.i(LOG_TAG, pagerChilds[pagerChilds.length-3].toHtml());
                    String tempPage;
                    if(pagerChilds[pagerChilds.length-2].toPlainTextString().startsWith("<a") ||
                            pagerChilds[pagerChilds.length-2].toPlainTextString().startsWith("<span") ){
                        tempPage = pagerChilds[pagerChilds.length-2].toPlainTextString();
                    } else
                        tempPage = pagerChilds[pagerChilds.length-3].toPlainTextString();
                    //Log.i(LOG_TAG, "Max page:"+tempPage);
                    if (tempPage!=null) {
                        maxPage = Integer.valueOf(tempPage);
                    }
                }
            */
                Log.i(LOG_TAG, "Max page:" + maxPage);
            } catch (ParserException e) {
                e.printStackTrace();
                Log.i(LOG_TAG, "Cannot get pages.");
            }
        }else{
            throw new AccountOutOfDateException("Out of date!");
        }

    }




}
