package com.bsoft.mob.ienr.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.BatchHandOverActivity;
import com.bsoft.mob.ienr.activity.MainActivity;
import com.bsoft.mob.ienr.adapter.BatchHandOverListAdapter;
import com.bsoft.mob.ienr.api.HandOverApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.handover.BatchHandOverRecord;
import com.bsoft.mob.ienr.model.handover.HandOverRecord;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.view.BsoftActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 批量 护理交接
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-15
 * Time: 14:19
 * Version:
 */
public class BatchHandOverFragment extends LeftMenuItemFragment {
    // 眉栏工具条
    // 下拉刷新的分组列表
    private ExpandableListView refreshView;
    // 列表
    private List<BatchHandOverRecord> mList;
    // 适配器
    private BatchHandOverListAdapter mAdapter;


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
        return R.layout.fragment_hand_over;
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
        GetDataTast task = new GetDataTast();
        tasks.add(task);
        task.execute();
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
        EmptyViewHelper.setEmptyView(refreshView,"refreshView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,refreshView);
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
                                BatchHandOverActivity.class);
                        intent.putExtra("ysxh", mList.get(groupPosition).HandOverRecordList.get(childPosition).YSXH);
                        intent.putExtra("jlxh", mList.get(groupPosition).HandOverRecordList.get(childPosition).JLXH);
                        intent.putExtra("zyh", mList.get(groupPosition).HandOverRecordList.get(childPosition).ZYH);

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
        actionBar.setBackAction(new BsoftActionBar.Action() {
            @Override
            public void performAction(View view) {
                ((MainActivity) getActivity()).toggle();
            }
            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }
            @Override
            public int getDrawable() {
                return R.drawable.ic_menu_black_24dp;
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
                }
            }
        };
    }



    class GetDataTast extends AsyncTask<Void, Void, Response<List<HandOverRecord>>> {


        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }


        @Override
        protected Response<List<HandOverRecord>> doInBackground(Void... params) {
            // change by louis
            if (mAppApplication.getCrrentArea() != null && "[isSurgery]".equals(mAppApplication.getCrrentArea().YGDM)) {
                //标记是 手术科室
                return HandOverApi.getInstance(getActivity()).getHandOverRecordListBySSKS(
                        mAppApplication.getAreaId(),//存放的是ssks
                        mAppApplication.jgId);
            } else {
                return HandOverApi.getInstance(getActivity()).getHandOverRecordList(
                        mAppApplication.getAreaId(),
                        mAppApplication.jgId);
            }


        }


        @Override
        protected void onPostExecute(Response<List<HandOverRecord>> result) {
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
                } else if (result.ReType == 0) {
                    mList = new ArrayList<>();
                    List<HandOverRecord> list = result.Data;
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            HandOverRecord record = list.get(i);
                            if (i == 0) {
                                BatchHandOverRecord batchHandOverRecord = new BatchHandOverRecord();
                                batchHandOverRecord.ZYH = record.ZYH;
                                batchHandOverRecord.BRXM = record.BRXM;
                                batchHandOverRecord.HandOverRecordList = new ArrayList<>();
                                batchHandOverRecord.HandOverRecordList.add(record);
                                mList.add(batchHandOverRecord);
                            } else {
                                boolean isHave = false;
                                for (BatchHandOverRecord item : mList) {
                                    if (record.ZYH.equals(item.ZYH)) {
                                        item.HandOverRecordList.add(record);
                                        isHave = true;
                                        break;
                                    }
                                }
                                if (!isHave) {
                                    BatchHandOverRecord batchHandOverRecord = new BatchHandOverRecord();
                                    batchHandOverRecord.ZYH = record.ZYH;
                                    batchHandOverRecord.BRXM = record.BRXM;
                                    batchHandOverRecord.HandOverRecordList = new ArrayList<>();
                                    batchHandOverRecord.HandOverRecordList.add(record);
                                    mList.add(batchHandOverRecord);
                                }
                            }
                        }
                    }
                    for (BatchHandOverRecord batchHandOverRecord : mList) {
                        for (HandOverRecord handOverRecord : batchHandOverRecord.HandOverRecordList) {
                            if (handOverRecord.ZTBZ.equals("2")) {
                                batchHandOverRecord.CheckCount++;
                            } else {
                                batchHandOverRecord.NotCheckCount++;
                            }
                        }
                    }
                    mAdapter = new BatchHandOverListAdapter(getActivity(), mList);
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
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }

}
