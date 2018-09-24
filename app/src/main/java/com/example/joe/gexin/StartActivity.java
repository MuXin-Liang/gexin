package com.example.joe.gexin;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.joe.contactor20.R;

/**
 * Created by Joe on 2017/3/16.
 */

public class StartActivity extends Activity {

    public static final int CODE_SEND_SMS = 0;
    public static final int CODE_READ_CONTACTS = 1;
    public static final int CODE_READ_EXTERNAL_STORAGE = 2;
    public static final int CODE_MULTI_PERMISSION = 100;

    public static final String PERMISSION_SEND_SMS = Manifest.permission.SEND_SMS;
    public static final String PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        new Thread(mRunnable).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(ContextCompat.checkSelfPermission(StartActivity.this,
                    PERMISSION_READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED&&
                    ContextCompat.checkSelfPermission(StartActivity.this,
                            PERMISSION_READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                startOurApp();
            }
            else {
                permissionsRequest(PERMISSION_SEND_SMS, CODE_SEND_SMS);
            }
        }
    };


    private void startOurApp() {
        ContactLab.initContactLab(StartActivity.this);
        if(ContactLab.returnContactNull()){
            Looper.prepare();
            new AlertDialog.Builder(StartActivity.this)
                    .setTitle("读取联系人为空")
                    .setMessage("  个信读取联系人列表为空。可能是由于没有开启权限或本地无联系人。\n  若未开启读取联系人权限，请设置读取联系人的权限，否则，个信将无法正常显示联系人\n  设置路径：设置->应用->个信->权限")
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getAppDetailSettingIntent(StartActivity.this);
                        }
                    })
                    .setNegativeButton("直接进入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(StartActivity.this, OldMsgReadActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .show();
            Looper.loop();
        }
        else {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this, OldMsgReadActivity.class);
            startActivity(intent);
            finish();
        }
    }





    @TargetApi(Build.VERSION_CODES.M)
    private void permissionsRequest(String PERMISSION_NAME,int CODE_PERMISSION) {
        try {
            //逐个检测是否已有权限
                if (ContextCompat.checkSelfPermission(StartActivity.this,
                        PERMISSION_NAME)
                        != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(StartActivity.this,
                                new String[]{PERMISSION_NAME},
                                CODE_PERMISSION);

                }
            else {
                    switch (PERMISSION_NAME) {
                        case PERMISSION_SEND_SMS:
                            permissionsRequest(PERMISSION_READ_CONTACTS,CODE_READ_CONTACTS);
                            break;
                        case PERMISSION_READ_CONTACTS:
                            permissionsRequest(PERMISSION_READ_EXTERNAL_STORAGE,CODE_READ_EXTERNAL_STORAGE);
                            break;
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "操作失败", Toast.LENGTH_SHORT).show();
        }
    }

    //跳转到设置权限页面
    private void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CODE_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionsRequest(PERMISSION_READ_CONTACTS,CODE_READ_CONTACTS);
                    //申请权限成功
                } else {
                    permissionsRequest(PERMISSION_READ_CONTACTS,CODE_READ_CONTACTS);
                    //申请权限失败
                }
                return;
            }

            case CODE_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionsRequest(PERMISSION_READ_EXTERNAL_STORAGE,CODE_READ_EXTERNAL_STORAGE);
                    //申请权限成功
                } else {
                    new AlertDialog.Builder(StartActivity.this)
                            .setTitle("申请读取联系人")
                            .setMessage("  个信需要申请读取联系人的权限，否则，个信将无法正常显示联系人\n  设置路径：设置->应用->个信->权限")
                            .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getAppDetailSettingIntent(StartActivity.this);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                    //申请权限失败
                }
                return;
            case CODE_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请权限成功
                    startOurApp();
                } else {
                    new AlertDialog.Builder(StartActivity.this)
                            .setTitle("申请存储权限")
                            .setMessage("  个信需要申请存储数据到本地的权限，否则，个信将无法正常保存信息，备注等内容\n  设置路径：设置->应用->个信->权限")
                            .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getAppDetailSettingIntent(StartActivity.this);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                    //申请权限失败
                }
                return;

        }
    }

    //检测是否有权限
    private void permissionsCheck(){
        if (ContextCompat.checkSelfPermission(StartActivity.this,
                PERMISSION_READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(StartActivity.this)
                    .setTitle("申请存储权限")
                    .setMessage("  个信需要申请存储数据到本地的权限，否则，个信将无法正常保存信息，备注等内容\n  设置路径：设置->应用->个信->权限")
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getAppDetailSettingIntent(StartActivity.this);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();

        }
        if (ContextCompat.checkSelfPermission(StartActivity.this,
                PERMISSION_READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(StartActivity.this)
                    .setTitle("申请读取联系人")
                    .setMessage("  个信需要申请读取联系人的权限，否则，个信将无法正常显示联系人\n  设置路径：设置->应用->个信->权限")
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getAppDetailSettingIntent(StartActivity.this);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }



}
