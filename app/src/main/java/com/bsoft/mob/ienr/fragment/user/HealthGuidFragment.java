/**
 * 5.4版健康教育主类文件
 *
 * @Title: HealthGuidFragment.java
 * @Package com.bsoft.mob.ienr.fragment.user
 * @Description: 5.4健康教育主类文件
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-11-12 下午3:16:41
 * @version V5.4
 */
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
import android.widget.Toast;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.HealthGuidActivity;
import com.bsoft.mob.ienr.adapter.HealthGuidListAdapter;
import com.bsoft.mob.ienr.api.HealthGuidApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.event.EventType;
import com.bsoft.mob.ienr.event.HealthGuidEvent;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.healthguid.HealthGuid;
import com.bsoft.mob.ienr.util.AgainLoginUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 5.4版健康教育
 *
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: HealthGuidFragment
 * @Description: 5.4版健康教育
 * @date 2015-11-12 下午3:16:41
 */
public class HealthGuidFragment extends BaseUserFragment {
    // 眉栏工具条

    // 下拉刷新的分组列表
    private ExpandableListView refreshView;
    // 宣教列表
    private List<HealthGuid> mList;
    // 宣教适配器
    private HealthGuidListAdapter mAdapter;


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
        return R.layout.fragment_health_guid;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        initActionBar();
        initRefreshView(rootLayout);
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
     * @Title: initRefreshView
     * @Description: 初始化下拉刷新列表
     */
    private void initRefreshView(View rootLayout) {
        refreshView = (ExpandableListView) rootLayout
                .findViewById(R.id.id_elv);
        refreshView.setOnChildClickListener(
                new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent,
                                                View v, int groupPosition, int childPosition,
                                                long id) {
                        Intent intent = new Intent(getActivity(),
                                HealthGuidActivity.class);
                        intent.putExtra("type", mList.get(groupPosition).GLLX);
                        intent.putExtra("operType", "2");
                        intent.putExtra("xh", mList.get(groupPosition).HealthGuidItems.get(childPosition).XH);
                        intent.putExtra("lxbh", mList.get(groupPosition).HealthGuidItems.get(childPosition).LXBH);

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
        actionBar.setTitle("健康教育");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                + mAppApplication.sickPersonVo.BRXM);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HealthGuidEvent baseEvent) {
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
     * @Title: getData
     * @Description: 获取健康宣教列表
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
    class GetDataTast extends AsyncTask<Void, Void, Response<List<HealthGuid>>> {

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
        protected Response<List<HealthGuid>> doInBackground(Void... params) {
            return HealthGuidApi.getInstance(getActivity()).GetHealthGuidList(
                    mAppApplication.getAreaId(), mAppApplication.sickPersonVo.ZYH,
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
        protected void onPostExecute(Response<List<HealthGuid>> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            getData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    mList = result.Data;
                    mAdapter = new HealthGuidListAdapter(getActivity(), mList);
                    refreshView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            TextView healthguid_name = (TextView) v.findViewById(R.id.id_tv);
                            healthguid_name.setSelected(!healthguid_name.isSelected());
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


}
