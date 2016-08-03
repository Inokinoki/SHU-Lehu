package com.veyxstudio.shulehu.util;

import android.support.annotation.Nullable;
import android.util.Log;

import com.veyxstudio.shulehu.object.Parseable;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * Created by Veyx Shaw on 2016/2/16.
 * Help to get data.
 */
public class CategoryHelper extends HttpHelper implements Parseable {

    private String cate = "";

    public CategoryHelper(){ super(HttpHelper.METHOD_GET, "http://bbs.lehu.shu.edu.cn", "utf-8"); }

    public void cate() throws HttpHelperException{
        super.start();
        ParseResult(super.getResult(), null);
        Log.i("HTML", cate);
    }
    public String getHTML(){return this.cate;}

    @Override
    public void ParseResult(String html, @Nullable String url) {
        Parser parser = Parser.createParser(html, "utf-8");
        HasAttributeFilter hasAttributeFilter =
                new HasAttributeFilter("class", KeyWordHelper.cateDivKey);
        try {
            NodeList nodeList = parser.extractAllNodesThatMatch(hasAttributeFilter);
            Node nodes[] = nodeList.toNodeArray();
            for (int i = 0; i < nodes.length; i++) {
                    cate += nodes[i].toHtml();
                    break;
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }
}
