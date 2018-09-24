package com.example.joe.gexin;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joe on 2017/3/10.
 */

public class ContactLabProxy {
    private static ArrayList<Contact> mSortedContacts;
    private static ArrayList<Contact> mSearchContacts;
    private static ContactLabProxy mContactLabProxy;
    private static ArrayList<Map<String, String>> mPhoneContacts;
    public final static String SORTED = "Sorted";
    public final static String SEARCH = "Search";
    private static String search_sorted_key;
    public static boolean phone_contact_init = false;

    private ContactLabProxy() {
        //init the arraylist
        mSortedContacts = new ArrayList<Contact>(getSortedContactsByPy(ContactLab.get().getContacts()));
        mPhoneContacts = new ArrayList<Map<String, String>>();
        initPhoneContacts();
        search_sorted_key = new String();
        search_sorted_key = SORTED;
    }

    private void initPhoneContacts() {
        for (Contact c : mSortedContacts) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("Name", c.getName());
            map.put("Phone", c.getPhoneNumber());
            mPhoneContacts.add(map);
        }
        phone_contact_init = true;
    }

    public static ContactLabProxy get() {
        if (mContactLabProxy == null)
            mContactLabProxy = new ContactLabProxy();
        return mContactLabProxy;
    }

    public ArrayList<Map<String, String>> getmPhoneContacts() {
        return mPhoneContacts;
    }

    public void refreshContactList() {
        mSortedContacts = new ArrayList<Contact>(getSortedContactsByPy(ContactLab.get().getContacts()));
        search_sorted_key = SORTED;
    }

    public ArrayList<Contact> getSortedContacts() {
        if (search_sorted_key.equals(SEARCH))
            return mSearchContacts;
        else
            return mSortedContacts;
    }

    public static void setKey(String a) {
        search_sorted_key = a;
    }

    public ArrayList<Contact> getSearchContacts(String search) {

        if (mSearchContacts == null)
            mSearchContacts = new ArrayList<Contact>();
        else
            mSearchContacts.clear();

        for (Contact c : mSortedContacts)
            if (c.getName().contains(search))
                mSearchContacts.add(c);

        search_sorted_key = SEARCH;
        return mSearchContacts;
    }

    public void saveAllRemarkName(Context context){
        ArrayList<Contact> mContacts=ContactLab.get().getContacts();
        for (Contact c : mContacts) {
            ContactLab.saveRemarkName(context,c);
        }
    }

    public void saveRemarkName(Context context,int position) {
        ContactLab.saveRemarkName(context,this.getSortedContacts().get(position));
    }



    private static Comparator mComparator = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            if (ChineseCharToEn.get().compareAlphabet(
                    ChineseCharToEn.get().getFirstLetter(((Contact) lhs).getName()),
                    ChineseCharToEn.get().getFirstLetter(((Contact) rhs).getName())
            ))
                return 0;
            else
                return 1;
        }
    };

    public ArrayList getSortedContactsByPy(ArrayList<Contact> mSortedContacts) {
        Collections.sort(mSortedContacts, mComparator);
        return mSortedContacts;
    }

    public ArrayList<String> getSelectedUUIDSet() {
        ArrayList<String> uuidSet = new ArrayList<String>();
        for (Contact c : mSortedContacts)
            if (c.getIsSelected())
                uuidSet.add(c.getmId());
        return uuidSet;
    }

    public void clearSelectedContacts() {
        for (Contact c : mSortedContacts)
            if (c.getIsSelected())
                c.setIsSelected(false);
    }
}
