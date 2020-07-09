/**
 * @Title: RiskMeasureListActivity.java
 * @Package com.bsoft.mob.ienr.activity.user
 * @Description: 风险措施主列表页
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-14 上午9:05:27
 * @version V1.0
 */
package com.bsoft.mob.ienr.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.NurseFormApi;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.risk.MeasureOverview;
import com.bsoft.mob.ienr.model.risk.SimMeasureRecord;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: RiskMeasureListActivity
 * @Description: 风险措施主列表页
 * @date 2015-12-14 上午9:05:27
 */
public class RiskMeasureListActivity extends BaseBarcodeActivity {

    // 下拉刷新的分组列表
    private ExpandableListView refreshView;
    // 全局应用程序对象


    private Context mContext;
    // 评估单号
    private String pgdh;
    // 评估序号
    private String pgxh;

    private List<MeasureOverview> mList;


    @Override
    public void initBarBroadcast() {
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void toRefreshData() {
        if (pgdh.equals("0") || pgxh.equals("0")) {
            return;
        }
        GetListTask task = new GetListTask();
        tasks.add(task);
        task.execute();
    }

    private void getParams() {
        pgdh = getIntent().getStringExtra("PGDH");
        pgxh = getIntent().getStringExtra("PGXH");
        if (EmptyTool.isBlank(pgdh)) {
            pgdh = "0";
        }
        if (EmptyTool.isBlank(pgxh)) {
            pgxh = "0";
        }
    }

    private void initView() {

        refreshView = (ExpandableListView) findViewById(R.id.id_elv);
        EmptyViewHelper.setEmptyView(refreshView);
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, refreshView);
        refreshView.setOnChildClickListener(
                new OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent,
                                                View v, int groupPosition, int childPosition,
                                                long id) {
                        Intent intent = new Intent(
                                RiskMeasureListActivity.this,
                                RiskMeasureActivity.class);
                        intent.putExtra("PGDH",
                                pgdh);
                        intent.putExtra("PGXH",
                                mList.get(groupPosition).RECOORD
                                        .get(childPosition).PGXH);
                        intent.putExtra("PGLX", mList.get(groupPosition).PGLX);
                        intent.putExtra("JLXH",
                                mList.get(groupPosition).RECOORD
                                        .get(childPosition).JLXH);
                        startActivity(intent);
                        return true;
                    }
                });
    }

    private void initActionBar() {
        actionBar.setTitle("风险评估措施列表");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH + mAppApplication.sickPersonVo.BRXM);
     /*   actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public void performAction(View view) {
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });*/
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_risk_measure_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        mContext = this;
        getParams();
        initView();
        initActionBar();

        toRefreshData();
    }


    class GetListTask extends
            AsyncTask<Void, Void, Response<List<MeasureOverview>>> {

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
        protected Response<List<MeasureOverview>> doInBackground(Void... params) {
            return NurseFormApi.getInstance(mContext).togetMeasureList(pgdh,
                    pgxh, mAppApplication.sickPersonVo.ZYH, mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description: </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<List<MeasureOverview>> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskMeasureListActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    mList = result.Data;
                    if (mList == null)
                        for (MeasureOverview measure : mList) {
                            if (measure.RECOORD == null) {
                                measure.RECOORD = new ArrayList<SimMeasureRecord>();
                            }
                        }
                    refreshView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            TextView groupname = (TextView) v.findViewById(R.id.id_tv);
                            groupname.setSelected(!groupname.isSelected());
                            return false;
                        }
                    });
                    MeasureAdapter adapter = new MeasureAdapter();
                    refreshView.setAdapter(adapter);
                    if (adapter.getGroupCount() > 0) {
                        refreshView.expandGroup(0);
                    }
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    private class MeasureAdapter extends BaseExpandableListAdapter {

        /*
         * (非 Javadoc) <p>Title: getChild</p> <p>Description: </p>
         *
         * @param groupPosition
         *
         * @param childPosition
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getChild(int, int)
         */
        @Override
        public SimMeasureRecord getChild(int groupPosition, int childPosition) {
            return mList.get(groupPosition).RECOORD.get(childPosition);
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
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_list_text_three_secondary, parent, false);
                vHolder = new ChildHolder();
                vHolder.tv_name = (TextView) convertView
                        .findViewById(R.id.id_tv_one);
                vHolder.tv_nurse = (TextView) convertView
                        .findViewById(R.id.id_tv_two);
                vHolder.tv_sign = (TextView) convertView
                        .findViewById(R.id.id_tv_three);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ChildHolder) convertView.getTag();
            }
            SimMeasureRecord simMeasureRecord = mList.get(groupPosition).RECOORD
                    .get(childPosition);
            Date date = DateUtil.getDateCompat(simMeasureRecord.CSSJ);
            String dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
            vHolder.tv_name.setText(dateStr);
            //
            if (Constant.DEBUG) {
                vHolder.tv_nurse.setText(simMeasureRecord.CSXM + "_debug_jlxh:" + simMeasureRecord.JLXH);
            } else {
                vHolder.tv_nurse.setText(simMeasureRecord.CSXM);
            }
          /*  if (EmptyTool.isBlank(mList.get(groupPosition).RECOORD
                    .get(childPosition).HSZQM)) {
                vHolder.tv_sign.setTextColor(ContextCompat.getColor(mContext,
                        R.color.red));
                vHolder.tv_sign.setText("未评价");
            } else {
                vHolder.tv_sign.setTextColor(getResources().getColor(
                        R.color.green));
                vHolder.tv_sign.setText("已评价");
            }*/

            if ("1".equals(simMeasureRecord.SFPJ)) {
                if (EmptyTool.isBlank(simMeasureRecord.CSPJ)
//########           && EmptyTool.isBlank(simMeasureRecord.ZGQK)
                        ) {
                    vHolder.tv_sign.setTextColor(ContextCompat.getColor(mContext,
                            R.color.red));
                    vHolder.tv_sign.setText("未评价");
                } else {
                    vHolder.tv_sign.setTextColor(getResources().getColor(
                            R.color.green));
//   ####                 vHolder.tv_sign.setText("已评价"+simMeasureRecord.CSPJ+"_"+simMeasureRecord.ZGQK);
                    vHolder.tv_sign.setText("已评价" + simMeasureRecord.CSPJ);
                }
            }
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
            return mList.get(groupPosition).RECOORD.size();
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
        public MeasureOverview getGroup(int groupPosition) {
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
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupHolder vHolder;
            if (convertView == null) {
                vHolder = new GroupHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_list_group_primary, parent, false);
                vHolder.tv_name = (TextView) convertView
                        .findViewById(R.id.id_tv);
                convertView.setTag(vHolder);
            } else {
                vHolder = (GroupHolder) convertView.getTag();
            }
            vHolder.tv_name.setText(mList.get(groupPosition).CSDMC);
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
    }

    class ChildHolder {
        TextView tv_name;
        TextView tv_nurse;
        TextView tv_sign;
    }
}
