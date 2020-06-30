package com.bsoft.mob.ienr.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.SizeHelper;
import com.bsoft.mob.ienr.model.ChoseVo;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-11 下午6:01:03
 * @类说明 选中控件
 */
@SuppressLint("ViewConstructor")
public class ChoseView extends LinearLayout {

    Context context;
    ChoseListener listener;
    ArrayList<ChoseVo> datas;
    ArrayList<View> views;
    int cuurent = 0;

    public ChoseView(Context context) {
        super(context);
        this.context = context;
    }

    public ChoseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setChoseListener(ChoseListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<ChoseVo> datas) {
        this.datas = datas;
        init();
    }

    public void chose(int index) {
        cuurent = index;
        changeBg(index);
    }

    /**
     * 初始化界面
     *
     * @param context
     */
    /*
	升级编号【56010040】============================================= start
增加医嘱执行情况显示，如2/5,2表示已完成，5表示总量
	================= Classichu 2017/11/20 15:15
	*/
    LinearLayout linearLayout;

    void init() {
			/*
			升级编号【56010056】============================================= start
			筛选视图刷新状态不一致,视图重复问题处理
			================= Classichu 2017/11/20 15:58
			*/
        //去除
        if (linearLayout != null) {
            removeView(linearLayout);
        }
		/* =============================================================== end */
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);

        views = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            LinearLayout v = getView(context, datas.get(i));
			/*
			升级编号【56010056】============================================= start
			筛选视图刷新状态不一致,视图重复问题处理
			================= Classichu 2017/11/20 15:58
			*/
            if (i == cuurent) {//change by louis
				/* =============================================================== end */
                v.setSelected(true);
                TextView textView = (TextView) v.getChildAt(0);
                textView.setSelected(true);

            } else {
                v.setSelected(false);
                TextView textView = (TextView) v.getChildAt(0);
                textView.setSelected(false);
            }
            v.setTag(i);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    int nowCurrent = Integer.valueOf(arg0.getTag().toString());
                    if (nowCurrent != cuurent) {
                        changeBg(nowCurrent);
                        cuurent = nowCurrent;
                        if (null != listener) {
                            listener.chose(getValue());
                        }
                    }
                }
            });
            views.add(v);
            linearLayout.addView(v);
        }
        addView(linearLayout);
    }

    /* =============================================================== end */
    void changeBg(int cur) {
        for (int i = 0; i < datas.size(); i++) {
            if (cur == i) {
                views.get(i).setSelected(true);
                TextView textView = (TextView) ((ViewGroup) views.get(i))
                        .getChildAt(0);
                textView.setSelected(true);
            } else {
                views.get(i).setSelected(false);

                TextView textView = (TextView) ((ViewGroup) views.get(i))
                        .getChildAt(0);
                textView.setSelected(false);
            }
        }
    }

    public LinearLayout getView(Context context, ChoseVo vo) {
        LinearLayout liner = new LinearLayout(context);
        liner.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.weight = 1.0f;
        liner.setLayoutParams(lp);
        //
        TextView text = new TextView(context);
        LinearLayout.LayoutParams lp_6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        text.setLayoutParams(lp_6);
        text.setGravity(Gravity.CENTER);
        text.setPadding(SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary(),
                SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary());
        TextViewCompat.setTextAppearance(text, R.style.ClassicTextAppearanceTertiary);
        text.setBackgroundResource(R.drawable.selector_classic_bg_selected_border);
        text.setTextColor(ContextCompat.getColor(context, R.color.selector_text_color));
        text.setText(vo.name);
        liner.addView(text);
        return liner;
    }

    public ChoseVo getValue() {
        return datas.get(cuurent);
    }

    public int getCurrent() {
        return cuurent;
    }

    public interface ChoseListener {
        public void chose(ChoseVo choseVo);
    }

}
