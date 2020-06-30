/**
 * @Title: NursePlanEvaluateActivity.java
 * @Package com.bsoft.mob.ienr.activity.user
 * @Description: 护理计划评价
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-4 下午1:46:48
 * @version V1.0
 */
package com.bsoft.mob.ienr.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.model.nurseplan.Measure;
import com.bsoft.mob.ienr.util.DisplayUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 田孝鸣(tianxm@bsoft.com.cn)
 * @ClassName: NursePlanMeasureActivity
 * @Description: 护理计划措施
 * @date 2016-11-15 下午3:19:58
 */
public class NursePlanMeasureActivity extends BaseBarcodeActivity {
    public static final int REQUEST_CODE = 2;

    private ListView measureListView;
    private TextView tv_time;
    private ImageView iv_select;
    private CheckBox ch_start;
    private CheckBox ch_end;
    private String time;
    private List<Measure> measureList;
    private ImageView measureAdd;
    private MeasureAdapter measureAdapter;

    @Override
    public void initBarBroadcast() {

    }

    @Override
    public void onResume() {
        super.onResume();
        measureAdapter = new MeasureAdapter(measureList);
        measureListView.setAdapter(measureAdapter);
        //不需要
        id_swipe_refresh_layout.setEnabled(false);
//        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,measureListView);
    }

    /**
     * 设置长宽显示参数
     */
    private void setLayoutParams() {

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = DisplayUtil.getHeightPixels(this) * 4 / 5;
        params.width = DisplayUtil.getWidthPixels(this) * 9 / 10;
        this.getWindow().setAttributes(params);
    }

