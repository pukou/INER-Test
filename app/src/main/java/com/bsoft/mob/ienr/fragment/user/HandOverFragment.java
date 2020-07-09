package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.HandOverActivity;
import com.bsoft.mob.ienr.api.HandOverApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.adapter.HandOverListAdapter;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.handover.HandOverForm;
import com.bsoft.mob.ienr.util.AgainLoginUtil;


import java.util.List;

/**
 * Description: 交接单
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-15
 * Time: 14:19
 * Version:
 */
public class HandOverFragment extends BaseUserFragment {
    // 眉栏工具条

    // 下拉刷新的分组列表
    private ExpandableListView refreshView;
    // 交接单列表
    private List<HandOverForm> mList;
    // 宣教适配器
    private HandOverListAdapter mAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBroadCast();
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }
    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_hand_over;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        initView(rootLayout);
        initActionBar();
        initRefreshView();
        toRefreshData();
    }

    /**
     * @param @param view 设定文件
     * @return void 返回类型
     * @throws
     * @Title: initView
     * @Description: 初始化界面
     */
    private void initView(View view) {
        refreshView = (ExpandableListView) view
                .findViewById(R.id.id_elv);
    }

    @Override
    protected void toRefreshData() {
        getData();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initRefreshView
     * @Description: 初始化下拉刷新列表
     */
    private void initRefreshView() {

        refreshView.setOnChildClickListener(
                new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent,
                                                View v, int groupPosition, int childPosition,
                                                long id) {
                        Intent intent = new Intent(getActivity(),
                                HandOverActivity.class);
                        intent.putExtra("ysxh", mList.get(groupPosition).YSXH);
                        intent.putExtra("jlxh", mList.get(groupPosition).HandOverRecordList.get(childPosition).JLXH);
                        intent.putExtra("yslx", mList.get(groupPosition).HandOverRecordList.get(childPosition).YSLX);

                        startActivity(intent);
                        return true;
                    }
                });
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initActionBar
     * @Description: 初始化工具条
     */
    private void initActionBar() {
        actionBar.setTitle("护理交接");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                + mAppApplication.sickPersonVo.BRXM);
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
                    actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                            + mAppApplication.sickPersonVo.BRXM);
                }
            }
        };
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getData
     * @Description: 获取健康宣教列表
     */
    private void getData() {
        GetDataTast task = new GetDataTast();
        tasks.add(task);
        task.execute();
    }

    class GetDataTast extends AsyncTask<Void, Void, Response<List<HandOverForm>>> {

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
        protected Response<List<HandOverForm>> doInBackground(Void... params) {
            return HandOverApi.getInstance(getActivity()).getHandOverList(
                    mAppApplication.sickPersonVo.ZYH,
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
        protected void onPostExecute(Response<List<HandOverForm>> result) {
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
                    mAdapter = new HandOverListAdapter(getActivity(), mList);
                    refreshView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            TextView hand_over_name = (TextView) v.findViewById(R.id.id_tv);
                            hand_over_name.setSelected(!hand_over_name.isSelected());
                            return false;
                        }
                    });
                    refreshView.setAdapter(mAdapter);
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


}
