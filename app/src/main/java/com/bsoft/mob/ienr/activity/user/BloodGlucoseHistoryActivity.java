package com.bsoft.mob.ienr.activity.user;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.adapter.DingBaseAdapter;
import com.bsoft.mob.ienr.api.BloodGlucoseApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.bloodglucose.BGHistoryData;
import com.bsoft.mob.ienr.model.bloodglucose.BloodGlucoseDetail;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar;

import java.util.List;

/**
 * Created by Ding.pengqiang
 * on 2016/12/27.
 */
public class BloodGlucoseHistoryActivity extends BaseBarcodeActivity {



    // 眉栏工具条
    // 眉栏工具条

    private TextView sTimeTxt;
    private TextView eTimeTxt;

    private View sltStimeView;
    private View sltEtimeView;

    //血糖按钮
    private TextView mBtn1;
    //胰岛素按钮
    private TextView mBtn2;

    private ListView mRefresh;
    // 当前选择的模块，刷新时使用
    private int current = 1;


    private void initView() {
        sTimeTxt = (TextView) findViewById(R.id.stime);
        eTimeTxt = (TextView) findViewById(R.id.etime);
        sltStimeView = findViewById(R.id.slt_stime_ly);
        sltEtimeView = findViewById(R.id.slt_etime_ly);
        mBtn1 = (TextView) findViewById(R.id.glucose_but1);
        mBtn2 = (TextView) findViewById(R.id.glucose_but2);
        mBtn1.setText("血糖");
        mBtn2.setText("胰岛素");
        mRefresh = (ListView) findViewById(R.id.id_lv);
        EmptyViewHelper.setEmptyView(mRefresh,"mRefresh");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,mRefresh);
    }

    /**
     * 初始化AppBar
     */
    private void initActionBar() {

        actionBar.setTitle("血糖记录列表");
        String brch = EmptyTool.isBlank(application.sickPersonVo.XSCH) ? "" : application.sickPersonVo.XSCH;
        actionBar.setPatient(brch + application.sickPersonVo.BRXM);
        actionBar.setBackAction(new BsoftActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.ic_arrow_back_black_24dp;
            }
            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }
            @Override
            public void performAction(View view) {
                finish();
            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_blood_glucose_history;
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        initView();

        initActionBar();
        initTime();
        initButtons();
    }

    void initTime() {

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        eTimeTxt.setText(eTimeStr);

        // 前天
        String startDate= DateTimeHelper.dateAddedDays(nowDate,-3);
        String sTimeStr = startDate;
        sTimeTxt.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String dateStr = null;
            int viewId = v.getId();
            if (viewId == R.id.slt_stime_ly) {
                dateStr = sTimeTxt.getText().toString();
            } else if (viewId == R.id.slt_etime_ly) {
                dateStr = eTimeTxt.getText().toString();
            }
            if (EmptyTool.isBlank(dateStr)) {
                return;
            }
            YmdHMs ymdHMs = DateTimeHelper.date2YmdHMs(dateStr);
            showPickerDateCompat(ymdHMs, viewId);
        }
    };

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);
        if (viewId == R.id.slt_stime_ly) {
            //选择开始时间
            String endDate = eTimeTxt.getText().toString();
            boolean after = DateTimeFactory.getInstance().dateAfter(nowDate, endDate);
            if (after) {
                showMsgAndVoiceAndVibrator("开始时间后于结束时间,请重新选择!");
                return;
            }
        } else if (viewId == R.id.slt_etime_ly) {
            //选择结束时间
            String startDate = sTimeTxt.getText().toString();
            boolean before = DateTimeFactory.getInstance().dateBefore(nowDate, startDate);
            if (before) {
                showMsgAndVoiceAndVibrator("结束时间先于开始时间，请重新选择!");
                return;
            }
        }
        initTimeTxt(nowDate, viewId);
    }

    private void initTimeTxt(String dateStr, int viewId) {
        String timeStr = dateStr;
        if (viewId == R.id.slt_etime_ly) {
            eTimeTxt.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            sTimeTxt.setText(timeStr);
        }

    }

    @Override
    protected void toRefreshData() {
        String startTime = sTimeTxt.getText().toString();
        String endTime =eTimeTxt.getText().toString();
        GetFormTask task = new GetFormTask();
        tasks.add(task);
        task.execute(startTime,endTime);
    }

    /**
     * 切换两个按钮加载不同的数据
     */
    private void initButtons() {
        mBtn1.setSelected(true);
        toRefreshData();

        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (current != 1) {
                    current = 1;

                    mBtn1.setSelected(true);
                    mBtn2.setSelected(false);
                    toRefreshData();

                }
            }
        });
        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (current != 2) {
                    current = 2;
                    mBtn1.setSelected(false);
                    mBtn2.setSelected(true);
                    toRefreshData();

                }
            }
        });

    }


    class GetFormTask extends
            AsyncTask<String, Void, Response<BGHistoryData> > {


        String zyh;

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
//            addView.removeAllViews();
        }

        @Override
        protected Response<BGHistoryData> doInBackground(String... params) {

            if (params == null || params.length < 1 || params == null) {
                return null;
            }

//            String time = mTime.getText().toString();


            String sTime = params[0];
            String eTime = params[1];
            zyh = application.sickPersonVo.ZYH;
            String areaId = application.getAreaId();
            String jgId = application.jgId;
            return BloodGlucoseApi.getInstance(BloodGlucoseHistoryActivity.this)
                    .getBloodGlucoseHistory(zyh,sTime,eTime,areaId,jgId);
        }

        @Override
        protected void onPostExecute(Response<BGHistoryData> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(BloodGlucoseHistoryActivity.this, application,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {
                                    toRefreshData();
                                }
                            }).showLoginDialog();

                }else if (result.ReType == 0){
                    BGHistoryData data = result.Data;
                    importList(data);
                }else {
                    showMsgAndVoice(result.Msg);
                  /*  MediaUtil.getInstance(BloodGlucoseHistoryActivity.this).playSound(
                            R.raw.wrong, BloodGlucoseHistoryActivity.this);*/
                }
            }else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

        }


    }
    private void importList(BGHistoryData data) {

        if (mBtn1.isSelected()){

            GlucoseCompleteAdapter adapter = new GlucoseCompleteAdapter(this, null, R.layout.item_list_batch_glucose_complete);
            mRefresh.setAdapter(adapter);
            adapter.updateRes(data.GLUCOSE);
        }else if (mBtn2.isSelected()){

            InsulinCompleteAdapter adapter1 = new InsulinCompleteAdapter(this ,null,R.layout.item_list_batch_insulin_complete);
            mRefresh.setAdapter(adapter1);
            adapter1.addRes(data.INSULIN);

        }


    }
    @Override
    public void initBarBroadcast() {

    }


    public class GlucoseCompleteAdapter extends DingBaseAdapter<BloodGlucoseDetail> {


        public GlucoseCompleteAdapter(Context context, List<BloodGlucoseDetail> data, int layoutResId) {
            super(context, data, layoutResId);

        }

        @Override
        protected void bindData(ViewHolder holder, BloodGlucoseDetail item) {
            TextView content = (TextView) holder.getView(R.id.item_batch_glucose_complete_content);
            TextView input = (TextView) holder.getView(R.id.item_batch_glucose_complete_input);
            TextView unit = (TextView) holder.getView(R.id.item_batch_glucose_complete_unit);
            TextView name = (TextView) holder.getView(R.id.item_batch_glucose_complete_username);
            TextView now = (TextView) holder.getView(R.id.item_batch_glucose_complete_nowtime);
            content.setText(item.XMNR + "    ");
            input.setText(item.JHNR);
            unit.setText(item.XMDW);
            String jlxm = item.JLXM == null ? "" : item.JLXM;
            name.setText("执行护士:\t" + jlxm);
            String jlsj = item.JLSJ == null ? "" : item.JLSJ;
            now.setText("执行时间:\t" + jlsj);
        }

    }
    public class InsulinCompleteAdapter extends DingBaseAdapter<BloodGlucoseDetail> {


        public InsulinCompleteAdapter(Context context, List<BloodGlucoseDetail> data, int layoutResId) {
            super(context, data, layoutResId);

        }

        @Override
        protected void bindData(ViewHolder holder, BloodGlucoseDetail item) {
            TextView time = (TextView) holder.getView(R.id.item_batch_insulin_complete_time);
            TextView title = (TextView) holder.getView(R.id.item_batch_insulin_complete_title);
            TextView input = (TextView) holder.getView(R.id.item_batch_insulin_complete_number);
            TextView name = (TextView) holder.getView(R.id.item_batch_insulin_complete_username);
            TextView now = (TextView) holder.getView(R.id.item_batch_insulin_complete_nowtime);
            time.setText(item.XMNR);
            title.setText(item.YDSMC);
            input.setText(item.JHNR + "\t\t"+item.XMDW);
            String jlxm = item.JLXM == null ? "" : item.JLXM;
            name.setText("执行护士:\t"+jlxm  );
            String jlsj = item.JLSJ == null ? "" : item.JLSJ;
            now.setText("执行时间:\t"+jlsj);
        }
    }

}
