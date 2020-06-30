package com.bsoft.mob.ienr.view.expand.dateselect;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.DateTimeTool;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.Calendar;

/**
 * Created by louisgeek on 2016/6/5.
 */
public class DateTimeSelectPopupWindow extends PopupWindow {
    private static final String TAG = "DateSelectPopupWindow";

    private View view;
    private Context mContext;
    private TextView id_btn_date_ok;
    private TextView id_btn_date_cancel;
    private DatePicker id_date_picker;
    private TimePicker id_time_picker;

    private int mYear;
    private int mMonthOfYear;
    private int mDayOfMonth;
    private int mHourOfDay;
    private int mMinute;
    private int mSecond;

    private String mNowDateTextInner;
    private String mStartDateTextInner;
    private String mEndDateTextInner;

    public DateTimeSelectPopupWindow(Context context, String nowDateTextInner, String startDateTextInner, String endDateTextInner) {
        super(context);
        mContext = context;
        mNowDateTextInner = nowDateTextInner;
        mStartDateTextInner = startDateTextInner;
        mEndDateTextInner = endDateTextInner;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_popupwindow_date_time_pick, null);

        id_date_picker = (DatePicker) view.findViewById(R.id.id_date_picker);
        id_time_picker = (TimePicker) view.findViewById(R.id.id_time_picker);

        initDateTimePicker();

        id_btn_date_ok = (TextView) view.findViewById(R.id.id_btn_date_ok);
        id_btn_date_cancel = (TextView) view.findViewById(R.id.id_btn_date_cancel);
        id_btn_date_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeSelectPopupWindow.this.dismiss();
                if (mOnDateSelectListener != null) {
                    mOnDateSelectListener.onDateSelect(mYear, mMonthOfYear, mDayOfMonth, mHourOfDay, mMinute, mSecond);
                }
            }
        });
        id_btn_date_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeSelectPopupWindow.this.dismiss();
            }
        });

        //设置PopupWindow的View
        this.setContentView(view);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置PopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//必须设置  ps:xml bg和这个不冲突
        this.setAnimationStyle(R.style.ClassicDateSelectViewAnimation);
        this.setTouchable(true);
        this.setFocusable(true);//设置后  返回按钮先消失popupWindow
        ///####this.update();
    }

    private Calendar calendar;

    private void initDateTimePicker() {
        if (EmptyTool.isBlank(mNowDateTextInner)) {
            mNowDateTextInner = DateTimeHelper.getServerDateTime();
        }
        //显示上一次选择日期时间数据
        calendar = DateTimeTool.dateTime2Calendar(mNowDateTextInner);
        mYear = calendar.get(Calendar.YEAR);
        mMonthOfYear = calendar.get(Calendar.MONTH);
        mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        mHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        DatePicker.OnDateChangedListener dcl = new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonthOfYear = monthOfYear;
                mDayOfMonth = dayOfMonth;
            }
        };
        //
        id_date_picker.init(mYear, mMonthOfYear, mDayOfMonth, dcl);
        //设置最小日期
        if (!EmptyTool.isBlank(mStartDateTextInner)) {
            if (DateTimeTool.compareTo(mNowDateTextInner, mStartDateTextInner) > 0) {
                Calendar calendar_s = DateTimeTool.dateTime2Calendar(mStartDateTextInner);
                long time_s = calendar_s.getTimeInMillis();
                id_date_picker.setMinDate(time_s);
            }
        }
        //设置最大日期
        if (!EmptyTool.isBlank(mEndDateTextInner)) {
            if (DateTimeTool.compareTo(mNowDateTextInner, mEndDateTextInner) <= 0) {
                Calendar calendar_e = DateTimeTool.dateTime2Calendar(mEndDateTextInner);
                long time_e = calendar_e.getTimeInMillis();
                id_date_picker.setMaxDate(time_e);
            }
        }

        if (mHourOfDay == 0 && mMinute == 0) {
            id_time_picker.setVisibility(View.GONE);
        } else {
            id_time_picker.setVisibility(View.VISIBLE);
            //设置时间
            id_time_picker.setIs24HourView(true);
            //
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

            {
                id_time_picker.setHour(mHourOfDay);
                id_time_picker.setMinute(mMinute);
            } else {
                id_time_picker.setCurrentHour(mHourOfDay);
                id_time_picker.setCurrentMinute(mMinute);
            }
            //第一次setOnTimeChangedListener会回调一次
            id_time_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener()

            {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    mHourOfDay = hourOfDay;
                    mMinute = minute;
                    mSecond = calendar.get(Calendar.SECOND);
                }
            });

        }
    }


    public interface OnDateSelectListener {
        void onDateSelect(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute, int second);

    }

    public void setOnDateSelectListener(OnDateSelectListener onDateSelectListener) {
        mOnDateSelectListener = onDateSelectListener;
    }

    private OnDateSelectListener mOnDateSelectListener;

}
