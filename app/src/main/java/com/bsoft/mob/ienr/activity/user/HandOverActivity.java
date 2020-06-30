package com.bsoft.mob.ienr.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.SignActivity;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.adapter.ReciveAreaAdapter;
import com.bsoft.mob.ienr.api.HandOverApi;
import com.bsoft.mob.ienr.api.OffLineApi;
import com.bsoft.mob.ienr.api.UserApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.dynamicui.handover.HandOverViewFactory;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.handover.HandOverForm;
import com.bsoft.mob.ienr.model.handover.HandOverRecord;
import com.bsoft.mob.ienr.model.handover.RelativeItem;
import com.bsoft.mob.ienr.model.kernel.AreaVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.OffLineUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Description: 交接单
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-15
 * Time: 14:19
 * Version:
 */
public class HandOverActivity extends BaseBarcodeActivity {


    private NestedScrollView mScrollView;
    private View timePageView;
    private TextView mTimeView;
    private Spinner mSpinner;
    private HandOverRecord handOverRecord;
    private HandOverViewFactory handOverViewFactory;

    private static final int RQ_GET_Hand_Over_Form = 0;


    @Override
    public void initBarBroadcast() {

    }


    /**
     * @param @param view
     * @return void
     * @throws
     * @Title: initView
     * @Description: 初始化界面
     */
    private void initView() {
        mScrollView = (NestedScrollView) findViewById(R.id.id_sv);
        timePageView = findViewById(R.id.id_ll_controller);
        SpinnerLayout startTimeLayout = (SpinnerLayout) findViewById(R.id.id_spinner_layout);
        mSpinner = startTimeLayout.getSpinner();
    }

    private void initSpinner() {

        if (!EmptyTool.isBlank(handOverRecord.YSLX) && handOverRecord.YSLX.equals("2")) {
            getAreaVoForSurgery();
        } else {
            // change by louis  Vector<AreaVo> areaVos = app.getAreaList();
            Vector<AreaVo> areaVos = new Vector<>(mAppApplication.getAreaList());
            AreaVo areaVo = null;
            for (AreaVo item : areaVos) {
                if (item.KSDM.equals(mAppApplication.getAreaId())) {
                    areaVo = item;
                    break;
                }
            }
            if (areaVo != null) {
                areaVos.remove(areaVo);
            }
            final ReciveAreaAdapter adapter = new ReciveAreaAdapter(HandOverActivity.this, areaVos, mAppApplication.getAreaId());
            mSpinner.setAdapter(adapter);
        }

    }

