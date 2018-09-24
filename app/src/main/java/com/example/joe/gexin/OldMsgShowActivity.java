package com.example.joe.gexin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.joe.contactor20.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Joe on 2017/3/7.
 */

public class OldMsgShowActivity extends SlideBackActivity {

    private ListView msgsShowlist;
    private SimpleAdapter msgAdapter;
    private ArrayList<HashMap<String, Object>> msgs_show_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_content_layout);
        msgsShowlist = (ListView) findViewById(R.id.msgs_show_list);
        init_list();
        msgAdapter = new SimpleAdapter(this,
                msgs_show_list,
                R.layout.msg_content_list_item,
                new String[]{MsgLab.Name, MsgLab.Content},
                new int[]{R.id.tv_msg_name_show, R.id.tv_msg_content_show});
        msgsShowlist.setAdapter(msgAdapter);

    }

    private void init_list() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("UUID");
        if (bundle != null && bundle.containsKey("uuid"))//判断bundle是否为空且含键值uuid
        {
            MsgSet msgSet = MsgLab.getMsgLab(OldMsgShowActivity.this).getMsgSetById(UUID.fromString(bundle.get("uuid").toString()));
            msgs_show_list = msgSet.getMsgs();
            //Collections.reverse(msgs_show_list);
        }
    }
}
