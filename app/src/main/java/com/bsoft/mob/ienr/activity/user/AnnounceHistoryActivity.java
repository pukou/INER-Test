package com.bsoft.mob.ienr.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.adapter.AnnounceAdapter;
import com.bsoft.mob.ienr.api.AnnounceApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.ParserModel;
import com.bsoft.mob.ienr.model.announce.AnnounceItem;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;

/**
 * 宣教历史  Created by hy on 14-3-21.
 */
public class AnnounceHistoryActivity extends BaseBarcodeActivity {

    public static final int DIALOG_LOADING = 0;


    private ListView mPtrListView;

    // private ListView mListView;

    // ArrayList<AsyncTask<?, ?, ?>> tasks = new ArrayList<AsyncTask<?, ?,
    // ?>>();

    // private DialogFragment newFragment;

    private View sltStimeView;
    private View sltEtimeView;

    private TextView stime;
    private TextView etime;

    private ImageView searchBtn;


    private void initActionBar() {

        actionBar.setTitle("宣教记录");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);

    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_announce_history;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        mPtrListView = (ListView) findViewById(R.id.id_lv);
        // mListView = mPtrListView;

        sltStimeView = findViewById(R.id.slt_stime_ly);
        sltEtimeView = findViewById(R.id.slt_etime_ly);

        stime = (TextView) findViewById(R.id.stime);
        etime = (TextView) findViewById(R.id.etime);

        EmptyViewHelper.setEmptyView(mPtrListView, "mPtrListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mPtrListView);
        searchBtn = (ImageView) findViewById(R.id.search);

        initTime();
        initSearchBtn();
        initActionBar();
    }

    @Override
    protected void toRefreshData() {

        String start = stime.getText().toString();
        String end = etime.getText().toString();
        performGetTask(start, end);
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    private void initTime() {

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        etime.setText(eTimeStr);

        // 前天
        String startDate= DateTimeHelper.dateAddedDays(nowDate,-1);
        String sTimeStr = startDate;
        stime.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);

        performGetTask(sTimeStr, eTimeStr);
    }

    private void initSearchBtn() {

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String start = stime.getText().toString();
                String end = etime.getText().toString();
                performGetTask(start, end);
            }
        });
    }

    public View.OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String dateStr = null;
            int viewId = v.getId();
            if (viewId == R.id.slt_stime_ly) {
                dateStr = stime.getText().toString();
            } else if (viewId == R.id.slt_etime_ly) {
                dateStr = etime.getText().toString();
            }
            if (EmptyTool.isBlank(dateStr)) {
                return;
            }

            YmdHMs ymdHMs = DateTimeHelper.date2YmdHMs(dateStr);
            showPickerDateCompat(ymdHMs, viewId);

        }
    };


    private void performGetTask(String start, String end) {
        GetDateTask task = new GetDateTask();
        task.execute(start, end);
        tasks.add(task);
    }

    /**
     * 查询数据
     *
     * @author hy
     */
    public class GetDateTask extends AsyncTask<String, Integer, ParserModel> {

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected ParserModel doInBackground(String... params) {

            if (params == null || params.length < 2) {
                return null;
            }


            if (mAppApplication.sickPersonVo == null) {
                return null;
            }

            String zyh = mAppApplication.sickPersonVo.ZYH;
            String brbq = mAppApplication.getAreaId();
            String start = params[0];
            String end = params[1];
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            ParserModel result = AnnounceApi.getInstance(
                    getApplicationContext()).GetPatientTeacherQuery(zyh, brbq,
                    start, end, jgid, sysType);
            return result;
        }

        @Override
        protected void onPostExecute(ParserModel result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null == result) {

                showMsgAndVoiceAndVibrator("加载失败");
                return;
            }
            if (result.isOK()) {
                @SuppressWarnings("unchecked")
                ArrayList<AnnounceItem> list = result.getList("Table1");
                AnnounceAdapter adapter = new AnnounceAdapter(
                        AnnounceHistoryActivity.this, list);
                mPtrListView.setAdapter(adapter);
            } else {
                result.showToast(AnnounceHistoryActivity.this);
            }
        }
    }

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);
        if (viewId == R.id.slt_stime_ly) {
            //选择开始时间
            String endDate = etime.getText().toString();
            boolean after = DateTimeFactory.getInstance().dateAfter(nowDate, endDate);
            if (after) {
                showMsgAndVoiceAndVibrator("开始时间后于结束时间,请重新选择!");
                return;
            }
        } else if (viewId == R.id.slt_etime_ly) {
            //选择结束时间
            String startDate = stime.getText().toString();
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
        if (viewId == R.id.slt_stime_ly) {
            stime.setText(timeStr);
        } else if (viewId == R.id.slt_etime_ly) {
            etime.setText(timeStr);
        }
    }

    @Override
    public void initBarBroadcast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (BarcodeActions.Bar_Get.equals(intent.getAction())) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        Intent result = new Intent(context,
                                UserModelActivity.class);
                        result.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        result.putExtra("barinfo", entity);
                        startActivity(result);
                    }

                } else if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    Intent result = new Intent(context, UserModelActivity.class);
                    result.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    result.putExtra("refresh", true);
                    startActivity(result);
                }
            }
        };
    }

    // @Override
    // public void initBarBroadcast() {
    //
    // barBroadcast = new BroadcastReceiver() {
    // @Override
    // public void onReceive(Context arg0, Intent intent) {
    // if (IBarCode.Refresh.equals(intent.getAction())) {
    //
    // if (stime != null && etime != null) {
    // String start = stime.getText().toString();
    // String end = etime.getText().toString();
    // performGetTask(start, end);
    // }
    // }
    // }
    // };
    // }

}
