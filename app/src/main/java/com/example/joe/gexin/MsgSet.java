package com.example.joe.gexin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Joe on 2017/3/5.
 */

public class MsgSet implements Serializable {
    private UUID uuid;
    private String mContent;
    private ArrayList<HashMap<String, Object>> Messages;

    public MsgSet(String content) {
        uuid = UUID.randomUUID();
        Messages = new ArrayList<>();
        mContent = content;
    }


    public void addMsg(String Name, String Content) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(MsgLab.Name, Name);
        map.put(MsgLab.Content, Content);
        Messages.add(map);
    }

    public ArrayList<HashMap<String, Object>> getMsgs() {
        return Messages;
    }

    public String getNames() {
        String Names = new String();
        int temp = 0;
        for (HashMap<String, Object> map : Messages) {
            if (temp < 3)
                Names = Names + " " + ((String) map.get(MsgLab.Name));
            else if (temp == 3)
                Names = Names + " " + "等";
            else break;
            temp++;
        }
        return Names;
    }

    public String getContent() {
        return mContent;
    }

    public UUID getId() {
        return uuid;
    }
}
