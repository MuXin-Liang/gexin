package com.example.joe.gexin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.joe.contactor20.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Joe on 2016/11/13.
 */
public class mContactListAdapter extends BaseAdapter {
    private Context context;
    private  ArrayList<Contact> mContacts;
    // 用来控制CheckBox的选中状况,Integer为listview中的位置



    public mContactListAdapter(Context context,ArrayList<Contact> mContacts) {
        // TODO Auto-generated constructor stub
        this.mContacts = mContacts;
        this.context = context;
        // 初始化数据
        initDate();
    }

    class ViewHolder {

        TextView nameTextview;
        TextView phoneNumTextview;
        TextView remarkTextview;
        CheckBox checkBox;
    }

    // 初始化isSelected的数据（全是false）
    private void initDate() {
    }

    public void refreshList(ArrayList<Contact> newList){
        mContacts=newList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return ContactLabProxy.get().getSortedContacts().size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    //getView() 绘制Item的函数
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        // 页面
        ViewHolder holder;


        Contact c = mContacts.get(position);


        String Name=c.getName();
        //String RemarkName=c.getRemarkName();
        String PhoneNum=c.getPhoneNumber();
        ArrayList<String> RemarkKeys= c.getRemarkKey();
        HashMap<String,String> RemarkMap=c.getRemarkMap();

        //将所有备注值转化为一个String语句
        String otherRemark=new String("");
        for (Iterator iter = RemarkKeys.iterator(); iter.hasNext();){
            otherRemark=otherRemark+" "+RemarkMap.get((String)iter.next());
        }

        //用Inflater绘制item界面（已优化）
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.contact_list_item, null);
            holder = new ViewHolder();

            holder.checkBox = (CheckBox) convertView.findViewById(R.id.contactor_list_item_checkbox);
            holder.nameTextview = (TextView) convertView
                    .findViewById(R.id.contactor_list_item_nameTextview);
            holder.phoneNumTextview = (TextView) convertView
                    .findViewById(R.id.contactor_list_item_phoneNumTextview);
            holder.remarkTextview = (TextView) convertView
                    .findViewById(R.id.contactor_list_item_remarkTextview);

            //将holder用setTag与convertView绑定
            convertView.setTag(holder);
        } else {
            // 若有空闲的convertView,取出holder填入
            holder = (ViewHolder) convertView.getTag();
        }


        holder.nameTextview.setText(Name);
        holder.phoneNumTextview.setText(PhoneNum);
        holder.remarkTextview.setText(otherRemark);

        // 设置checkBox的Listener 并根据原来的状态来设置新的状态
        holder.checkBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (mContacts.get(position).getIsSelected()) {
                    mContacts.get(position).setIsSelected(false);
                } else {
                    mContacts.get(position).setIsSelected(true);
                }

            }
        });


        // 根据isSelected来设置checkbox的选中状况
        holder.checkBox.setChecked(mContacts.get(position).getIsSelected());
        return convertView;
    }

    /************************************************************************/


    public  void SetAllSelected(){
        for (int i = 0; i < ContactLabProxy.get().getSortedContacts().size(); i++) {
            mContacts.get(i).setIsSelected(true);
        }
    }

    public  void SetAllNotSelected(){
        for (int i = 0; i < ContactLabProxy.get().getSortedContacts().size(); i++) {
            mContacts.get(i).setIsSelected(false);
        }
    }

}
