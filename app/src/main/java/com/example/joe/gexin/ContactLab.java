package com.example.joe.gexin;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Joe on 2016/11/13.
 */
public class ContactLab extends Application {
    private static ContactLab sContactLab;
    private Context mAppContext;
    public static String REMARK = "remark";
    private static ArrayList<Contact> mContacts;
    private static boolean haveInit=false;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void initContactLab(Context context){
        if (!ContactLab.isInit()) {
            ContactLab.get(context);
            testReadAllContacts(context);
            ContactLab.getRemarkNames(context);
            ContactLabProxy.get();
            ContactLab.setInit(true);
        }
    }

    //单例模式

    /*****************************************************************/

    private ContactLab(Context appContext) {
        mAppContext = appContext.getApplicationContext();
        mContacts = new ArrayList<Contact>();
    }

    public static ContactLab get() {
        return sContactLab;
    }

    public static ContactLab get(Context c) {
        if (sContactLab == null) {
            sContactLab = new ContactLab(c.getApplicationContext());
        }
        return sContactLab;
    }

    /*****************************************************************/
    public static boolean returnContactNull(){
        if (mContacts.isEmpty())
            return true;
        else
            return false;
    }

    public static void setInit(boolean init) {
        haveInit = init;
    }

    public static boolean isInit() {
        if (sContactLab != null)
            if (haveInit)
                return true;
            else
                return false;
        else
            return false;
    }

    public ArrayList<Contact> getContacts() {
        return mContacts;
    }

    public Contact getContactById(String id) {
        for (Contact c : mContacts) {
            if (c.getmId().equals(id))
                return c;
        }
        return null;
    }

    public Contact getContactByPosition(int position) {
        return mContacts.get(position);
    }

    public void addContactor(Contact c) {
        mContacts.add(c);
    }


    //使用SharedPreference保存增长备注
    //保存单个Contact备注的
    public static boolean saveRemarkName(Context context,Contact c) {
        try {
            /*
            SharedPreferences.Editor editor =
                    ContextApplication.getAppContext().getSharedPreferences("RemarkName", 0).edit();

            editor.putString(c.getPhoneNumber()+RemarkName,
                    c.getRemarkContent(RemarkName));
            editor.commit();
            */
            putHashMap(context,c.getPhoneNumber(),c.getRemarkMap());
            Log.d("saveRemarkName",c.getRemarkMap().get("(姓名)")+c.getPhoneNumber());

        } catch (Exception e) {
            Log.e("CONTACTLAB", "error saving Info", e);
            return false;
        }
        return true;
    }

    //从SharePreference中将保存的Remarks取出
    public static void getRemarkNames(Context context) {
        for (Contact c : mContacts) {
            HashMap<String,String> a=getHashMap(context,c.getPhoneNumber());

            if (a != null) {
                Log.d("getRemarkNames not null",a.get("(姓名)")+c.getPhoneNumber());
                c.setRemarkMap(a);
                Iterator iter = a.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    c.setNewRemark(context,(String) entry.getKey(),(String) entry.getValue());
                    }

            }
        }


    }


    public static String SceneList2String(HashMap<String, String> hashmap)
            throws IOException {
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(hashmap);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String SceneListString = new String(Base64.encode(
                byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // 关闭objectOutputStream
        objectOutputStream.close();
        return SceneListString;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String , String> String2SceneList(
            String SceneListString) throws StreamCorruptedException,
            IOException, ClassNotFoundException {
        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        HashMap<String, String> SceneList = (HashMap<String , String>) objectInputStream
                .readObject();
        objectInputStream.close();
        return SceneList;
    }

    public static boolean putHashMap(Context context, String key,
                                     HashMap<String , String> hashmap) {
        SharedPreferences settings = context.getSharedPreferences(
                "RemarkName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        try {
            String liststr = SceneList2String(hashmap);
            editor.putString(key, liststr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public static HashMap<String, String> getHashMap(Context context,
                                                       String key) {
        SharedPreferences settings = context.getSharedPreferences(
                "RemarkName", Context.MODE_PRIVATE);
        String liststr = settings.getString(key, "defaultname");
        if (liststr == "defaultname") {
            return null;
        }
        else {
            try {
                return String2SceneList(liststr);
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    //读取联系人的函数
    @TargetApi(Build.VERSION_CODES.M)
    public static void testReadAllContacts(Context context) {

        //获取读取联系人的Cursor
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        int contactIdIndex = 0;
        int nameIndex = 0;

        if (cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }


        //大循环
        while (cursor.moveToNext()) {

            Contact c = new Contact(false);
            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);
            c.setName(name);
            c.setNewRemark(context,"(姓名)",name);


            //查找该联系人的phone信息

            //通过号码Cursor
            Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null, null);
            int phoneIndex = 0;
            if (phones.getCount() > 0) {
                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }
            //通过位置获取具体号码的String
            //可能有多个电话号码,只取最后一个电话号码为准
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phoneIndex);
                c.setPhoneNumber(phoneNumber);
            }


            //增加该联系人
            ContactLab.get(context).addContactor(c);

        }
    }
}
