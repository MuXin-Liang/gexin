package com.example.joe.gexin;

import android.app.Application;
import android.content.Context;

/**
 * Created by Joe on 2016/11/2.
 */
public class ContextApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        ContextApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ContextApplication.context;
    }
}
