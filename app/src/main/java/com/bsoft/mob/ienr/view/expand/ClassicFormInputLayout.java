package com.bsoft.mob.ienr.view.expand;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.util.tools.SizeTool;

import java.util.List;


/**
 * Created by louisgeek on 2017/8/21.
 */

public class ClassicFormInputLayout extends LinearLayout {
    private final static int DEFLAUT_PADDING_LEFT_RIGHT_START_END = 10;
    private final static int DEFLAUT_PADDING_LEFT_RIGHT_START_END_SMALL = 5;

    public ClassicFormInputLayout(Context context) {
        this(context, null);
    }

    public ClassicFormInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassicFormInputLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        //
        LayoutParams ll_lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        ll_lp.weight = 1.0f;
        this.setLayoutParams(ll_lp);
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        ///####this.setBackgroundResource(R.drawable.selector_classic_item_bg_primary);
        //
        initStartLayout();
        initCenterLayout();
        initEndLayout();
    }

    private LinearLayout mStartLayout;
    private LinearLayout mCenterLayout;
    private LinearLayout mEndLayout;
    private Context mContext;

    private void initStartLayout() {
        mStartLayout = new LinearLayout(mContext);
        LayoutParams ll_lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        mStartLayout.setGravity(Gravity.CENTER_VERTICAL);
        mStartLayout.setLayoutParams(ll_lp);
        this.addView(mStartLayout);
    }

    private void initEndLayout() {
        mEndLayout = new LinearLayout(mContext);
        LayoutParams ll_lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        mEndLayout.setGravity(Gravity.CENTER_VERTICAL);
        mEndLayout.setLayoutParams(ll_lp);
        this.addView(mEndLayout);
    }


    private void initCenterLayout() {
        mCenterLayout = new LinearLayout(mContext);
        LayoutParams ll_lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        ll_lp.weight = 1.0f;
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
      /*  int padding=SizeTool.dp2px(10);
        mCenterLayout.setPadding(padding,0,padding,0);*/
        mCenterLayout.setGravity(Gravity.CENTER_VERTICAL);
        mCenterLayout.setLayoutParams(ll_lp);
        this.addView(mCenterLayout);
    }

    private boolean isInputAble=true;
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

    ///////////////////////////////////////////////////////////////////////////
    //  Start 2017/8/21
    public ClassicFormInputLayout addStartText(CharSequence text) {
        return addStartText(text,0, null);
    }
    public ClassicFormInputLayout addStartText(CharSequence text,int textColor) {
        return addStartText(text,textColor, null);
    }
    public ClassicFormInputLayout addStartText(CharSequence text,int textColor, OnClickListener listener) {
        TextView startTxt = new TextView(mContext);
        startTxt.setLines(1);
        startTxt.setText(text);
        if (textColor!=0) {
            startTxt.setTextColor(textColor);
        }
       /* int padding = SizeTool.dp2px(10);//10dp
        startTxt.setPadding(padding, padding, padding, padding);*/
        if (listener != null) {
            startTxt.setOnClickListener(listener);
        }
        startTxt.setBackgroundResource(R.drawable.selector_classic_bg_click_primary);
        mStartLayout.addView(startTxt);
        return this;
    }

    public ClassicFormInputLayout addStartDrawable(Drawable drawable) {
        return addStartDrawable(drawable, null);
    }

    public ClassicFormInputLayout addStartDrawable(Drawable drawable, OnClickListener listener) {
        ImageView startImg = new ImageView(mContext);
        startImg.setImageDrawable(drawable);
        if (listener != null) {
            startImg.setOnClickListener(listener);
        }
        int padding = SizeTool.dp2px(DEFLAUT_PADDING_LEFT_RIGHT_START_END);
        startImg.setPadding(padding, padding, padding, padding);
        startImg.setBackgroundResource(R.drawable.selector_classic_bg_click_primary);
        mStartLayout.addView(startImg);
        return this;
    }

    public ClassicFormInputLayout addEndText(CharSequence text) {
        return addEndText(text, null);
    }

    public ClassicFormInputLayout addEndText(CharSequence text, OnClickListener listener) {
        TextView endTxt = new TextView(mContext);
        endTxt.setLines(1);
        endTxt.setText(text);
      /*  int padding = SizeTool.dp2px(10);//10dp
        endTxt.setPadding(padding, padding, padding, padding);*/
        if (listener != null) {
            endTxt.setOnClickListener(listener);
        }
        endTxt.setBackgroundResource(R.drawable.selector_classic_bg_click_primary);
        mEndLayout.addView(endTxt);
        return this;
    }

    public ClassicFormInputLayout addEndDrawable(Drawable drawable) {
        return addEndDrawable(drawable, null);
    }

    public ClassicFormInputLayout addEndDrawable(Drawable drawable, OnClickListener listener) {
        ImageView endImg = new ImageView(mContext);
        endImg.setImageDrawable(drawable);
        if (listener != null) {
            endImg.setOnClickListener(listener);
        }
        int padding = SizeTool.dp2px(DEFLAUT_PADDING_LEFT_RIGHT_START_END);
        endImg.setPadding(padding, padding, padding, padding);
        endImg.setBackgroundResource(R.drawable.selector_classic_bg_click_primary);
        mEndLayout.addView(endImg);
        return this;
    }

    public ClassicFormInputLayout addCenterView(View view) {
        addCenterView(view, DEFLAUT_PADDING_LEFT_RIGHT_START_END_SMALL);
        return this;
    }

    public ClassicFormInputLayout addCenterView(View view, int paddingLeftRight_Dp) {
        mCenterLayout.addView(view);
        int paddingLeftRight = SizeTool.dp2px(paddingLeftRight_Dp);
        mCenterLayout.setPadding(paddingLeftRight, 0, paddingLeftRight, 0);
        return this;
    }

    public void setTextByKey(String key) {
        if (dropSelectEditView != null){
            dropSelectEditView.setTextByKey(key);
        }
    }
    public ClassicInputLayout getInputLayoutInCenterLayout() {
        return dropSelectEditView != null ? dropSelectEditView.getClassicInputLayout() : null;
    }

    public LinearLayout getCenterLayout() {
        return mCenterLayout;
    }

    private ClassicDropSelectEditView dropSelectEditView;

    public ClassicFormInputLayout addCenterEditView(String text) {
        return addCenterEditView(text, null, null, true,false, DEFLAUT_PADDING_LEFT_RIGHT_START_END_SMALL);
    }
    public ClassicFormInputLayout addCenterEditView(String text, String hintText, List<Pair<String, String>> stringList, boolean editAble, boolean clearAble) {
        return  addCenterEditView(text, hintText, stringList, editAble, clearAble,DEFLAUT_PADDING_LEFT_RIGHT_START_END_SMALL);
    }
    public ClassicFormInputLayout addCenterEditView(String text, String hintText, List<Pair<String, String>> stringList, boolean editAble, boolean clearAble, int paddingLeftRight_Dp) {
        dropSelectEditView = new ClassicDropSelectEditView(mContext, editAble);
        LayoutParams ll_lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        ll_lp.weight = 1.0f;
        dropSelectEditView.setLayoutParams(ll_lp);
        dropSelectEditView.setHint(hintText);
        dropSelectEditView.setText(text);
        //dropSelectEditView.setInputAble(inputAble);
        dropSelectEditView.getClassicInputLayout().setContentClearImageButtonEnabled(clearAble);
        dropSelectEditView.setBackgroundResource(R.drawable.selector_classic_bg_edit);
        dropSelectEditView.setupDropDownSelectData(stringList);
        mCenterLayout.addView(dropSelectEditView);
        int paddingLeftRight = SizeTool.dp2px(paddingLeftRight_Dp);
        mCenterLayout.setPadding(paddingLeftRight, 0, paddingLeftRight, 0);
        return this;
    }


    //  End 2017/8/21
    ///////////////////////////////////////////////////////////////////////////
}
