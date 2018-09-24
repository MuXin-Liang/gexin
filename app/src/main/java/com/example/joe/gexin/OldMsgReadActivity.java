package com.example.joe.gexin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.joe.contactor20.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Joe on 2017/3/5.
 */

public class OldMsgReadActivity extends Activity implements View.OnClickListener {
    private ListView msg_list, contact_list;
    private FloatingActionButton bt_WriteMsg;
    private SimpleAdapter adapter;
    private TextView tv_no_new_msg;
    private ViewPager viewPager;
    private ArrayList<View> pageView;
    private TextView title_tv,tv_no_contact2;
    private TabLayout tabLayout;
    private PagerAdapter mPagerAdapter;
    private SimpleAdapter contact_adapter;
    private List<Map<String, String>> temp_list;

    private int[] tabIcons = {
            R.drawable.old_msg_list,
            R.drawable.contact,
            R.drawable.setting
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_main_layout);
        initView();
        new Thread(mRunnable).start();
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        list_notify();
    }

    int i = 0;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, 0, "替换内容群发短信说明");
        menu.add(0, 1, 0, "Excel导入联系人说明");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case 1:
                Intent intent1=new Intent();
                intent1.setClass(OldMsgReadActivity.this,GuideExcelActivity.class);
                startActivity(intent1);
                break;
            case 0:
                Intent intent=new Intent();
                intent.setClass(OldMsgReadActivity.this,GuideUsageActivity.class);
                startActivity(intent);
                break;

        }
        return super.onMenuItemSelected(featureId, item);

    }


    private void initView() {
        MsgLab.initMsgLab(this);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        title_tv = (TextView) findViewById(R.id.vp_title);

        final LayoutInflater inflater = getLayoutInflater();

        View msg_list_layout = inflater.inflate(R.layout.vp_msg_layout, null);
        View setting_layout = inflater.inflate(R.layout.vp_setting_layout, null);
        View contact_list_layout = inflater.inflate(R.layout.vp_contact_layout, null);

        tv_no_contact2=(TextView) contact_list_layout.findViewById(R.id.tv_no_contact2);

        Button bt_usage=(Button)setting_layout.findViewById(R.id.bt_usage);
        registerForContextMenu(bt_usage);

        bt_usage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.showContextMenu();
            }
        });

        Button bt_about_us=(Button)setting_layout.findViewById(R.id.bt_call_us);
        bt_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(OldMsgReadActivity.this,About_Us_Activity.class);
                startActivity(intent);
            }
        });

        Button bt_about_gexin=(Button)setting_layout.findViewById(R.id.bt_about);
        bt_about_gexin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(OldMsgReadActivity.this,About_Gexin_Activity.class);
                startActivity(intent);
            }
        });
        Button bt_faq=(Button)setting_layout.findViewById(R.id.QandA);
        bt_faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(OldMsgReadActivity.this,FAQActivity.class);
                startActivity(intent);
            }
        });



        contact_list = (ListView) contact_list_layout.findViewById(R.id.main_contact_listview);
        temp_list = ContactLabProxy.get().getmPhoneContacts();

        if(temp_list.isEmpty())
            tv_no_contact2.setVisibility(View.VISIBLE);
        else
            tv_no_contact2.setVisibility(View.INVISIBLE);

        contact_adapter = new SimpleAdapter(OldMsgReadActivity.this,
                temp_list,
                R.layout.contact_main_list_item,
                new String[]{"Name", "Phone"},
                new int[]{R.id.tv_contact_list_item_name, R.id.tv_contact_list_item_phone})

        {
            //在这个重写的函数里设置 每个 item 中按钮的响应事件
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final int p = position;
                final View view = super.getView(position, convertView, parent);
                final TextView phone = (TextView) view.findViewById(R.id.tv_contact_list_item_phone);
                ImageButton button = (ImageButton) view.findViewById(R.id.bt_contact_call);
                button.setOnClickListener(new android.view.View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + phone.getText().toString().replaceAll("[^0-9]", ""));
                        intent.setData(data);
                        startActivity(intent);
                    }
                });
                return view;
            }
        };

        contact_list.setAdapter(contact_adapter);

        msg_list = (ListView) msg_list_layout.findViewById(R.id.msg_listview);
        tv_no_new_msg = (TextView) msg_list_layout.findViewById(R.id.tv_no_new_msg);
        bt_WriteMsg = (FloatingActionButton) msg_list_layout.findViewById(R.id.msg_write_btn);
        bt_WriteMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(OldMsgReadActivity.this, MsgSendActivity.class);
                startActivity(intent);

            }
        });
        msgset_list = new ArrayList<Map<String, Object>>();
        getMsgSetsData();
        adapter = new SimpleAdapter(this, msgset_list,
                R.layout.msg_list_item,
                new String[]{MsgLab.Name, MsgLab.Content},
                new int[]{R.id.msg_list_names, R.id.msg_list_content}
        );


        msg_list.setAdapter(adapter);


        //跳转到OldMsgShowActivtiy
        msg_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Adapter adapter = parent.getAdapter();
                Map<String, Object> map = (HashMap<String, Object>) adapter.getItem(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("uuid", (map.get(MsgLab.Id)).toString());
                intent.putExtra("UUID", bundle);
                intent.setClass(OldMsgReadActivity.this, OldMsgShowActivity.class);
                startActivity(intent);
            }
        });


        msg_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {
                final int po2 = position;
                final AlertDialog.Builder dialog = new AlertDialog.Builder(OldMsgReadActivity.this);
                dialog.setTitle("确认删除？");
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Adapter adapter = parent.getAdapter();
                        Map<String, Object> map = (HashMap<String, Object>) adapter.getItem(po2);
                        MsgLab.getMsgLab(OldMsgReadActivity.this).deleteMsgSet(
                                MsgLab.getMsgLab(OldMsgReadActivity.this).getMsgSetById((UUID) (map.get(MsgLab.Id))));
                        list_notify();
                    }
                });
                dialog.show();

                return true;
            }
        });

        if (msg_list.getCount() > 0)
            tv_no_new_msg.setVisibility(View.INVISIBLE);
        else
            tv_no_new_msg.setVisibility(View.VISIBLE);

        pageView = new ArrayList<View>();
        pageView.add(msg_list_layout);
        pageView.add(contact_list_layout);
        pageView.add(setting_layout);


        mPagerAdapter = new PagerAdapter() {
            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public int getCount() {
                return pageView.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView(pageView.get(position));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(pageView.get(position));
                return pageView.get(position);
            }
        };

        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
        });
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setOffscreenPageLimit(2);

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setCustomView(getTabView(0));
        tabLayout.getTabAt(1).setCustomView(getTabView(1));
        tabLayout.getTabAt(2).setCustomView(getTabView(2));
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_layout_item, null);
        ImageView img_title = (ImageView) view.findViewById(R.id.tab_layout_img);
        img_title.setImageResource(tabIcons[position]);
        return view;
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            iconChangeColor(arg0);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void iconChangeColor(int position) {
        switch (position) {
            case 0:
                title_tv.setText("短信");
                break;
            case 1:

                title_tv.setText("联系人");
                break;
            case 2:
                title_tv.setText("设置");
                break;
        }
    }

    public void list_notify() {
        getMsgSetsData();
        adapter.notifyDataSetChanged();

        if (msg_list.getCount() > 0)
            tv_no_new_msg.setVisibility(View.INVISIBLE);
        else
            tv_no_new_msg.setVisibility(View.VISIBLE);
    }


    private List<Map<String, Object>> msgset_list;

    private List<Map<String, Object>> getMsgSetsData() {
        msgset_list.clear();
        ArrayList<MsgSet> MsgSets = MsgLab.getMsgLab(this).getMsgSets();
        Map<String, Object> map;

        for (MsgSet MsgSet : MsgSets) {
            map = new HashMap<String, Object>();
            map.put(MsgLab.Name, MsgSet.getNames());
            map.put(MsgLab.Content, MsgSet.getContent());
            map.put(MsgLab.Id, MsgSet.getId());
            msgset_list.add(map);
        }
        Collections.reverse(msgset_list);
        return msgset_list;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_about:
                break;
            case R.id.bt_usage:
        }
    }
}
