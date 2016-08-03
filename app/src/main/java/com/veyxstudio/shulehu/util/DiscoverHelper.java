package com.veyxstudio.shulehu.util;

import android.content.Context;
import android.util.Log;

import com.veyxstudio.shulehu.object.Parseable;
import com.veyxstudio.shulehu.object.RunRecord;
import com.veyxstudio.shulehu.object.TradeRecord;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Veyx Shaw on 16-1-9.
 * Help to get the data of discovery function.
 *
 * Parse web page according to the source.
 * Create Record List and return the list.
 */
public class DiscoverHelper extends HttpHelper
        implements Parseable{
    private final String LOG_TAG = "DiscoverHelper";

    public DiscoverHelper(String url){
        super(HttpHelper.METHOD_POST, url, "utf-8");

    }

    @Override
    public void ParseResult(String html, String url) {
        Parser parser = Parser.createParser(html,"utf-8");
        try {
            nodeList = parser.extractAllNodesThatMatch(new TagNameFilter("td"));
        } catch (ParserException e) {
            e.printStackTrace();
        }
        if (url.equals(URLHelper.cardTrainingDetail)){
            // Create a RunRecord List
            runRecordList = new ArrayList<>();
            // Iterate the NodeList
            SimpleNodeIterator iterator = nodeList.elements();
            while (iterator.hasMoreNodes()) {
                iterator.nextNode();
                // Transform each node to a RunRecord and add it to the list
                if (iterator.hasMoreNodes()) {
                    Node node = iterator.nextNode();
                    RunRecord runRecord =
                            new RunRecord(node.toPlainTextString());
                    runRecordList.add(runRecord);
                    Log.i(LOG_TAG, runRecord.getTime());
                }
            }
        }else if (url.equals(URLHelper.cardTradeDetail)){
            // Create trade total
            //<input name="startDay" onfocus="setday(this)" value="2016-03-15 00:21">
            //<input name="endDay" onfocus="setday(this)" value="2016-03-22 00:21">
            Parser totalParser = Parser.createParser(html,"utf-8");
            HasAttributeFilter ctl00_Contentplaceholder1_Label1 =
                    new HasAttributeFilter("id","ctl00_Contentplaceholder1_Label1");
            try {
                NodeList totalList = totalParser.extractAllNodesThatMatch(ctl00_Contentplaceholder1_Label1);
                NodeIterator totalIterator = totalList.elements();
                tradeTotal = "暂无信息";
                while(totalIterator.hasMoreNodes()){
                    tradeTotal = totalIterator.nextNode().toPlainTextString();
                }
                Log.i(LOG_TAG, tradeTotal);
            } catch (ParserException e) {
                e.printStackTrace();
            }
            // Create a tradeRecord List
            tradeRecordList = new ArrayList<>();
            // Iterate the NodeList
            SimpleNodeIterator iterator = nodeList.elements();
            while (iterator.hasMoreNodes()) {
                iterator.nextNode();
                // Transform each node to a tradeRecord and add it to the list
                String[] temp = new String[]{"","","",""};
                for (int i=0; i<4;i++) {
                    if (iterator.hasMoreNodes())
                        temp[i] = iterator.nextNode().toPlainTextString();
                }
                Log.i(LOG_TAG,temp[0]+" "+temp[1]+" "+temp[2]+" "+temp[3]);
                TradeRecord tradeRecord = new TradeRecord(temp[0], temp[1], temp[2], temp[3]);
                tradeRecordList.add(tradeRecord);
            }
        }
    }

    public List<RunRecord> getRunRecordList(){
            return runRecordList;
    }
    public List<TradeRecord> getTradeRecordList(){
        return tradeRecordList;
    }
    public String getTradeTotal(){
        return tradeTotal;
    }
    private NodeList nodeList;
    private List<RunRecord> runRecordList;
    private List<TradeRecord> tradeRecordList;
    private String tradeTotal;

    public void discover() throws HttpHelperException,AccountOutOfDateException{
        super.start();
        if(!super.getResult().contains(KeyWordHelper.notLogin)) {
            this.ParseResult(super.getResult(), super.getUrl());
        }else{
            throw new AccountOutOfDateException("Out of date!");
        }
    }

}
