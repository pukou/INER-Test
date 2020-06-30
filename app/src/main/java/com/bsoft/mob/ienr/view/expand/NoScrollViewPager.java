package com.bsoft.mob.ienr.view.expand;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Classichu on 2018/2/1.
 */

public class NoScrollViewPager extends ViewPager {
    private boolean scrollAble = false;

    public void setScrollAble(boolean scrollAble) {
        this.scrollAble = scrollAble;
    }

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollAble) {
            //不消费
            return true;//可用试试 return true
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!scrollAble) {
            //不拦截
            return false;//肯定为 false
        }
        return super.onInterceptTouchEvent(ev);
    }
}
