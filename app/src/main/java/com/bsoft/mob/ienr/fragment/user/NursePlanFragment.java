package com.bsoft.mob.ienr.fragment.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.NursePlanActivity;
import com.bsoft.mob.ienr.api.NursePlanApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurseplan.Plan;
import com.bsoft.mob.ienr.model.nurseplan.SimpleRecord;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.List;

/**
 * Description: 护理计划
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-24
 * Time: 14:26
 * Version:
 */
public class NursePlanFragment extends BaseUserFragment {
    // 眉栏工具条

    // 下拉刷新的分组列表
    private ExpandableListView refreshView;
    // 计划数据
    private List<Plan> mList;
    // 计划适配器
    private PlanListAdapter mAdapter;

    /*
     * (非 Javadoc) <p>Title: onCreate</p> <p>Description: fragment生命周期--创建</p>
     *
     * @param savedInstanceState
     *
     * @see com.bsoft.mob.ienr.activity.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBroadCast();
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }
    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_nurse_plan;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        initView(rootLayout);
        initActionBar();
        initRefreshView();
        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        getData();
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
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initActionBar
     * @Description: 初始化工具条
     */
    private void initActionBar() {
        actionBar.setTitle("护理计划");
        String brch = EmptyTool.isBlank(mAppApplication.sickPersonVo.BRCH) ? "" : mAppApplication.sickPersonVo.BRCH;
        actionBar.setPatient(brch + mAppApplication.sickPersonVo.BRXM);
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
                new OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent,
                                                View v, int groupPosition, int childPosition,
                                                long id) {

                        Intent intent = new Intent(getActivity(),
                                NursePlanActivity.class);
                        intent.putExtra("WTXH",
                                mList.get(groupPosition).SimpleRecord
                                        .get(childPosition).XH);
                        intent.putExtra("GLLX", mList.get(groupPosition).GLLX);
                        intent.putExtra("GLXH", mList.get(groupPosition).XH);
                        if (mList.get(groupPosition).SimpleRecord
                                .get(childPosition).UMBER.equals("0"))
                            intent.putExtra("ISADD", true);
                        else
                            intent.putExtra("ISADD", false);
                        startActivityForResult(intent,
                                NursePlanActivity.REQUEST_CODE);

                        return true;
                    }
                });
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

    /*
     * (非 Javadoc) <p>Title: onActivityResult</p> <p>Description: </p>
     *
     * @param requestCode
     *
     * @param resultCode
     *
     * @param data
     *
     * @see android.support.v4.app.Fragment#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NursePlanActivity.REQUEST_CODE
                && resultCode == Activity.RESULT_OK)
        {toRefreshData();}
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getData
     * @Description: 获取计划问题列表
     */
    private void getData() {
        GetDataTast task = new GetDataTast();
        tasks.add(task);
        task.execute();
    }

    /**
     * 网络请求并处理获取的数据
     *
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: getDataTast
     * @Description: 网络请求并处理获取的数据
     * @date 2015-11-19 上午11:35:46
     */
    class GetDataTast extends AsyncTask<Void, Void, Response<List<Plan>>> {

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
        protected Response<List<Plan>> doInBackground(Void... params) {
            return NursePlanApi.getInstance(getActivity()).getPlanList(
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
        protected void onPostExecute(Response<List<Plan>> result) {
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
                    mAdapter = new PlanListAdapter(getActivity(), mList);
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

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: PlanListAdapter
     * @Description: 计划列表适配器
     * @date 2015-11-19 下午1:34:23
     */
    class PlanListAdapter extends BaseExpandableListAdapter {
        Context mContext;
        List<Plan> list;

        /**
         * <p>
         * Title: PlanListAdapter
         * </p>
         * <p>
         * Description: 构造函数，初始化上下文和计划列表参数
         * </p>
         *
         * @param mContext
         * @param list
         */
        public PlanListAdapter(Context mContext, List<Plan> list) {
            super();
            this.mContext = mContext;
            this.list = list;
        }

        /*
         * (非 Javadoc) <p>Title: getChild</p> <p>Description: 获取子项 </p>
         *
         * @param groupPosition 父项位置
         *
         * @param childPosition 子项位置
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getChild(int, int)
         */
        @Override
        public SimpleRecord getChild(int groupPosition, int childPosition) {
            return list.get(groupPosition).SimpleRecord.get(childPosition);
        }

        /*
         * (非 Javadoc) <p>Title: getChildId</p> <p>Description: 获取子项id </p>
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
            return Long.parseLong(list.get(groupPosition).SimpleRecord
                    .get(childPosition).XH);
        }

        /*
         * (非 Javadoc) <p>Title: getChildView</p> <p>Description: 获取子项视图</p>
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
        public View getChildView(final int groupPosition,
                                 final int childPosition, boolean isLastChild, View convertView,
                                 ViewGroup parent) {
            ChildHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_list_text_one_secondary_icon, parent,false);
                vHolder = new ChildHolder();
                vHolder.tv_itemname = (TextView) convertView
                        .findViewById(R.id.id_tv);
                vHolder.id_tv_more = (TextView) convertView
                        .findViewById(R.id.id_tv_more);
                Drawable drawable = ContextCompatHelper.getDrawable(mContext,R.drawable.ic_add_black_24dp);
                vHolder.id_tv_more.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ChildHolder) convertView.getTag();
            }
            vHolder.tv_itemname.setText(list.get(groupPosition).SimpleRecord
                    .get(childPosition).MS);
            vHolder.id_tv_more.setText(list.get(groupPosition).SimpleRecord
                    .get(childPosition).UMBER);
            vHolder.id_tv_more.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),
                            NursePlanActivity.class);
                    intent.putExtra("WTXH",
                            list.get(groupPosition).SimpleRecord
                                    .get(childPosition).XH);
                    intent.putExtra("GLLX", list.get(groupPosition).GLLX);
                    intent.putExtra("GLXH", mList.get(groupPosition).XH);
                    intent.putExtra("ISADD", true);
                    startActivityForResult(intent,
                            NursePlanActivity.REQUEST_CODE);

                }
            });
            return convertView;
        }

        /*
         * (非 Javadoc) <p>Title: getChildrenCount</p> <p>Description:
         * 获取某个父项下子项数量</p>
         *
         * @param groupPosition
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            return list.get(groupPosition).SimpleRecord == null ? 0 : list
                    .get(groupPosition).SimpleRecord.size();
        }

        /*
         * (非 Javadoc) <p>Title: getGroup</p> <p>Description: 获取父项</p>
         *
         * @param groupPosition
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getGroup(int)
         */
        @Override
        public Plan getGroup(int groupPosition) {
            return list.get(groupPosition);
        }

        /*
         * (非 Javadoc) <p>Title: getGroupCount</p> <p>Description:获取父项数量 </p>
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getGroupCount()
         */
        @Override
        public int getGroupCount() {
            return list.size();
        }

        /*
         * (非 Javadoc) <p>Title: getGroupId</p> <p>Description: 获取父项的id</p>
         *
         * @param groupPosition
         *
         * @return
         *
         * @see android.widget.ExpandableListAdapter#getGroupId(int)
         */
        @Override
        public long getGroupId(int groupPosition) {
            return Long.parseLong(list.get(groupPosition).XH);
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
            ParentHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_list_group_primary,  parent,false);
                vHolder = new ParentHolder();
                vHolder.tv_planname = (TextView) convertView
                        .findViewById(R.id.id_tv);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ParentHolder) convertView.getTag();
            }
            vHolder.tv_planname.setText(list.get(groupPosition).MS);
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

    class ParentHolder {
        TextView tv_planname;
    }

    class ChildHolder {
        TextView tv_itemname;
        TextView id_tv_more;
    }


}
