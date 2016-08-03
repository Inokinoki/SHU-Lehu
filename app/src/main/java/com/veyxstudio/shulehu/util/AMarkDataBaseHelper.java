package com.veyxstudio.shulehu.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Veyx Shaw on 2016/4/5.
 * Article Mark Database helper.
 */
public class AMarkDataBaseHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "AMarkDataBaseHelper";

    public static String tableName 	= "ArticleMarkTable";
    public static String tagAColume	= "A";      // Aid
    public static String tagBColume	= "B";      // Name

    public AMarkDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+tableName+ "("+ tagAColume + " INTEGER," + tagBColume + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LOG_TAG, "Databse upgrade");
    }


}
