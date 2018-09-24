package com.example.joe.gexin;

import android.app.Application;
import android.content.Context;

/**
 * Created by Joe on 2016/11/13.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        //获取Context
        super.onCreate();
        context = getApplicationContext();
    }

    //返回
    public static Context getContextObject() {
        return context;
    }
}
