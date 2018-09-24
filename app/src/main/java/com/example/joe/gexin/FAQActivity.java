package com.example.joe.gexin;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;


import com.example.joe.contactor20.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Joe on 2017/3/27.
 */

public class FAQActivity extends Activity {
    private ListView faq_ListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_layout);
        faq_ListView=(ListView)findViewById(R.id.FAQ_listview);
        requestData();
    }

    private void requestData() {
        ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String,String>>();
        for(int i=1;i<8;i++) {
            HashMap<String, String> item = new HashMap<String, String>();
            switch (i) {
                case 1:
                    item.put("question", this.getResources().getString(R.string.Q1));
                    item.put("answer", this.getResources().getString(R.string.A1));
                    break;
                case 2:
                    item.put("question", this.getResources().getString(R.string.Q2));
                    item.put("answer", this.getResources().getString(R.string.A2));
                    break;
                case 3:
                    item.put("question", this.getResources().getString(R.string.Q3));
                    item.put("answer", this.getResources().getString(R.string.A3));
                    break;
                case 4:
                    item.put("question", this.getResources().getString(R.string.Q4));
                    item.put("answer", this.getResources().getString(R.string.A4));
                    item.put("img","yes");
                    break;
                case 5:
                    item.put("question", this.getResources().getString(R.string.Q5));
                    item.put("answer", this.getResources().getString(R.string.A5));
                    break;
                case 6:
                    item.put("question", this.getResources().getString(R.string.Q6));
                    item.put("answer", this.getResources().getString(R.string.A6));
                    break;
                case 7:
                    item.put("question", this.getResources().getString(R.string.Q7));
                    item.put("answer", this.getResources().getString(R.string.A7));
                    break;
            }
            datas.add(item);
        }


        ExpandAdapter adapter = new ExpandAdapter(this, datas);
        faq_ListView.setAdapter(adapter);
    }
}
