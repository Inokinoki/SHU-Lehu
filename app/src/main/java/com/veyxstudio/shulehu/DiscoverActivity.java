package com.veyxstudio.shulehu;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.veyxstudio.shulehu.handler.DiscoverHandler;
import com.veyxstudio.shulehu.object.RunRecord;
import com.veyxstudio.shulehu.object.TradeRecord;
import com.veyxstudio.shulehu.util.AccountOutOfDateException;
import com.veyxstudio.shulehu.util.DiscoverHelper;
import com.veyxstudio.shulehu.util.HttpHelperException;
import com.veyxstudio.shulehu.util.KeyWordHelper;
import com.veyxstudio.shulehu.util.URLHelper;
import com.veyxstudio.shulehu.view.BackOnClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

/**
 * Created by Veyx Shaw on 16-1-9.
 * Execute the task of getting card information.
 */
public class DiscoverActivity extends AppCompatActivity
        implements Runnable{
    private String LOG_TAG = "DiscoverActivity";

    private Snackbar loadSnackBar;

    public void closeSnackBar(){
        if (loadSnackBar!=null)
            loadSnackBar.dismiss();
    }

    private DiscoverHandler discoverHandler;
    private DiscoverHelper discoverHelper;

    private List<RunRecord> runRecordList;
    private List<TradeRecord> tradeRecordList;
    private String tradeTotal;

    public List<RunRecord> getRunRecordList() {
        return runRecordList;
    }
    public List<TradeRecord> getTradeRecordList() {
        return tradeRecordList;
    }
    public String getTradeTotal(){return tradeTotal;}

    private DatePickerDialog beginDatePicker;
    private DatePickerDialog endDatePicker;
    private DatePickerDialog.OnDateSetListener onBeginDateSetListener;
    private DatePickerDialog.OnDateSetListener onEndDateSetListener;

    public void clearBusy() {this.busy = false;}
    private boolean busy = false;

    private String result;
    private String url;

    public String getUrl(){
        return this.url;
    }

    private ImageButton beginTimePicker;
    private ImageButton endTimePicker;
    private Button start;
    private Button reset;
    private TextView begin;
    private TextView end;

    private String beginTime;
    private String endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        discoverHandler = new DiscoverHandler(this);
        initView();
        initData();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.discover_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_action_arrow_left);
        toolbar.setNavigationOnClickListener(new BackOnClickListener(this));
        url = this.getIntent().getExtras().getString("URL","");
        if (url.equals(URLHelper.cardTrainingDetail)){
            // Card Training view init.
            LinearLayout timePickerLayout =
                    (LinearLayout)findViewById(R.id.discover_time_picker_layout);
            timePickerLayout.setVisibility(View.GONE);
        } else {
            // Card Trade view init.
            start = (Button)findViewById(R.id.discover_time_picker_start);
            reset = (Button)findViewById(R.id.discover_time_picker_reset);
            begin = (TextView)findViewById(R.id.discover_time_begin);
            end = (TextView)findViewById(R.id.discover_time_end);
            // Set
            beginTimePicker = (ImageButton)findViewById(R.id.discover_time_picker_begin);
            beginTimePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int y,m,d;
                    String[] dates = begin.getText().toString().replace("+"," ").split(" ")[0].split("-");
                    y = Integer.parseInt(dates[0]);
                    m = Integer.parseInt(dates[1])-1;
                    d = Integer.parseInt(dates[2]);
                    beginDatePicker.updateDate(y,m,d);
                    beginDatePicker.show();
                }
            });
            endTimePicker = (ImageButton)findViewById(R.id.discover_time_picker_end);
            endTimePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int y,m,d;
                    String[] dates = end.getText().toString().replace("+"," ").split(" ")[0].split("-");
                    y = Integer.parseInt(dates[0]);
                    m = Integer.parseInt(dates[1])-1;
                    d = Integer.parseInt(dates[2]);
                    endDatePicker.updateDate(y,m,d);
                    endDatePicker.show();
                }
            });

            // Set DatePickerDialog Callback
            onBeginDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    beginTime = year+"-"+(monthOfYear+1)+"-"+dayOfMonth+"+"+
                        begin.getText().toString().replace("+"," ").split(" ")[1];
                    begin.setText(beginTime.replace("+", " "));
                    Log.i(LOG_TAG, beginTime);
                }
            };
            onEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    endTime = year+"-"+(monthOfYear+1)+"-"+dayOfMonth+"+"+
                            end.getText().toString().replace("+"," ").split(" ")[1];
                    end.setText(endTime.replace("+", " "));
                    Log.i(LOG_TAG, endTime);
                }
            };
            beginDatePicker = new DatePickerDialog(this, onBeginDateSetListener,0,0,0);
            endDatePicker = new DatePickerDialog(this, onEndDateSetListener,0,0,0);

            // Set commit and reset Button
            reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat("yyyy-MM-dd+kk:mm", Locale.getDefault());
                    beginTime = simpleDateFormat.format(new Date(System.currentTimeMillis()-7*24*3600*1000));
                    endTime = simpleDateFormat.format(new Date(System.currentTimeMillis()));
                    begin.setText(beginTime.replace("+", " "));
                    end.setText(endTime.replace("+", " "));
                }
            });
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!busy) {
                        loadSnackBar = Snackbar.make(findViewById(R.id.discover_layout),
                                R.string.article_load, Snackbar.LENGTH_INDEFINITE);
                        loadSnackBar.show();
                        new Thread(DiscoverActivity.this).start();
                    } else {
                        Toast.makeText(DiscoverActivity.this,
                                R.string.discover_busy, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initData(){
        // Init time
        if(url.equals(URLHelper.cardTradeDetail)) {
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd+kk:mm", Locale.getDefault());
            beginTime = simpleDateFormat.format(new Date(System.currentTimeMillis() - 7 * 24 * 3600 * 1000));
            endTime = simpleDateFormat.format(new Date(System.currentTimeMillis()));
            begin.setText(beginTime.replace("+", " "));
            end.setText(endTime.replace("+", " "));
        }
        loadSnackBar = Snackbar.make(findViewById(R.id.discover_layout),
                R.string.article_load, Snackbar.LENGTH_INDEFINITE);
        loadSnackBar.show();
        new Thread(this).start();
    }

    @Override
    public void run() {
        this.busy = true;
        // Generate DiscoverHelper
        if (discoverHelper==null) {
            discoverHelper = new DiscoverHelper(url);
            discoverHelper.addCookie(KeyWordHelper.pPassport,
                    getSharedPreferences(KeyWordHelper.pFileName, Context.MODE_PRIVATE)
                            .getString(KeyWordHelper.pPassport, ""));
            discoverHelper.setNeedEncode(false);
        }
        try {
            if (url.equals(URLHelper.cardTrainingDetail)) {
                discoverHelper.discover();
                Log.i(this.LOG_TAG,
                        discoverHelper.getRunRecordList().size() + "");
                this.runRecordList = discoverHelper.getRunRecordList();
            } else if (url.equals(URLHelper.cardTradeDetail)) {
                // Get value
                // Value format
                Log.i(LOG_TAG, beginTime);
                Log.i(LOG_TAG, endTime);
                discoverHelper.addParam(KeyWordHelper.coverStartTime, beginTime, false);
                discoverHelper.addParam(KeyWordHelper.coverEndTime, endTime, false);
                discoverHelper.discover();
                Log.i(this.LOG_TAG,
                        discoverHelper.getTradeRecordList().size() + "");
                this.tradeRecordList = discoverHelper.getTradeRecordList();
                tradeTotal = discoverHelper.getTradeTotal();
            }
            Log.i(LOG_TAG, "Send Message.");
            discoverHandler.sendEmptyMessage(KeyWordHelper.LOAD_OK);
        }catch (HttpHelperException e){
            e.printStackTrace();
            discoverHandler.sendEmptyMessage(KeyWordHelper.NO_NETWORK);
        }catch (AccountOutOfDateException e){
            e.printStackTrace();
            discoverHandler.sendEmptyMessage(KeyWordHelper.STATE_OUT);
        }
    }

}
