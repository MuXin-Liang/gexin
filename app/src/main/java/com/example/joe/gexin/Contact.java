package com.example.joe.gexin;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Joe on 2016/11/13.
 */
public class Contact {
    private boolean isExcel;
    private String phoneNumber;
    private String name;
    private String mId;
    private boolean isSelected;
    // 添加新备注部分
    private ArrayList<String> mRemarkKey;
    private HashMap<String, String> mRemarkMap;

    public Contact(boolean isExcel) {
        this.isExcel=isExcel;
        mId = UUID.randomUUID().toString();
        isSelected = false;
    }

    private ArrayList<String> getmRemarkKey() {
        if (mRemarkKey == null)
            mRemarkKey = new ArrayList<String>();
        return mRemarkKey;
    }

    public String getRemarkContent(String Name) {
        return getmRemarkMap().get(Name);
    }

    public void DeleteOtherRemark(String Name) {
        getmRemarkMap().remove(Name);
        getmRemarkKey().remove(Name);
    }

    public void ChangeRemark(String NewName, String OldName, String Content) {
        getmRemarkKey().remove(OldName);
        getmRemarkKey().add(NewName);
        getmRemarkMap().remove(OldName);
        getmRemarkMap().put(NewName, Content);
    }

    private HashMap<String, String> getmRemarkMap() {
        if (mRemarkMap == null)
            mRemarkMap = new HashMap<String, String>();
        return mRemarkMap;
    }

    public void setNewRemark(Context context,String remarkName, String remarkContent) {
        if (getRemarkKey().contains(remarkName))
            getmRemarkMap().put(remarkName, remarkContent);
        else {
            getmRemarkKey().add(remarkName);
            getmRemarkMap().put(remarkName, remarkContent);
        }
        if(!isExcel&&!remarkName.equals("(姓名)"))
            ContactLab.saveRemarkName(context,this);
    }

    public void setRemarkMap(HashMap<String,String> a){
        mRemarkMap=a;
    }

    public ArrayList<String> getRemarkKey() {
        return getmRemarkKey();
    }

    public HashMap<String, String> getRemarkMap() {
        return getmRemarkMap();
    }

    /*************************************************************************/


    @Override
    public String toString() {
        return name;
    }

    public String getmId() {
        return mId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /*
    public void setRemarkName(String remarkName) {

        this.remarkName = remarkName;
    }
    */

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    /*public String getRemarkName() {
        return remarkName;
    }*/

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean a) {
        isSelected = a;
    }
}
