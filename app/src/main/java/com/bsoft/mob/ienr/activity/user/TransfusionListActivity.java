package com.bsoft.mob.ienr.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.adapter.TransfusionAdapter;
import com.bsoft.mob.ienr.adapter.TransfusionTourRecordAdapter;
import com.bsoft.mob.ienr.api.AdviceApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.advice.TransfusionData;
import com.bsoft.mob.ienr.model.advice.TransfusionInfoVo;
import com.bsoft.mob.ienr.model.advice.TransfusionTourRecordVo;
import com.bsoft.mob.ienr.model.advice.TransfusionVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-13 上午11:35:56
 * @类说明 输液单
 */
public class TransfusionListActivity extends BaseBarcodeActivity {

    protected static final int DIALOG_VISIT = 1;

    private ListView listView;

    private TextView time;

    private View sltDateView;

    private TransfusionAdapter mAdapter;


    private void actionGetDateTask(byte type, String... params) {
        GetDataTask getDataTask = new GetDataTask(type);
        tasks.add(getDataTask);
        getDataTask.execute(params);
    }

    public void findView() {

        listView = (ListView) findViewById(R.id.id_lv);
        time = (TextView) findViewById(R.id.time);
        findViewById(R.id.image).setVisibility(View.GONE);
        sltDateView = findViewById(R.id.slt_date_ly);

        initListView();

        initActionBar();

        initDateView();
        //
        String dateStr = DateTimeHelper.getServerDate();
        updateTimeTxt(dateStr,R.id.time);
    }

