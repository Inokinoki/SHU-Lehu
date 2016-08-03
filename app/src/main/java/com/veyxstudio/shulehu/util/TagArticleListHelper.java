package com.veyxstudio.shulehu.util;

import android.util.Log;

import com.veyxstudio.shulehu.object.ArticleList;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Veyx Shaw on 2016/2/17.
 * Show articles in Tags
 */
public class TagArticleListHelper extends HttpHelper{
    private static final String LOG_TAG = "TagArticleListHelper";

    public TagArticleListHelper(String key){
        super(METHOD_GET, URLHelper.baseTag, "utf-8");
        super.setNeedEncode(false);
        super.addParam("key", key, true);
    }
    public TagArticleListHelper(String key, int page){
        this(key);
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

    public void load() throws HttpHelperException {
        super.addParam("page", page + "", true);
        super.start();

        String result = super.getResult();
        // Get items.
        Parser parser = Parser.createParser(result, "utf-8");
        Log.i(LOG_TAG,result);
        articleListList = new ArrayList<>();
        try {
                TagNameFilter trFilter = new TagNameFilter("tr");
                NodeList trList = parser.extractAllNodesThatMatch(trFilter);
                Node[] trItems = trList.toNodeArray();
                if(trItems.length>1) {
                    Log.i(LOG_TAG,trItems.length+"");
                    for (int i = 1; i < trItems.length; i++) {
                        ArticleList articleList = new ArticleList(trItems[i].toHtml());
                        articleListList.add(articleList);
                    }
                }
        } catch (ParserException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "Cannot get items.");
        }

        Parser pagerParser = Parser.createParser(result, "utf-8");
        HasAttributeFilter pagerFilter = new HasAttributeFilter("class","pager");
        try {
            NodeList pagerNode = pagerParser.extractAllNodesThatMatch(pagerFilter);
            Node[]items = pagerNode.toNodeArray();
            if (items.length>0){
                // Get pager
                Node[] pagerChilds = items[0].getChildren().toNodeArray();
                if (pagerChilds.length>1) {
                    //Log.i(LOG_TAG, pagerChilds[pagerChilds.length-3].toHtml());
                    String tempPage;
                    for (int i = pagerChilds.length - 1; i >= 0; i--) {
                        if (!pagerChilds[i].toPlainTextString().equals(" ")) {
                            if (!pagerChilds[i].toPlainTextString().contains("下一页")
                                    &&!pagerChilds[i].toPlainTextString().contains("上一页")
                                    &&!pagerChilds[i].toPlainTextString().contains("(共")) {
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
            Log.i(LOG_TAG, "Max page:"+ maxPage);
        } catch (ParserException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "Cannot get pages.");
        }

    }




}
