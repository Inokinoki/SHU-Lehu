package com.veyxstudio.shulehu.object;

/**
 * Created by Veyx Shaw on 16-1-9.
 * Store the trade record.
 */
public class TradeRecord {
    public TradeRecord(String date ,String time
            ,String money ,String detail){
        this.date = date;
        this.time = time;
        this.money = money;
        this.detail = detail;
    }
    private String date;
    private String time;
    private String money;
    private String detail;

    public String getMoney() {
        return money;
    }
    public String getDetail() {
        return detail;
    }
    public String getDate() {
        return date;
    }
    public String getTime(){
        return this.time;
    }
}
