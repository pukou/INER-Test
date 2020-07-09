/**
 * @Title: RiskEvaluationFragment.java
 * @Package com.bsoft.mob.ienr.fragment.user
 * @Description: 风险评估
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-7 下午2:43:23
 * @version V1.0
 */
package com.bsoft.mob.ienr.fragment.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.RiskEvaluateActivity;
import com.bsoft.mob.ienr.api.NurseFormApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.event.EventType;
import com.bsoft.mob.ienr.event.RiskEvaluationEvent;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.risk.RiskOverview;
import com.bsoft.mob.ienr.model.risk.SimRiskRecord;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: RiskEvaluationFragment
 * @Description: 风险评估
 * @date 2015-12-7 下午2:43:23
 */
public class RiskEvaluationFragment extends BaseUserFragment {
    // 眉栏工具条
    // 下拉刷新的分组列表
    private ExpandableListView refreshView;
    // 风险列表
    private List<RiskOverview> mList;
    // 风险列表适配器
    private RiskListAdapter mAdapter;


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_risk_evaluation;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        initView(rootLayout);
        initActionBar();
        initBroadCast();
        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        getData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RiskEvaluationEvent baseEvent) {
        if (EventType.REFRESH == baseEvent.eventType) {
            toRefreshData();
//            Toast.makeText(mContext, "REFRESH", Toast.LENGTH_LONG).show();
        } else if (EventType.FINISH == baseEvent.eventType) {
            Toast.makeText(mContext, "FINISH", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "other", Toast.LENGTH_LONG).show();
        }

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
     * @Title: initActionBar
     * @Description: 初始化工具条
     */
    private void initActionBar() {
        actionBar.setTitle("风险评估");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                + mAppApplication.sickPersonVo.BRXM);
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

        EmptyViewHelper.setEmptyView(refreshView, "refreshView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, refreshView);

        refreshView.setOnChildClickListener(
                new OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent,
                                                View v, int groupPosition, int childPosition,
                                                long id) {
                        Intent intent = new Intent(getActivity(),
                                RiskEvaluateActivity.class);
                        intent.putExtra("PGDH", mList.get(groupPosition).PGDH);
                        intent.putExtra("PGXH", mList.get(groupPosition).PGJL
                                .get(childPosition).PGXH);
                        intent.putExtra("PGLX", mList.get(groupPosition).PGJL
                                .get(childPosition).PGLX);
                        intent.putExtra("BDMC", mList.get(groupPosition).PGDMC);
                        intent.putExtra("ISADD", false);
                        startActivity(intent);
                        return true;
                    }
                });
    }


    private void getData() {
        GetDataTask task = new GetDataTask();
        tasks.add(task);
        task.execute();
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: GetDataTask
     * @Description: 获取风险列表内部类
     * @date 2015-12-8 下午4:31:16
     */
    class GetDataTask extends
            AsyncTask<Void, Void, Response<List<RiskOverview>>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: </p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description: </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<List<RiskOverview>> doInBackground(Void... params) {
            return NurseFormApi.getInstance(getActivity()).togetRiskList(
                    mAppApplication.sickPersonVo.ZYH, mAppApplication.jgId, mAppApplication.getAreaId());
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description: </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<List<RiskOverview>> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();

                } else if (result.ReType == 0) {
                    mList = result.Data;
                    if (mList != null) {
                        for (RiskOverview overview : mList) {
                            if (overview.PGJL == null) {
                                overview.PGJL = new ArrayList<>();
                            }
                        }
                    }
                    mAdapter = new RiskListAdapter();
                    refreshView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            TextView risk_name = (TextView) v.findViewById(R.id.risk_name);
                            risk_name.setSelected(!risk_name.isSelected());
                            return false;
                        }
                    });
                    refreshView.setAdapter(mAdapter);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(mContext, "zfq shuang ", Toast.LENGTH_SHORT).show();
        if (resultCode == Activity.RESULT_OK) {
            toRefreshData();
        }

    }


    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: RiskListAdapter
     * @Description: 风险列表适配器
     * @date 2015-12-8 下午4:37:32
     */
    class RiskListAdapter extends BaseExpandableListAdapter {

        /*
         * (非 Javadoc) <p>Title: getChild</p> <p>Description: </p>
         *
         * @param arg0
         *
         * @param arg1
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getChild(int, int)
         */
        @Override
        public SimRiskRecord getChild(int arg0, int arg1) {
            return mList.get(arg0).PGJL.get(arg1);
        }

        /*
         * (非 Javadoc) <p>Title: getChildId</p> <p>Description: </p>
         *
         * @param groupPosition
         *
         * @param childPosition
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getChildId(int, int)
         */
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        /*
         * (非 Javadoc) <p>Title: getChildView</p> <p>Description: </p>
         *
         * @param groupPosition
         *
         * @param childPosition
         *
         * @param isLastChild
         *
         * @param convertView
         *
         * @param parent
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getChildView(int, int,
         * boolean, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.item_list_text_two_secondary_icon, parent, false);
                vHolder = new ChildHolder();
                vHolder.tv_time = (TextView) convertView
                        .findViewById(R.id.detail_name);
                vHolder.tv_nurse = (TextView) convertView
                        .findViewById(R.id.detail_num);
                vHolder.tv_goal = (TextView) convertView
                        .findViewById(R.id.id_tv_more);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ChildHolder) convertView.getTag();
            }
            vHolder.tv_goal.setText(mList.get(groupPosition).PGJL
                    .get(childPosition).ZKMS
                    + "("
                    + mList.get(groupPosition).PGJL.get(childPosition).PGZF
                    + ")");
            vHolder.tv_nurse.setText(mList.get(groupPosition).PGJL
                    .get(childPosition).PGHS);
//			vHolder.tv_time.setText(DateUtil.get8To7Sstr(
//					mList.get(groupPosition).PGJL.get(childPosition).PGSJ)
//					.substring(5));
            String time = (mList.get(groupPosition).PGJL.get(childPosition).PGSJ);
            time = time.substring(5);

            vHolder.tv_time.setText(time);

            return convertView;
        }

        /*
         * (非 Javadoc) <p>Title: getChildrenCount</p> <p>Description: </p>
         *
         * @param groupPosition
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            return mList.get(groupPosition).PGJL.size();
        }

        /*
         * (非 Javadoc) <p>Title: getGroup</p> <p>Description: </p>
         *
         * @param groupPosition
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getGroup(int)
         */
        @Override
        public RiskOverview getGroup(int groupPosition) {
            return mList.get(groupPosition);
        }

        /*
         * (非 Javadoc) <p>Title: getGroupCount</p> <p>Description: </p>
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getGroupCount()
         */
        @Override
        public int getGroupCount() {
            return mList.size();
        }

        /*
         * (非 Javadoc) <p>Title: getGroupId</p> <p>Description: </p>
         *
         * @param groupPosition
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getGroupId(int)
         */
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        /*
         * (非 Javadoc) <p>Title: getGroupView</p> <p>Description: </p>
         *
         * @param groupPosition
         *
         * @param isExpanded
         *
         * @param convertView
         *
         * @param parent
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
         * android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.item_list_group_risk, parent, false);
                vHolder = new GroupHolder();
                vHolder.tv_name = (TextView) convertView
                        .findViewById(R.id.risk_name);
                vHolder.tv_desc = (TextView) convertView
                        .findViewById(R.id.risk_decrib);
                vHolder.iv_add = (ImageView) convertView
                        .findViewById(R.id.risk_add);
                vHolder.tv_star = (TextView) convertView
                        .findViewById(R.id.risk_star);
                convertView.setTag(vHolder);
            } else {
                vHolder = (GroupHolder) convertView.getTag();
            }
            vHolder.tv_desc.setText(mList.get(groupPosition).PGMS);
            vHolder.tv_name.setText(mList.get(groupPosition).PGDMC);
            if (!EmptyTool.isBlank(mList.get(groupPosition).TXJH)) {
                if (mList.get(groupPosition).TXJH.equals("1"))
                    vHolder.tv_desc.setTextColor(ContextCompat.getColor(mContext,
                            R.color.blue));
                else if (mList.get(groupPosition).TXJH.equals("2"))
                    vHolder.tv_desc.setTextColor(ContextCompat.getColor(mContext,
                            R.color.red));
            }
            String txrq = mList.get(groupPosition).TXRQ;
            if (!EmptyTool.isBlank(txrq)) {
                if (DateTimeHelper.dateTimeAfter(txrq)) {
                    vHolder.tv_star.setVisibility(View.VISIBLE);
                } else {
                    vHolder.tv_star.setVisibility(View.GONE);
                }
            } else {
                vHolder.tv_star.setVisibility(View.GONE);
            }
            vHolder.iv_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),
                            RiskEvaluateActivity.class);
                    intent.putExtra("PGDH", mList.get(groupPosition).PGDH);
                    intent.putExtra(
                            "PGLX",
                            mList.get(groupPosition).PGLX);
                    intent.putExtra("BDMC", mList.get(groupPosition).PGDMC);
                    intent.putExtra("ISADD", true);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        /*
         * (非 Javadoc) <p>Title: hasStableIds</p> <p>Description: </p>
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#hasStableIds()
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

        /*
         * (非 Javadoc) <p>Title: isChildSelectable</p> <p>Description: </p>
         *
         * @param groupPosition
         *
         * @param childPosition
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
         */
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    class GroupHolder {
        TextView tv_name;
        TextView tv_desc;
        ImageView iv_add;
        TextView tv_star;
    }

    class ChildHolder {
        TextView tv_time;
        TextView tv_goal;
        TextView tv_nurse;
    }
}
