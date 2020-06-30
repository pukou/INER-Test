/**
 * @Title: NursePlanFragment.java
 * @Package com.bsoft.mob.ienr.fragment.user
 * @Description:护理计划
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-11-18 下午3:11:46
 * @version V1.0
 */
package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.adapter.ClincialEventListAdapter;
import com.bsoft.mob.ienr.api.LifeSignApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.clinicalevent.ClinicalEventInfo;
import com.bsoft.mob.ienr.model.clinicalevent.ClinicalEventSaveData;
import com.bsoft.mob.ienr.model.clinicalevent.ClinicalEventType;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.io.IOException;
import java.util.List;

/**
 * Description: 临床事件
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-12-08
 * Time: 14:32
 * Version:
 */
public class ClinicalEventFragment extends BaseUserFragment {
    // 眉栏工具条

    // 下拉刷新的分组列表
    private ExpandableListView refreshView;
    // 临床事件数据
    private List<ClinicalEventType> mList;
    // 计划适配器
    private ClincialEventListAdapter mAdapter;

    private int groupId, itemId, operType;


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_clinical_event;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        refreshView = (ExpandableListView) rootLayout
                .findViewById(R.id.id_elv);
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, refreshView);
        initActionBar();
        initBroadCast();
        toRefreshData();
    }


    @Override
    protected void toRefreshData() {
        getData();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initActionBar
     * @Description: 初始化工具条
     */
    private void initActionBar() {
        actionBar.setTitle("临床事件");
        String brch = EmptyTool.isBlank(mAppApplication.sickPersonVo.BRCH) ? "" : mAppApplication.sickPersonVo.BRCH;
        actionBar.setPatient(brch + mAppApplication.sickPersonVo.BRXM);
    }


    /**
     * @param
     * @return void
     * @throws
     * @Title: initBroadCast
     * @Description: 初始化条码处理广播接收器
     */
    private void initBroadCast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                            + mAppApplication.sickPersonVo.BRXM);
                }
            }
        };
    }

    private void getData() {
        GetDataTast task = new GetDataTast();
        tasks.add(task);
        task.execute();
    }

    class GetDataTast extends AsyncTask<Void, Void, Response<List<ClinicalEventType>>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description:执行网络请求 </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<List<ClinicalEventType>> doInBackground(Void... params) {
            return LifeSignApi.getInstance(getActivity()).getClinicalEventTypeList(
                    mAppApplication.sickPersonVo.ZYH,
                    mAppApplication.user.YHID,
                    mAppApplication.getAreaId(),
                    mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<List<ClinicalEventType>> result) {
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
                    mList = result.Data;
                    setupAdapter();
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

    private void editData() {
        EditDataTast task = new EditDataTast();
        tasks.add(task);
        task.execute();
    }

    class EditDataTast extends AsyncTask<Void, Void, Response<List<ClinicalEventType>>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description:执行网络请求 </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<List<ClinicalEventType>> doInBackground(Void... params) {
            if (operType == 0) {//编辑
                String data = "";
                try {
                    ClinicalEventSaveData saveData = new ClinicalEventSaveData();
                    saveData.ZYH = mAppApplication.sickPersonVo.ZYH;
                    saveData.YHID = mAppApplication.user.YHID;
                    saveData.BQID = mAppApplication.getAreaId();
                    saveData.JGID = mAppApplication.jgId;
                    saveData.ClinicalEventType = mList.get(groupId);
                    data = JsonUtil.toJson(saveData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return LifeSignApi.getInstance(getActivity()).clinicalEventSave(data);
            } else if (operType == 1) {//删除
                return LifeSignApi.getInstance(getActivity()).clinicalEventDelete(mList.get(groupId).ClinicalEventInfoList.get(itemId).SJXH,
                        mAppApplication.sickPersonVo.ZYH, mAppApplication.user.YHID, mAppApplication.getAreaId(), mAppApplication.jgId);
            } else {
                return null;
            }
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<List<ClinicalEventType>> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            editData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    mList = result.Data;
                    setupAdapter();
                    showMsgAndVoice(result.Msg);
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

    private void setupAdapter() {
        mAdapter = new ClincialEventListAdapter(getActivity(), mList, mAppApplication.user.YHID);
        mAdapter.setClickListener(new ClincialEventListAdapter.ClickListener() {
            @Override
            public void add(View view, int groupPosition) {
                groupId = groupPosition;
                //展开当前组
                refreshView.expandGroup(groupPosition);
                ClinicalEventInfo clinicalEventInfo = new ClinicalEventInfo();
                clinicalEventInfo.SJXH = "-1";
                clinicalEventInfo.SJGS = "3";
                clinicalEventInfo.JZXH = mAppApplication.sickPersonVo.ZYH;
                clinicalEventInfo.JZHM = mAppApplication.sickPersonVo.ZYH;
                clinicalEventInfo.SJFL = mList.get(groupPosition).TypeValue;
                String datetime = DateTimeHelper.getServerDateTime();
                clinicalEventInfo.FSSJ = datetime;
                clinicalEventInfo.SJMS = "";
                clinicalEventInfo.JLGH = mAppApplication.user.YHID;
                clinicalEventInfo.JLSJ = datetime;
                clinicalEventInfo.XTBZ = "0";
                clinicalEventInfo.JGID = mAppApplication.jgId;
                clinicalEventInfo.MODIFIED = true;
                mList.get(groupPosition).ClinicalEventInfoList.add(clinicalEventInfo);
                mList.get(groupPosition).Count = String.valueOf(Integer.parseInt(mList.get(groupPosition).Count) + 1);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void save(View view, int groupPosition) {
                groupId = groupPosition;
                operType = 0;
                editData();
            }

            @Override
            public void time(View view, int groupPosition, int childPosition, String fssj) {

                if (mList.get(groupPosition).ClinicalEventInfoList.get(childPosition).JLGH.equals(mAppApplication.user.YHID)) {
                    groupId = groupPosition;
                    itemId = childPosition;
                    fssj = fssj.replace("发生时间：", "");
                    showDateTimePickerCompat(fssj, view.getId());
                } else {
                    showMsgAndVoiceAndVibrator("不允许修改别人录入的临床事件数据!");
                }
            }

            @Override
            public void xtbs(View view, int groupPosition, int childPosition) {
                if (mList.get(groupPosition).ClinicalEventInfoList.get(childPosition).JLGH.equals(mAppApplication.user.YHID)) {
                    groupId = groupPosition;
                    itemId = childPosition;
                    operType = 1;//删除
                    if (mList.get(groupPosition).ClinicalEventInfoList.get(childPosition).SJXH.equals("-1")) {
                        mList.get(groupPosition).ClinicalEventInfoList.remove(childPosition);
                        mList.get(groupPosition).Count = String.valueOf(Integer.parseInt(mList.get(groupPosition).Count) - 1);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        editData();
                    }
                } else {
                    showMsgAndVoiceAndVibrator("不允许删除别人录入的临床事件数据!");
                }
            }
        });
        refreshView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                TextView clinicalevent_name = (TextView) v.findViewById(R.id.clinicalevent_name);
                clinicalevent_name.setSelected(!clinicalevent_name.isSelected());
                return false;
            }
        });
        refreshView.setAdapter(mAdapter);
    }


    @Override
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {

        String datetime = DateTimeFactory.getInstance()
                .ymdhms2DateTime(year, month, dayOfMonth,
                        hourOfDay, minute, 0);
        mList.get(groupId).ClinicalEventInfoList.get(itemId).FSSJ = datetime;
        mList.get(groupId).ClinicalEventInfoList.get(itemId).MODIFIED = true;
        mAdapter.notifyDataSetChanged();
    }


}