    private void getParams() {
        measureList = (List<Measure>) getIntent().getSerializableExtra("measureList");
    }
    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }
    private void initView() {
        measureListView = (ListView) findViewById(R.id.id_lv);
        tv_time = (TextView) findViewById(R.id.id_tv_2_for_bar_image);
        tv_time.setText(time);
        iv_select = (ImageView) findViewById(R.id.id_iv_for_bar_image);
        iv_select.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String datetime = tv_time
                        .getText().toString();
                String dateTime = DateTimeHelper.getServer_yyyyMMddHHmm00(datetime);
                YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateTime);
                showPickerDateTimeCompat(ymdHMs, R.id.id_tv_2_for_bar_image);
            }
        });
        ch_start = (CheckBox) findViewById(R.id.nurseplan_start);
        ch_end = (CheckBox) findViewById(R.id.nurseplan_end);
        measureAdd = (ImageView) findViewById(R.id.nurseplan_measure_add);
        measureAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showInputDiaolog("请输入护理措施", 1);
            }
        });
    }

    private void initActionBar() {
        actionBar.setTitle("编辑措施");
        actionBar.setBackAction(new Action() {
            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }
            @Override
            public void performAction(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_arrow_back_black_24dp;
            }
        });
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "执行";
            }
            @Override
            public void performAction(View view) {
                List<Measure> operList = new ArrayList<Measure>();
                for (Measure measure : measureList) {
                    if (measure.SELECTED) {
                        operList.add(measure);
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("measureList", (Serializable) operList);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_nurse_plan_measure;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        time = DateTimeHelper.getServer_yyyyMMddHHmm00();
        getParams();
        setLayoutParams();
        initView();
        initActionBar();
    }

    /*
     * (非 Javadoc) <p>Title: onDateTimeSet</p> <p>Description: </p>
     *
     * @param year
     *
     * @param monthOfYear
     *
     * @param dayOfMonth
     *
     * @param hourOfDay
     *
     * @param minute
     *
     * @param viewId
     *
     * @see com.bsoft.mob.ienr.activity.base.BaseActivity#onDateTimeSet(int, int,
     * int, int, int, int)
     */
    @Override
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {
        String dateTime = DateTimeFactory.getInstance().ymdhms2DateTime(year, month, dayOfMonth, hourOfDay, minute, 0);

        time = dateTime;
        tv_time.setText(time);
        for (Measure measure : measureList) {
            if (measure.SELECTED) {
                if (ch_start.isChecked()) {
                    measure.KSSJ = time;
                }
                if (ch_end.isChecked()) {
                    measure.JSSJ = time;
                }
            }
        }
        measureAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInputCompleteed(String content, int viewId) {
        super.onInputCompleteed(content, viewId);
        switch (viewId) {
            case 1:
                if (measureList == null)
                    measureList = new ArrayList<>();
                if (!EmptyTool.isBlank(content)) {
                    Measure measure = new Measure();
                    measure.CSZH = measureList.size() > 0 ? measureList.get(0).CSZH : "0";
                    measure.CSMS = content;
                    measure.XJBZ = 1;
                    measure.ZDYBZ = 1;
                    measure.CSXH = "0";
                    measure.SELECTED = true;
                    if (ch_start.isChecked()) {
                        measure.KSSJ = time;
                        measure.KSGH = mAppApplication.user.YHID;
                        measure.KSXM = mAppApplication.user.YHXM;
                    }
                    if (ch_end.isChecked()) {
                        measure.JSSJ = time;
                        measure.JSGH = mAppApplication.user.YHID;
                        measure.JSXM = mAppApplication.user.YHXM;
                    }
                    measureList.add(measure);
                    measureAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }

    }

    class MeasureAdapter extends BaseAdapter {
        private List<Measure> list;

        boolean isFirst = true;

        public MeasureAdapter(List<Measure> list) {
            super();
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Measure getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(NursePlanMeasureActivity.this)
                        .inflate(R.layout.item_list_nurseplan_measure, parent,false);
                vHolder = new ViewHolder();
                vHolder.tv_name = (EditText) convertView
                        .findViewById(R.id.nurseplan_item_name);
                vHolder.spinnerLayout = (SpinnerLayout) convertView.findViewById(R.id.id_spinner_layout);
                vHolder.sp_type = vHolder.spinnerLayout.getSpinner();
                vHolder.cb_selected = (CheckBox) convertView
                        .findViewById(R.id.checkBox);
                vHolder.tv_starttime = (TextView) convertView
                        .findViewById(R.id.nurseplan_item_starttime);
                vHolder.tv_endtime = (TextView) convertView
                        .findViewById(R.id.nurseplan_item_endtime);
                vHolder.tv_startperson = (TextView) convertView
                        .findViewById(R.id.nurseplan_item_startperson);
                vHolder.tv_endperson = (TextView) convertView
                        .findViewById(R.id.nurseplan_item_endjperson);
                vHolder.id_ll_more = convertView.findViewById(R.id.id_ll_more);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }
            isFirst = true;
            EditText _ed = vHolder.tv_name;
            vHolder.tv_name.setText(list.get(position).CSMS);
            isFirst = false;
            _ed.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!EmptyTool.isBlank(s.toString()) && !isFirst) {
                        list.get(position).CSMS = s.toString();
                        list.get(position).MODIFIED = true;
                    }

                }
            });
            vHolder.spinnerLayout.setVisibility(View.VISIBLE);
            vHolder.sp_type.setEnabled(list.get(position).ZDYBZ != 0);
            vHolder.spinnerLayout.setEnabled(list.get(position).ZDYBZ != 0);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parent.getContext()
                    , R.array.plan_measure,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vHolder.sp_type.setAdapter(adapter);
            vHolder.sp_type.setSelection(list.get(position).XJBZ);
            vHolder.sp_type
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int arg2, long arg3) {
                            list.get(position).XJBZ = arg2;
                            list.get(position).MODIFIED = true;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });
            vHolder.cb_selected.setChecked(list.get(position).SELECTED);
            vHolder.cb_selected.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (vHolder.cb_selected.isChecked()) {
                        list.get(position).SELECTED = true;
                        vHolder.cb_selected.setChecked(true);
                        Measure measure = list.get(position);
                        if (ch_start.isChecked()) {
                            measure.KSSJ = time;
                            measure.KSGH = mAppApplication.user.YHID;
                            measure.KSXM = mAppApplication.user.YHXM;
                        }
                        if (ch_end.isChecked()) {
                            measure.JSSJ = time;
                            measure.JSGH = mAppApplication.user.YHID;
                            measure.JSXM = mAppApplication.user.YHXM;
                        }
                        measureAdapter.notifyDataSetChanged();
                    } else {
                        list.get(position).SELECTED = false;
                        vHolder.cb_selected.setChecked(false);
                        Measure measure = list.get(position);
                        if (ch_start.isChecked()) {
                            measure.KSSJ = "";
                            measure.KSGH = "";
                            measure.KSXM = "";
                        }
                        if (ch_end.isChecked()) {
                            measure.JSSJ = "";
                            measure.JSGH = "";
                            measure.JSXM = "";
                        }
                        measureAdapter.notifyDataSetChanged();
                    }
                }
            });
            if (!TextUtils.isEmpty(list.get(position).KSSJ)
                    ||!TextUtils.isEmpty(list.get(position).JSSJ)
                    ||!TextUtils.isEmpty(list.get(position).KSXM)
                    ||!TextUtils.isEmpty(list.get(position).JSXM)
                    ){
                vHolder.id_ll_more.setVisibility(View.VISIBLE);
            }else{
                vHolder.id_ll_more.setVisibility(View.GONE);
            }
            vHolder.tv_starttime.setText(list.get(position).KSSJ);
            vHolder.tv_endtime.setText(list.get(position).JSSJ);
            vHolder.tv_startperson.setText(list.get(position).KSXM);
            vHolder.tv_endperson.setText(list.get(position).JSXM);
            return convertView;
        }
    }

    class ViewHolder {
        EditText tv_name;
        SpinnerLayout spinnerLayout;
        Spinner sp_type;
        CheckBox cb_selected;
        TextView tv_starttime;
        TextView tv_endtime;
        TextView tv_startperson;
        TextView tv_endperson;
        View id_ll_more;

    }
}
