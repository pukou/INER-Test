package com.bsoft.mob.ienr.activity.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.SignActivity;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.EvaluateApi;
import com.bsoft.mob.ienr.api.OffLineApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeFormat;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.dynamicui.evaluate.Classification;
import com.bsoft.mob.ienr.dynamicui.evaluate.EvaluateViewFactory;
import com.bsoft.mob.ienr.dynamicui.evaluate.EvaluateViewFactory.DateTimePickerListener;
import com.bsoft.mob.ienr.dynamicui.evaluate.EvaluateViewFactory.SignClickListener;
import com.bsoft.mob.ienr.dynamicui.evaluate.EvaluateViewFactory.VEntity;
import com.bsoft.mob.ienr.dynamicui.evaluate.Form;
import com.bsoft.mob.ienr.dynamicui.evaluate.ItemNode;
import com.bsoft.mob.ienr.dynamicui.evaluate.ValueEntity;
import com.bsoft.mob.ienr.dynamicui.evaluate.ValueForm;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.SelectResult;
import com.bsoft.mob.ienr.model.evaluate.CheckCls;
import com.bsoft.mob.ienr.model.evaluate.CheckForm;
import com.bsoft.mob.ienr.model.evaluate.CheckItem;
import com.bsoft.mob.ienr.model.evaluate.EvaluateFormItem;
import com.bsoft.mob.ienr.model.evaluate.EvaluateResponse;
import com.bsoft.mob.ienr.model.evaluate.EvaluateSaveRespose;
import com.bsoft.mob.ienr.model.evaluate.EvaluateTempDataBean;
import com.bsoft.mob.ienr.model.evaluate.SaveForm;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSymptomTempDataBean;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.FormSyncUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.OffLineUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * 护理评估页 Created by hy on 14-3-24.
 */
public class NurseEvaluateActivity extends BaseBarcodeActivity {

    /**
     * 请求获取已记录评估单列表
     */
    private static final int RQ_GET_RECORD_FORM_LIST = 0;

    /**
     * 请求获取签名用户ID
     */
    private static final int RQ_GET_USERID = RQ_GET_RECORD_FORM_LIST + 1;

    /**
     * 请求获取评估单列表
     */
    private static final int RQ_GET_FORM_LIST = RQ_GET_USERID + 1;


    private NestedScrollView mScrollView;

    private Request request;

    private EvaluateViewFactory viewFactory;

    private View timePageView;

    private TextView mTimeView;

    private CheckForm check;
    private Form form;

    private boolean isReview = false;//是否审阅
    private boolean isSigned = false;//是否签名
    private boolean isShowReviewMenu = false;

