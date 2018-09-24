package com.example.joe.gexin;

import android.Manifest;

/**
 * Created by Joe on 2017/3/11.
 */

public class SettingLab {
    public static String EXCEL_NAME = "姓名";
    public static String EXCEL_PHONE = "号码";
    public static String COLOR_GREY = "#676972";
    public static String COLOR_BLUE = "#ffffff";

    public static final int CODE_Send_Msg = 50;

    public static final String PERMISSION_SEND_SMS = Manifest.permission.SEND_SMS;
    public static final String PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    public static String COLOR(int i) {
        return COLOR_BLUE;
    }
}
