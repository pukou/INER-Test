package com.bsoft.mob.ienr.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.adapter.ViewPagerAdapter;
import com.bsoft.mob.ienr.api.HandOverApi;
import com.bsoft.mob.ienr.api.OffLineApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeFormat;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.dynamicui.handover.HandOverViewFactory;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.handover.HandOverClassify;
import com.bsoft.mob.ienr.model.handover.HandOverForm;
import com.bsoft.mob.ienr.model.handover.HandOverOption;
import com.bsoft.mob.ienr.model.handover.HandOverProject;
import com.bsoft.mob.ienr.model.handover.HandOverRecord;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.OffLineUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Description: 交接单
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-15
 * Time: 14:19
 * Version:
 */
public class BatchHandOverActivity extends BaseBarcodeActivity {

    NestedScrollView mScrollViewSender;
    NestedScrollView mScrollViewReceive;
    Button btnBatchCheck;
    View timePageView;
    TextView mTimeView;
    HandOverRecord handOverRecord;
    HandOverViewFactory handOverViewFactorySender;
    HandOverViewFactory handOverViewFactoryReceiver;
    ViewPager viewPager;

    @Override
    public void initBarBroadcast() {

    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void toRefreshData() {
        GetDataTast task = new GetDataTast();
        tasks.add(task);
        task.execute();
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_batch_hand_over;
    }

    //
    @Override
    protected void initView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        handOverRecord = new HandOverRecord();
        handOverRecord.JLXH = intent.getStringExtra("jlxh");
        handOverRecord.YSXH = intent.getStringExtra("ysxh");
        handOverRecord.ZYH = intent.getStringExtra("zyh");

        timePageView = findViewById(R.id.id_ll_controller);
        viewPager = (ViewPager) findViewById(R.id.id_vp);
        initActionBar();
        initTimePageView();
        initViewPager();
        initBatchCheck();
    }


    private void initBatchCheck() {
        btnBatchCheck = (Button) findViewById(R.id.btnBatchCheck);
        btnBatchCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handOverRecord.HandOverFormBefore != null && handOverRecord.HandOverFormAfert != null) {
                    try {
                        for (HandOverClassify classifyBefore : handOverRecord.HandOverFormBefore.HandOverClassifyList) {
                            for (HandOverProject projectBefore : classifyBefore.HandOverProjectList) {
                                if (!projectBefore.ISSELECT || !projectBefore.SYFW.equals("3")) {
                                    continue;
                                }
                                for (HandOverClassify classifyAfter : handOverRecord.HandOverFormAfert.HandOverClassifyList) {
                                    for (int i = 0; i < classifyAfter.HandOverProjectList.size(); i++) {
                                        HandOverProject projectAfter = classifyAfter.HandOverProjectList.get(i);
                                        if (projectAfter.XMBS.equals(projectBefore.XMBS)) {
                                            classifyAfter.HandOverProjectList.remove(i);
                                            HandOverProject finalProjectAfter = deepCopyObject(projectBefore);
                                            finalProjectAfter.JLXM = projectAfter.JLXM;
                                            finalProjectAfter.JJQH = projectAfter.JJQH;
                                            finalProjectAfter.ISMODIFY = true;
                                            for (HandOverOption option : finalProjectAfter.HandOverOptionList) {
                                                option.JLXX = "0";
                                                option.ISMODIFY = true;
                                            }
                                            classifyAfter.HandOverProjectList.add(i, finalProjectAfter);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    View viewReceiver = buildReceiverUi(handOverRecord.HandOverFormAfert);
                    if (viewReceiver != null) {
                        if (mScrollViewReceive.getChildCount() > 0) {
                            mScrollViewReceive.removeAllViews();
                        }
                        mScrollViewReceive.addView(viewReceiver);
                        showMsgAndVoice("核对完成");
                    }
                }
            }
        });
    }

    private void setBatchCheckShow() {
        if (!EmptyTool.isBlank(handOverRecord.ZTBZ) && handOverRecord.ZTBZ.equals("2")) {
            btnBatchCheck.setVisibility(View.GONE);
        } else {
            btnBatchCheck.setVisibility(View.VISIBLE);
        }
    }

    private void initTimePageView() {

        mTimeView = (TextView) timePageView
                .findViewById(R.id.nurse_datetime_txt);

        mTimeView.setOnClickListener(onClickListener);

        String yyyyMMddHHmm = DateTimeHelper.getServer_yyyyMMddHHmm00();
        initTimeTxt(yyyyMMddHHmm, R.id.nurse_datetime_txt);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            String dateStr = mTimeView.getText().toString();
            if (EmptyTool.isBlank(dateStr)) {
                return;
            }
            // 导入年月数据
            YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateStr);
            showPickerDateTimeCompat(ymdHMs, viewId);
        }
    };

