package com.example.joe.gexin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.example.joe.contactor20.R;

/**
 * Created by Joe on 2017/3/29.
 */

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        Intent intent=new Intent();
        intent.setClass(WelcomeActivity.this,StartActivity.class);
        startActivity(intent);
        finish();
        //用以识别是否为第一次启动
        /*

        float nowVersionCode = getVersionCode(WelcomeActivity.this);

        Log.i("welcome","最新版本号："+nowVersionCode);

        SharedPreferences sp= getSharedPreferences("welcomeInfo",MODE_PRIVATE);
        float spVersionCode =sp.getFloat("spVerisionCode",0);

        Log.i("welcome","记录版本号："+spVersionCode);
        if(nowVersionCode>spVersionCode)
        {
            setContentView(R.layout.start_layout);
            SharedPreferences.Editor editor=sp.edit();
            editor.putFloat("spVersionCode",nowVersionCode);
            editor.commit();
        }
        else {
            Intent intent=new Intent();
            intent.setClass(WelcomeActivity.this,StartActivity.class);
            startActivity(intent);
        }
        */
    }

    private float getVersionCode(Context context){
        float versionCode=0;
        try {
            versionCode =context.getPackageManager().getPackageInfo(
                    context.getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return versionCode;
    }
}