    private void initDateView() {

        sltDateView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String dateStr = time.getText().toString();
                if (EmptyTool.isBlank(dateStr)) {
                    return;
                }
                int viewId = time.getId();
                YmdHMs ymdHMs = DateTimeHelper.date2YmdHMs(dateStr);
                showPickerDateCompat(ymdHMs, viewId);
            }
        });
    }

    private void updateTimeTxt(String dateStr, int viewId) {
        if (viewId == R.id.time) {
            time.setText(dateStr);
            toRefreshData();
        }

    }

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);
        updateTimeTxt(nowDate, viewId);
    }

    private void initListView() {

        // adapter = new TransfusionListPatientAdapter(this);
        // listView.setAdapter(adapter);

        // listView.setOnItemClickListener(new OnItemClickListener() {
        //
        // @Override
        // public void onItemClick(AdapterView<?> parent, View view,
        // int position, long id) {
        //
        // TransfusionVo item = (TransfusionVo) listView
        // .getAdapter().getItem(position);
        //
        // if (item != null) {
        // actionGetDateTask(GetDataTask.GET_DETAIL, item.SYDH);
        // }
        //
        // }
        //
        // });

        // listView.setChoiceMode(
        // ListView.CHOICE_MODE_MULTIPLE);


    }

    @Override
    protected void toRefreshData() {
        actionGetDateTask(GetDataTask.GET_LIST);
    }

    protected void showCreateDialogCompat(int id, Bundle args) {

        switch (id) {

            case DIALOG_VISIT:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                Context context = mContext;
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_root_linear, null, false);
                ListView listView = new ListView(context);

                linearLayout.addView(listView);    EmptyViewHelper.setEmptyView(listView, "listView");
                @SuppressWarnings("unchecked")
                ArrayList<TransfusionTourRecordVo> list = (ArrayList<TransfusionTourRecordVo>) args
                        .getSerializable("list");
                View txt = ViewBuildHelper.buildDialogTitleTextView(mContext, "巡视记录");
                builder.setView(linearLayout)
                        //.setTitle("巡视记录")
                        .setCustomTitle(txt);

                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                TransfusionTourRecordAdapter adapter = new TransfusionTourRecordAdapter(
                        TransfusionListActivity.this, list);
                listView.setAdapter(adapter);
                builder.create().show();
                break;
            default:

        }

    }

    private void initActionBar() {

        actionBar.setTitle("输液单");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);


        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "历史";
            }
            @Override
            public void performAction(View view) {

                if (mAdapter != null && mAdapter.hasCheckedItm()) {
                    String mSYDH = mAdapter.mSYDH;
                    actionGetDateTask(GetDataTask.GET_DETAIL, mSYDH);
                    return;
                }

                showMsgAndVoiceAndVibrator("请先选择查询项");
            }

            @Override
            public int getDrawable() {

                return R.drawable.menu_history_n;
            }
        });
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_transfusion_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        findView();
    }

    /**
     * 异步加载
     */
    private class GetDataTask extends AsyncTask<String, Void, Response<TransfusionData>> {

        public static final byte GET_LIST = 0;

        public static final byte GET_DETAIL = 1;

        private byte mRequestType = GET_LIST;

        public GetDataTask(byte rqtType) {
            this.mRequestType = rqtType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<TransfusionData> doInBackground(String... params) {


            String jgid = mAppApplication.jgId;
            AdviceApi api = AdviceApi.getInstance(getApplicationContext());

            switch (mRequestType) {
                case GET_LIST:
                    if (mAppApplication.sickPersonVo == null) {
                        return null;
                    }
                    String ZYH = mAppApplication.sickPersonVo.ZYH;
                    String sTime = time.getText().toString();
                    return api.GetTransfusionListPatient(ZYH, sTime, "", jgid);
                case GET_DETAIL:
                    if (params == null || params.length < 1
                            || EmptyTool.isBlank(params[0])) {
                        return null;
                    }
                    return api.GetTransfusion(params[0], jgid);
                    default:
            }

            return null;
        }

        @SuppressWarnings({"unchecked", "deprecation"})
        @Override
        protected void onPostExecute(Response<TransfusionData> result) {

            hideSwipeRefreshLayout();
            // adapter.clearData();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(TransfusionListActivity.this, mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    switch (mRequestType) {
                        case GET_LIST:

                            ArrayList<TransfusionVo> tList = (ArrayList<TransfusionVo>) result.Data.SYD;

                            ArrayList<TransfusionInfoVo> infoList = (ArrayList<TransfusionInfoVo>) result.Data.SYMX;
                            if (null != tList) {
                                mAdapter = new TransfusionAdapter(getApplicationContext(),
                                        tList, infoList);
                                listView.setAdapter(mAdapter);
                            } else {
                                mAdapter = null;
                                listView.setAdapter(null);
                                showMsgAndVoiceAndVibrator("列表为空");
                            }
                            break;
                        case GET_DETAIL:
                            ArrayList<TransfusionTourRecordVo> list = (ArrayList<TransfusionTourRecordVo>) result.Data.SYXS;
                            if (null != list) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                showCreateDialogCompat(DIALOG_VISIT, bundle);
                            } else {

                                showMsgAndVoiceAndVibrator("列表为空");
                            }

                    }
                } else {

                    showMsgAndVoice(result.Msg);
                    return;
                }
            } else {

                showMsgAndVoiceAndVibrator("加载失败");
                return;
            }
        }
    }

    @Override
    public void initBarBroadcast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (BarcodeActions.Bar_Get.equals(intent.getAction())) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        Intent result = new Intent(context,
                                UserModelActivity.class);
                        result.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        result.putExtra("barinfo", entity);
                        startActivity(result);
                    }

                } else if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    Intent result = new Intent(context, UserModelActivity.class);
                    result.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    result.putExtra("refresh", true);
                    startActivity(result);
                }
            }
        };
    }

    // @Override
    // public void initBarBroadcast() {
    // barBroadcast = new BroadcastReceiver() {
    // @Override
    // public void onReceive(Context arg0, Intent intent) {
    // if (IBarCode.Refresh.equals(intent.getAction())) {
    // actionGetDateTask(GetDataTask.GET_LIST);
    // }
    // }
    // };
    // }

}
