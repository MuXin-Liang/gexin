package com.example.joe.gexin;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.joe.contactor20.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 2017/3/18.
 */

public class GuideUsageActivity extends Activity {
    private List<ImageView> imageViewList;

    private ViewPager mStartViewPager;
    private ArrayList<View> pageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_layout);
        initData();// 初始化数据
        initView();// 初始化控件
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mStartViewPager = (ViewPager) findViewById(R.id.guide_vp);
        GuideAdapter adapter = new GuideAdapter();
        mStartViewPager.setAdapter(adapter);
        mStartViewPager.setCurrentItem(0);
        mStartViewPager.setOffscreenPageLimit(2);
    }

    /**
     * TODO：初始化ViewPager数据 void
     */
    private void initData() {
        int[] imageResIDs = {
                R.drawable.guide_image1,
                R.drawable.guide_image2,
                R.drawable.guide_image3,
                R.drawable.guide_image4,
                R.drawable.guide_image5,
                R.drawable.guide_image6
        };
        imageViewList = new ArrayList<>();

        ImageView iv;// 图片

        for (int i = 0; i < imageResIDs.length; i++) {
            iv = new ImageView(this);
            iv.setBackgroundResource(imageResIDs[i]);
            imageViewList.add(iv);
        }
    }

    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /*
         * 删除元素
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = imageViewList.get(position);
            container.addView(iv);// 1. 向ViewPager中添加一个view对象
            return iv; // 2. 返回当前添加的view对象
        }
    }
}
