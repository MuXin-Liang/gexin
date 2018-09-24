package com.example.joe.gexin;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.joe.contactor20.R;

//程序若想正常运行需获得发短信的权限（我已经在AndroidMainfest.xml中声明了android.permission.SEND_SMS）
public class MsgSendActivity extends SlideBackActivity implements View.OnClickListener {

    ArrayList<String> sendUUIDSet; //联系人位置数组
    private EditText et_main_number; // 声明电话号码输入框
    private EditText et_main_content; // 声明短信内容输入框
    private Button contactors_btn,bt_main_send;
    public final static int Send2Contact = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_msg_layout);
        // 初始化各界面控件
        et_main_number = (EditText) findViewById(R.id.et_main_number);
        et_main_content = (EditText) findViewById(R.id.et_main_content);
        bt_main_send=(Button)findViewById(R.id.bt_main_send);
        bt_main_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightsRequest();
            }
        });
        contactors_btn = (Button) findViewById(R.id.bt_main_contact);
        contactors_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        finish();
    }

    public void selectContact() { // 设置bt_main_contact的点击监听

        // 跳转到联系人Activity
        Intent intent = new Intent();
        intent.setClass(MsgSendActivity.this, MainActivity.class);
        startActivityForResult(intent, MsgSendActivity.Send2Contact);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case MsgSendActivity.Send2Contact:
                sendUUIDSet = ContactLabProxy.get().getSelectedUUIDSet();// 读取内容
                if (!sendUUIDSet.isEmpty()) {
                    ArrayList<Contact> mContacts = ContactLab.get().getContacts();
                    ContactLab c = ContactLab.get();


                    String names = new String();

                    for (int i = 0; i < sendUUIDSet.size(); i++) {
                        names = names + " " + c.getContactById(sendUUIDSet.get(i)).getName();
                    }
                    //依照names的长度决定怎样显示
                    if (names.length() > 20) {
                        et_main_number.setText("已选取" + sendUUIDSet.size() + "位联系人");
                    } else {
                        et_main_number.setText("发送至" + names);
                    }

                    et_main_number.setEnabled(false);
                } else {
                    et_main_number.setText("");
                    et_main_number.setEnabled(true);
                }
                break;
        }

    }

    private CustomDialog customDialog;


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SettingLab.CODE_Send_Msg: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage();

                } else {
                    Toast.makeText(MsgSendActivity.this,"发送短信失败！因为缺乏权限",Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void rightsRequest() {
        try {

            if (ContextCompat.checkSelfPermission(MsgSendActivity.this,
                    SettingLab.PERMISSION_SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MsgSendActivity.this,
                        new String[]{SettingLab.PERMISSION_SEND_SMS},
                        SettingLab.CODE_Send_Msg);
            }
            else {
                sendMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "获取权限失败！请进入权限开启界面开启权限", Toast.LENGTH_SHORT).show();
        }
    }


    public void sendMessage() { // 设置bt_main_send的点击监听
        //发送短信模块
        /*
            判断是否接受了联系人Activity发来的Bundle，判断依据为输入号码框的Enabled属性
                1.若未选取联系人，
                  则从输入号码框获得获得电话号码（目前我暂定多个电话号码需用英文状态下的句号"."分隔（一般输入法在数字输入界面的句号即为此））
                  并判断是否为有效号码
                2.若已选取联系人
                  已经从bundle接受了存储联系人位置的position数组
                  从ContactLab获取联系人信息并发送短信
        */

        String numbers[];


        //1.获取号码部分
        if (et_main_number.isEnabled()) {
            String number = et_main_number.getText().toString();// 将电话号码以字符串形式获得
            if (!TextUtils.isEmpty(number) && isPhone(number))   //判断号码格式是否正确
                numbers = number.split("[.]");                  // 用String.split方法将电话号码分开，放入一个数组中，并将此数组对空间地址赋给numbers[]
            else {
                Toast.makeText(MsgSendActivity.this, "号码格式不正确！", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            numbers = new String[sendUUIDSet.size()];
            for (int i = 0; i < sendUUIDSet.size(); i++) {
                if (ContactLab.get().getContactById(sendUUIDSet.get(i)).getPhoneNumber() == null) {
                    Log.d("No~", "号码格式不正确");
                    Toast.makeText(MsgSendActivity.this, "存在不正确号码格式！", Toast.LENGTH_SHORT).show();
                    return;
                }
                numbers[i] = ContactLab.get().getContactById(sendUUIDSet.get(i)).getPhoneNumber().replaceAll("[^0-9]", "");
            }
        }

        //2.短信替换部分
        int contactCount = numbers.length;

        // 获得短信内容(由于一次最多只能发70个字符，因此先获得一下原内容，将原内容中需要替换的部分替换后再与70相比，若小于70则直接发送，否则需拆分短信后多条发送)
        String originalContent = et_main_content.getText().toString();

        //短信内容判空
        if (originalContent.isEmpty()) {
            Toast.makeText(MsgSendActivity.this, "短信内容不能为空= =", Toast.LENGTH_SHORT).show();
            return;
        }

        String actualContent[] = new String[contactCount]; // 暂时用numbers.length表示需要给多少人发短信

        if (et_main_number.isEnabled()) {
            /*如果是在输入框直接打号码，则不作替换操作*/
            for (int i = 0; i < contactCount; i++)
                actualContent[i] = originalContent;
        }
        else {

            // 将每一条短信需要替换的内容在此替换，需要替换的内容暂时以XXX表示
            for (int i = 0; i < contactCount; i++) {
                Contact c = ContactLab.get().getContactById(sendUUIDSet.get(i));
                actualContent[i] = originalContent;
                for (int j = 0; j < c.getRemarkKey().size(); j++) {
                    String key = c.getRemarkKey().get(j);
                    Log.e("key", key);
                    actualContent[i] = actualContent[i].replace(key, c.getRemarkContent(key));
                }
                Log.e("Content", actualContent[i]);
            }
        }


        //2.4 短信内容预览

        msgPreview(contactCount, numbers, actualContent, originalContent);

    }

    private int temp_count = 0;

    private void msgPreview(final int contactcount, final String[] numbers, final String[] actualContents, final String originalContent) {
        customDialog = new CustomDialog(MsgSendActivity.this);
        Contact c;

        if (et_main_number.isEnabled()) {
            customDialog.setFirstTitle("发送至: " + numbers[temp_count]);
        } else {
            c = ContactLab.get().getContactById(sendUUIDSet.get(temp_count));
            customDialog.setFirstTitle("发送至: " + c.getName());
        }

        customDialog.setFirstMsg("短信内容: " + actualContents[temp_count]);
        customDialog.setNextOnclickListener("下一条", new CustomDialog.onNextOnclickListener() {
            @Override
            public void onNextClick() {
                if (temp_count < contactcount - 1) {
                    temp_count++;
                    Contact c = ContactLab.get().getContactById(sendUUIDSet.get(temp_count));
                    customDialog.setTitle("发送至: " + c.getName());
                    customDialog.setMessage("短信内容: " + actualContents[temp_count]);
                } else Toast.makeText(MsgSendActivity.this, "已经是最后一条了", Toast.LENGTH_SHORT).show();
            }
        });
        customDialog.setPreOnclickListener("上一条", new CustomDialog.onPreOnclickListener() {
            @Override
            public void onPreClick() {
                if (temp_count > 0) {
                    temp_count--;
                    Contact c = ContactLab.get().getContactById(sendUUIDSet.get(temp_count));
                    customDialog.setTitle("发送至: " + c.getName());
                    customDialog.setMessage("短信内容: " + actualContents[temp_count]);
                } else Toast.makeText(MsgSendActivity.this, "这是第一条短信", Toast.LENGTH_SHORT).show();
            }
        });


        customDialog.setCancelOnclickListener(new CustomDialog.onCancelOnclickListener() {
            @Override
            public void onCancelClick() {
                customDialog.dismiss();
            }
        });

        customDialog.setConfirmOnclickListener(new CustomDialog.onConfirmOnclickListener() {
            @Override
            public void onConfirmClick() {
                finalSendMsg(contactcount, numbers, actualContents, originalContent);
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }


    private void finalSendMsg(final int contactCount, final String[] numbers, final String[] actualContent, final String originalContent) {

        //2.5 创建显示发送进度的进度条
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setIcon(R.mipmap.ic_launcher);// 设置提示的title的图标，默认是没有的
        dialog.setTitle("正在发送短信...");
        dialog.setMax(contactCount);
        dialog.setMessage("已发送");
        dialog.show();



        //3. 实现具体的发短信的代码
        //SmsManager smsManager = SmsManager.getDefault();// 调用SmsManager类的getDefault()方法获得一个该类的实例

        for (int i = 0; i < contactCount; i++) {// 此处的i代表正在给第i个人发短信

            if (actualContent[i].getBytes().length < 130) { // 如果短信字数小于70可直接发送

                //smsManager.sendTextMessage(numbers[i], null, actualContent[i], null, null);// 将短信发出


				/*
                 * sendTextMessage参数说明
				 *
				 * destinationAddress： 收件人地址
				 *
				 * scAddress： 短信中心号码，null为默认中心号码
				 *
				 * sentIntent： 当消息发出时，成功或者失败的信息报告通过PendingIntent来广播。如果该参数为空，
				 * 则发信程序会被所有位置程序检查一遍， 这样会导致发送时间延长。
				 *
				 * deliveryIntent：
				 * 当消息发送到收件人时，该PendingIntent会被广播。pdu数据在状态报告的extended data
				 * ("pdu")中
				 */
                Toast.makeText(MsgSendActivity.this, "已发送给" + numbers[i], Toast.LENGTH_SHORT).show();
            } else {// 如果短信字数大于70需要多条发送
                // 先将短信拆分
                //ArrayList<String> actualContents = smsManager.divideMessage(actualContent[i]);

                // 循环发送
                //smsManager.sendMultipartTextMessage(numbers[i], null, actualContents, null, null);//现在发短信调用的方法，此方法可以对拆分的长短信自动合并
                Toast.makeText(MsgSendActivity.this, "已发送给" + numbers[i], Toast.LENGTH_SHORT).show();

            }
            dialog.incrementProgressBy(1);
        }
        dialog.dismiss();

        //3.5短信发送结果提示框
        final AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
        dialog2.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog2.setTitle("已成功发送");
        dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                saveMsg(contactCount, numbers, actualContent, originalContent);
            }
        });
        dialog2.setMessage("成功" + contactCount + "条，失败0条");
        dialog2.show();
    }

    private void saveMsg(final int contactcount, final String[] numbers, final String[] actualContents, final String originalContent) {
        //4 存储本次发送的短信
        MsgSet msgSet = new MsgSet(originalContent);
        for (int temp_count = 0; temp_count < contactcount; temp_count++) {

            if (et_main_number.isEnabled())

                msgSet.addMsg(numbers[temp_count], actualContents[temp_count]);

            else {

                Contact c = ContactLab.get(MsgSendActivity.this).getContactById(sendUUIDSet.get(temp_count));
                ;
                msgSet.addMsg(c.getName(), actualContents[temp_count]);
            }
        }


        MsgLab.getMsgLab(MsgSendActivity.this).saveNewMsgSet(msgSet);
        ContactLabProxy.get().clearSelectedContacts();
        et_main_number.setText("");
        et_main_number.setEnabled(true);
        et_main_content.setText("");
    }

    /**
     * 判断电话号码是否符合格式.
     *
     * @param inputText the input text
     * @return true, if is phone
     */
    public static boolean isPhone(String inputText) {
        Pattern p = Pattern.compile("^((14[0-9])|(13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
        Matcher m = p.matcher(inputText);
        return m.matches();
    }

    //代码冗余：写短信到本地
    private void writeToDataBase(String phoneNumber, String smsContent) {
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("body", smsContent);
        values.put("type", "2");
        values.put("read", "1");// 1表示已读
        this.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
    }


    public void cancleActivity(View v) { // 设置bt_main_cancle的点击监听

        finish();// 结束当前界面

    }

}