    private boolean isEdit = false;//是否修改操作
    //福建协和客户化：时间格式
    private String SJGS;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {

       /* final int[] itemDrawables = {R.drawable.menu_create,
                R.drawable.menu_view};*/
        final int[] itemDrawables = {R.drawable.menu_create};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_create, R.string.comm_menu_add}};
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
    /*    for (int itemDrawableResid : itemDrawables) {
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

    private void getData() {
        getData(false);
    }

    private void getData(boolean isNewPage) {
        Intent intent = getIntent();
        if (intent != null) {
            String datetime = DateTimeHelper.getServerDateTime();
            initTimeTxt(datetime, mTimeView.getId());

            request = new Request();
            request.ysxh = intent.getStringExtra("YSXH");
            request.lybs = intent.getStringExtra("LYBS");
            //转科不检查病区
            request.isZKNotCheckBQ = intent.getBooleanExtra("isZKNotCheckBQ", false);
            request.isNewPage = isNewPage;
            request.type = GetDateTask.QRT_BY_YSXH;

            ationGetDateTask(GetDateTask.QRT_BY_YSXH, request.ysxh);
        }
    }

    private void initTimePageView() {

        mTimeView = (TextView) timePageView
                .findViewById(R.id.nurse_datetime_txt);

        mTimeView.setOnClickListener(onClickListener);

        String datetime = DateTimeHelper.getServerDateTime();
        initTimeTxt(datetime, R.id.nurse_datetime_txt);
    }

    private OnClickListener onClickListener = new OnClickListener() {

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


    protected void onMenuItemClick(int drawableId) {

        if (drawableId == R.drawable.menu_create) {// 新增护理评估
//            finish();
            getData(true);
        }/* else if (drawableId == R.drawable.menu_view) {// 查看先前记录

            goToEvaluateFormListActivity();

        }*/ else if (drawableId == R.drawable.menu_sign
                || drawableId == R.drawable.menu_cancel_sign) {// 签名
            if (request == null || EmptyTool.isBlank(request.jlxh)) {
                showMsgAndVoiceAndVibrator("请先保存评估单，再进行签名操作");
                return;
            }
            if (isReview && isSigned) {
                showMsgAndVoiceAndVibrator("已经审阅，不能再进行取消签名操作");
                return;
            }
            // 全局签名
            startSignActivity(false, null);
        } else if (drawableId == R.drawable.menu_fresh) {// 刷新
            if (request != null) {
                ationGetDateTask(request.type, request.getBH());
            }
        } else if (drawableId == R.drawable.menu_review) {// 审阅

            if (request == null) {
                showMsgAndVoiceAndVibrator("请获取评估单，再进行审阅操作");
                return;
            }
            if (request.type == GetDateTask.QRT_BY_YSXH) {
                showMsgAndVoiceAndVibrator("请先保存评估单，再进行审阅操作");
                return;
            }
            actionReview(ReviewAboutTask.ACTION_REVIEW, request.jlxh);
        } else if (drawableId == R.drawable.menu_cancel_review) {// 取消 审阅

            if (request == null) {
                showMsgAndVoiceAndVibrator("请获取评估单，再进行取消 审阅操作");
                return;
            }
            if (request.type == GetDateTask.QRT_BY_YSXH) {
                showMsgAndVoiceAndVibrator("请先保存评估单，再进行取消 审阅操作");
                return;
            }
            actionReview(ReviewAboutTask.ACTION_CANCEL_REVIEW, request.jlxh);
        } else if (drawableId == R.drawable.menu_save) {
            actionSaveTask();
        }
    }


    private void goToEvaluateFormListActivity() {
        startEvaluateFormListActivity(
                NurseEvaluateFormListActivity.TYPE_RECORD_FORM,
                RQ_GET_RECORD_FORM_LIST);
    }

    private void ConfirmSave() {
        View root = mScrollView.getChildAt(0);
        Form form = (Form) root.getTag();

        if (viewFactory == null) {
            showMsgAndVoiceAndVibrator("请求失败：获取UI数据错误");
            return;
        }

        Map<Classification, VEntity[]> map = new HashMap<Classification, VEntity[]>();
        for (Classification cf : form.clazzs) {
            ArrayList<VEntity> list = getRequestList(cf);
            if (cf.XSFLLX.equals("1")) {
                //工厂类中 cf.modFlag 生成有问题 不能及时响应子项内容改变 索性先去掉
               /* if (!cf.modFlag) {
                    continue;
                }*/
            } else {
                if (list.size() <= 0) {
                    continue;
                }
            }
            VEntity[] array = list.toArray(new VEntity[list.size()]);
            map.put(cf, array);
        }
        //工厂类中 form.modFlag 生成有问题 不能及时响应子项内容改变 索性先去掉
//        if (map.size() <= 0 && (!form.modFlag)) {
        if (map.size() <= 0) {
            showMsgAndVoiceAndVibrator("当前没有要保存或更新的数据");
            return;
        }
        actionSaveTask(map);
    }

    private void startEvaluateFormListActivity(int startType, int requestCode) {
        Intent intent = new Intent(this, NurseEvaluateFormListActivity.class);
        intent.putExtra(NurseEvaluateFormListActivity.EXTRE_INT_START_TYPE,
                startType);
        startActivityForResult(intent, requestCode);
    }

    private void actionSaveTask() {
        List<CheckItem> items = new ArrayList<CheckItem>();
        for (Classification cf : form.clazzs)
            for (CheckCls cls : check.CLS) {
                if (cls.ITEMS == null)
                    continue;
                if (cls.FLID == cf.ID) {
                    for (CheckItem item : cls.ITEMS) {
                        if ("1".equals(item.TXBZ)) {
                            continue;
                        }
                        if (item.ITEM == null) {
                            items.add(item);
                        } else if (item.ITEM.size() < 1) {
                            items.add(item);
                        }
                    }
                }
            }
        if (items.size() > 0) {
            StringBuffer content = new StringBuffer();
            for (CheckItem item : items) {
                content.append(item.XMMC).append("\n");
            }
            ShowWarn(content.toString());
            return;
        }
        ConfirmSave();
    }

    private void ShowWarn(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
        builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "是否保存")); // 设置标题
        builder.setMessage("以下项目未填写：\n" + content); // 设置内容
        if (form.save.Value == 1) {
            builder.setPositiveButton(getString(R.string.project_operate_ok),
                    new DialogInterface.OnClickListener() { // 设置确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ConfirmSave();
                        }
                    });
        }
        builder.setNegativeButton("继续填写",
                new DialogInterface.OnClickListener() { // 设置取消按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    @SuppressWarnings("unchecked")
    private void actionSaveTask(Map<Classification, VEntity[]> map) {
        // 离线保存
        if (!OffLineUtil.WifiConnected(this)) {

            View cRootView = mScrollView.getChildAt(0);
            if (cRootView == null) {
                return;
            }
            EvaluateSaveRespose respose = getSaveString(cRootView, map);
            respose.jgid = application.jgId;
            respose.bqdm = application.getAreaId();
            String data = "";
            try {
                data = JsonUtil.toJson(respose);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String url = OffLineApi.getInstance(this).url;
            String uri = url + "evaluation/post/SaveEvaluation";
            if (OffLineUtil.offLineSave(this, uri, 2, data,
                    application.sickPersonVo.BRXM, "护理评估",
                    application.user.YHXM)) {
                showMsgAndVoice("当前网络未连接，已为您保存在本地。网络连接好后，请到【离线保存】菜单中提交。");
                return;
            }
        }
        SaveDateTask task = new SaveDateTask();
        tasks.add(task);
        task.execute(map);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == EvaluateViewFactory.REQ_GUID) {
            // 宣教
            getRelate("3");
            return;
        } else if (requestCode == EvaluateViewFactory.REQ_LIFE) {
            // 生命体征
            getRelate("5");
            return;
        } else if (requestCode == EvaluateViewFactory.REQ_RISK) {
            // 风险
            getRelate("2");
            return;
        }

        if (data == null) {
            return;
        }

        if (requestCode == RQ_GET_RECORD_FORM_LIST) {

            String txsj = data.getStringExtra("TXSJ");
            Date date = DateUtil.getDateCompat(txsj);
            String time = DateUtil.format_yyyyMMdd_HHmm.format(date);
            if (!EmptyTool.isBlank(time)) {
                mTimeView.setText(time);
            }
            isEdit = true;//修改数据时不做刷新操作
            request = new Request();
            request.jlxh = data.getStringExtra("JLXH");
            request.lybs = data.getStringExtra("LYBS");
            request.ysxh = data.getStringExtra("YSXH");
            request.type = GetDateTask.QRT_BY_JLXH;

            ationGetDateTask(GetDateTask.QRT_BY_JLXH, request.jlxh);
            return;
        }


        if (requestCode == RQ_GET_USERID) {

            String yhid1 = data.getStringExtra(SignActivity.EXTRA_YHID_KEY_1);
            String extra = data.getStringExtra(SignActivity.EXTRA_STRING_KEY);

            // 全局签名
            if (EmptyTool.isBlank(extra)) {

                if (mScrollView.getChildCount() <= 0) {
                    return;
                }
                View root = mScrollView.getChildAt(0);
                Form form = (Form) root.getTag();

                boolean signed = !EmptyTool.isBlank(form.QMGH);

                byte action = signed ? SignAboutTask.ACTION_CANCEL_SING
                        : SignAboutTask.ACTION_SING;
                actionSign(action, null, yhid1, null, "0", "0");
                return;
            }

            // 独立签名
            String yhid2 = data.getStringExtra(SignActivity.EXTRA_YHID_KEY_2);
            try {
                Classification cf = JsonUtil.fromJson(extra,
                        Classification.class);

                boolean cancelSign = !EmptyTool.isBlank(cf.HSQM1)
                        || !EmptyTool.isBlank(cf.HSQM2);

                actionSign(cancelSign ? SignAboutTask.ACTION_CANCEL_SING
                                : SignAboutTask.ACTION_SING, String.valueOf(cf.ID),
                        yhid1, yhid2, String.valueOf(cf.DLBZ),
                        String.valueOf(cf.QMBZ));
                return;
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
            }
            return;
        }

    }

    private void actionSign(byte mActionType, String... params) {

        SignAboutTask task = new SignAboutTask(mActionType);
        tasks.add(task);
        task.execute(params);
    }

    class SignAboutTask extends AsyncTask<String, String, Response<EvaluateResponse>> {

        public static final byte ACTION_SING = 0;
        public static final byte ACTION_CANCEL_SING = ACTION_SING + 1;

        private byte mActionType = ACTION_SING;

        public SignAboutTask(byte mActionType) {
            this.mActionType = mActionType;
        }

        @Override
        protected void onPreExecute() {
            showLoadingDialog(mActionType == ACTION_SING ? R.string.signing : R.string.cancel_signing);
        }

        @Override
        protected Response<EvaluateResponse> doInBackground(String... params) {

            if (params == null || params.length < 5) {
                return null;
            }

            if (request == null || EmptyTool.isBlank(request.jlxh)) {
                return null;
            }

            String jlxh = request.jlxh;
            String ysfl = params[0];
            String hsqm1 = params[1];
            String hsqm2 = params[2];
            String dlbz = params[3];
            String qmbz = params[4];
            String jgid = application.jgId;

            String ysxh = request.ysxh;
            String lybs = request.lybs;

            EvaluateApi api = EvaluateApi.getInstance(NurseEvaluateActivity.this);

            Response<EvaluateResponse> response = new Response<>();
            if (mActionType == ACTION_SING) {
                response = api.EvaluationSignature(jlxh, ysxh, ysfl, lybs,
                        hsqm1, hsqm2, dlbz, qmbz, jgid, Constant.sysType);
            } else if (mActionType == ACTION_CANCEL_SING) {
                response = api.CancelEvaluationSignature(jlxh, ysxh, ysfl,
                        lybs, hsqm1, hsqm2, dlbz, qmbz, jgid, Constant.sysType);
            }
            return response;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<EvaluateResponse> result) {

            hideLoadingDialog();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(NurseEvaluateActivity.this, application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        return;
                    }
                }).showLoginDialog();

            }
            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
              /*  MediaUtil.getInstance(NurseEvaluateActivity.this).playSound(
                        R.raw.wrong, NurseEvaluateActivity.this);*/
                return;
            }

            showMsgAndVoice(result.Msg);
            // 刷新操作
            if (request != null) {
                ationGetDateTask(request.type, request.getBH());
            }


        }
    }

    private void startSignActivity(boolean doubleSign, String extra) {
        Intent intent = new Intent(this, SignActivity.class);

        if (doubleSign) {
            intent.putExtra(SignActivity.ACTION_SIGN,
                    SignActivity.ACTION_EXTRA_SING_BOUSE);
        } else {
            intent.putExtra(SignActivity.ACTION_SIGN,
                    SignActivity.ACTION_EXTRA_SIGN_SIGNLE);
        }
        intent.putExtra(SignActivity.EXTRA_STRING_KEY, extra);
        startActivityForResult(intent, RQ_GET_USERID);
    }

    class Request {
        public String ysxh;
        public String jlxh;
        public String lybs;
        public boolean isZKNotCheckBQ;
        public boolean isNewPage;
        public byte type;

        /**
         * 根据请求类型，获取编号
         *
         * @return
         */
        public String getBH() {
            if (type == GetDateTask.QRT_BY_JLXH) {
                return jlxh;
            } else {// 默认根据ysxh
                return ysxh;
            }
        }
    }


    @Override
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {

        String dateTime = DateTimeFactory.getInstance().ymdhms2DateTime(year, month, dayOfMonth, hourOfDay, minute, 0);
        initTimeTxt(dateTime, viewId);
    }

    private void initTimeTxt(String datetime, int viewId) {
        String timeStr = "";
        if (!EmptyTool.isBlank(SJGS)) {
            //福建协和客户化：按照时间格式处理字符串
//            timeStr = formatMySelf(datetime);
            SJGS = changeFormaterStr(SJGS);
            timeStr = DateTimeFactory.getInstance().dateTime2Custom(datetime, SJGS);
        } else {
            timeStr = DateTimeHelper.getServer_yyyyMMddHHmm00(datetime);
        }

        View timeView = findViewById(viewId);
        if (timeView != null && timeView instanceof TextView) {
            ((TextView) timeView).setText(timeStr);
        }
    }

    private String changeFormaterStr(String SJGS) {
        switch (SJGS) {
            case "yyyy.mm.dd hh:mm:ss":
                SJGS = "yyyy.MM.dd HH:mm:ss";
                break;
            case "yyyy.mm.dd hh:mm":
                SJGS = "yyyy.MM.dd HH:mm";
                break;
            case "yyyy/mm/dd":
                SJGS = "yyyy/MM/dd";
                break;
            case "yyyy.mm.dd":
                SJGS = "yyyy.MM.dd";
                break;
            case "dd/mm hh:mm":
                SJGS = "dd/MM  HH:mm";
                break;
            case "dd/mm":
                SJGS = "dd/MM";
                break;
            case "mm.dd":
                SJGS = "MM.dd";
                break;
            case "hh:mm:ss":
                SJGS = "HH:mm:ss";
                break;
            case "HH:MM:SS":
                SJGS = "HH:mm:ss";
                break;
            case "hh:mm":
                SJGS = "HH;mm";
                break;
            default:
        }
        return SJGS;
    }


    private void resetView() {
        /*final int[] itemDrawables = {R.drawable.menu_create,
                R.drawable.menu_view};*/
        final int[] itemDrawables = {R.drawable.menu_create};
        final int[][] itemStringDrawables = {{
                R.drawable.menu_create, R.string.comm_menu_add
        }};
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
     /*   for (int itemDrawableResid : itemDrawables) {
            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        } */
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

        updateFloatMenuItems(floatMenuItemList);
    }

    private void initActionBar() {

        actionBar.setTitle("护理评估");
        actionBar.setPatient(application.sickPersonVo.BRCH
                + application.sickPersonVo.BRXM);
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
                goToEvaluateFormListActivity();
            }
        });
        actionBar.addAction(new Action() {
            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }

            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public void performAction(View view) {
                actionSaveTask();
            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_nurse_evaluate;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {


        mScrollView = (NestedScrollView) findViewById(R.id.id_sv);
        timePageView = findViewById(R.id.id_ll_controller);

        //add  by louis
        //api.GetNewEvaluationList 的 sql语句中  yslx已经<90  所以单子都不会显示
        //api.GetNewEvaluationListForYslx  会出现不<90的 所以要隐藏掉审阅按钮
        String yslx = getIntent().getStringExtra("YSLX");
        try {
            int yslx_int = Integer.valueOf(yslx);
            isShowReviewMenu = yslx_int < 90;//大于90的不显示
        } catch (Exception e) {
            //e.printStackTrace();
        }
        //
        initActionBar();
        initTimePageView();
        toRefreshData();
        resetView();
    }

    @Override
    protected void toRefreshData() {
        //新增时才支持刷新
        if (!isEdit) {
            getData();
        } else {
            //修改时直接隐藏滚动条，不做数据请求操作
            hideSwipeRefreshLayout();
        }
    }

    class SaveDateTask extends
            AsyncTask<Map<Classification, VEntity[]>, Response<EvaluateFormItem>, Response<EvaluateFormItem>> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<EvaluateFormItem> doInBackground(
                Map<Classification, VEntity[]>... params) {

            if (params == null) {
                return null;
            }

            if (request == null || application.sickPersonVo == null
                    || application.user == null) {
                return null;
            }

            View cRootView = mScrollView.getChildAt(0);
            if (cRootView == null) {
                return null;
            }
            EvaluateSaveRespose mRespose = getSaveString(cRootView, params[0]);
            mRespose.bqdm = application.getAreaId();
            mRespose.jgid = application.jgId;

            //福建协和客户化：自动签名
        /* 2018-6-7 19:19:02 动态签名 ###  for (SaveForm saveForm : mRespose.lists) {
                if (EmptyTool.isBlank(saveForm.HSQM1) && saveForm.entities != null && saveForm.entities.length > 0) {
                    saveForm.HSQM1 = application.user.YHID;
                }
            }*/
            String data = null;
            try {
                data = JsonUtil.toJson(mRespose);
            } catch (IOException e) {
                e.printStackTrace();
            }

            EvaluateApi api = EvaluateApi.getInstance(NurseEvaluateActivity.this);
            Response<EvaluateFormItem> response = api.SaveEvaluation(data);
            if (response.ReType == 0) {
                publishProgress(response);
            }
            return response;
        }

        @Override
        protected void onProgressUpdate(Response<EvaluateFormItem>... values) {

            if (values == null || values.length < 1 || values[0] == null) {
                showMsgAndVoiceAndVibrator("请求失败：解析数据错误");
                return;
            }
            if (values[0].ReType == 100) {
                new AgainLoginUtil(NurseEvaluateActivity.this, application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        actionSaveTask();
                    }
                }).showLoginDialog();
                return;
            }
            if (values[0].ReType != 0) {
                if (values[0].ReType != 0) {
                    showMsgAndVoice(values[0].Msg);
                    /*MediaUtil.getInstance(NurseEvaluateActivity.this).playSound(
                            R.raw.wrong, NurseEvaluateActivity.this);*/
                }
                return;
            }
            if (mScrollView.getChildCount() > 0) {
                mScrollView.removeAllViews();
                mScrollView.invalidate();
            }


            View view = parseResponse(values[0]);

            if (view == null) {
                showMsgAndVoiceAndVibrator("请求失败：解析数据错误");
                return;
            }

            Form form = (Form) view.getTag();
            boolean globalSign = form.globalSign;
            boolean signed = EmptyTool.isBlank(form.QMGH);
            boolean review = !"1".equals(form.SYZT);
            isSigned = !signed;
            isReview = !review;
            taggleMenu(globalSign, signed, review);
            if (!review) {
                setReviewSateText("当前状态：已审阅");
            } else {
                setReviewSateText("当前状态：待审阅");
            }
            setFormScore(form.Score);
            mScrollView.addView(view);

            request = new Request();
            request.ysxh = form.YSXH;
            request.jlxh = form.ID;
            request.lybs = form.LYBS;
            request.type = GetDateTask.QRT_BY_JLXH;
        }

        @Override
        protected void onPostExecute(Response<EvaluateFormItem> result) {

            hideLoadingDialog();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(NurseEvaluateActivity.this, application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        actionSaveTask();
                        return;
                    }
                }).showLoginDialog();

            }
            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
                /*MediaUtil.getInstance(NurseEvaluateActivity.this).playSound(
                        R.raw.wrong, NurseEvaluateActivity.this);*/
            } else {
                showMsgAndVoice(R.string.project_save_success);
            }

        }
    }

    private SaveForm getFormHead(Form form, String timerStr) {
        SaveForm saveForm = new SaveForm();
        saveForm.ID = form.ID;
        saveForm.YSXH = form.YSXH;
        saveForm.Score = form.Score;
        saveForm.TXGH = application.user.YHID;
        saveForm.TXSJ = mTimeView.getText().toString();
        saveForm.JLSJ = timerStr;
        saveForm.JLGH = application.user.YHID;
        saveForm.YSLX = form.YSLX;
        saveForm.ZYH = application.sickPersonVo.ZYH;
        saveForm.LYBS = "0";
        return saveForm;
    }

    private EvaluateSaveRespose getSaveString(View view, Map<Classification, VEntity[]> param) {
        Form form = (Form) view.getTag();

        String timeStr = DateTimeHelper.getServerDateTime();
        EvaluateSaveRespose respose = new EvaluateSaveRespose();
        respose.saveForm = getFormHead(form, timeStr);
        Set<Classification> key = param.keySet();
        for (Iterator<Classification> it = key.iterator(); it.hasNext(); ) {
            Classification classification = (Classification) it.next();
            SaveForm saveForm = new SaveForm();
            saveForm.entities = param.get(classification);
            saveForm.ID = String.valueOf(classification.ID);
            saveForm.YSXH = form.YSXH;
            saveForm.Score = form.Score;
            saveForm.TXGH = application.user.YHID;
            saveForm.TXSJ = mTimeView.getText().toString();
            saveForm.JLSJ = timeStr;
            saveForm.JLGH = application.user.YHID;
            saveForm.YSLX = form.YSLX;
            saveForm.ZYH = application.sickPersonVo.ZYH;
            saveForm.FLLX = classification.FLLX;
            saveForm.XSFLLX = classification.XSFLLX;
            //
            saveForm.HSQM1 = classification.HSQM1;
            //
            saveForm.LYBS = "0";
            respose.lists.add(saveForm);
        }
        return respose;
    }

    class GetDateTask extends AsyncTask<String, Response<EvaluateFormItem>, Response<EvaluateFormItem>> {

        public static final byte QRT_BY_YSXH = 0;
        public static final byte QRT_BY_JLXH = 1;

        private byte mType = QRT_BY_YSXH;

        public GetDateTask(byte type) {
            this.mType = type;
        }

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<EvaluateFormItem> doInBackground(String... params) {

            if (params == null || params.length < 1) {
                return null;
            }

            String jgid = application.jgId;
            String lybs = request.lybs;
            String txsj = mTimeView.getText().toString().trim();
            try {
                txsj = URLEncoder.encode(txsj, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            EvaluateApi api = EvaluateApi.getInstance(NurseEvaluateActivity.this);

            if (mType == QRT_BY_JLXH) {//根据记录序号获取评估表单

                String jlxh = params[0];
                Response<EvaluateFormItem> response = api.GetEvaluation(jlxh, jgid, lybs,
                        request.ysxh, txsj, Constant.sysType);

                if (response.ReType == 0) {
                    publishProgress(response);
                }
                return response;
            } else {// 默认根据ysxh
                if (application.sickPersonVo == null) {
                    return null;
                }
                String bqdm = application.getAreaId();
                String ysxh = params[0];
                String zyh = application.sickPersonVo.ZYH;
                Response<EvaluateFormItem> response = api.GetNewEvaluation(request.isZKNotCheckBQ, request.isNewPage, bqdm, zyh, ysxh,
                        txsj, jgid, Constant.sysType);

                if (response.ReType == 0) {
                    publishProgress(response);
                }
                return response;
            }

        }

        @Override
        protected void onProgressUpdate(Response<EvaluateFormItem>... values) {

            if (values == null || values.length < 1 || values[0] == null) {
                showMsgAndVoiceAndVibrator("请求失败：解析数据错误");
                return;
            }
            if (values[0].ReType == 100) {
                new AgainLoginUtil(NurseEvaluateActivity.this, application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        toRefreshData();
                    }
                }).showLoginDialog();
                return;
            }
            if (values[0].ReType != 0) {
                showMsgAndVoice(values[0].Msg);
                return;
            }

            View view = parseResponse(values[0]);

            if (view == null) {
                showMsgAndVoiceAndVibrator("请求失败：解析数据错误");
                return;
            }

            if (mScrollView.getChildCount() > 0) {
                mScrollView.removeAllViews();
                mScrollView.invalidate();
            }

            Form form = (Form) view.getTag();
            boolean globalSign = form.globalSign;
            boolean signed = EmptyTool.isBlank(form.QMGH);
            boolean review = !"1".equals(form.SYZT);
            isReview = !review;
            isSigned = !signed;
            taggleMenu(globalSign, signed, review);
            if (!review) {
                setReviewSateText("当前状态：已审阅");
            } else {
                setReviewSateText("当前状态：待审阅");
            }
            setFormScore(form.Score);
            if (!TextUtils.isEmpty(form.TXSJ)) {
                String dateTime = DateTimeFactory.getInstance().custom2DateTime(form.TXSJ, DateTimeFormat.yyyy_MM_dd_HHmmSS);
                mTimeView.setText(dateTime);
            }
            mScrollView.addView(view);
        }

        @Override
        protected void onPostExecute(Response<EvaluateFormItem> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(NurseEvaluateActivity.this, application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        toRefreshData();
                        return;
                    }
                }).showLoginDialog();

            }
            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
               /* MediaUtil.getInstance(NurseEvaluateActivity.this).playSound(
                        R.raw.wrong, NurseEvaluateActivity.this);*/
                resetView();
            } else {
                //成功后  去获取  数据获取方式
                String YSLXTemp = "17";//YSLX=17 评估时间最近的有效记录  默认值
                if (result.Data != null && result.Data.form != null && !EmptyTool.isBlank(result.Data.form.YSLX)) {
                    YSLXTemp = result.Data.form.YSLX;
                }
                //请求 数据获取方式
                MyGetHqfsTask myGetHqfsTask = new MyGetHqfsTask();
                tasks.add(myGetHqfsTask);
                myGetHqfsTask.execute(YSLXTemp);
            }

        }
    }

    class MyGetHqfsTask extends AsyncTask<String, Integer, Response<String>> {
        @Override
        protected Response<String> doInBackground(String... strings) {
            //add 2017年4月26日09:02:50
            //
            String YSLXTemp = strings[0];
            EvaluateApi api = EvaluateApi.getInstance(NurseEvaluateActivity.this);
            Response<String> response = api.getSJHQFS(YSLXTemp, mAppApplication.jgId);
            return response;
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);
            tasks.remove(this);

            String hqfs = "2";//hqfs=2 评估时间最近的有效记录  默认值
            if (result != null && result.ReType == 0 && !EmptyTool.isBlank(result.Data)) {
                //成功
                hqfs = result.Data;
            }
            //保存获取方式
            mAppApplication.mHQFSTemp = hqfs;
        }


    }

    private View parseResponse(Response<EvaluateFormItem> response) {

        if (response != null && response.ReType == 0) {

            EvaluateFormItem item = response.Data;
            form = item.form;
            //协和 打开老评估单能够保存
            request.jlxh = form.ID;
            //
            check = item.BTX;
            boolean isSync = item.IsSync == 0 ? false : true;
            if (isSync) {
                SelectResult selectResult = item.list.get(0);
                new FormSyncUtil().InvokeSync(this,
                        selectResult, application.jgId, tasks);
//                new FormSyncUtil().InvokeAsync(this, syncList,
//                        application.jgId, tasks);
            }
            View child = buildUi(form);
            return child;
        }
        return null;
    }


    private View buildUi(Form form) {

        if (form == null) {
            return null;
        }
        viewFactory = new EvaluateViewFactory(this, this, dtPickerListener,
                signClickListener, check);
        viewFactory.Txsj = mTimeView.getText().toString().trim();
        LinearLayout child = viewFactory.builderUi(form);
        return child;
    }

    private void setReviewSateText(String state) {

        TextView txt = (TextView) findViewById(R.id.evaluate_state_txt);
        txt.setText(state);

    }

    private void setFormScore(String score) {
        TextView sc = (TextView) findViewById(R.id.evaluate_score_txt);
        if (score == null || EmptyTool.isBlank(score)) {
            score = "0";
        }
        sc.setText("总分：" + score);
    }

    private void taggleMenu(boolean globalSign, boolean signed, boolean review) {
        int[] itemDrawables = {};
        int[][] itemStringDrawables = {};
        if (globalSign) {

            if (signed) {
                itemDrawables = ArrayUtils.add(itemDrawables, 0,
                        R.drawable.menu_sign);
                itemStringDrawables = ArrayUtils.insert(0, itemStringDrawables,
                        new int[]{R.drawable.menu_sign, R.string.comm_menu_sign});
            } else {
                itemDrawables = ArrayUtils.add(itemDrawables, 0,
                        R.drawable.menu_cancel_sign);
                itemStringDrawables = ArrayUtils.insert(0, itemStringDrawables,
                        new int[]{R.drawable.menu_cancel_sign, R.string.comm_menu_cancel_sign});
            }
        }

        if (isShowReviewMenu) {
            if (review) {
                itemDrawables = ArrayUtils.add(itemDrawables, 0,
                        R.drawable.menu_review);
                itemStringDrawables = ArrayUtils.insert(0, itemStringDrawables,
                        new int[]{R.drawable.menu_review, R.string.comm_menu_review});
            } else {
                itemDrawables = ArrayUtils.add(itemDrawables, 0,
                        R.drawable.menu_cancel_review);
                itemStringDrawables = ArrayUtils.insert(0, itemStringDrawables,
                        new int[]{R.drawable.menu_cancel_review, R.string.comm_menu_cancel_review});
            }
        }
        /*itemDrawables = ArrayUtils.addAll(itemDrawables,
                R.drawable.menu_create, R.drawable.menu_view,
                R.drawable.menu_fresh, R.drawable.menu_save);*/
        itemDrawables = ArrayUtils.addAll(itemDrawables,
                R.drawable.menu_create,
                R.drawable.menu_fresh, R.drawable.menu_save);
        itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                new int[]{R.drawable.menu_create, R.string.comm_menu_add},
                new int[]{R.drawable.menu_fresh, R.string.comm_menu_refresh},
                new int[]{R.drawable.menu_save, R.string.comm_menu_save}
        );
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
      /*  for (int itemDrawableResid : itemDrawables) {
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
        updateFloatMenuItems(floatMenuItemList);

    }

    private void actionReview(byte type, String... params) {
        ReviewAboutTask task = new ReviewAboutTask(type);
        tasks.add(task);
        task.execute(params);
    }

    /**
     * 审读 task
     *
     * @author hy
     */
    class ReviewAboutTask extends AsyncTask<String, String, Response<EvaluateResponse>> {

        public static final byte ACTION_REVIEW = 0;

        public static final byte ACTION_CANCEL_REVIEW = ACTION_REVIEW + 1;

        private byte mActionType = ACTION_REVIEW;

        public ReviewAboutTask(byte mActionType) {

            this.mActionType = mActionType;
        }

        @Override
        protected void onPreExecute() {
            showLoadingDialog(mActionType == ACTION_CANCEL_REVIEW ? getString(R.string.cancel_reviewing)
                    : getString(R.string.reviewing));
        }

        @Override
        protected Response<EvaluateResponse> doInBackground(String... params) {

            if (params == null || params.length < 1) {
                return null;
            }

            if (application.user == null) {
                return null;
            }

            String jlxh = params[0];
            String sygh = application.user.YHID;
            String jgid = application.jgId;
            EvaluateApi api = EvaluateApi.getInstance(NurseEvaluateActivity.this);

            Response<EvaluateResponse> response = new Response<>();
            if (mActionType == ACTION_CANCEL_REVIEW) {
                response = api.CancelEvaluationReview(jlxh, sygh, jgid,
                        Constant.sysType);
            } else if (mActionType == ACTION_REVIEW) {
                response = api.EvaluationReview(jlxh, sygh, jgid,
                        Constant.sysType);
            }

            return response;

        }

        @Override
        protected void onPostExecute(Response<EvaluateResponse> result) {

            hideLoadingDialog();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(NurseEvaluateActivity.this, application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        return;
                    }
                }).showLoginDialog();

            }

            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
                /*MediaUtil.getInstance(NurseEvaluateActivity.this).playSound(
                        R.raw.wrong, NurseEvaluateActivity.this);*/
            }
            if (result.ReType == 0) {
                showMsgAndVoice(result.Msg);
                setReviewSateText(mActionType == ACTION_CANCEL_REVIEW ? "当前状态：待审阅"
                        : "当前状态：已审阅");
                // 刷新操作
                if (request != null) {
                    ationGetDateTask(request.type, request.getBH());
                }
            }


        }
    }

    private void getRelate(String dzlx) {
        GetRelativeData task = new GetRelativeData();
        tasks.add(task);
        task.execute(dzlx);
    }

    class GetRelativeData extends AsyncTask<String, Void, Response<EvaluateFormItem>> {
        private String dzlx = "0";

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<EvaluateFormItem> doInBackground(String... params) {

            if (params == null || params.length < 1) {
                return null;
            }
            if (application.user == null) {
                return null;
            }
            dzlx = params[0];
            String txsj = mTimeView.getText().toString().trim();
            try {
                txsj = URLEncoder.encode(txsj, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            EvaluateApi api = EvaluateApi.getInstance(NurseEvaluateActivity.this);
            return api.GetRelatvieData(application.sickPersonVo.ZYH,
                    request.ysxh, params[0], application.getAreaId(),
                    txsj, application.jgId);

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<EvaluateFormItem> result) {

            hideLoadingDialog();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(NurseEvaluateActivity.this, application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        return;
                    }
                }).showLoginDialog();
                return;
            }

            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
                /*MediaUtil.getInstance(NurseEvaluateActivity.this).playSound(
                        R.raw.wrong, NurseEvaluateActivity.this);*/
                return;
            }

            EvaluateFormItem item = result.Data;
            //add

            if (dzlx.equals("5")) {

                if ("3".equals(mAppApplication.mHQFSTemp)) {//当前创建的记录
                    int count;
                    if (item.form.clazzs != null && item.form.clazzs.size() > 0) {
                        count = mAppApplication.mLifeSymptomTempDataBean == null ? 0 : mAppApplication.mLifeSymptomTempDataBean.size();
                        if (count > 0) {
                            Log.i(Constant.TAG_COMM, "onPostExecute: count" + count);
                            for (int i = 0; i < item.form.clazzs.size(); i++) {
                                if (item.form.clazzs.get(i).itemNodes != null && item.form.clazzs.get(i).itemNodes.size() > 0) {
                                    for (int j = 0; j < item.form.clazzs.get(i).itemNodes.size(); j++) {

                                        if (item.form.clazzs.get(i).itemNodes.get(j).inputs != null && item.form.clazzs.get(i).itemNodes.get(j).inputs.size() > 0) {
                                            for (int k = 0; k < item.form.clazzs.get(i).itemNodes.get(j).inputs.size(); k++) {
                                                String xm = item.form.clazzs.get(i).itemNodes.get(j).Dzxm;
                                                LifeSymptomTempDataBean lifeSymptomTempDataBean = mAppApplication.mLifeSymptomTempDataBean.get(xm);
                                                if (lifeSymptomTempDataBean == null) {
                                                    continue;
                                                }
                                                String nowData = lifeSymptomTempDataBean.Data;
                                                item.form.clazzs.get(i).itemNodes.get(j).inputs.get(k).Value = nowData;
                                                item.form.clazzs.get(i).itemNodes.get(j).inputs.get(k).dzbdjl_my = lifeSymptomTempDataBean.CJH;
                                                count--;
                                                if (count <= 0) {
                                                    break;
                                                }
                                            }
                                        }
                                        if (item.form.clazzs.get(i).itemNodes.get(j).numbers != null && item.form.clazzs.get(i).itemNodes.get(j).numbers.size() > 0) {
                                            for (int k = 0; k < item.form.clazzs.get(i).itemNodes.get(j).numbers.size(); k++) {
                                                String xm = item.form.clazzs.get(i).itemNodes.get(j).Dzxm;
                                                LifeSymptomTempDataBean lifeSymptomTempDataBean = mAppApplication.mLifeSymptomTempDataBean.get(xm);
                                                if (lifeSymptomTempDataBean == null) {
                                                    continue;
                                                }
                                                String nowData = lifeSymptomTempDataBean.Data;
                                                item.form.clazzs.get(i).itemNodes.get(j).numbers.get(k).Value = nowData;
                                                item.form.clazzs.get(i).itemNodes.get(j).inputs.get(k).dzbdjl_my = lifeSymptomTempDataBean.CJH;
                                                count--;
                                                if (count <= 0) {
                                                    break;
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }

                }
           /* if (mAppApplication.mLifeSymptomTempDataBean!=null){
            //用完清空
            mAppApplication.mLifeSymptomTempDataBean.clear();
            }*/
                //add 2017年4月26日08:50:22
            } else if (dzlx.equals("2")) {

                if ("3".equals(mAppApplication.mHQFSTemp)) {//当前创建的记录
                    int count;
                    if (item.form.clazzs != null && item.form.clazzs.size() > 0) {
                        count = mAppApplication.mEvaluateTempDataBean == null ? 0 : mAppApplication.mEvaluateTempDataBean.size();
                        if (count > 0) {
                            Log.i(Constant.TAG_COMM, "onPostExecute 222: count" + count);
                            for (int i = 0; i < item.form.clazzs.size(); i++) {
                                if (item.form.clazzs.get(i).itemNodes != null && item.form.clazzs.get(i).itemNodes.size() > 0) {
                                    for (int j = 0; j < item.form.clazzs.get(i).itemNodes.size(); j++) {
                                        if (item.form.clazzs.get(i).itemNodes.get(j).inputs != null && item.form.clazzs.get(i).itemNodes.get(j).inputs.size() > 0) {
                                            for (int k = 0; k < item.form.clazzs.get(i).itemNodes.get(j).inputs.size(); k++) {
                                                String xm = item.form.clazzs.get(i).itemNodes.get(j).Dzbd;//Dzbd 对应PGLX
                                                EvaluateTempDataBean bean = mAppApplication.mEvaluateTempDataBean.get(xm);
                                                if (bean != null) {
                                                    item.form.clazzs.get(i).itemNodes.get(j).inputs.get(k).Value = bean.PGZF;
                                                    item.form.clazzs.get(i).itemNodes.get(j).inputs.get(k).dzbdjl_my = bean.PGXH;
                                                    //
                                                    count--;
                                                    if (count <= 0) {
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (item.form.clazzs.get(i).itemNodes.get(j).numbers != null && item.form.clazzs.get(i).itemNodes.get(j).numbers.size() > 0) {
                                            for (int k = 0; k < item.form.clazzs.get(i).itemNodes.get(j).numbers.size(); k++) {
                                                String xm = item.form.clazzs.get(i).itemNodes.get(j).Dzbd;//Dzbd 对应PGLX
                                                EvaluateTempDataBean bean = mAppApplication.mEvaluateTempDataBean.get(xm);
                                                if (bean != null) {
                                                    item.form.clazzs.get(i).itemNodes.get(j).numbers.get(k).Value = bean.PGZF;
                                                    item.form.clazzs.get(i).itemNodes.get(j).numbers.get(k).dzbdjl_my = bean.PGXH;
                                                    //
                                                    count--;
                                                    if (count <= 0) {
                                                        break;
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }

                }
              /* 不清空 if (mAppApplication.mEvaluateTempDataBean!=null){
                    mAppApplication.mEvaluateTempDataBean.clear();
                }*/
            } else if (dzlx.equals("3")) {
                //todo
            }
            ValueForm values = parseRelate(item.form);
            if (values.root.values != null) {
                if (dzlx.equals("2")) {
                    viewFactory.resetRisk(values.root.values);
                } else if (dzlx.equals("3")) {
                    viewFactory.resetGuid(values.root.values);
                } else if (dzlx.equals("5")) {
                    viewFactory.resetLife(values.root.values);
                }
            }
        }

    }

    private ValueForm parseRelate(Form form) {
        try {
            String json = JsonUtil.toJson(form, JsonSerialize.Inclusion.NON_NULL);
            String regex = "Input\":\\[(.*?)\\]";
            ValueForm entity = new ValueForm();
            jsonInterception(json, regex, entity);
            regex = "Numeric\":\\[(.*?)\\]";
            jsonInterception(json, regex, entity);
            return entity;
        } catch (Exception e) {
            Log.e(Constant.TAG, e.getMessage(), e);
        }
        return null;
    }

    private void jsonInterception(String json, String regex, ValueForm entity)
            throws Exception {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        Matcher matcher = pattern.matcher(json);
        while (!matcher.hitEnd() && matcher.find()) {
            String str = matcher.group(1);
            //JSONObject obj = new JSONObject(str);
            str = "[" + str + "]";
            JSONArray  arrs = new JSONArray(str);

            for(int i = 0; i < arrs.length(); i++) {
                JSONObject obj = arrs.getJSONObject(i);

                Iterator ite = obj.keys();
                ValueEntity node = new ValueEntity();
                while (ite.hasNext()) {
                    String key = ite.next().toString();
                    if (key.equals("ID")) {
                        node.ID = Integer.parseInt(obj.getString(key));
                    }
                    if (key.equals("GroupId")) {
                        node.GroupId = Integer.parseInt(obj.getString(key));
                    }
                    if (key.equals("dzbdjl_my")) {
                        //add
                        if (obj.getString(key) != null && !obj.getString(key).equals("null")) {
                            node.dzbdjl_my = obj.getString(key);
                        }
                    } else if (key.equals("Value")) {
                        if (obj.getString(key).equals("null")) {
                            continue;
                        } else {
                            node.VALUE = obj.getString(key);
                        }
                    }
                }
                if (node.VALUE != null && !node.VALUE.equals("")) {
                    entity.root.values.add(node);
                }
            }
        }
    }

    private DateTimePickerListener dtPickerListener = new DateTimePickerListener() {

        @Override
        public void onDateTimeClick(View view, String sjgs) {

            if (!(view instanceof TextView)) {
                return;
            }

            SJGS = sjgs;
            TextView timeTxt = (TextView) view;
            int viewId = timeTxt.getId();
            String dateStr = timeTxt.getText().toString();

            // 导入年月数据
            if (!EmptyTool.isBlank(dateStr)) {
                if (!EmptyTool.isBlank(SJGS)) {
                    SJGS = changeFormaterStr(SJGS);
                    dateStr = DateTimeFactory.getInstance().custom2DateTime(dateStr, SJGS);
                } else {
                    dateStr = DateTimeHelper.getServer_yyyyMMddHHmm00(dateStr);
                }
            }
            YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateStr);
            showPickerDateTimeCompat(ymdHMs, viewId);
        }
    };

    private SignClickListener signClickListener = new SignClickListener() {

        @Override
        public void onSign(View view) {

            if (request == null) {
                Log.e(Constant.TAG, "请求错误 ：参数未赋值");
                return;
            }

            if (EmptyTool.isBlank(request.jlxh)) {
                showMsgAndVoiceAndVibrator("请求失败：请先保存表单数据");
                return;
            }

            try {
                Classification cf = (Classification) view.getTag();
                String json = JsonUtil.toJson(cf);
                startSignActivity("1".equals(cf.QMBZ), json);
                return;
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
            }
        }

        //fixmee
        @Override
        public void onAutoSign(Classification classification, boolean cancelSign) {
            //
            // 自动签名
            String yhid = mAppApplication.user.YHID;

            for (int i = 0; i < form.clazzs.size(); i++) {
                if (form.clazzs.get(i).ID == classification.ID) {
                    form.clazzs.get(i).HSQM1 = cancelSign ? "" : yhid;
                    viewFactory.updateSginView(classification.ID, cancelSign);
                    break;
                }
            }

        }
    };

    /**
     * 解析当前所更改的数据
     *
     * @param cf
     * @return
     */
    private ArrayList<VEntity> getRequestList(Classification cf) {

        ArrayList<VEntity> result = new ArrayList<VEntity>();

        if (cf == null || viewFactory == null) {
            return result;
        }

        SparseArray<ArrayList<VEntity>> map = viewFactory.map;

        if (map == null) {
            return result;
        }

        List<ItemNode> nodes = cf.itemNodes;
        if (nodes == null) {
            return result;
        }

        for (ItemNode node : nodes) {

            int index = map.indexOfKey(node.ID);
            if (index < 0) {
                continue;
            }
            result.addAll(map.get(node.ID));
        }

        return result;
    }

    private void ationGetDateTask(byte type, String... params) {
        GetDateTask task = new GetDateTask(type);
        tasks.add(task);
        task.execute(params);

    }

    @Override
    public void initBarBroadcast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    resetView();
                }
            }
        };
    }
}
