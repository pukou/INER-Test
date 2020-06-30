package com.bsoft.mob.ienr.view.expand;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RadioGroup;

public class RadioGroupLayout extends RadioGroup {

    public RadioGroupLayout(Context context) {
        this(context, null);
    }

    public RadioGroupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isInputAble = true;

    public void setInputAble(boolean inputAble) {
        isInputAble = inputAble;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isInputAble) {
            return super.onInterceptTouchEvent(ev);
        }
        return true;
    }

}
