package com.example.joe.gexin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joe.contactor20.R;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends SlideBackActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ListView listView;
    private mContactListAdapter adapter;
    private Button finish_btn, select_all_btn;
    private static ArrayList<String> sendUUIDSet; //存放即将发送联系人的uuid
    private Context mContext;
    private EditText et_search;
    private TextView tv_no_contact;
    private PopupWindow mPopupWindow;
    ImageButton excel_read_btn;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_main_layout);
        sendUUIDSet = new ArrayList<String>();
        sendUUIDSet.clear();
        createProgressDialog();
        new Thread(mRunnable).start();
    }

    /*创建进程，此时主界面显示进度条，进程负责读取联系人，成功后通过mHandler返回一个成功信息*/
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (!ContactLab.isInit()) ;
            Message msg = mHandler.obtainMessage();
            msg.what = 90;
            mHandler.sendMessage(msg);

        }
    };

    Runnable et_search_thread = new Runnable() {
        @Override
        public void run() {
            if (TextUtils.isEmpty(et_search.getText())) {
                ContactLabProxy.setKey(ContactLabProxy.SORTED);
                adapter.refreshList(ContactLabProxy.get().getSortedContacts());
            } else
                adapter.refreshList(ContactLabProxy.get().getSearchContacts(et_search.getText().toString()));
        }
    };

    /*该mHandler负责读取信息完成的处理，若成功则开始加载界面*/
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 90)
                mProgressDialog.cancel();
            initView();
        }

        ;
    };

    /*这三个函数用于mListAdapter与MainActivity间的通讯*/
    public void listView_Notify() {
        adapter.refreshList(ContactLabProxy.get().getSortedContacts());
        if(ContactLab.get().getContacts().isEmpty())
            tv_no_contact.setVisibility(View.VISIBLE);
        else
            tv_no_contact.setVisibility(View.INVISIBLE);
    }


    /*创建进度条函数*/
    private void createProgressDialog() {
        mContext = this;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("...正在读取联系人...");
        mProgressDialog.show();
    }


    private void initView() {
        // TODO Auto-generated method stub
        tv_no_contact=(TextView)findViewById(R.id.tv_no_contact);
        et_search = (EditText) findViewById(R.id.et_search_contact);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mHandler.post(et_search_thread);

            }
        });

        listView = (ListView) findViewById(R.id.contactor_listview);
        adapter = new mContactListAdapter(MainActivity.this, ContactLabProxy.get().getSearchContacts(""));
        if(ContactLab.get().getContacts().isEmpty())
            tv_no_contact.setVisibility(View.VISIBLE);
        else
            tv_no_contact.setVisibility(View.INVISIBLE);
        listView.setAdapter(adapter);


        //给listview注册上下文菜单
        registerForContextMenu(listView);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //完成按钮
        finish_btn = (Button) findViewById(R.id.select_finish);
        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent();
                setResult(MsgSendActivity.Send2Contact, intent);
                finish();
            }
        });

        excel_read_btn = (ImageButton) findViewById(R.id.excel_read_btn);
        excel_read_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.showAsDropDown(view);
                backgroundAlpha(0.7f);
            }
        });

        View popupView = getLayoutInflater().inflate(R.layout.popupwindow, null);
        Button excel_btn = (Button) popupView.findViewById(R.id.btn_popup);
        excel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDoc();
                mPopupWindow.dismiss();
            }
        });


        mPopupWindow = new PopupWindow(popupView, LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

        select_all_btn = (Button) findViewById(R.id.select_all);
        select_all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_all_btn.getText().equals("全不选")) {
                    //已经全选,则取消全选
                    adapter.SetAllNotSelected();
                    adapter.notifyDataSetChanged();
                    select_all_btn.setText("全选");
                } else {
                    //未全选
                    adapter.SetAllSelected();
                    adapter.notifyDataSetChanged();
                    select_all_btn.setText("全不选");
                }

            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }


    /*ListView创建上下文菜单，长按时弹出*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, 0, "修改备注");
        menu.add(0, 1, 0, "增加备注");
        menu.add(0, 2, 0, "删除备注");
        super.onCreateContextMenu(menu, v, menuInfo);

        //创建菜单的第二种方式
        //MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.list_item_context, menu);
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        //修改备注选项
        if (item.getItemId() == 0) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final int position = info.position;//注:此position为Contacts的position

            final ListView lv = new ListView(this);
            final ArrayAdapter<String> madapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
            lv.setAdapter(madapter);

            //填入备注信息
            final ArrayList<String> keys = ContactLabProxy.get().getSortedContacts().get(position).getRemarkKey();
            for (int i = 0; i < keys.size(); i++) {
                madapter.add(keys.get(i) + ": " + ContactLabProxy.get().getSortedContacts().get(position).getRemarkContent(keys.get(i)));
            }

            //给ListView注册Dialog
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int Remarkposition, long id) {
                    //代码生成布局样式
                    final LinearLayout layout = new LinearLayout(MainActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    final EditText et1 = new EditText(MainActivity.this);
                    final EditText et2 = new EditText(MainActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(20, 20, 30, 10);
                    et1.setLayoutParams(lp);
                    et2.setLayoutParams(lp);
                    et1.setBackground(null);
                    et2.setBackground(null);
                    et1.setHint("备注名");
                    et2.setHint("备注内容");
                    et1.setText(keys.get(Remarkposition));
                    layout.addView(et1);
                    layout.addView(et2);

                    //此position为remark position
                    final int po = position;

                    new AlertDialog.Builder(MainActivity.this).setTitle("修改该备注")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(layout)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String input1 = et1.getText().toString();
                                    String input2 = et2.getText().toString();
                                    if (input1.equals("")) {
                                        Toast.makeText(getApplicationContext(), "备注不能为空" + input1, Toast.LENGTH_SHORT).show();
                                    } else if (input2.equals("")) {
                                        Toast.makeText(getApplicationContext(), "备注不能为空" + input2, Toast.LENGTH_SHORT).show();
                                    } else {

                                        //执行修改
                                        Contact c = ContactLabProxy.get().getSortedContacts().get(position);
                                        c.ChangeRemark(input1, c.getRemarkKey().get(Remarkposition), input2);

                                        //刷新listview
                                        adapter.notifyDataSetChanged();
                                        ArrayList<String> keys = ContactLabProxy.get().getSortedContacts().get(position).getRemarkKey();
                                        madapter.clear();
                                        for (int i = 0; i < keys.size(); i++)
                                            madapter.add(keys.get(i) + ": " + ContactLabProxy.get().getSortedContacts().get(position).getRemarkContent(keys.get(i)));
                                        madapter.notifyDataSetChanged();
                                        ContactLabProxy.get().saveRemarkName(MainActivity.this,position);
                                    }
                                }

                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            });
            new AlertDialog.Builder(this)
                    .setTitle("选择要修改的备注")
                    .setView(lv)
                    .setNegativeButton("确定", null)
                    .show();

        }

        //增加备注选项
        else if (item.getItemId() == 1) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final int position = info.position;
            //弹出修改备注的输入框
            final LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            final EditText et1 = new EditText(this);
            final EditText et2 = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(20, 20, 30, 10);
            et1.setBackground(null);
            et2.setBackground(null);
            et1.setLayoutParams(lp);
            et2.setLayoutParams(lp);
            et1.setHint("备注名");
            et2.setHint("备注内容");
            layout.addView(et1);
            layout.addView(et2);

            new AlertDialog.Builder(this).setTitle("填写新备注")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(layout)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String input1 = et1.getText().toString();
                            String input2 = et2.getText().toString();
                            if (input1.equals("")) {
                                Toast.makeText(getApplicationContext(), "备注不能为空" + input2, Toast.LENGTH_SHORT).show();
                            } else if (input2.equals("")) {
                                Toast.makeText(getApplicationContext(), "备注不能为空" + input2, Toast.LENGTH_SHORT).show();
                            } else {
                                ContactLabProxy.get().getSortedContacts().get(position).setNewRemark(MainActivity.this,input1, input2);
                                adapter.notifyDataSetChanged();
                                ContactLabProxy.get().saveRemarkName(MainActivity.this,position);
                            }
                        }

                    })
                    .setNegativeButton("取消", null)
                    .show();

        }
        //删除备注选项
        else if (item.getItemId() == 2) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final int position = info.position;//注:此position为Contactors的position

            //新建一个listView
            final ListView lv = new ListView(this);
            final ArrayAdapter<String> madapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
            lv.setAdapter(madapter);
            //填入备注信息
            ArrayList<String> keys = ContactLabProxy.get().getSortedContacts().get(position).getRemarkKey();
            for (int i = 0; i < keys.size(); i++) {
                madapter.add(keys.get(i) + ": " + ContactLabProxy.get().getSortedContacts().get(position).getRemarkContent(keys.get(i)));
            }


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int Remarkposition, long id) {
                    //此position为remark position
                    final int po = position;

                    new AlertDialog.Builder(MainActivity.this).setTitle("确定删除该备注？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {


                                    ContactLabProxy.get().getSortedContacts().get(position).getRemarkMap().remove(
                                            ContactLabProxy.get().getSortedContacts().get(position).getRemarkKey().get(Remarkposition)
                                    );
                                    ContactLabProxy.get().getSortedContacts().get(position).getRemarkKey().remove(Remarkposition);

                                    adapter.notifyDataSetChanged();
                                    //刷新adapter
                                    ArrayList<String> keys = ContactLabProxy.get().getSortedContacts().get(position).getRemarkKey();
                                    madapter.clear();
                                    for (int i = 0; i < keys.size(); i++)
                                        madapter.add(keys.get(i) + ": " + ContactLabProxy.get().getSortedContacts().get(position).getRemarkContent(keys.get(i)));
                                    madapter.notifyDataSetChanged();
                                    ContactLabProxy.get().saveRemarkName(MainActivity.this,position);
                                }

                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            });

            new AlertDialog.Builder(this)
                    .setTitle("选择要删除的备注")
                    .setView(lv)
                    .setNegativeButton("确定", null)
                    .show();
        }
        return super.onMenuItemSelected(featureId, item);
    }


    /*读取EXCEL文件函数*/
    public void TestReadXLSX(File captureFilePath /*int sheetNum, int columnIndex,
                             String fileName*/) {
        try {
            //创建文件输入流

            InputStream input = new FileInputStream(captureFilePath);

            //
            POIFSFileSystem fs = new POIFSFileSystem(input);

            //得到EXCEL工作簿对象
            HSSFWorkbook wb = new HSSFWorkbook(fs);

            //得到EXCEL工作表对像
            HSSFSheet sheet = wb.getSheetAt(0);

            //得到行迭代器
            Iterator<Row> rows = sheet.rowIterator();

            HSSFRow firstrow = (HSSFRow) rows.next();
            Iterator<Cell> firstcells = firstrow.cellIterator();
            ArrayList<String> title = new ArrayList<String>();
            while (firstcells.hasNext()) {
                HSSFCell firstcell = (HSSFCell) firstcells.next();
                switch (firstcell.getCellType()) {
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        title.add(String.valueOf((long) firstcell.getNumericCellValue()));
                        break;
                    case HSSFCell.CELL_TYPE_STRING:
                        title.add(firstcell.getStringCellValue());
                    case HSSFCell.CELL_TYPE_BOOLEAN:
                        break;
                    case HSSFCell.CELL_TYPE_FORMULA:
                        break;
                }
            }
            for (String temp : title)
                System.out.println(temp);

            int column = 0;
            //利用迭代器检测是否有下一个行对象，如有将此对象赋给row
            while (rows.hasNext()) {
                HSSFRow row = (HSSFRow) rows.next();

                //得到行对象的单元格迭代器，并用此遍历行对象中的单元格对象并打印单元格
                Iterator<Cell> cells = row.cellIterator();
                Contact c = new Contact(true);
                //一行
                column = 0;
                while (cells.hasNext()) {
                    HSSFCell cell = (HSSFCell) cells.next();

                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            if (title.get(column).equals(SettingLab.EXCEL_PHONE)) {
                                c.setPhoneNumber(String.valueOf((long) cell.getNumericCellValue()));
                            } else {
                                c.setNewRemark( MainActivity.this,title.get(column) , String.valueOf((long) cell.getNumericCellValue()));
                            }


                            break;
                        case HSSFCell.CELL_TYPE_STRING:
                            System.out.println(title.get(column));
                            if (title.get(column).equals(SettingLab.EXCEL_NAME)) {
                                c.setName(cell.getStringCellValue());
                                c.setNewRemark(MainActivity.this,"(姓名)",cell.getStringCellValue());
                            } else if (title.get(column).equals(SettingLab.EXCEL_PHONE)) {

                            } else {
                                c.setNewRemark( MainActivity.this,title.get(column) , cell.getStringCellValue());
                            }

                            break;
                        case HSSFCell.CELL_TYPE_BOOLEAN:
                            System.out.println(cell.getBooleanCellValue());
                            break;
                        case HSSFCell.CELL_TYPE_FORMULA:
                            System.out.println(cell.getCellFormula());
                            break;
                        default:
                            System.out.println("unsuported sell type");
                            break;
                    }
                    column++;

                }
                if(c.getName()==null)
                    c.setName("无姓名");
                ContactLab.get(getParent()).addContactor(c);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Toast.makeText(MainActivity.this, "地址错误：" + captureFilePath, Toast.LENGTH_SHORT).show();
        }


        ContactLabProxy.get().refreshContactList();
        listView_Notify();
        registerForContextMenu(listView);//给listview注册上下文菜单
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    //打开文件浏览器函数
    public void selectDoc() {
        /*
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1100);
        */


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.ms-excel");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1100);

    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = uri.getPath();
            if (!path.endsWith(".xls")) {
                Toast.makeText(MainActivity.this, "请选择一个xls格式的文件！", Toast.LENGTH_SHORT).show();
                selectDoc();
            } else {
                //path=path.replaceAll("/","//");
                Log.e("path",path);
                //"/storage/emulated/0/Download/123.xls"
                Environment.getExternalStorageDirectory();

                TestReadXLSX(path);
            }
        }

    }
    */


    //从文件浏览器选择文件后，返回信息中返回选择文件的路径
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1100) {
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String string = uri.toString();
            File file;
            String a[] = new String[2];
            //判断文件是否在sd卡中
            if (string.indexOf(String.valueOf(Environment.getExternalStorageDirectory())) != -1) {
                //对Uri进行切割
                a = string.split(String.valueOf(Environment.getExternalStorageDirectory()));
                //获取到file
                file = new File(Environment.getExternalStorageDirectory(), a[1]);
                //判断文件是否在手机内存中
            } else if (string.indexOf(String.valueOf(Environment.getDataDirectory())) != -1) {
                //对Uri进行切割
                a = string.split(String.valueOf(Environment.getDataDirectory()));
                //获取到file
                file = new File(Environment.getDataDirectory(), a[1]);
            } else {
                //出现其他没有考虑到的情况
                Toast.makeText(MainActivity.this, "解析失败", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("TAG", "onActivityResult" + file.getAbsolutePath());
            TestReadXLSX(file);
        }
    }


}
