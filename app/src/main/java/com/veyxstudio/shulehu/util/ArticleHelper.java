package com.veyxstudio.shulehu.util;

import android.util.Log;

import com.veyxstudio.shulehu.object.Article;

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
public class ArticleHelper extends HttpHelper{
    private static final String LOG_TAG = "ArticleHelper";

    public ArticleHelper(int aid){
        super(METHOD_GET, "http://bbs.lehu.shu.edu.cn/Article.aspx", "utf-8");
        super.addParam("aid", aid + "", true);
    }
    public ArticleHelper(int aid, int page){
        this(aid);
        super.addParam("page", page + "", true);
    }

    public int getPage() {return page;}
    public void setPage(int page){this.page = page;}
    public String getTitle(){return  this.title;}
    public int getMaxPage() { return maxPage; }
    public List<Article> getArticleList(){return this.articleList;}

    private String title = "";
    private List<Article> articleList;
    private int page = 1;
    private int maxPage = 1;

    public void load() throws HttpHelperException, AccountOutOfDateException{
        super.addParam("page", page + "", true);
        super.start();

        String result = super.getResult();
        if(!result.contains(KeyWordHelper.notLogin)) {
            // Parse title.
            Parser titleParser = Parser.createParser(result, "utf-8");
            TagNameFilter titleTag = new TagNameFilter("title");
            try {
                NodeList titleNode = titleParser.extractAllNodesThatMatch(titleTag);
                SimpleNodeIterator iterator = titleNode.elements();
                while (iterator.hasMoreNodes()) {
                    title += iterator.nextNode().toPlainTextString();
                    Log.i(LOG_TAG, title);
                }
                if (title.contains(" - 上海大学乐乎论坛"))
                    title = title.substring(0, title.indexOf(" - 上海大学乐乎论坛"));
            } catch (ParserException e) {
                e.printStackTrace();
                Log.i(LOG_TAG, "Cannot get title.");
            }
            // Get items.
            Parser parser = Parser.createParser(result, "utf-8");
            articleList = new ArrayList<>();
            HasAttributeFilter articleTable = new HasAttributeFilter("class", "articletable");
            try {
                NodeList itemNode = parser.extractAllNodesThatMatch(articleTable);
                Node[] items = itemNode.toNodeArray();
                // Not need the last one(the comment article table)
                int i;
                if (page == 1) i = 0;
                else i = 1;
                for (; i < items.length - 1; i++) {
                    Article article = new Article(items[i].toHtml());
                    articleList.add(article);
                }
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
                                if (!pagerChilds[i].toPlainTextString().contains("下一页") &&
                                        !pagerChilds[i].toPlainTextString().contains("上一页") &&
                                        !pagerChilds[i].toPlainTextString().contains("共")) {
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
                Log.i(LOG_TAG, "Max page:" + maxPage);
            } catch (ParserException e) {
                e.printStackTrace();
                Log.i(LOG_TAG, "Cannot get pages.");
            }
        }else {
            throw new AccountOutOfDateException("Out of date!");
        }

    }




}
