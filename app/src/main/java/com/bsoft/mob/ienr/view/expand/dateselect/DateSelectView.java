package com.bsoft.mob.ienr.view.expand.dateselect;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.components.datetime.DateTimeFormat;
import com.bsoft.mob.ienr.components.datetime.DateTimeTool;
import com.bsoft.mob.ienr.helper.VectorOrImageResHelper;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.KeyBoardTool;
import com.bsoft.mob.ienr.util.tools.SizeTool;

import java.util.Calendar;


/**
 * Created by louisgeek on 2016/6/5.
 */
public class DateSelectView extends AppCompatTextView {
    private static final String TAG = "DateSelectView";
    private Context mContext;
    private String mShowDateText;
    private String mStartDateText;
    private String mEndDateText;
    private String mFormatStr;


    public DateSelectView(Context context) {
        this(context, null);
    }

    public DateSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        initDate();//初始化

        int padding = SizeTool.dp2px(8);
        this.setPadding(this.getPaddingLeft() + padding, this.getPaddingTop() + padding, this.getPaddingRight() + padding, this.getPaddingBottom() + padding);
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //
                KeyBoardTool.hideKeyboard(v);

                DateTimeSelectPopupWindow myPopupwindow = new DateTimeSelectPopupWindow(mContext, mShowDateText, mStartDateText, mEndDateText);
                myPopupwindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                myPopupwindow.setOnDateSelectListener(new DateTimeSelectPopupWindow.OnDateSelectListener() {
                    @Override
                    public void onDateSelect(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute, int second) {
                        if (hourOfDay == 0 && minute == 0 && second == 0) {
                            Calendar calendar = DateTimeTool.ymd2Calendar(year, monthOfYear, dayOfMonth);
                            String date = DateTimeTool.calendar2Date(calendar);
                            //修正定义的格式
                            mShowDateText = DateTimeTool.date2Custom(date, mFormatStr);
                        } else {
                            Calendar calendar = DateTimeTool.ymdhms2Calendar(year, monthOfYear, dayOfMonth, hourOfDay, minute, second);
                            String dateTime = DateTimeTool.calendar2DateTime(calendar);
                            //修正定义的格式
                            mShowDateText = DateTimeTool.dateTime2Custom(dateTime, mFormatStr);
                        }
                        ((DateSelectView) v).setText(mShowDateText);
                    }
                });
            }
        });
        Drawable drawableLeft = getCompoundDrawables()[0] != null ? getCompoundDrawables()[0] : VectorOrImageResHelper.getDrawable(mContext, R.drawable.ic_date_range_black_24dp);
        Drawable drawableTop = getCompoundDrawables()[1];
        Drawable drawableRight = getCompoundDrawables()[2] != null ? getCompoundDrawables()[0] : VectorOrImageResHelper.getDrawable(mContext, R.drawable.ic_expand_more_black_24dp);
        Drawable drawableBottom = getCompoundDrawables()[3];
        drawableLeft.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        drawableRight.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        this.setCompoundDrawablePadding(SizeTool.dp2px(10));
        this.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        if (this.getBackground() == null) {
            this.setBackgroundResource(R.drawable.selector_classic_bg_text);
        }
        if (this.getGravity() == (Gravity.TOP | Gravity.START)) {
            this.setGravity(Gravity.CENTER_VERTICAL);
        }
    }


    public void setupDateText(String showDateText) {
        setupDateText(showDateText, null, null, null);
    }

    public void setupDateText(String showDateText, String formatStr) {
        setupDateText(showDateText, null, null, formatStr);
    }


    public void setupDateText(String showDateText, CharSequence hint, String formatStr) {
        setupDateText(showDateText, null, null, hint, formatStr);
    }


    public void setupDateText(String showDateText, String startDateText, String endDateText, String formatStr) {
        setupDateText(showDateText, startDateText, endDateText, null, formatStr);
    }


    public void setupDateText(String showDateText, String startDateText, String endDateText, CharSequence hint, String formatStr) {
       /* Log.i(TAG, "setupDateText: nowDateText:" + showDateText);
        Log.i(TAG, "setupDateText: startDateText:" + startDateText);
        Log.i(TAG, "setupDateText: endDateText:" + endDateText);*/
        //
        this.mShowDateText = showDateText;
        this.mStartDateText = startDateText;
        this.mEndDateText = endDateText;
        this.mFormatStr = EmptyTool.isBlank(formatStr) ? DateTimeFormat.FORMAT_DATE_TIME : formatStr;
        this.setText(mShowDateText);
        this.setHint(EmptyTool.isBlank(hint) ? "请选择时间" : hint);
    }

    private void initDate() {
        setupDateText(null);
    }


    @Deprecated //getNowData
    public CharSequence getText() {
        return super.getText();
    }


    public String getNowData() {
        return this.getText() == null ? "" : this.getText().toString();
    }

    public void setInputAble(boolean inputAble) {
        this.setClickable(inputAble);
    }


}
