package com.bsoft.mob.ienr.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.BloodHistoryActivity;
import com.bsoft.mob.ienr.activity.SignActivity;
import com.bsoft.mob.ienr.adapter.BloodRecieveAdapter;
import com.bsoft.mob.ienr.api.BloodTransfusionApi;
import com.bsoft.mob.ienr.api.UserApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.TestDataHelper;
import com.bsoft.mob.ienr.model.LoginUser;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.blood.BloodRecieveSaveData;
import com.bsoft.mob.ienr.model.blood.BloodReciveInfo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 血液签收
 */
public class BloodRecieveFragment extends LeftMenuItemFragment {
    private final static int BLOOD_RECIEVE_REQUEST = 1;
    private final static int BLOOD_RECIEVE_RESULT = -1;

    private ArrayList<BloodReciveInfo> mBloodReciveInfoListRaw;
    private BloodRecieveAdapter mAdapter;
    private ListView mListView;
    private TextView tv_datestart, tv_dateend;
    private Date d_start, d_end;
    private ImageView imgbtn_search;
    private TextView tv_fggh;


    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {


        String date = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);

        initTimeTxt(date, viewId);

    }

    @Override
    public void onResume() {
        super.onResume();
        mAppApplication.isChangeUser = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppApplication.isChangeUser = true;
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_blood_recieve;
    }

    @Override
    protected void toRefreshData() {
        d_start = DateUtil.getDateCompat(tv_datestart.getText().toString());
        d_end = DateUtil.getDateCompat(tv_dateend.getText().toString());
        if (d_end.before(d_start)) {
            showMsgAndVoiceAndVibrator("开始时间不能晚于结束时间!");
            return;
        }
        GetDataTask task = new GetDataTask();
        tasks.add(task);
        task.execute();
    }

    @Override
    protected void initView(View mainView, Bundle savedInstanceState) {
        tv_datestart = (TextView) mainView.findViewById(R.id.stime);
        tv_dateend = (TextView) mainView.findViewById(R.id.etime);

        tv_fggh = (TextView) mainView.findViewById(R.id.id_tv_2);
        TextView id_tv = (TextView) mainView.findViewById(R.id.id_tv);
        // TextView id_tv_2 = (TextView) mainView.findViewById(R.id.id_tv_2);
        id_tv.setText("送血护士姓名：");
        tv_fggh.setText("请先扫描送血护士工牌");
        final TextView stimeTitle = (TextView) mainView
                .findViewById(R.id.stime_title);
        final TextView etimeTitle = (TextView) mainView
                .findViewById(R.id.etime_title);

        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);

        mListView = (ListView) mainView.findViewById(R.id.id_lv);
        ArrayList<BloodReciveInfo> list = new ArrayList<>();
        mAdapter = new BloodRecieveAdapter(getActivity(), list, false);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.changeItemSelectedStatus(position);
            }
        });
        EmptyViewHelper.setEmptyView(mListView, "mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);

        imgbtn_search = (ImageView) mainView.findViewById(R.id.search);
        imgbtn_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });

        initBarBroadCast();
        initActionbar();
        initDate();
        toRefreshData();
    }

 /*   @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {R.drawable.menu_view};

        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
        for (int itemDrawableResid : itemDrawables) {
            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }

        return floatMenuItemList;
    }
*/

    /**
     * 响应RayMenu item点击
     */
   /* private void onMenuItemClick(int drawableRes) {


        if (drawableRes == R.drawable.menu_view) {// 查询
            goToBloodHistory();
        }
    }
*/
    private void goToBloodHistory() {
        Intent intent = new Intent(getActivity(),
                BloodHistoryActivity.class);
        intent.putExtra(BloodHistoryActivity.Operat_INT_START_TYPE, BloodHistoryActivity.Operat_Recieve);
        startActivity(intent);
    }

    private void initBarBroadCast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (BarcodeActions.Refresh.equals(intent.getAction())) {

                } else if (BarcodeActions.Bar_Get.equals(intent.getAction())) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    String hg = tv_fggh.getText().toString();
                    if ("请先扫描送血护士工牌".equals(hg)) {
                        hg = "";
                    }
                    if (entity.TMFL == 3 && !hg.isEmpty()) {// 工号牌
                        scanRecieve(entity.TMQZ + entity.TMNR);
                    } else if (entity.TMFL == 3 && hg.isEmpty()) {// 护工条码
                        doGetCaresInfo(entity.TMQZ + entity.TMNR);
                    } else if (entity.TMFL == 9) {// 标本条码
                        setItemCheck(entity.TMQZ + entity.TMNR);
                    }

                }
            }
        };
    }

    private void setFSGH(LoginUser loginUser) {
        try {
            tv_fggh.setText(loginUser.YHXM);
            tv_fggh.setTag(loginUser.YHID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initActionbar() {
        actionBar.setTitle("血液签收");

        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {
                handRecieve();
            }

            public String getText() {
                return "执行";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
        actionBar.addAction(new Action() {
            @Override
            public int getDrawable() {
                return R.drawable.menu_history_n;
            }

            @Override
            public String getText() {
                return "历史";
            }

            @Override
            public void performAction(View view) {
                goToBloodHistory();
            }
        });

    }

    private void initDate() {
        String nowDate = DateTimeHelper.getServerDate();
        tv_datestart.setText(nowDate);
        tv_dateend.setText(nowDate);
        tv_datestart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String dateStr = tv_datestart
                        .getText().toString();
                showDatePickerCompat(dateStr, tv_datestart.getId());

            }
        });
        tv_dateend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String dateStr = tv_dateend.getText().toString();
                showDatePickerCompat(dateStr, tv_dateend.getId());

            }
        });
    }

    private void initTimeTxt(String dateStr, int viewId) {
        String timeStr = dateStr;
        if (viewId == R.id.stime) {

            tv_datestart.setText(timeStr);
//			if (!timeStr.equals(tv_datestart)) {
//				sTime = timeStr;
//				getHttpTask = new GetHttpTask();
//				getHttpTask.execute();
//			}

        }

        if (viewId == R.id.etime) {
            tv_dateend.setText(timeStr);
        }

    }

    class GetDataTask extends AsyncTask<Void, Void, Response<List<BloodReciveInfo>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<BloodReciveInfo>> doInBackground(Void... params) {
            return BloodTransfusionApi.getInstance(getActivity())
                    .getBloodRecieveList(mAppApplication.getAreaId(), "0",
                            tv_datestart.getText().toString(),
                            tv_dateend.getText().toString(), mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<List<BloodReciveInfo>> result) {
            super.onPostExecute(result);
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
                    mBloodReciveInfoListRaw = (ArrayList<BloodReciveInfo>) result.Data;
                    if (EmptyTool.isEmpty(mBloodReciveInfoListRaw)) {
                        mBloodReciveInfoListRaw = new ArrayList<>();
                        //# showMsgAndVoice("暂无血液需要签收!");
                        //
                        TestDataHelper.buidTestData(BloodReciveInfo.class, mBloodReciveInfoListRaw);
                    }
                    mAdapter.refreshData(mBloodReciveInfoListRaw);

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

    class DoRecieveTask extends AsyncTask<String, Void, Response<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<String> doInBackground(String... params) {
            if (params.length < 1) {
                return null;
            }
            ArrayList<String> ybbh = new ArrayList<>();
            for (BloodReciveInfo blood : mBloodReciveInfoListRaw) {
                if (blood.isSelected) {
                    String bh = blood.XMID;
                    ybbh.add(bh);
                }
            }
            if (ybbh.size() <= 0) {
                showMsgAndVoiceAndVibrator("请选选择要签收的项目!");
                return null;
            }
            BloodRecieveSaveData saveData = new BloodRecieveSaveData();
            saveData.SampleId = ybbh;
            saveData.HSGH = params[0];
            saveData.HGGH = tv_fggh.getText().toString();
            saveData.JGID = mAppApplication.jgId;

            String json;
            try {
                json = JsonUtil.toJson(saveData);
                // 调用api
                return BloodTransfusionApi.getInstance(getActivity()).saveBloodRecieve(json);
            } catch (IOException e) {
                showMsgAndVoiceAndVibrator("出错了");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);
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
                    if (!EmptyTool.isBlank(result.Msg)) {
                        showMsgAndVoice(result.Msg);
                    } else {
                        showMsgAndVoice("操作成功!");
                    }
                   /* MediaUtil.getInstance(getActivity()).playSound(R.raw.success,
                            getActivity());*/
                } else {
                    if (!EmptyTool.isBlank(result.Msg)) {
                        showMsgAndVoice(result.Msg);
                    } else {
                        showMsgAndVoiceAndVibrator("操作失败!");
                    }
                    /*MediaUtil.getInstance(getActivity()).playSound(R.raw.wrong,
                            getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            toRefreshData();
        }
    }

    class GetCarersInfoTask extends AsyncTask<String, Void, Response<LoginUser>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<LoginUser> doInBackground(String... params) {

            if (params.length < 1) {
                return null;
            }
            String tmnr = params[0];
            return UserApi.getInstance(getActivity()).getLoginUserByXk(tmnr, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<LoginUser> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    setFSGH(result.Data);
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

    // 扫描一项选中一项
    private void setItemCheck(String tmnr) {
        for (BloodReciveInfo blood : mBloodReciveInfoListRaw) {
            if (blood.XMTM.equals(tmnr)) {
                blood.isSelected = true;
                mAdapter.refreshData(mBloodReciveInfoListRaw);
                break;
            }
        }
    }

    // 检查时候有选中的项目
    private boolean checkItemSelect() {
        boolean hasSelected = false;
        for (BloodReciveInfo blood : mBloodReciveInfoListRaw) {
            if (blood.isSelected == true) {
                hasSelected = true;
                break;
            }
        }

        return hasSelected;
    }

    // 扫描执行，直接获取护工工工号牌条码，不打开签名页
    private void scanRecieve(String xphm) {
        if (!checkItemSelect()) {
            showMsgAndVoiceAndVibrator("请先选择要接收的项目!");
            return;
        }

        String hg = tv_fggh.getText().toString();
        if (hg.equals("")) {
            showMsgAndVoiceAndVibrator("请先扫描送血护士工牌!");
            return;
        }

        Intent intent = new Intent(getActivity(), SignActivity.class);
        // 这里使用的是双签的模式，用的是SignActivity1，
        // 这要原因是单签模式返回的员工代码是当前登录的员工，而SignAcitivity1是我当时处理输血双签是新加的，第一个签名护士默认就已经签上了
        intent.putExtra(SignActivity.ACTION_SIGN, SignActivity.ACTION_EXTRA_SIGN_SIGNLE);

        intent.putExtra("GHP", xphm);
        startActivityForResult(intent, BLOOD_RECIEVE_REQUEST);
    }

    // 手动执行，先打开签名页
    private void handRecieve() {
        if (!checkItemSelect()) {
            showMsgAndVoiceAndVibrator("请先选择要接收的项目!");
            return;
        }
        doRecieve(mAppApplication.user.YHID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BLOOD_RECIEVE_REQUEST
                && resultCode == BLOOD_RECIEVE_RESULT) {
            String hgxp = data.getStringExtra(SignActivity.EXTRA_YHID_KEY_1);
            doRecieve(hgxp);
        }
    }

    private void doRecieve(String userId) {

        String hg = tv_fggh.getText().toString();
        if (hg.equals("")) {
            showMsgAndVoiceAndVibrator("请先扫描送血护士工牌!");
            return;
        }
        DoRecieveTask task = new DoRecieveTask();
        tasks.add(task);
        task.execute(userId);
    }

    private void doGetCaresInfo(String tmnr) {
        if (tmnr.equals("")) {
            showMsgAndVoiceAndVibrator("请先扫描送血护士工牌!");
            return;
        }
        GetCarersInfoTask task = new GetCarersInfoTask();
        tasks.add(task);
        task.execute(tmnr);
    }


}