    private void initTimePageView() {

        mTimeView = (TextView) timePageView
                .findViewById(R.id.nurse_datetime_txt);

        mTimeView.setOnClickListener(onClickListener);

        String ymdHM = DateTimeHelper.getServer_yyyyMMddHHmm00();
        initTimeTxt(ymdHM, R.id.nurse_datetime_txt);
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
    public void onDateTimeSet(int year, int monthOfYear, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {

        String dateTime = DateTimeFactory.getInstance().ymdhms2DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minute, 0);
        initTimeTxt(dateTime, viewId);
    }

    @Override
    public void onDateSet(int year, int monthOfYear, int dayOfMonth, int viewId) {

        String date = DateTimeFactory.getInstance().ymd2Date(year, monthOfYear, dayOfMonth);

        initTimeTxt4OnlyData(date, viewId);
    }

    private void initTimeTxt4OnlyData(String date, int viewId) {
        String timeStr =date;
        View timeView = findViewById(viewId);
        if (timeView != null && timeView instanceof TextView) {
            ((TextView) timeView).setText(timeStr);
        }
    }

    private void initTimeTxt(String ymdHM, int viewId) {
        String timeStr = ymdHM;
        View timeView = findViewById(viewId);
        if (timeView != null && timeView instanceof TextView) {
            ((TextView) timeView).setText(timeStr);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == HandOverViewFactory.REQ_LIFE) {
            // 生命体征
            getRelate("5");
            return;
        } else if (requestCode == HandOverViewFactory.REQ_RISK) {
            // 风险
            getRelate("2");
            return;
        }
        if (requestCode == RQ_GET_Hand_Over_Form) {//签名

            if (data == null) {
                return;
            }
            String extra = data.getStringExtra(SignActivity.EXTRA_STRING_KEY);

            // 全局签名
            if (EmptyTool.isBlank(extra)) {
                signature();
            }
        }
    }

    private void signature() {
        // 执行发送操作
        SendTast task = new SendTast();
        tasks.add(task);
        task.execute();
    }

    class SendTast extends AsyncTask<Void, Void, Response<String>> {

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
        protected Response<String> doInBackground(Void... params) {

            return HandOverApi.getInstance(HandOverActivity.this)
                    .sendHandOverRecord(handOverRecord.JLXH);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<String> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HandOverActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            signature();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice("签名成功");
                    handOverRecord.ZTBZ = "1";

                } else {
                    showMsgAndVoice(result.Msg);
                    /*MediaUtil.getInstance(HandOverActivity.this).playSound(
                            R.raw.wrong, HandOverActivity.this);*/
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
            setResult(Activity.RESULT_OK);
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
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);
        actionBar.setBackAction(new Action() {
            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }

            @Override
            public void performAction(View view) {
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_arrow_back_black_24dp;
            }
        });
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public void performAction(View view) {
                if (handOverRecord.ZTBZ.equals("1")) {
                    showMsgAndVoiceAndVibrator("当前交接单已发送，不允许修改！");
                    return;
                } else if (handOverRecord.ZTBZ.equals("2")) {
                    showMsgAndVoiceAndVibrator("当前交接单已完成，不允许修改！");
                    return;
                }
                saveData();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_hand_over;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        handOverRecord = new HandOverRecord();
        handOverRecord.JLXH = intent.getStringExtra("jlxh");
        handOverRecord.YSXH = intent.getStringExtra("ysxh");
        handOverRecord.YSLX = intent.getStringExtra("yslx");

        initView();
        initActionBar();
        initTimePageView();
        initSpinner();
        getData();
    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        ArrayList<Integer> itemDrawables = new ArrayList<Integer>();
        itemDrawables.add(R.drawable.menu_delete);
        itemDrawables.add(R.drawable.menu_create);
        itemDrawables.add(R.drawable.menu_save);
        itemDrawables.add(R.drawable.menu_sign);

        final int[][] itemStringDrawables = {
                {R.drawable.menu_delete, R.string.comm_menu_delete},
                {R.drawable.menu_create, R.string.comm_menu_add},
                {R.drawable.menu_save, R.string.comm_menu_save},
                {R.drawable.menu_sign, R.string.comm_menu_sign}};

        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
     /*   for (int itemDrawableResid : itemDrawables) {
            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }*/
        for (int[] itemDrawableRes : itemStringDrawables) {
            int itemDrawableResid = itemDrawableRes[0];
            int textResId = itemDrawableRes[1];
            String text = textResId > 0 ? getString(textResId) : null;
            IFloatMenuItem floatMenuItem = new TextFloatMenuItem(itemDrawableResid, text) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }
        return floatMenuItemList;
    }


    /**
     * 响应RayMenu item点击
     */
    private void onMenuItemClick(int drawableRes) {


        if (drawableRes == R.drawable.menu_create) {
            addData();
        } else if (drawableRes == R.drawable.menu_save) {
            if (handOverRecord.ZTBZ.equals("1")) {
                showMsgAndVoiceAndVibrator("当前交接单已发送，不允许修改！");
                return;
            } else if (handOverRecord.ZTBZ.equals("2")) {
                showMsgAndVoiceAndVibrator("当前交接单已完成，不允许修改！");
                return;
            }
            saveData();
        } else if (drawableRes == R.drawable.menu_delete) {
            if (EmptyTool.isBlank(handOverRecord.JLXH)
                    || handOverRecord.JLXH.equals("0")) {
                showMsgAndVoiceAndVibrator("请先去保存数据，再删除！");
                return;
            }
            if (handOverRecord.ZTBZ.equals("1")) {
                showMsgAndVoiceAndVibrator("当前交接单已发送，不允许删除！");
                return;
            } else if (handOverRecord.ZTBZ.equals("2")) {
                showMsgAndVoiceAndVibrator("当前交接单已完成，不允许删除！");
                return;
            }
            delData();
        } else if (drawableRes == R.drawable.menu_sign) {
            if (EmptyTool.isBlank(handOverRecord.JLXH)
                    || handOverRecord.JLXH.equals("0")) {
                showMsgAndVoiceAndVibrator("请先去保存数据，再签名！");
                return;
            }
            if (handOverRecord.ZTBZ.equals("1")) {
                showMsgAndVoiceAndVibrator("当前交接单已发送，不允许重复签名！");
                return;
            } else if (handOverRecord.ZTBZ.equals("2")) {
                showMsgAndVoiceAndVibrator("当前交接单已完成，不允许重复签名！");
                return;
            }
            // 全局签名
            startSignActivity(false, null);
        }
    }

    private void startSignActivity(boolean doubleSign, String extra) {
        Intent intent = new Intent(HandOverActivity.this, SignActivity.class);

        if (doubleSign) {
            intent.putExtra(SignActivity.ACTION_SIGN,
                    SignActivity.ACTION_EXTRA_SING_BOUSE);
        } else {
            intent.putExtra(SignActivity.ACTION_SIGN,
                    SignActivity.ACTION_EXTRA_SIGN_SIGNLE);
        }
        intent.putExtra(SignActivity.EXTRA_STRING_KEY, extra);
        startActivityForResult(intent, RQ_GET_Hand_Over_Form);
    }

    private void delData() {
        // 执行删除操作
        DelDataTast task = new DelDataTast();
        tasks.add(task);
        task.execute();
    }

    class DelDataTast extends AsyncTask<Void, Void, Response<String>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.deleteing);
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
        protected Response<String> doInBackground(Void... params) {

            return HandOverApi.getInstance(HandOverActivity.this)
                    .delHandOverRecord(handOverRecord.JLXH);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<String> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HandOverActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            delData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoiceAndVibrator("删除成功");
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    showMsgAndVoice(result.Msg);
                   /* MediaUtil.getInstance(HandOverActivity.this).playSound(
                            R.raw.wrong, HandOverActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    private void saveData() {
        // 离线保存
        if (!OffLineUtil.WifiConnected(HandOverActivity.this)) {
            handOverRecord.ZYH = mAppApplication.sickPersonVo.ZYH;
            handOverRecord.ZYHM = mAppApplication.sickPersonVo.ZYHM;
            handOverRecord.BRXM = mAppApplication.sickPersonVo.BRXM;
            handOverRecord.BRXB = String.valueOf(mAppApplication.sickPersonVo.BRXB);
            handOverRecord.BRCH = mAppApplication.sickPersonVo.BRCH;

            //chaneg by louis
            if (mAppApplication.getCrrentArea() != null && "[isSurgery]".equals(mAppApplication.getCrrentArea().YGDM)) {
                //标记是 手术科室
                if (!EmptyTool.isBlank(handOverRecord.YSLX) && handOverRecord.YSLX.equals("3")) {
                    //新需求 当交接单是术后交接单 3 的时候  brbq应存放ssks  jsbq 是要交接的 brbq
                    handOverRecord.BRBQ = mAppApplication.getAreaId();//当是手术科室时候  KSDM 存放 SSKS
                } else {
                    //mAppApplication.getAreaId();//当是手术科室时候  KSDM 存放 SSKS  【所以不用这个】
                    handOverRecord.BRBQ = mAppApplication.sickPersonVo.BRBQ;//不是术后交接单 保存病人原来的brbq
                }
            } else {
                handOverRecord.BRBQ = mAppApplication.getAreaId();//普通的brbq
            }

            handOverRecord.JSBQ = ((AreaVo) mSpinner.getSelectedItem()).KSDM;
            handOverRecord.TXSJ = mTimeView.getText().toString();
            handOverRecord.TXGH = mAppApplication.user.YHID;
            handOverRecord.JLGH = mAppApplication.user.YHID;
            handOverRecord.JLGH = mAppApplication.user.YHID;
            handOverRecord.DYCS = "0";
            handOverRecord.JGID = mAppApplication.jgId;

            String data = "";
            try {
                data = JsonUtil.toJson(handOverRecord);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String url = OffLineApi.getInstance(this).url;
            String uri = url + "handover/post/saveHandOverRecord";
            if (OffLineUtil.offLineSave(HandOverActivity.this, uri, 2, data,
                    mAppApplication.sickPersonVo.BRXM, "护理交接",
                    mAppApplication.user.YHXM))
                showMsgAndVoice("当前网络未连接，已为您保存在本地。网络连接好后，请到【离线保存】菜单中提交。");

            return;
        }
        // 执行保存操作
        SaveDataTast task = new SaveDataTast();
        tasks.add(task);
        task.execute();
    }

    class SaveDataTast extends AsyncTask<Void, Void, Response<HandOverRecord>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
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
                handOverRecord.ZYH = mAppApplication.sickPersonVo.ZYH;
                handOverRecord.ZYHM = mAppApplication.sickPersonVo.ZYHM;
                handOverRecord.BRXM = mAppApplication.sickPersonVo.BRXM;
                handOverRecord.BRXB = String.valueOf(mAppApplication.sickPersonVo.BRXB);
                handOverRecord.BRCH = mAppApplication.sickPersonVo.BRCH;
                //chaneg by louis
                if (mAppApplication.getCrrentArea() != null && "[isSurgery]".equals(mAppApplication.getCrrentArea().YGDM)) {
                    //标记是 手术科室
                    if (!EmptyTool.isBlank(handOverRecord.YSLX) && handOverRecord.YSLX.equals("3")) {
                        //新需求 当交接单是术后交接单 3 的时候  brbq应存放ssks  jsbq 是要交接的 brbq
                        handOverRecord.BRBQ = mAppApplication.getAreaId();//当是手术科室时候  KSDM 存放 SSKS
                    } else {
                        //mAppApplication.getAreaId();//当是手术科室时候  KSDM 存放 SSKS  【所以不用这个】
                        handOverRecord.BRBQ = mAppApplication.sickPersonVo.BRBQ;//不是术后交接单 保存病人原来的brbq
                    }
                } else {
                    handOverRecord.BRBQ = mAppApplication.getAreaId();//普通的brbq
                }

                handOverRecord.JSBQ = ((AreaVo) mSpinner.getSelectedItem()).KSDM;
                handOverRecord.TXSJ = mTimeView.getText().toString();
                handOverRecord.TXGH = mAppApplication.user.YHID;
                handOverRecord.JLGH = mAppApplication.user.YHID;
                handOverRecord.JLGH = mAppApplication.user.YHID;
                handOverRecord.DYCS = "0";
                handOverRecord.JGID = mAppApplication.jgId;

                data = JsonUtil.toJson(handOverRecord);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return HandOverApi.getInstance(HandOverActivity.this)
                    .saveHandOverRecord(data);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final Response<HandOverRecord> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HandOverActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    handOverRecord = result.Data;
                    showMsgAndVoice(R.string.project_save_success);

                } else {
                    showMsgAndVoice(result.Msg);
                 /*   MediaUtil.getInstance(HandOverActivity.this).playSound(
                            R.raw.wrong, HandOverActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    private void addData() {
        // 执行新增操作
        handOverRecord.JLXH = "0";
        getData();
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    private void getData() {
        GetDataTast task = new GetDataTast();
        tasks.add(task);
        task.execute();
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

        @Override
        protected Response<HandOverRecord> doInBackground(Void... params) {

            String txsj = mTimeView.getText().toString().trim();
            try {
                txsj = URLEncoder.encode(txsj, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return HandOverApi.getInstance(HandOverActivity.this)
                    .getHandOverRecord(handOverRecord.JLXH, handOverRecord.YSXH,
                            mAppApplication.sickPersonVo.ZYH, txsj, mAppApplication.jgId);
        }


        @Override
        protected void onPostExecute(Response<HandOverRecord> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HandOverActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            getData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    handOverRecord = result.Data;
                    HandOverForm form = handOverRecord.HandOverFormBefore;
                    View view = buildUi(form);
                    if (view != null) {
                        if (mScrollView.getChildCount() > 0) {
                            mScrollView.removeAllViews();
                        }
                        mScrollView.addView(view);
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

    private void getAreaVoForSurgery() {
        GetgetAreaVoForSurgeryTast task = new GetgetAreaVoForSurgeryTast();
        tasks.add(task);
        task.execute();
    }

    class GetgetAreaVoForSurgeryTast extends AsyncTask<Void, Void, Response<List<AreaVo>>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
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
        protected Response<List<AreaVo>> doInBackground(Void... params) {

            return UserApi.getInstance(HandOverActivity.this)
                    .getAreaVoForSurgery(mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<List<AreaVo>> result) {
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HandOverActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            getAreaVoForSurgery();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    final ReciveAreaAdapter adapter = new ReciveAreaAdapter(HandOverActivity.this, new Vector<>(result.Data), "");
                    mSpinner.setAdapter(adapter);

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

    private void getRelate(String dzlx) {
        GetRelativeData task = new GetRelativeData();
        tasks.add(task);
        task.execute(dzlx);
    }

    class GetRelativeData extends AsyncTask<String, Void, Response<List<RelativeItem>>> {
        private String dzlx = "0";

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<RelativeItem>> doInBackground(String... params) {

            if (params == null || params.length < 1) {
                return null;
            }
            if (mAppApplication.user == null) {
                return null;
            }
            dzlx = params[0];
            String txsj = mTimeView.getText().toString().trim();
            try {
                txsj = URLEncoder.encode(txsj, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HandOverApi api = HandOverApi.getInstance(HandOverActivity.this);
            return api.getRelativeData(mAppApplication.sickPersonVo.ZYH, dzlx, txsj, mAppApplication.jgId);

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<List<RelativeItem>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
               /* MediaUtil.getInstance(HandOverActivity.this).playSound(
                        R.raw.wrong, HandOverActivity.this);*/
                return;
            }

