package com.bsoft.mob.ienr.fragment.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.NurseFocusActivity;
import com.bsoft.mob.ienr.activity.user.NursePlanActivity;
import com.bsoft.mob.ienr.adapter.PlanListAdapter;
import com.bsoft.mob.ienr.api.NursePlanApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurseplan.Plan;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.List;

/**
 * Description: 护理焦点
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-24
 * Time: 14:26
 * Version:
 */
public class NurseFocusFragment extends BaseUserFragment {
    // 眉栏工具条
    // 下拉刷新的分组列表
    private ExpandableListView refreshView;
    // 计划数据
    private List<Plan> mList;
    // 计划适配器
    private PlanListAdapter mAdapter;


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
        return R.layout.fragment_nurse_focus;
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

        //解决下拉刷新冲突
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
        actionBar.setTitle("护理焦点");
        if (mAppApplication!=null&&mAppApplication.sickPersonVo!=null) {
            String brch = EmptyTool.isBlank(mAppApplication.sickPersonVo.BRCH) ? "" : mAppApplication.sickPersonVo.BRCH;
            actionBar.setPatient(brch + mAppApplication.sickPersonVo.BRXM);
        }
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
                new OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent,
                                                View v, int groupPosition, int childPosition,
                                                long id) {

                        Intent intent = new Intent(getActivity(),
                                NurseFocusActivity.class);
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
                                NurseFocusActivity.REQUEST_CODE);

                        return true;
                    }
                });
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
                }
            }
        };
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NursePlanActivity.REQUEST_CODE
                && resultCode == Activity.RESULT_OK){
            toRefreshData();
    }}

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

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }


        @Override
        protected Response<List<Plan>> doInBackground(Void... params) {
            return NursePlanApi.getInstance(getActivity()).getFocusList(
                    mAppApplication.sickPersonVo.ZYH,
                    mAppApplication.getAreaId(),
                    mAppApplication.jgId);
        }


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
                    refreshView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            TextView nursefrom_name = (TextView) v.findViewById(R.id.id_tv);
                            nursefrom_name.setSelected(!nursefrom_name.isSelected());
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
