package com.bsoft.mob.ienr.fragment.user;

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

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.adapter.BTAdapter;
import com.bsoft.mob.ienr.api.BloodTransfusionApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.blood.BloodTransfusionInfo;
import com.bsoft.mob.ienr.model.blood.BloodTransfusionTourInfo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-11 上午11:20:14
 * @类说明 血袋上交
 */
public class BloodBagDeliveryFragment extends BaseUserFragment {


    private ListView mListView;

    private TextView stime, etime;

    private View sltStimeView;
    private View sltEtimeView;

    private ImageView searchBtn;

    private ArrayList<BloodTransfusionInfo> mList = null;
    private ArrayList<BloodTransfusionInfo> resultList = null;

    private BTAdapter mAdapter;

    private BloodTransfusionTourInfo Previous_Info;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_blood_bag_delivery;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {

        mListView = root
                .findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(mListView, "mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);
        stime = (TextView) root.findViewById(R.id.stime);
        etime = (TextView) root.findViewById(R.id.etime);


        sltStimeView = root.findViewById(R.id.slt_stime_ly);
        sltEtimeView = root.findViewById(R.id.slt_etime_ly);

        final TextView stimeTitle = (TextView) sltStimeView
                .findViewById(R.id.stime_title);
        final TextView etimeTitle = (TextView) sltEtimeView
                .findViewById(R.id.etime_title);

        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);

        searchBtn = (ImageView) root.findViewById(R.id.search);

        initAcionBar();
        initTime();
        initSearchBtn();
        initBroadCast();
        toRefreshData();

    }

    private void initTime() {

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        etime.setText(eTimeStr);

        // 前天
        String startDate = DateTimeHelper.dateAddedDays(nowDate, -7);
        String sTimeStr = startDate;
        stime.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);


    }


    public OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String dateStr = null;
            int id = v.getId();
            if (id == R.id.slt_stime_ly) {
                dateStr = stime.getText().toString();
            } else if (id == R.id.slt_etime_ly) {
                dateStr = etime.getText().toString();
            }
            showDatePickerCompat(dateStr, id);
        }
    };

    private void initTimeTxt(String dateStr, int viewId) {
        String timeStr = dateStr;
        if (viewId == R.id.slt_etime_ly) {
            etime.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            stime.setText(timeStr);
        }

    }

    private void initSearchBtn() {

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });
    }

    @Override
    public void onDateSet(int year, int monthOfYear, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, monthOfYear, dayOfMonth);
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


    private void initAcionBar() {

        actionBar.setTitle("血袋上交");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                + mAppApplication.sickPersonVo.BRXM);
        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {
                if (mAdapter == null || mAdapter.checkedPostion == -1) {
                    showMsgAndVoiceAndVibrator("你还未选择");
                    return;
                }
                actionOperateTask();
            }

            @Override
            public String getText() {
                return "执行";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });

    }

    @Override
    protected void toRefreshData() {
        GetHttpTask getHttpTask = new GetHttpTask();
        tasks.add(getHttpTask);
        getHttpTask.execute();
    }

    private void initBroadCast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                            + mAppApplication.sickPersonVo.BRXM);
                    toRefreshData();
                } else if (BarcodeActions.Bar_Get.equals(action)) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        FastSwitchUtils.fastSwith(
                                (UserModelActivity) getActivity(), entity);
                    }

                }
            }
        };
    }


    class GetHttpTask extends AsyncTask<String, Integer, Response<List<BloodTransfusionInfo>>> {

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<BloodTransfusionInfo>> doInBackground(String... params) {

            BloodTransfusionApi api = BloodTransfusionApi.getInstance(getActivity());
            if (mAppApplication.sickPersonVo == null) {
                return null;
            }
            String sTimeStr = stime.getText().toString();
            String eTimeStr = etime.getText().toString();
            //加一天
            /*String dateTime = DateTimeHelper.dateTimeAddedDays(eTimeStr, 1);
            eTimeStr = DateTimeFactory.getInstance().dateTime2Date(dateTime);*/
            String dateTime = DateTimeFactory.getInstance().date2DateTime(eTimeStr);
            String dateTime_nextDay = DateTimeHelper.dateTimeAddedDays(dateTime, 1);
            eTimeStr = DateTimeFactory.getInstance().dateTime2Date(dateTime_nextDay);

            String jgid = mAppApplication.jgId;
            String zyh = mAppApplication.sickPersonVo.ZYH;
            return api.GetBloodTransfusionList(sTimeStr, eTimeStr, zyh, jgid);
        }

        @Override
        protected void onPostExecute(Response<List<BloodTransfusionInfo>> result) {

            hideSwipeRefreshLayout();

            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    @SuppressWarnings("unchecked")
                    ArrayList<BloodTransfusionInfo> list = (ArrayList<BloodTransfusionInfo>) result.Data;
                    mList = list;
                    filterAndSetList();

                } else {
                    showMsgAndVoice(result.Msg);
                    /*MediaUtil.getInstance(getActivity()).playSound(
                            R.raw.wrong, getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    protected void filterAndSetList() {

        if (mList == null) {
            showMsgAndVoice("暂无输血记录");
            return;
        }

        resultList = new ArrayList<BloodTransfusionInfo>();

        for (BloodTransfusionInfo entity : mList) {
            if (!EmptyTool.isBlank(entity.JSR) && (entity.SJPB == null || entity.SJPB.equals("0")) && (entity.HSPB == null || entity.HSPB.equals("0"))) {
                resultList.add(entity);
            }
        }

        if (resultList.size() == 0) {
            mAdapter = null;
        } else {
            mAdapter = new BTAdapter(getActivity(), resultList);
        }
        mListView.setAdapter(mAdapter);

    }

    void actionOperateTask() {
        OperateTask getTask = new OperateTask();
        tasks.add(getTask);
        getTask.execute();
    }

    // 上交血袋操作
    class OperateTask extends AsyncTask<String, Void, Response<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<String> doInBackground(String... params) {

            if (mAppApplication.user == null || mAdapter == null) {
                return null;
            }

            return BloodTransfusionApi.getInstance(getActivity()).saveBloodBagRecieve(
                    resultList.get(mAdapter.checkedPostion).SXDH,
                    mAppApplication.user.YHID, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<String> result) {

            tasks.remove(this);
            hideLoadingDialog();

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionOperateTask();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(result.Msg);

                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：参数错误");
                return;
            }
        }
    }

}
