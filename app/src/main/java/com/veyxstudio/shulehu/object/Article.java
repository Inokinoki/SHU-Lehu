package com.veyxstudio.shulehu.object;

import android.text.Html;
import android.util.Log;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

/**
 * Created by Veyx Shaw on 2016/3/22.
 * Describe an article in app.
 */
public class Article {
    private final String LOG_TAG = "Article";

    public Article(){

    }

    public Article(String articleTable){
        Log.i(LOG_TAG, "length:"+ articleTable.length());
        parseUsername(articleTable);
        parseDate(articleTable);
        parseSignature(articleTable);
        parseContent(articleTable);
        //Parser parser = Parser.createParser(articleTable, "utf-8");
//        try {
//
//            // Failed
////            while(contentTableIterator.hasMoreNodes()){
////                Node contentTable = contentTableIterator.nextNode();
////                NodeList table = contentTable.getChildren();
////                NodeIterator tableIterator = table.elements();
////                while (tableIterator.hasMoreNodes()){
////                    Node tbody = tableIterator.nextNode();
////                    Log.i(LOG_TAG, tbody.toHtml());
//////                    NodeList tr = tbody.getChildren();
//////                    NodeIterator trIterator = tr.elements();
//////                    while (trIterator.hasMoreNodes()) {
//////                        Log.i(LOG_TAG, trIterator.nextNode().toHtml());
//////                    }
////                }
////            }
//        } catch (ParserException e) {
//            e.printStackTrace();
//            Log.i(LOG_TAG,"Parse error!");
//            username = "Null";
//            date = "Null";
//            content = "Null";
//            signature = "Null";
//        }
    }

    private String username = " ";
    private String date = "发表于 1980/1/1 0:00:00";
    private String content = "<p>Error</p>";

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getSignature() {
        return signature;
    }

    private String signature;

    private void parseUsername(String table){
        Parser parser = Parser.createParser(table, "utf-8");
        TagNameFilter strongUserName = new TagNameFilter("a");
        try {
            NodeList nodeList = parser.extractAllNodesThatMatch(strongUserName);
            Node[] nodes = nodeList.toNodeArray();
            if (nodes.length>0) {
                username = nodes[0].toPlainTextString();
            }
        } catch (ParserException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Failed to parse.");
        }
        Log.i(LOG_TAG, "username:"+ username);
    }
    private void parseDate(String table){
        Parser parser = Parser.createParser(table, "utf-8");
        TagNameFilter tdFilter = new TagNameFilter("td");
        HasAttributeFilter topAlignFilter = new HasAttributeFilter("valign" , "top");
        AndFilter dateFilter = new AndFilter(topAlignFilter, tdFilter);
        try {
            NodeList nodeList = parser.extractAllNodesThatMatch(dateFilter);
            SimpleNodeIterator nodeIterator = nodeList.elements();
            while (nodeIterator.hasMoreNodes()){
                Node node = nodeIterator.nextNode();
                if (node.toPlainTextString().startsWith("发表于")){
                    date = node.toPlainTextString();
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Failed to parse.");
        }
        Log.i(LOG_TAG, "date:"+ date);
    }
    private void parseContent(String table){
        //class="articlecontent" id="articlecontent"
        Parser parser = Parser.createParser(table, "utf-8");
        HasAttributeFilter idFilter = new HasAttributeFilter("id","articlecontent");
        HasAttributeFilter classFilter = new HasAttributeFilter("class","articlecontent");
        AndFilter contentFilter = new AndFilter(idFilter, classFilter);
        try {
            NodeList nodeList = parser.extractAllNodesThatMatch(contentFilter);
            Node[] nodes = nodeList.toNodeArray();
            if (nodes.length>0) {
                content = "<html><head>" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href='file:///android_asset/article.css'>" +
                        "<script type=\"text/javascript\" href='file:///android_asset/imgresize.js'></script>"+
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"></head><body>" +
                        nodes[0].toHtml() +
                        "<script type=\"text/javascript\">windows.onload=ResizeAllImage;</script>"+
                        "</body></html>";
            }
        } catch (ParserException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Failed to parse.");
        }
        Log.i(LOG_TAG, "content:"+ content);
    }
    private void parseSignature(String table){
        Parser parser = Parser.createParser(table, "utf-8");
        HasAttributeFilter signatureFilter = new HasAttributeFilter("id","signature");
        try {
            NodeList nodeList = parser.extractAllNodesThatMatch(signatureFilter);
            Node[] nodes = nodeList.toNodeArray();
            if (nodes.length>0) {
                // Regexp to remove html tag
                signature = Html.fromHtml(nodes[0].toPlainTextString()).toString()
                        .replaceAll("<(.|\n)*?>"," ");
            }
        } catch (ParserException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Failed to parse.");
        }
        Log.i(LOG_TAG, "signature:"+ signature);
    }

}
