/**
 * @Title: RiskEvaluateListActivity.java
 * @Package com.bsoft.mob.ienr.activity.user
 * @Description: 风险评估列表
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-1-6 上午11:07:59
 * @version V1.0
 */
package com.bsoft.mob.ienr.activity.user;

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

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.NurseFormApi;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.risk.RiskOverview;
import com.bsoft.mob.ienr.model.risk.SimRiskRecord;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: RiskEvaluateListActivity
 * @Description: 风险评估列表
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2016-1-6 上午11:07:59
 *
 */
public class RiskEvaluateListActivity extends BaseBarcodeActivity {


    // 眉栏工具条
    // 下拉刷新的分组列表
    private ExpandableListView refreshView;
    // 风险列表
    private List<RiskOverview> mList;
    // 风险列表适配器
    private RiskListAdapter mAdapter;



    /**
     *
     * @Title: initActionBar
     * @Description: 初始化工具条
     * @param
     * @return void
     * @throws
     */
    private void initActionBar() {
        actionBar.setTitle("风险评估");
        actionBar.setPatient(application.sickPersonVo.XSCH
                + application.sickPersonVo.BRXM);
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_risk_evaluation_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        initView();
        initActionBar();
        toRefreshData();
    }
    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }
    /**
     *
     * @Title: initView
     * @Description: 初始化界面
     * @param @param view 设定文件
     * @return void 返回类型
     * @throws
     */
    private void initView() {
        refreshView = (ExpandableListView) findViewById(R.id.id_elv);

        refreshView.setOnChildClickListener(
                new OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent,
                                                View v, int groupPosition, int childPosition,
                                                long id) {
                        Intent intent = new Intent(
                                RiskEvaluateListActivity.this,
                                RiskEvaluateActivity.class);
                        intent.putExtra("PGDH", mList.get(groupPosition).PGDH);
                        intent.putExtra("PGXH", mList.get(groupPosition).PGJL
                                .get(childPosition).PGXH);
                        intent.putExtra("PGLX", mList.get(groupPosition).PGJL
                                .get(childPosition).PGLX);
                        intent.putExtra("BDMC", mList.get(groupPosition).PGDMC);
                        intent.putExtra("ISADD", false);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                });
    }


    @Override
    protected void toRefreshData() {
        GetDataTask task = new GetDataTask();
        tasks.add(task);
        task.execute();
    }

    /*
         * (非 Javadoc) <p>Title: initBarBroadcast</p> <p>Description: </p>
         *
         * @see com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity#initBarBroadcast()
         */
    @Override
    public void initBarBroadcast() {

    }

    /**
     * @ClassName: GetDataTask
     * @Description: 获取风险列表内部类
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @date 2015-12-8 下午4:31:16
     *
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
            return NurseFormApi
                    .getInstance(RiskEvaluateListActivity.this)
                    .togetRiskList(application.sickPersonVo.ZYH, application.jgId, application.getAreaId());
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
                    new AgainLoginUtil(RiskEvaluateListActivity.this, application,
                            new AgainLoginUtil.LoginSucessListener() {
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

    /**
     * @ClassName: RiskListAdapter
     * @Description: 风险列表适配器
     * @author 吕自聪 lvzc@bsoft.com.cn
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
                convertView = LayoutInflater
                        .from(RiskEvaluateListActivity.this).inflate(
                                R.layout.item_list_text_three_secondary,  parent,false);
                //!!!
                convertView.setBackgroundResource(R.color.classicViewBg);
                vHolder = new ChildHolder();
                vHolder.tv_time = (TextView) convertView
                        .findViewById(R.id.id_tv_one);
                vHolder.tv_nurse = (TextView) convertView
                        .findViewById(R.id.id_tv_three);
                vHolder.tv_goal = (TextView) convertView
                        .findViewById(R.id.id_tv_two);
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
            vHolder.tv_time.setText(mList.get(groupPosition).PGJL.get(childPosition).PGSJ.substring(5));
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
                convertView = LayoutInflater
                        .from(RiskEvaluateListActivity.this).inflate(
                                R.layout.item_list_group_risk,  parent,false);
                vHolder = new GroupHolder();
                vHolder.tv_name = (TextView) convertView
                        .findViewById(R.id.risk_name);
                vHolder.tv_desc = (TextView) convertView
                        .findViewById(R.id.risk_decrib);
                vHolder.iv_add = (ImageView) convertView
                        .findViewById(R.id.risk_add);
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
            vHolder.iv_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RiskEvaluateListActivity.this,
                            RiskEvaluateActivity.class);
                    intent.putExtra("PGDH", mList.get(groupPosition).PGDH);
                    intent.putExtra("PGLX", mList.get(groupPosition).PGLX);
                    intent.putExtra("BDMC", mList.get(groupPosition).PGDMC);
                    intent.putExtra("ISADD", true);
                    startActivity(intent);
                    finish();
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
    }

    class ChildHolder {
        TextView tv_time;
        TextView tv_goal;
        TextView tv_nurse;
    }
}
