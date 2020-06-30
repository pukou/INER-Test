package com.bsoft.mob.ienr.view.expand;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.bsoft.mob.ienr.R;

public class SpinnerLayout extends LinearLayout {
    private Spinner mSpinner;

    public SpinnerLayout(Context context) {
        this(context, null);
    }

    public SpinnerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpinnerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_spinner_view, this);
        mSpinner = (Spinner) view.findViewById(R.id.id_spinner);

    }

    public Spinner getSpinner() {
        return mSpinner;
    }

    private boolean mEnabled = true;
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mEnabled = enabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mEnabled){
            return true;
        }
        int action = ev.getAction();
//        Log.i("zzfq", "onInterceptTouchEvent: " + action);
        switch (action) {
            case MotionEvent.ACTION_UP:
                mSpinner.performClick();
                return true;
            default:
        }
        return super.onInterceptTouchEvent(ev);
    }

}