            List<RelativeItem> values = result.Data;
            if (values != null) {
                if (dzlx.equals("2")) {
                    handOverViewFactory.resetRisk(values);
                } else if (dzlx.equals("5")) {
                    handOverViewFactory.resetLife(values);
                }
            }
        }
    }

    private View buildUi(HandOverForm form) {

        if (form == null) {
            return null;
        }
        boolean enable = true;
        if (!EmptyTool.isBlank(handOverRecord.ZTBZ) && (handOverRecord.ZTBZ.equals("1") || handOverRecord.ZTBZ.equals("2"))) {
            enable = false;
        }
        handOverViewFactory = new HandOverViewFactory(this, this, enable, dateTimePickerListener);
        handOverViewFactory.Txsj = mTimeView.getText().toString().trim();
        LinearLayout child = handOverViewFactory.builderUi(form, 0);
        return child;
    }

    // add by louis
    private HandOverViewFactory.DateTimePickerListener dateTimePickerListener = new HandOverViewFactory.DateTimePickerListener() {
        @Override
        public void onDateTimeClick(View view) {
            if (!(view instanceof TextView)) {
                return;
            }

            TextView timeTxt = (TextView) view;
            int viewId = timeTxt.getId();
            String dateStr = timeTxt.getText().toString();

            // 导入年月数据
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


}
