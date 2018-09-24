package com.example.joe.gexin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Joe on 2017/3/6.
 */

public class mMsgDBHelper extends SQLiteOpenHelper {

    public final static int version = 1;
    public final static String DBNAME = "MsgSetDB";
    public final static String MSGSET = "msgset";

    public mMsgDBHelper(Context context) {
        super(context, DBNAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        String create_msgset_sql =
                "CREATE TABLE if not exists [" + DBNAME + "]" + "" +
                        "(_id integer primary key autoincrement," + MSGSET + " text, UUID text)";
        db.execSQL(create_msgset_sql);

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
