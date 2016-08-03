package com.veyxstudio.shulehu.object;

import android.util.Log;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

/**
 * Created by Veyx Shaw on 2016/3/25.
 * Describe an article list.
 */
public class ArticleList {

    private final String LOG_TAG = "ArticleList";


    public ArticleList(String listTable){
        Log.i(LOG_TAG, "length:" + listTable.length());
        parseTitle(listTable);
        parseDate(listTable);
        parseClick(listTable);
        parseSource(listTable);
    }

    private String title = "未知";
    private int aid = 1;
    private String date = "1980/1/1 0:00:00";
    private String click = "0/0";
    private String source = "";

    public String getTitle() { return title; }
    public int getAid() { return aid; }
    public String getDate() { return date; }
    public String getClick() { return click; }
    public String getSource() {
        return source;
    }



    private void parseTitle(String table){
        // <td class="subject"><a>
        Parser parser = Parser.createParser(table, "utf-8");
        HasAttributeFilter subjectFilter = new HasAttributeFilter("class","subject");
        HasAttributeFilter subjectEssenceFilter = new HasAttributeFilter("class","subject essence");
        HasAttributeFilter subjectBestFilter = new HasAttributeFilter("class","subject best");
        OrFilter allSubjectFilter = new OrFilter(subjectFilter,
                new OrFilter(subjectEssenceFilter,subjectBestFilter));
        try {
            NodeList nodeList = parser.extractAllNodesThatMatch(allSubjectFilter);
            Node[] nodes = nodeList.toNodeArray();
            if (nodes.length>0) {
                TagNameFilter titleFilter = new TagNameFilter("a");
                NodeList titleList = nodes[0].getChildren();
                Node[] titleOne = titleList.extractAllNodesThatMatch(titleFilter).toNodeArray();
                if (titleOne.length>0) {
                    title = titleOne[0].toPlainTextString();
                    title = title.trim();
                    parseAid(titleOne[0].toHtml());
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Failed to parse.");
        }
        Log.i(LOG_TAG, "title:"+ title);
    }
    private void parseDate(String table){
        Parser parser = Parser.createParser(table, "utf-8");
        TagNameFilter spanFilter = new TagNameFilter("span");
        HasAttributeFilter dateClassFilter = new HasAttributeFilter("class" , "date");
        AndFilter dateFilter = new AndFilter(dateClassFilter, spanFilter);
        try {
            NodeList nodeList = parser.extractAllNodesThatMatch(dateFilter);
            SimpleNodeIterator nodeIterator = nodeList.elements();
            while (nodeIterator.hasMoreNodes()){
                Node node = nodeIterator.nextNode();
                // Choose the latest date.
                date = node.toPlainTextString();
            }
        } catch (ParserException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Failed to parse.");
        }
        Log.i(LOG_TAG, "date:"+ date);
    }
    private void parseClick(String table){
        //<td><span class="red">0/142</span></td>
        Parser parser = Parser.createParser(table, "utf-8");
        TagNameFilter clickFilter = new TagNameFilter("td");
        try {
            NodeList nodeList = parser.extractAllNodesThatMatch(clickFilter);
            Node[] nodes = nodeList.toNodeArray();
            if (nodes.length>2) {
                click = nodes[2].toPlainTextString().trim();
            }
        } catch (ParserException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Failed to parse.");
        }
        Log.i(LOG_TAG, "click:"+ click);
    }
    private void parseSource(String table){
        Parser parser = Parser.createParser(table, "utf-8");
        TagNameFilter sourceFilter = new TagNameFilter("td");
        try {
            NodeList nodeList = parser.extractAllNodesThatMatch(sourceFilter);
            Node[] nodes = nodeList.toNodeArray();
            if (nodes.length>4) {
                NodeList sourceList = nodes[4].getChildren();
                TagNameFilter sourceOneFilter = new TagNameFilter("a");
                NodeList sourceOne = sourceList.extractAllNodesThatMatch(sourceOneFilter);
                if (sourceOne.toNodeArray().length>0)
                    source = sourceOne.toNodeArray()[0].toPlainTextString();
            }
        } catch (ParserException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Failed to parse.");
        }
        Log.i(LOG_TAG, "source:"+ source);
    }

    private void parseAid(String aTag){
        if (aTag.contains("href=\"http://bbs.lehu.shu.edu.cn/Article.aspx?aid=")){
            String aidTemp = aTag.substring(
                    aTag.indexOf("href=\"http://bbs.lehu.shu.edu.cn/Article.aspx?aid=") +
                            "href=\"http://bbs.lehu.shu.edu.cn/Article.aspx?aid=".length() ,
                            aTag.indexOf("http://bbs.lehu.shu.edu.cn/Article.aspx") +
                            aTag.substring(aTag.indexOf("http://bbs.lehu.shu.edu.cn/Article.aspx")).indexOf("\""));
            Log.i(LOG_TAG, "aidTemp:" + aidTemp);
            aidTemp = aidTemp.replace(" ","");
            aid = Integer.valueOf(aidTemp);
            Log.i(LOG_TAG, "aid:" + aid);
        } else {
            aid = 1;
        }
    }

}
