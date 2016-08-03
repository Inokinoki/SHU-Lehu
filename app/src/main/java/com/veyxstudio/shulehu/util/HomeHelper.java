package com.veyxstudio.shulehu.util;

import android.support.annotation.Nullable;
import android.util.Log;

import com.veyxstudio.shulehu.object.Parseable;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

/**
 * Created by Veyx Shaw on 2016/2/9.
 * Load and handle home page content.
 */
public class HomeHelper extends HttpHelper implements Parseable{

    private String home = "";

    public HomeHelper(){
        super(HttpHelper.METHOD_GET, "http://bbs.lehu.shu.edu.cn", "utf-8");
    }

    public String getHTML(){
        return this.home;
    }

    public void home() throws HttpHelperException{
        super.start();
        ParseResult(super.getResult(), null);
        // Log.i("HTML", home);
    }

    @Override
    public void ParseResult(String html, @Nullable String url) {
        Parser parser = Parser.createParser(html, "utf-8");
        HasAttributeFilter hasAttributeFilter =
                new HasAttributeFilter("class", KeyWordHelper.homeDivKey);
        try {
            NodeList nodeList = parser.extractAllNodesThatMatch(hasAttributeFilter);
            Node nodes[] = nodeList.toNodeArray();
            for (int i = 0; i < nodes.length; i++) {
                switch (i){
                    case 1:case 2:case 4:
                    case 6:case 7:case 8:
                    case 11:case 13:
                        home += nodes[i].toHtml();
                        break;
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }
}
