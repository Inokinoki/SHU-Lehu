package com.veyxstudio.shulehu.handler;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.veyxstudio.shulehu.ArticleListActivity;
import com.veyxstudio.shulehu.DiscoverActivity;
import com.veyxstudio.shulehu.LoginActivity;
import com.veyxstudio.shulehu.object.RunRecord;
import com.veyxstudio.shulehu.R;
import com.veyxstudio.shulehu.object.TradeRecord;
import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.util.URLHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

/**
 * Created by Veyx Shaw on 16-1-26.
 * Handle discover ready events.
 */
public class DiscoverHandler extends Handler{
    private String LOG_TAG = "DiscoverHandler";

    public DiscoverHandler(DiscoverActivity discoverActivity){
        activityReference = new WeakReference<>(discoverActivity);
    }


    private WeakReference<DiscoverActivity> activityReference;

    @Override
    public void handleMessage(Message msg) {
        Log.i(LOG_TAG,"Handle Message.");
        switch (msg.what){
            case KeyWordHelper.LOAD_OK:
                generateList();
                activityReference.get().clearBusy();activityReference.get().closeSnackBar();
                break;
            case KeyWordHelper.STATE_OUT:
                Toast.makeText(activityReference.get(),
                        R.string.no_SHUPassport,
                        Toast.LENGTH_SHORT).show();
                DiscoverActivity activity = activityReference.get();
                activity.clearBusy();activity.closeSnackBar();
                Intent intent = new Intent(activity, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(KeyWordHelper.relogin,1);
                intent.putExtras(bundle);
                activity.startActivity(intent);
                activity.finish();
                break;
            case KeyWordHelper.NO_NETWORK:
                Toast.makeText(activityReference.get(),
                        R.string.no_network,
                        Toast.LENGTH_SHORT).show();
                activityReference.get().clearBusy();activityReference.get().closeSnackBar();
                break;
        }
    }

    private void generateList(){
        DiscoverActivity activity;
        if (activityReference.get().getUrl().
                equals(URLHelper.cardTrainingDetail)) {
            activity = activityReference.get();
            int count = 0;
            Log.i(LOG_TAG,"Generate Run Record List");
            ListView listView = (ListView)activity.findViewById(R.id.list_discovery);
            SimpleAdapter adapter;
            String[] keys = {"Time"};
            int[] ids = {R.id.list_run_time};
            ListIterator<RunRecord> iterator = activity.getRunRecordList().listIterator();
            ArrayList<HashMap<String,String>> listItem =
                    new ArrayList<>();
            while (iterator.hasNext()){
                HashMap<String, String> map = new HashMap<>();
                map.put("Time" , iterator.next().getTime());
                listItem.add(map);
                count++;
            }
            adapter = new SimpleAdapter(activity,
                    listItem, R.layout.list_run, keys, ids);
            listView.setAdapter(adapter);
            TextView total = (TextView)activity.findViewById(R.id.discover_trade_total);
            String totalStirng = activity.getResources().getText(R.string.discover_total).toString()
                    + count;
            total.setText(totalStirng);
        } else {
            activity = activityReference.get();
            // Generate Trade total
            TextView total = (TextView)activity.findViewById(R.id.discover_trade_total);
            total.setText(activity.getTradeTotal());
            // Generate Trade Record List
            Log.i(LOG_TAG, "Generate Trade Record List");
            ListView listView = (ListView)activity.findViewById(R.id.list_discovery);
            SimpleAdapter adapter;
            String[] keys = {/*"Time",*/ "Date", "Money", "Detail"};
            int[] ids = {/*R.id.trade_list_time,*/ R.id.trade_list_date,
                    R.id.trade_list_money, R.id.trade_list_detail};
            ListIterator<TradeRecord> iterator = activity.getTradeRecordList().listIterator();
            ArrayList<HashMap<String,String>> listItem = new ArrayList<>();
            while (iterator.hasNext()){
                HashMap<String, String> map = new HashMap<>();
                TradeRecord tradeRecord = iterator.next();
                //map.put("Time" , tradeRecord.getTime());
                map.put("Date" , tradeRecord.getDate());
                map.put("Money" , tradeRecord.getMoney());
                map.put("Detail" , tradeRecord.getDetail());
                listItem.add(map);
            }
            adapter = new SimpleAdapter(activity,
                    listItem, R.layout.list_money, keys, ids);
            listView.setAdapter(adapter);
        }
    }
}
