package com.example.joe.gexin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Joe on 2017/3/5.
 */

public class MsgLab {
    private static MsgLab sMsgLab;
    private ArrayList<MsgSet> mMsgSets;
    private Context mAppContext;
    public static String Name = "Names";
    public static String Content = "Content";
    public static String Id = "Id";
    private static boolean haveInit = false;
    mMsgDBHelper db = new mMsgDBHelper(ContextApplication.getAppContext());

    private MsgLab(Context appContext) {
        mAppContext = appContext;
        mMsgSets = new ArrayList<MsgSet>();
        initDB();
    }

    public static void initMsgLab(Context c) {
        if (!haveInit) {

            getMsgLab(c);

            haveInit = true;
        }
    }

    private void initDB() {
        db = new mMsgDBHelper(ContextApplication.getAppContext());
        ArrayList<MsgSet> msgSets = this.getAllObject();
        for (MsgSet msgSet : msgSets) {
            addMsgSet(msgSet);
        }

    }

    public static MsgLab getMsgLab(Context c) {
        if (sMsgLab == null)
            sMsgLab = new MsgLab(c);
        return sMsgLab;
    }

    public ArrayList<MsgSet> getAllObject() {

        ArrayList<MsgSet> MsgSets = new ArrayList<MsgSet>();
        SQLiteDatabase database1 = db.getWritableDatabase();
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + mMsgDBHelper.DBNAME, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Log.d("data-id", cursor.getString(0));
                byte data[] = cursor.getBlob(cursor.getColumnIndex(mMsgDBHelper.MSGSET));
                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
                try {
                    ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
                    MsgSet msgSet = (MsgSet) inputStream.readObject();
                    MsgSets.add(msgSet);
                    inputStream.close();
                    arrayInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("Persons-Count", Integer.toString(MsgSets.size()));
        return MsgSets;
    }


    public void saveNewMsgSet(MsgSet msgSet) {
        addMsgSet(msgSet);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(msgSet);
            objectOutputStream.flush();
            byte data[] = arrayOutputStream.toByteArray();
            objectOutputStream.close();
            arrayOutputStream.close();
            SQLiteDatabase database = db.getWritableDatabase();
            database.execSQL("insert into " + mMsgDBHelper.DBNAME + " (" + mMsgDBHelper.MSGSET + ",UUID) values(?,'" + msgSet.getId().toString() + "')", new Object[]{data});
            database.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteMsgSetInSql(UUID uuid) {
        try {
            SQLiteDatabase database = db.getWritableDatabase();
            database.execSQL("delete from " + mMsgDBHelper.DBNAME + " where UUID='" + uuid.toString() + "'");
            database.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addMsgSet(MsgSet msgSet) {
        mMsgSets.add(msgSet);
    }

    public void deleteMsgSet(MsgSet msgSet) {
        mMsgSets.remove(msgSet);
        UUID uuid = msgSet.getId();
        deleteMsgSetInSql(uuid);
    }

    public ArrayList<MsgSet> getMsgSets() {
        return mMsgSets;
    }

    public MsgSet getMsgSetById(UUID uuid) {
        for (MsgSet msgSet : mMsgSets) {
            if (msgSet.getId().equals(uuid)) {
                return msgSet;
            }
        }

        return null;
    }

}