    @Override
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {
        String yyyyMMddHHmm = DateTimeFactory.getInstance()
                .ymdhms2Custom(year, month, dayOfMonth, hourOfDay, minute, 0,
                        DateTimeFormat.yyyy_MM_dd_HHmm);
        initTimeTxt(yyyyMMddHHmm, viewId);
    }

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {
        String date = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);
        initTimeTxt4OnlyData(date, viewId);
    }

    private void initTimeTxt4OnlyData(String timeStr, int viewId) {
        View timeView = findViewById(viewId);
        if (timeView != null && timeView instanceof TextView) {
            ((TextView) timeView).setText(timeStr);
        }
    }

    private void initTimeTxt(String yyyyMMddHHmm, int viewId) {
        View timeView = findViewById(viewId);
        if (timeView != null && timeView instanceof TextView) {
            ((TextView) timeView).setText(yyyyMMddHHmm);
        }
    }

    private ArrayList<View> pagelist = new ArrayList<View>();

    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: initPager
     * @Description: 初始化滑动页
     */
    private void initViewPager() {

        View receiverView = getLayoutInflater().inflate(R.layout.layout_hand_over_batch_receiver, null, false);
        View senderView = getLayoutInflater().inflate(R.layout.layout_hand_over_batch_sender, null, false);
        TextView right = receiverView.findViewById(R.id.id_tv);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager.getCurrentItem();
                if (current != pagelist.size() - 1) {
                    current++;
                    viewPager.setCurrentItem(current);
                }

            }
        });
        TextView left = senderView.findViewById(R.id.id_tv);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager.getCurrentItem();
                if (current != 0) {
                    current--;
                    viewPager.setCurrentItem(current);
                }
            }
        });
        pagelist.add(receiverView);
        pagelist.add(senderView);
        viewPager.setAdapter(new ViewPagerAdapter(pagelist));
        viewPager.setCurrentItem(pagelist.size() - 1);// 选择最后一个view

        mScrollViewSender = (NestedScrollView) receiverView
                .findViewById(R.id.id_sv);
        mScrollViewReceive = (NestedScrollView) senderView
                .findViewById(R.id.id_sv);
        toRefreshData();
    }

    private void receive() {
        // 执行发送操作
        ReceiveTast task = new ReceiveTast();
        tasks.add(task);
        task.execute();
    }

    class ReceiveTast extends AsyncTask<Void, Void, Response<HandOverRecord>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.doing);
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
        protected Response<HandOverRecord> doInBackground(Void... params) {
            String data = "";
            try {
                handOverRecord.JSSJ = mTimeView.getText().toString();
                handOverRecord.JSGH = mAppApplication.user.YHID;
                data = JsonUtil.toJson(handOverRecord);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return HandOverApi.getInstance(BatchHandOverActivity.this)
                    .receiveHandOverRecord(data);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<HandOverRecord> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(BatchHandOverActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            receive();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(result.Msg);
                    finish();

                } else {
                    showMsgAndVoice(result.Msg);
                   /* MediaUtil.getInstance(BatchHandOverActivity.this).playSound(
                            R.raw.wrong, BatchHandOverActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

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
        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {
                if (handOverRecord.ZTBZ.equals("2")) {
                    showMsgAndVoiceAndVibrator("当前交接单已完成，不允许修改！");
                    return;
                }
                saveData();
            }

            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }


    private void saveData() {
        // 离线保存
        if (!OffLineUtil.WifiConnected(BatchHandOverActivity.this)) {
            handOverRecord.JSSJ = mTimeView.getText().toString();
            handOverRecord.JSGH = mAppApplication.user.YHID;

            String data = "";
            try {
                data = JsonUtil.toJson(handOverRecord);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String url = OffLineApi.getInstance(this).url;
            String uri = url + "handover/post/receiveHandOverRecord";
            if (OffLineUtil.offLineSave(BatchHandOverActivity.this, uri, 2, data,
                    mAppApplication.sickPersonVo.BRXM, "护理交接",
                    mAppApplication.user.YHXM)) {
                showMsgAndVoice("当前网络未连接，已为您保存在本地。网络连接好后，请到【离线保存】菜单中提交。");
            }
            return;
        }
        receive();
    }


    class GetDataTast extends AsyncTask<Void, Void, Response<HandOverRecord>> {

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
        protected Response<HandOverRecord> doInBackground(Void... params) {

            String txsj = mTimeView.getText().toString().trim();
            try {
                txsj = URLEncoder.encode(txsj, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return HandOverApi.getInstance(BatchHandOverActivity.this)
                    .getHandOverRecord(handOverRecord.JLXH, handOverRecord.YSXH,
                            handOverRecord.ZYH, txsj, mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<HandOverRecord> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(BatchHandOverActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    handOverRecord = result.Data;
                    setBatchCheckShow();
                    HandOverForm formSender = handOverRecord.HandOverFormBefore;
                    View viewSender = buildSendUi(formSender);
                    if (viewSender != null) {
                        if (mScrollViewSender.getChildCount() > 0) {
                            mScrollViewSender.removeAllViews();
                        }
                        mScrollViewSender.addView(viewSender);
                    }

                    HandOverForm formReceiver = handOverRecord.HandOverFormAfert;
                    View viewReceiver = buildReceiverUi(formReceiver);
                    if (viewReceiver != null) {
                        if (mScrollViewReceive.getChildCount() > 0) {
                            mScrollViewReceive.removeAllViews();
                        }
                        mScrollViewReceive.addView(viewReceiver);
                    }

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

    private View buildSendUi(HandOverForm form) {

        if (form == null) {
            return null;
        }
        handOverViewFactorySender = new HandOverViewFactory(this, false, dateTimePickerListener);
        handOverViewFactorySender.Txsj = mTimeView.getText().toString().trim();
        LinearLayout child = handOverViewFactorySender.builderUi(form, 10000);
        return child;
    }

    private View buildReceiverUi(HandOverForm form) {

        if (form == null) {
            return null;
        }
        boolean enable = true;
        if (handOverRecord.ZTBZ.equals("2")) {
            enable = false;
        }
        handOverViewFactoryReceiver = new HandOverViewFactory(this, enable, dateTimePickerListener);
        handOverViewFactoryReceiver.Txsj = mTimeView.getText().toString().trim();
        LinearLayout child = handOverViewFactoryReceiver.builderUi(form, 20000);
        return child;
    }

    private HandOverViewFactory.DateTimePickerListener dateTimePickerListener = new HandOverViewFactory.DateTimePickerListener() {
        @Override
        public void onDateTimeClick(View view) {
            if (!(view instanceof TextView)) {
                return;
            }

            TextView timeTxt = (TextView) view;
            int viewId = timeTxt.getId();
            String dateStr = timeTxt.getText().toString();
            if (EmptyTool.isBlank(dateStr)) {
                dateStr = DateTimeHelper.getServerDateTime();
            }
            String formatStr = "";
            if (timeTxt.getTag(R.id.id_hold_time_format) != null) {
                formatStr = (String) timeTxt.getTag(R.id.id_hold_time_format);
            }
            if (formatStr.contains("-") && !formatStr.contains(":")) {
                //日期
                YmdHMs ymdHMs = DateTimeHelper.date2YmdHMs(dateStr);
                showPickerDateCompat(ymdHMs, viewId);
            } else {
                //日期和时间
                YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateStr);
                showPickerDateTimeCompat(ymdHMs, viewId);
            }
        }
    };


    public <T> T deepCopyObject(T obj)
            throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(obj);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        T dest = (T) in.readObject();
        return dest;
    }

}
