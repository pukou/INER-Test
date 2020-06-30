package com.bsoft.mob.ienr.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.AdviceApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.advice.TransfusionData;
import com.bsoft.mob.ienr.model.advice.TransfusionVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;

/**
 * 治疗医嘱（输氧单为主） Created by hy on 14-3-24.
 */
public class OxygenListActivity extends BaseBarcodeActivity {

    private ListView pullToRefreshListView;
    private TextView time;

    private View sltDateView;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void toRefreshData() {
        GetDataTask getDataTask = new GetDataTask();
        tasks.add(getDataTask);
        getDataTask.execute();
    }

    public void findView() {

        pullToRefreshListView = (ListView) findViewById(R.id.id_lv);
        time = (TextView) findViewById(R.id.time);
        findViewById(R.id.image).setVisibility(View.GONE);
        sltDateView = findViewById(R.id.slt_date_ly);

        initActionBar();

        initDateView();

        String ymdHM = DateTimeHelper.getServer_yyyyMMddHHmm00();
        initTimeTxt(ymdHM, R.id.time);
    }

    private void initDateView() {

        sltDateView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int viewId = view.getId();
                String dateStr = time.getText().toString();
                if (EmptyTool.isBlank(dateStr)) {
                    return;
                }
                YmdHMs ymdHMs = DateTimeHelper.date2YmdHMs(dateStr);
                showPickerDateCompat(ymdHMs, viewId);
            }
        });
    }

    private void initTimeTxt(String strt, int viewId) {
        if (viewId == R.id.time) {
            time.setText(strt);
            //
            toRefreshData();
        }

    }

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);

        initTimeTxt(nowDate, viewId);
    }


    private void initActionBar() {

        actionBar.setTitle("输氧单");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);

    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_oxygen_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        findView();
    }

    /**
     * 异步加载
     */
    private class GetDataTask extends AsyncTask<Void, Void, Response<TransfusionData>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<TransfusionData> doInBackground(Void... params) {


            if (mAppApplication.sickPersonVo == null) {
                return null;
            }
            String ZYH = mAppApplication.sickPersonVo.ZYH;
            String sTime = time.getText().toString();
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            // TODO
            return AdviceApi.getInstance(getApplicationContext())
                    .GetTransfusionListPatient(ZYH, sTime, "", jgid);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<TransfusionData> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(OxygenListActivity.this, mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    if (result.Data != null) {

                        ArrayList<TransfusionVo> tList = (ArrayList<TransfusionVo>) result.Data.SYD;
                        // TODO
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
                if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    toRefreshData();
                } else if (BarcodeActions.Bar_Get.equals(intent.getAction())) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        Intent result = new Intent(context,
                                UserModelActivity.class);
                        result.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        result.putExtra("barinfo", entity);
                        startActivity(result);
                    }
                }
            }
        };
    }
}
