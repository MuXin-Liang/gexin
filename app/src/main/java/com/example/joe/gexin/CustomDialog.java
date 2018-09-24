package com.example.joe.gexin;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.joe.contactor20.R;

/**
 * Created by Joe on 2017/3/5.
 */

public class CustomDialog extends Dialog {
    private Button confirm;//确定按钮
    private Button cancel;//取消按钮
    private ImageButton next;
    private ImageButton pre;
    private TextView titleTv;//消息标题文本
    private TextView messageTv;//消息提示文本
    private String titleStr;//从外界设置的title文本
    private String messageStr;//从外界设置的消息文本
    //确定文本和取消文本的显示内容
    private String nextStr, preStr;


    private onNextOnclickListener nextOnclickListener;
    private onPreOnclickListener preOnclickListener;
    private onCancelOnclickListener cancelOnclickListener;
    private onConfirmOnclickListener confirmOnclickListener;


    public void setNextOnclickListener(String str, onNextOnclickListener onNextOnclickListener) {
        if (str != null) {
            nextStr = str;
        }
        this.nextOnclickListener = onNextOnclickListener;
    }

    public void setPreOnclickListener(String str, onPreOnclickListener onPreOnclickListener) {
        if (str != null) {
            preStr = str;
        }
        this.preOnclickListener = onPreOnclickListener;
    }

    public void setCancelOnclickListener(onCancelOnclickListener onCancelOnclickListener) {
        this.cancelOnclickListener = onCancelOnclickListener;
    }

    public void setConfirmOnclickListener(onConfirmOnclickListener onConfirmOnclickListener) {
        this.confirmOnclickListener = onConfirmOnclickListener;
    }

    public CustomDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_msg_pre_dialog);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextOnclickListener != null) {
                    nextOnclickListener.onNextClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preOnclickListener != null) {
                    preOnclickListener.onPreClick();
                }
            }
        });

        //设置取消按钮被点击后，向外界提供监听
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelOnclickListener != null) {
                    cancelOnclickListener.onCancelClick();
                }
            }
        });

        //设置取消按钮被点击后，向外界提供监听
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmOnclickListener != null) {
                    confirmOnclickListener.onConfirmClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
        if (messageStr != null) {
            messageTv.setText(messageStr);
        }
        //如果设置按钮的文字
        if (nextStr != null) {
        }
        if (preStr != null) {
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        pre = (ImageButton) findViewById(R.id.msg_pre_pre);
        next = (ImageButton) findViewById(R.id.msg_pre_next);
        cancel = (Button) findViewById(R.id.msg_pre_cancel);
        confirm = (Button) findViewById(R.id.msg_pre_confirm);
        titleTv = (TextView) findViewById(R.id.msg_pre_name);
        messageTv = (TextView) findViewById(R.id.msg_pre_content);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */

    public void setTitle(String title) {
        titleStr = title;
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
    }

    public void setFirstTitle(String title) {
        titleStr = title;
    }

    public void setFirstMsg(String msg) {
        messageStr = msg;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
        if (messageStr != null) {
            messageTv.setText(messageStr);
        }
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onPreOnclickListener {
        public void onPreClick();
    }

    public interface onNextOnclickListener {
        public void onNextClick();
    }

    public interface onCancelOnclickListener {
        public void onCancelClick();
    }

    public interface onConfirmOnclickListener {
        public void onConfirmClick();
    }

}
