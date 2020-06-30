package com.bsoft.mob.ienr.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseActivity;
import com.bsoft.mob.ienr.adapter.BloodRecieveAdapter;
import com.bsoft.mob.ienr.api.BloodTransfusionApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.TestDataHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.blood.BloodReciveInfo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-13 上午11:37:00
 * @类说明 体征查询
 */
public class BloodHistoryActivity extends BaseActivity {

    public static final byte Operat_Recieve = 1;

    protected byte Operat_Last = Operat_Recieve;

    public static final String Operat_INT_START_TYPE = "start_type";

    private ListView mListView;

    private BloodRecieveAdapter mAdapter;

    private TextView sTimeTxt;
    private TextView eTimeTxt;

    private View sltStimeView;
    private View sltEtimeView;

    private ImageView searchBtn;

    private ArrayList<BloodReciveInfo> mList = new ArrayList<BloodReciveInfo>();


    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_blood_history;
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            Operat_Last = intent.getByteExtra(Operat_INT_START_TYPE, Operat_Recieve);
        }

        findView();
        toRefreshData();
    }

    private void initSearchBtn() {

        searchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });
    }


    public void actionDeleteTask(String xmid) {
        DeleDataTask task = new DeleDataTask(Operat_Last);
        tasks.add(task);
        task.execute(xmid);
    }

    public void findView() {

        searchBtn = (ImageView) findViewById(R.id.search);

        sTimeTxt = (TextView) findViewById(R.id.stime);
        eTimeTxt = (TextView) findViewById(R.id.etime);
        sltStimeView = findViewById(R.id.slt_stime_ly);
        sltEtimeView = findViewById(R.id.slt_etime_ly);
        final TextView stimeTitle = (TextView) findViewById(R.id.stime_title);
        final TextView etimeTitle = (TextView) findViewById(R.id.etime_title);

        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);
        mListView = (ListView) findViewById(R.id.id_lv);
        EmptyViewHelper.setEmptyView(mListView, "mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);
        actionBar.setTitle(getString(R.string.bhys_title));

        initTime();
        initListView();
        initSearchBtn();
    }

    private void initListView() {

        //BloodRecieveAdapter
        mAdapter = new BloodRecieveAdapter(this, mList, true);
        //adapter = new BloodRecieveAdapter(this,,true);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemLongClickListener(
                new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, final int position, long id) {

                        BloodReciveInfo item = mAdapter.getItem(position);
                        if (item != null) {
                            DevliyDialong(item.XMID);
                            return true;
                        }

                        return false;
                    }
                });


    }

    @Override
    protected void toRefreshData() {
        GetDataTask getDataTask = new GetDataTask(Operat_Last);
        tasks.add(getDataTask);
        getDataTask.execute();//
    }

    private void DevliyDialong(final String xmid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BloodHistoryActivity.this);
        builder.setMessage("是否确定进行撤消？");
        builder.setPositiveButton(getString(R.string.project_operate_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                actionDeleteTask(xmid);
            }
        });

        builder.setNegativeButton(getString(R.string.project_operate_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });//建设
        builder.create().show();

    }

    void initTime() {
        // 当天
        String endDateStr = DateTimeHelper.getServerDate();
        eTimeTxt.setText(endDateStr);

        // 前天
        String startDateStr=DateTimeHelper.dateAddedDays(endDateStr,-1);
        sTimeTxt.setText(startDateStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);
    }

    public OnClickListener onClickListener = new OnClickListener() {

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
            String endDate= eTimeTxt.getText().toString();
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

    private void initTimeTxt(String nowDate, int viewId) {
        if (viewId == R.id.slt_etime_ly) {
            eTimeTxt.setText(nowDate);
        } else if (viewId == R.id.slt_stime_ly) {
            sTimeTxt.setText(nowDate);
        }

    }

    /**
     * 异步加载
     */
    private class GetDataTask extends AsyncTask<Void, Void, Response<List<BloodReciveInfo>>> {

        private byte operatType;

        public GetDataTask(byte operatType) {
            this.operatType = operatType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }


        @Override
        protected Response<List<BloodReciveInfo>> doInBackground(Void... params) {

            String sTime = sTimeTxt.getText().toString();
            String eTime = eTimeTxt.getText().toString();

            switch (operatType) {
                case Operat_Recieve: {
                    //即建设大街见
                    return BloodTransfusionApi.getInstance(getApplicationContext())
                            .getBloodRecieveList(mAppApplication.getAreaId(), "1", sTime, eTime, mAppApplication.jgId);
                }
                default:
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response<List<BloodReciveInfo>> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(BloodHistoryActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    mList = (ArrayList<BloodReciveInfo>) result.Data;
                    if (EmptyTool.isEmpty(mList)) {
                        mList = new ArrayList<>();
                        showMsgAndVoice("暂无血液需要签收!");
                        //
                        TestDataHelper.buidTestData(BloodReciveInfo.class, mList);
                    }
                    mAdapter.refreshData(mList);

                } else {
                    showMsgAndVoice(result.Msg);
                    return;
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }

    }

    /**
     * 异步加载
     */
    private class DeleDataTask extends AsyncTask<String, Void, Response<String>> {

        private byte operatType;
        String xmid;

        public DeleDataTask(byte operatType) {
            this.operatType = operatType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.deleteing);
        }

        @Override
        protected Response<String> doInBackground(String... params) {
            xmid = params[0];
            switch (operatType) {
                case Operat_Recieve: {
                    return BloodTransfusionApi.getInstance(getApplicationContext())
                            .devliyBloodRecieve(xmid, mAppApplication.jgId);
                }
                default:
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(BloodHistoryActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionDeleteTask(xmid);
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    if (!EmptyTool.isBlank(result.Msg)) {
                        showMsgAndVoice(result.Msg);
                    } else {
                        showMsgAndVoice("操作成功!");
                    }
                    //重新加载
                    toRefreshData();
                  /*  MediaUtil.getInstance(getApplicationContext()).playSound(R.raw.success,
                            getApplicationContext());*/
                } else {
                    if (!EmptyTool.isBlank(result.Msg)) {
                        showMsgAndVoice(result.Msg);
                    } else {
                        showMsgAndVoiceAndVibrator("操作失败!");
                    }
                    /*MediaUtil.getInstance(getApplicationContext()).playSound(R.raw.wrong,
                            getApplicationContext());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

        }

    }


}
