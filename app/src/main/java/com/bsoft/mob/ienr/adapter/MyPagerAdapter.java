package com.bsoft.mob.ienr.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Classichu on 2018/2/1.
 */

public class MyPagerAdapter extends PagerAdapter {
    private List<View> mViewList = new ArrayList<>();

    public MyPagerAdapter(List<View> mViewList) {
        this.mViewList = mViewList;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViewList.get(position);
        container.addView(view);
        return view;
        // return super.instantiateItem(container, position);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = mViewList.get(position);
        container.removeView(view);
        //super.destroyItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
